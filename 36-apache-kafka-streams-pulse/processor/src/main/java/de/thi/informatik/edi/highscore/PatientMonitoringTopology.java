package de.thi.informatik.edi.highscore;

import java.time.Duration;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Suppressed;
import org.apache.kafka.streams.kstream.Suppressed.BufferConfig;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Windowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.thi.informatik.edi.highscore.model.BodyTemp;
import de.thi.informatik.edi.highscore.model.CombinedVitals;
import de.thi.informatik.edi.highscore.model.Pulse;

public class PatientMonitoringTopology {
	private static final Logger logger = LoggerFactory.getLogger(PatientMonitoringTopology.class);
	public static Topology build() {
		StreamsBuilder builder = new StreamsBuilder();
		
		KStream<String, Pulse> pulseEvents = builder.stream(Configurator.PULSE_EVENTS, 
				Consumed.with(Serdes.String(), JsonSerdes.pulse())
					.withTimestampExtractor(new VitalTimestampExtractor()));
		
		KStream<String, BodyTemp> bodyTempEvents = builder.stream(Configurator.BODY_TEMP_EVENTS, 
				Consumed.with(Serdes.String(), JsonSerdes.bodyTemp())
					.withTimestampExtractor(new VitalTimestampExtractor()));
        
		// Verzögerungen können über einen "Grace" beschrieben werden
		// -> Recht klein, z.B. eine Sekunde
		// -> Zu klein: informationen gehen verloren
		// -> Zu groß: verfälscht, man muss den Graze abwarten
		TimeWindows window = TimeWindows.ofSizeAndGrace(Duration.ofSeconds(60), Duration.ofSeconds(5));
		KTable<Windowed<String>, Long> pulseCounts = 
				pulseEvents
					.groupByKey()
					.windowedBy(window)
					.count()
					.suppress(Suppressed.untilWindowCloses(BufferConfig.unbounded().shutDownWhenFull()));
		// -> was ist der Zeitbezug für das "suppress" Ergebnis, also das letzte Ereignis was sich hier als Count ergibt
		
		KStream<String, Long> highPulse = pulseCounts.toStream()
			.filter((key, value) -> value >= 100)
			.map((windowedKey, value) -> KeyValue.pair(windowedKey.key(), value)); // <-
		
		KStream<String, BodyTemp> highTemp = bodyTempEvents.filter((key, value) -> value.getTemperature() > 38);
		
		JoinWindows joinWindows = JoinWindows.ofTimeDifferenceAndGrace(Duration.ofSeconds(60), Duration.ofSeconds(10));
		
		// KStream zu KStream, hier JoinWindow als Möglichkeit zum Kombinieren
		// <- hier ansetzen
		KStream<String, CombinedVitals> vitalsJoined = highPulse.join(highTemp, (pulseRate, bodyTemp) -> 
			new CombinedVitals(pulseRate.intValue(), bodyTemp), joinWindows);
		
		vitalsJoined.to(Configurator.ALERTS);
		
        pulseCounts
	        .toStream()
	        .print(Printed.<Windowed<String>, Long>toSysOut().withLabel("pulse-counts"));
	    highPulse.print(Printed.<String, Long>toSysOut().withLabel("high-pulse"));
	    highTemp.print(Printed.<String, BodyTemp>toSysOut().withLabel("high-temp"));
	    vitalsJoined.print(Printed.<String, CombinedVitals>toSysOut().withLabel("vitals-joined"));
		
		return builder.build();
	}
}
