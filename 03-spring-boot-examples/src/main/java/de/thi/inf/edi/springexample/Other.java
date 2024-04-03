package de.thi.inf.edi.springexample;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.websocket.server.PathParam;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/basic")
public class Other {
	private Example example;
	
	public Other(Example example) {
		this.example = example;
	}
	
	// HTTP-Request, was wäre hilfreich
	// beim Mapping auf diese Methode?
	// -> HTTP-Methode
	// -> HTTP-Pfad
	@GetMapping("/test") // -> /{id}
	public List<SomeData> doOtherthing() { // -> @PathVariable String id
		List<SomeData> list = this.example.doSomething();
		return list;
	}
	
	@PostConstruct
	public void onStartup() {
		this.example.addSomething(new SomeData("Test1"));
		this.example.addSomething(new SomeData("Test2"));
	}
	
	// TODO: HTTP-Request handling für Add-Element
}
