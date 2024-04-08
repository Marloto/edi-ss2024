package de.thi.informatik.edi.pubsub.manager;

import java.util.HashMap;
import java.util.Map;

public class Broker {
	
	Map<String, Topic> topics = new HashMap<>();
	
	public Topic createTopic(String topic) {
		Topic existing = topics.get(topic);
		if(existing != null) {
			return existing;
		}
		Topic t = new Topic(topic);
		this.topics.put(topic, t);
		return t;
	}
	
	public Topic subscribe(String topic, Subscriber sub) {
		Topic t = this.createTopic(topic);
		t.subscribe(sub);
		return t;
	}
	
	public void publish(String topic, String message) {
		Topic t = this.createTopic(topic);
		t.publish(message);
	}

}