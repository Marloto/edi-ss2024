package de.thi.informatik.edi.pubsub;

public class Main {
	public static void main(String[] args) {
		// Broker raus nehmen
		// Topic -> MessageChannel der den Sender und Receiver kennt
		// Sende Methode muss in einer Liste persistieren
		// Receiver muss Nachricht herausnehmen und diese zu erst verarbeiten (FIFO)
		// Antwort an Absender
		Broker broker = new Broker();
		
		SomeSubscriber subscriber1 = new SomeSubscriber(broker);
		SomeSubscriber subscriber2 = new SomeSubscriber(broker);
		SomeSubscriber subscriber3 = new SomeSubscriber(broker);
		
		SomePublisher publisher = new SomePublisher(broker);
		publisher.doSomething();
		
	}
}