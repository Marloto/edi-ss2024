package de.thi.informatik.edi.shop.checkout.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import de.thi.informatik.edi.shop.checkout.services.messages.ShoppingOrderMessage;
import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;

@Service
public class ShoppingOrderMessageProducerService {
	
	private static Logger logger = LoggerFactory.getLogger(ShoppingOrderMessageProducerService.class);
	
	private MessageProducerService messages;
	
	@Value("${kafka.orderTopic:order}")
	private String topic;

	private ShoppingOrderService service;

	public ShoppingOrderMessageProducerService(ShoppingOrderService service, MessageProducerService messages) {
		this.service = service;
		this.messages = messages;
	}

	@PostConstruct
	public void init() {
		logger.info("Init push to producer for order updates");
		Flux<Tuple3<String, String, Object>> flux = this.service.getUpdates()
				.map(ShoppingOrderMessage::fromOrder)
				.map(el -> Tuples.of(topic, el.getId().toString(), el));
		this.messages.send(flux);
	}

}
