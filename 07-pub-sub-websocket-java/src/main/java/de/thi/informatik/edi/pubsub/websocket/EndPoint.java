package de.thi.informatik.edi.pubsub.websocket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Observer;

import de.thi.informatik.edi.pubsub.manager.Broker;
import de.thi.informatik.edi.pubsub.manager.Subscriber;
import de.thi.informatik.edi.pubsub.manager.Topic;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;


@ServerEndpoint("/{topic}")
public class EndPoint {
	
	// Application Server creates on instance per connection, thus, manage changes
	// in a global way
	private static Broker manager = new Broker();

    @OnOpen
    public void onOpen(Session session, @PathParam("topic") String topic) throws IOException {
    	System.out.println("Connection created for " + topic);

    	// Handle subscribe
        manager.subscribe(topic, new Subscriber() {
			public void notify(String msg) {
				if(session.isOpen()) { // <- über Parameter der Methode onOpen
					try {
						session.getBasicRemote().sendText(msg);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
    }

    @OnMessage
    public void echo(String message, @PathParam("topic") String topic) {
    	// Handle publish
    	manager.publish(topic, message);
    }

    @OnError
    public void onError(Throwable t) {
        t.printStackTrace();
    }

    @OnClose
    public void onClose(Session session) {

    }
}