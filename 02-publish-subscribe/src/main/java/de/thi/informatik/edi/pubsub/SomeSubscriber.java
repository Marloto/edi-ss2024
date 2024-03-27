package de.thi.informatik.edi.pubsub;

public class SomeSubscriber extends Subscriber {
	private Broker broker;
	private Topic topic;

	public SomeSubscriber(Broker broker) {
		this.broker = broker;
		this.topic = this.broker.subscribe("example", this);
	}

	@Override
	public void notify(String msg) {
		System.out.println("Nachricht erhalten: " + msg);
		
	}
}
