package de.thi.informatik.edi.shop.checkout.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.thi.informatik.edi.shop.checkout.services.messages.ShippingMessage;
import reactor.core.publisher.Flux;

@Service
public class ShippingMessageConsumerService {

	private static Logger logger = LoggerFactory.getLogger(ShippingMessageConsumerService.class);

	@Value("${kafka.shippingTopic:shipping}")
	private String topic;

	private MessageConsumerService consumer;
	
	public ShippingMessageConsumerService(@Autowired MessageConsumerService consumer) {
		this.consumer = consumer;
	}
	
	private ShippingMessage deserialize(String value) {
		try {
			return new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).readValue(value, ShippingMessage.class);
		} catch (JsonProcessingException e) {
			logger.error("Error while handling message: " + value, e);
			return new ShippingMessage();
		}
	}
	
	public Flux<ShippingMessage> getShippingMessages() {
		return this.consumer.getMessages(topic)
				.map(el -> el.getT2())
				.map(this::deserialize)
				.filter(el -> el.getOrderRef() != null);
	}
}
