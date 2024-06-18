package de.thi.informatik.edi.shop.checkout.services;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.thi.informatik.edi.shop.checkout.model.ShoppingOrder;
import de.thi.informatik.edi.shop.checkout.model.ShoppingOrderStatus;
import de.thi.informatik.edi.shop.checkout.repositories.ShoppingOrderRepository;
import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.Many;

@Service
public class ShoppingOrderService {
	private ShoppingOrderRepository orders;
	
	private CartMessageConsumerService cartMessages;
	private PaymentMessageConsumerService paymentMessages;
	
	// Sink for updates
	private Many<ShoppingOrder> updates;

	private ShippingMessageConsumerService shippingMessages;

	public ShoppingOrderService(@Autowired ShoppingOrderRepository orders, 
			@Autowired CartMessageConsumerService cartMessages,
			@Autowired PaymentMessageConsumerService paymentMessages, 
			@Autowired ShippingMessageConsumerService shippingMessages) {
		this.orders = orders;
		this.cartMessages = cartMessages;
		this.paymentMessages = paymentMessages;
		this.shippingMessages = shippingMessages;
		this.updates = Sinks.many().multicast().onBackpressureBuffer();
	}
	
	@PostConstruct
	private void init() {
		this.cartMessages.getCartCreatedMessages()
			.map(el -> el.getId())
			.subscribe(this::createOrderWithCartRef);
		
		this.cartMessages.getAddedToCartMessages().subscribe(el -> addItemToOrderByCartRef(
				el.getId(), 
				el.getArticle(), 
				el.getName(), 
				el.getPrice(), 
				el.getCount()));
		
		this.cartMessages.getDeletedFromCartMessages().subscribe(el -> deleteItemFromOrderByCartRef(
				el.getId(), 
				el.getArticle()));
		
		this.paymentMessages.getPaymentMessages()
			.filter(el -> "PAYED".equals(el.getStatus()))
			.map(el -> el.getOrderRef())
			.subscribe(this::updateOrderIsPayed);
		this.shippingMessages.getShippingMessages()
			.filter(el -> "SHIPPED".equals(el.getStatus()))
			.map(el -> el.getOrderRef())
			.subscribe(this::updateOrderIsShipped);
	}

	
	public void addItemToOrderByCartRef(UUID cartRef, UUID article, String name, double price, int count) {
		ShoppingOrder order = this.getOrCreate(cartRef);
		order.addItem(article, name, price, count);
		this.orders.save(order);
		
	}
	
	private Optional<ShoppingOrder> findByCartRef(UUID cartRef) {
		return this.orders.findByCartRef(cartRef);
	}

	public void deleteItemFromOrderByCartRef(UUID cartRef, UUID article) {
		ShoppingOrder order = this.getOrCreate(cartRef);
		order.removeItem(article);
		this.orders.save(order);
	}

	public ShoppingOrder createOrderWithCartRef(UUID cartRef) {
		ShoppingOrder order = this.getOrCreate(cartRef);
		this.orders.save(order);
		return order;
	}

	private ShoppingOrder getOrCreate(UUID cartRef) {
		Optional<ShoppingOrder> orderOption = this.findByCartRef(cartRef);
		ShoppingOrder order;
		if(orderOption.isEmpty()) {
			order = new ShoppingOrder(cartRef);
		} else {
			order = orderOption.get();
		}
		return order;
	}

	public void updateOrder(UUID id, String firstName, String lastName, String street, String zipCode,
			String city) {
		ShoppingOrder order = findById(id);
		order.update(firstName, lastName, street, zipCode, city);
		this.orders.save(order);
	}

	private ShoppingOrder findById(UUID id) {
		Optional<ShoppingOrder> optional = this.orders.findById(id);
		if(optional.isEmpty()) {
			throw new IllegalArgumentException("Unknown order with id " + id.toString());
		}
		ShoppingOrder order = optional.get();
		return order;
	}

	public void placeOrder(UUID id) {
		ShoppingOrder order = findById(id);
		if(order.getStatus().equals(ShoppingOrderStatus.CREATED)) {
			order.setStatus(ShoppingOrderStatus.PLACED);
			this.emitOrderUpdate(order);
			this.orders.save(order);
		}
	}

	public ShoppingOrder find(UUID id) {
		return this.findById(id);
	}

	public void updateOrderIsPayed(UUID id) {
		ShoppingOrder order = this.findById(id);
		if(order.getStatus() == ShoppingOrderStatus.PLACED) {
			order.setStatus(ShoppingOrderStatus.PAYED);
			this.emitOrderUpdate(order);
			this.orders.save(order);
		}
	}

	public void updateOrderIsShipped(UUID id) {
		ShoppingOrder order = this.findById(id);
		if(order.getStatus() == ShoppingOrderStatus.PAYED) {
			order.setStatus(ShoppingOrderStatus.SHIPPED);
			this.emitOrderUpdate(order);
			this.orders.save(order);
		}
	}

	private void emitOrderUpdate(ShoppingOrder order) {
		this.updates.tryEmitNext(order);
	}
	
	public Flux<ShoppingOrder> getUpdates() {
		return updates.asFlux();
	}
}
