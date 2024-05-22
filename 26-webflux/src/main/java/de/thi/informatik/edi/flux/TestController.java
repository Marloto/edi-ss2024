package de.thi.informatik.edi.flux;

import javax.annotation.PostConstruct;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.Many;

@RestController
@RequestMapping("/test")
public class TestController {
	private Flux<Long> fluxCold;
	private Flux<Long> fluxHot;

	@PostConstruct // <- wird erzeugt, wenn Service / Controller Konstruiert wurde (new TestController) anschließend wird .init aufgerufen
	public void init() {		
		fluxCold = Flux.<Long>create(sink -> {
			System.out.println("flux sink created");
			sink.next(1L);
			sink.next(2L);
			sink.next(3L);
			sink.complete();
		});
		Many<Long> buffer = Sinks.many().multicast().onBackpressureBuffer();
		buffer.tryEmitNext(1L);
		buffer.tryEmitNext(2L);
		buffer.tryEmitNext(3L);
		buffer.tryEmitComplete();
		
		fluxHot = buffer.asFlux().doOnEach(System.out::println);
		
	}
	
    @GetMapping
    public Flux<Long> test() {
    	System.out.println("test-called");
    	
    	
    	return fluxHot;
//    	return flux;
        //return Flux.interval(Duration.ofSeconds(1));
    }
}
