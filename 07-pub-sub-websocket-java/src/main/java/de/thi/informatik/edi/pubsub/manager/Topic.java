package de.thi.informatik.edi.pubsub.manager;

import java.util.ArrayList;
import java.util.List;

public class Topic {
	private String topic;
	private List<Subscriber> list = new ArrayList<>();
	
	public Topic(String topic) {
		this.topic = topic;
	}
	
	public String getTopic() {
		return topic;
	}
	
	public void subscribe(Subscriber sub) {
		this.list.add(sub);
	}

	public void publish(String msg) {
		// Point-To-Point: Liste einführen, und nachricht einreihen
		// ggf. folgendes asynchron ausführen
		for(Subscriber sub : list) {
			// ToDo: Async. handling?
			// Extend: Use topic-buffer?
			// Point-To-Point: der erste der "Funktioniert" dem notify
			//   aufrufen
			sub.notify(msg);
		}
	}
}