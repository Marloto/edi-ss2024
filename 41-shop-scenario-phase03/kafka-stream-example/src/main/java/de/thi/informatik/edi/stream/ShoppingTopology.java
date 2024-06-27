package de.thi.informatik.edi.stream;

import java.time.Duration;
import java.util.UUID;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.StreamJoined;
import org.apache.kafka.streams.kstream.Suppressed;
import org.apache.kafka.streams.kstream.Suppressed.BufferConfig;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Windowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.thi.informatik.edi.stream.messages.ArticleAddedToCartMessage;
import de.thi.informatik.edi.stream.messages.CartMessage;

public class ShoppingTopology {
    private static final Logger logger = LoggerFactory.getLogger(ShoppingTopology.class);
    
    public static Topology build() {
            StreamsBuilder builder = new StreamsBuilder();
            
KStream<UUID, CartMessage> cart = builder.stream("cart", Consumed.with(Serdes.UUID(), JsonSerdes.cartMessage()));
cart.print(Printed.toSysOut());

//Count cart adds
KStream<UUID, Long> cartArticleCount = builder.stream("cart", Consumed.with(Serdes.UUID(), JsonSerdes.cartMessage()))
	.filter((key, value) -> value instanceof ArticleAddedToCartMessage) // only use add message, todo maybe use delete as well
	.mapValues((value) -> (ArticleAddedToCartMessage) value) // map values to use right datatypes
	.groupBy((key, value) -> value.getArticle(), Grouped.with(Serdes.UUID(), JsonSerdes.addedToCartMessage())) // group by article id
	.windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofSeconds(60))) // create time window to have counts per 60s
	.count(Materialized.with(Serdes.UUID(), Serdes.Long())) // count add messages
	.suppress(Suppressed.untilWindowCloses(BufferConfig.unbounded().shutDownWhenFull())) // only use last count result, dont use intermediate counts
	.toStream() // create stream (count creates table)
	.selectKey((key, value) -> key.key()); // when using windowedBy, key will be "Windowed<UUID>" -> rekey to article id
            
// FlatMap for Order Items and count
KStream<UUID, Long> orderItemCount = builder.stream("order", 
		Consumed.with(Serdes.UUID(), JsonSerdes.shoppingOrderMessage()))
	.filter((key, value) -> "PAYED".equals(value.getStatus()))
	.flatMap((key, value) -> value.getItems().stream().map(el -> KeyValue.pair(el.getArticle(), (long) el.getCount())).toList())
	.groupByKey(Grouped.with(Serdes.UUID(), Serdes.Long()))
	.aggregate(() -> 0L, (key, value, before) -> before + value, Materialized.with(Serdes.UUID(), Serdes.Long())).toStream();

// Join both
//orderItemCount.join(cartArticleCount, (a, b) -> a / b, Joined.with(Serdes.UUID(), Serdes.Long(), Serdes.Long())).print(Printed.toSysOut());
orderItemCount.join(
		cartArticleCount, 
		(a, b) -> (double) a / (double) b, 
		JoinWindows.ofTimeDifferenceWithNoGrace(Duration.ofSeconds(60)), 
		StreamJoined.with(Serdes.UUID(), Serdes.Long(),Serdes.Long()))
	.print(Printed.toSysOut());



            
//            cartCounts.foreach((key, value) -> System.out.println(key + ": " + value));
//            order.foreach((key, value) -> System.out.println(key + ": " + value));
            cart.print(Printed.toSysOut());
            
            return builder.build();
    }
}
