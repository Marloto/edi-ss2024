package de.thi.informatik.edi.pubsub;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class ChannelController {
	@Autowired
	public BrokerManager manager;
	
	@GetMapping("/register/{topic}/{client}")
	public ResponseEntity register(@PathVariable String topic, @PathVariable String client) {
		System.out.println("Register " + client + " in " + topic);
		manager.register(topic, client);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	@GetMapping("/load/{topic}/{client}")
	public List<String> load(@PathVariable String topic, @PathVariable String client) {
		System.out.println("Load for " + client + " in " + topic);
		return manager.getMessages(topic, client);
	}
	
	@GetMapping("/broadcast/{topic}/{message}")
	public ResponseEntity broadcast(@PathVariable String topic, @PathVariable String message) {
		System.out.println("Broadcast \"" + message + "\" in " + topic);
		manager.publish(topic, message);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}