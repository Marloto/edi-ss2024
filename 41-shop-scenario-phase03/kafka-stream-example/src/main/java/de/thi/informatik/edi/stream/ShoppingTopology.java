package de.thi.informatik.edi.stream;

import java.time.Duration;
import java.util.UUID;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Joined;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Windowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.thi.informatik.edi.stream.messages.AggregatePrice;
import de.thi.informatik.edi.stream.messages.OrderAndPayment;
import de.thi.informatik.edi.stream.messages.PaymentMessage;
import de.thi.informatik.edi.stream.messages.ShoppingOrderMessage;

public class ShoppingTopology {
    private static final Logger logger = LoggerFactory.getLogger(ShoppingTopology.class);
    
    public static Topology build() {
            StreamsBuilder builder = new StreamsBuilder();
            
            return builder.build();
    }
}
