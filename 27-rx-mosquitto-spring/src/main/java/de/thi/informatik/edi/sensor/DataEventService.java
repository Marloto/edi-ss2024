package de.thi.informatik.edi.sensor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

@Service
public class DataEventService {
	
	@Value("${mqtt.topic:servers/#}")
	private String topic;

	private static final Pattern p = Pattern.compile("servers/(.*?)/(.*)");

	private MessageBrokerService service;

	private Flux<DataEvent> events;
	
	public DataEventService(MessageBrokerService service) {
		this.service = service;
	}
	
	@PostConstruct
	public void init() {
		// Topic, Value (als String)
		
		// Lösung mit einer flatMap-Methode, Logik wird in dieser abgearbeitet
//		this.service.subscribe(topic)
//			.flatMap(el -> {
//				Matcher matcher = p.matcher(el.getT1());
//				if(matcher.find()) {
//					String server = matcher.group(1);
//					String type = matcher.group(2);
//					double value = Double.valueOf(el.getT2());
//					return Mono.just(new DataEvent(server, type, value));
//				}
//				return Mono.empty();
//			});
		
		// Aufteilen in einzelne Operatoren, vorteil um später die Parallelisierung von Rx zu nutzen
		this.events = this.service.subscribe(topic)
				.map(el -> Tuples.of(p.matcher(el.getT1()), el.getT2())) // Tuple2<Matcher, String>
				.filter(el -> el.getT1().find()) // Bedingung
				.map(el -> new DataEvent(
						el.getT1().group(1),
						el.getT1().group(2), 
						Double.valueOf(el.getT2())));
		
		// - [ ] Servernamen und Typ extrahieren
		// - [ ] Datenpaket (String) in Double umwandeln
		// - [ ] Instanz von DataEvent erzeugen
	}
	
	public Flux<DataEvent> eventsByType(String type) {
		return events.filter(el -> el.getType().equals(type));
	}
}


/**
servers/990c4410fb4a/cpu-usage 0.01005025125628145
servers/990c4410fb4a/cpu-free 0.9899497487437185
servers/990c4410fb4a/free-mem 6982.91015625
servers/990c4410fb4a/process/usage-mem 56037376
servers/990c4410fb4a/process/heap-total 17932288
servers/990c4410fb4a/total-mem 7959.4609375
servers/990c4410fb4a/freemem-percentage 0.877309432269577
**/