package de.thi.informatik.edi.simple.websocket;

import java.io.IOException;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;


@ServerEndpoint("/{topic}")
public class WebSocketServer {

    @OnOpen
    public void onOpen(Session session, @PathParam("topic") String topic) throws IOException {
    	System.out.println("Connection created for " + topic);
        session.getAsyncRemote().sendText("something");
    }

    @OnMessage
    public void onMessage(String message) {
        System.out.println("Message: " + message);
    }

    @OnError
    public void onError(Throwable t) {
        t.printStackTrace();
    }

    @OnClose
    public void onClose(Session session) {
    	System.out.println("Closed connection");
    }
}