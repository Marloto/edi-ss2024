package de.thi.informatik.edi.pubsub;

public class SomePublisher {

	private Broker broker;
	private Topic topic;

	public SomePublisher(Broker broker) {
		this.broker = broker;
		this.topic = this.broker.createTopic("example");
	}

	public void doSomething() {
		topic.publish("Hello, World!");
	}

}
