package de.thi.informatik.edi.pubsub;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.thi.informatik.edi.pubsub.manager.Broker;
import de.thi.informatik.edi.pubsub.manager.Subscriber;

@Component
public class BrokerManager {
	@Autowired
	private Broker broker;
	
	private Map<String, Deque<String>> buffer = new HashMap<>();
	
	public void publish(String topic, String message) {
		this.broker.publish(topic, message);
	}
	
	public void register(String topic, String clientId) {
		if(!buffer.containsKey(clientId)) {
			buffer.put(clientId, new LinkedList<>());
		}
		broker.subscribe(topic, new Subscriber() {
			public void notify(String msg) {
				buffer.get(clientId).add(msg);
			}
		});
	}

	public List<String> getMessages(String topic, String client) {
		List<String> resp = new ArrayList<>();
		Deque<String> list = buffer.get(client);
		if(list != null) {
			while(list.size() > 0) {
				resp.add(list.removeFirst());
			}
		}
		return resp;
	}
}