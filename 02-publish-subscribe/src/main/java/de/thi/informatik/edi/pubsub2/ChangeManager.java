package de.thi.informatik.edi.pubsub2;


public abstract class ChangeManager {
	public abstract void register(Channel sub, Observer obs);
	public abstract void notify(Channel sub, String message);
	
	public Channel create(String topic) {
		return new Channel(topic, this);
	}
}