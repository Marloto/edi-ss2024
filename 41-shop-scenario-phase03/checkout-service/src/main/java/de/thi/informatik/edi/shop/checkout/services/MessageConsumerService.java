package de.thi.informatik.edi.shop.checkout.services;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.Many;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;

@Service
public class MessageConsumerService {

	private static Logger logger = LoggerFactory.getLogger(ShippingMessageConsumerService.class);
	
	@Value("${kafka.servers:localhost:9092}")
	private String servers;
	@Value("${kafka.group:checkout}")
	private String group;
	
	private KafkaConsumer<String, String> consumer;
	private boolean running;
	private TaskExecutor executor;

	private Flux<Tuple3<String, String, String>> data;
	
	private Set<String> topics;

	private boolean changed;
	
	public MessageConsumerService(@Autowired TaskExecutor executor) {
		this.executor = executor;
		this.running = true;
		this.topics = new HashSet<>();
	}

	@PostConstruct
	private void init() throws UnknownHostException {
		Properties config = new Properties();
		config.put("client.id", InetAddress.getLocalHost().getHostName());
		config.put("bootstrap.servers", servers);
		config.put("group.id", group);
		config.put("key.deserializer", StringDeserializer.class.getName());
		config.put("value.deserializer", StringDeserializer.class.getName());
		logger.info("Connect to " + servers + " as " + config.getProperty("client.id") + "@" + group);
		this.consumer = new KafkaConsumer<>(config);
		
		//this.consumer.subscribe(List.of(getTopic()));
		
		Many<ConsumerRecord<String, String>> sink = Sinks.many().multicast().onBackpressureBuffer();
		
		this.executor.execute(() -> {
			while (running) {
				try {					
					if(topics.isEmpty()) {
						Thread.sleep(1000);
						continue;
					}
					if(changed) {
						logger.info("Subscribe to " + topics.toString());
						consumer.subscribe(topics);
						changed = false;
					}
					ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
					records.forEach(el -> sink.tryEmitNext(el));
					consumer.commitSync();
				} catch(Exception e) {
					e.printStackTrace();
				}
			}				
		});
		
		this.data = sink.asFlux().map(el -> Tuples.of(el.topic(), el.key(), el.value()));
	}

	protected Flux<Tuple2<String, String>> getMessages(String topic) {
		if(!this.topics.contains(topic)) {
			this.topics.add(topic);
			this.changed = true;
		}
		return data.filter(el -> el.getT1().equals(topic)).map(el -> Tuples.of(el.getT2(), el.getT3()));
	}
	
	@PreDestroy
	private void shutDown() {
		this.running = false;
	}
}
