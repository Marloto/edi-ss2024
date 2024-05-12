package de.thi.informatik.edi.sensor;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExampleService {
	private DataEventService service;

	public ExampleService(@Autowired DataEventService service) {
		this.service = service;
	}

	@PostConstruct
	public void init() {
		this.service.eventsByType("cpu-usage")
			.subscribe(System.out::println);
		// ggf. mit gleitenden Fenster als Beispiel weiterführen
	}
}
