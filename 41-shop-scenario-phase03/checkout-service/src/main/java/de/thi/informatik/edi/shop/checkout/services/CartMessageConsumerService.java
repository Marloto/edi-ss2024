package de.thi.informatik.edi.shop.checkout.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.thi.informatik.edi.shop.checkout.services.messages.ArticleAddedToCartMessage;
import de.thi.informatik.edi.shop.checkout.services.messages.CartMessage;
import de.thi.informatik.edi.shop.checkout.services.messages.CreatedCartMessage;
import de.thi.informatik.edi.shop.checkout.services.messages.DeleteArticleFromCartMessage;
import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Flux;

@Service
public class CartMessageConsumerService {
	
	private static Logger logger = LoggerFactory.getLogger(CartMessageConsumerService.class);
	
	@Value("${kafka.cartTopic:cart}")
	private String topic;

	private Flux<CartMessage> messages;

	private MessageConsumerService consumer;

	public CartMessageConsumerService(@Autowired MessageConsumerService consumer) {
		this.consumer = consumer;
	}
	
	@PostConstruct
	private void init() {
		messages = this.consumer.getMessages(topic)
			.map(el -> 
				el.getT2())
			.map(this::deserialize)
			.filter(el -> el.getType() != null);
	}
	
	private CartMessage deserialize(String value) {
		try {
			return new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).readValue(value, CartMessage.class);
		} catch (JsonProcessingException e) {
			logger.error("Error while handling message: " + value, e);
			return new CartMessage();
		}
	}

	public Flux<ArticleAddedToCartMessage> getAddedToCartMessages() {
		return messages.filter(el -> el instanceof ArticleAddedToCartMessage).map(el -> (ArticleAddedToCartMessage)el);
	}
	
	public Flux<DeleteArticleFromCartMessage> getDeletedFromCartMessages() {
		return messages.filter(el -> el instanceof DeleteArticleFromCartMessage).map(el -> (DeleteArticleFromCartMessage)el);
	}
	
	public Flux<CreatedCartMessage> getCartCreatedMessages() {
		return messages.filter(el -> el instanceof CreatedCartMessage).map(el -> (CreatedCartMessage)el);
	}
	
}
