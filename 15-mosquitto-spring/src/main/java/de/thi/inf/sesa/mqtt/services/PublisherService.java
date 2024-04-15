package de.thi.inf.sesa.mqtt.services;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
public class PublisherService {
    @Value("${mqtt.broker}")
    private String broker;

    @Value("${mqtt.client}")
    private String clientId;

    @Value("${mqtt.topic}")
    private String topic;
    
    private int qos = 2;

	private MqttClient sampleClient;

    @PostConstruct
    private void connect() {
    	try {
    		MemoryPersistence persistence = new MemoryPersistence();
    		sampleClient = new MqttClient(broker, clientId, persistence);
    		MqttConnectOptions connOpts = new MqttConnectOptions();
    		connOpts.setCleanSession(true);
    		sampleClient.connect(connOpts);
    	} catch (MqttException me) {
    		me.printStackTrace();
    	}
    }
    
    @PreDestroy
    private void disconn() {
    	try {
    		sampleClient.disconnect();
    	} catch (MqttException me) {
    		me.printStackTrace();
    	}
    }
    
    public void publish(String msg) {
    	try {
    		MqttMessage message = new MqttMessage(msg.getBytes());
    		message.setQos(qos);
			sampleClient.publish(topic, message);
    	} catch (MqttException me) {
    		me.printStackTrace();
    	}
    }
}