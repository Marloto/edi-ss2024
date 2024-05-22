package de.thi.informatik.edi.flux;

import java.io.IOException;
import java.time.Duration;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.Many;

public class WebFluxExample<T> {
	public static void main(String[] args) throws IOException {
		// Einfachste Variante, erzeugt ein oder kein Element, 
		// just garantiert hier das es ein Element
//		Mono<String> colorPublisher = Mono.just("RED");
//		colorPublisher.subscribe(System.out::println);
		
//		Flux<String> colorsPublisher = Flux.just("RED", "BLUE", "ORANGE");
//		colorsPublisher.subscribe(System.out::println);
//		colorsPublisher.subscribe(System.out::println);
		
		// Anlehnung an Streaming-API, collector
//		List<String> block = colorsPublisher.collectList().block();
//		System.out.println(block);
//		String block2 = colorPublisher.block();
//		System.out.println(block2);
		
//		Flux<Long> interval = Flux.interval(Duration.ofSeconds(1));
//		interval.subscribe(System.out::println);
		
		
//		Flux<String> flux = Flux.<String>create(sink -> {
//			System.out.println("Called...");
//			sink.next("A");
//			sink.next("B");
//			sink.next("C");
//		});
//		
//		flux.subscribe(System.out::println);
//		flux.subscribe(System.out::println);
//		
//		Many<Object> many = Sinks.many().multicast().onBackpressureBuffer();
//		Flux<Object> flux2 = many.asFlux();
//		flux2.subscribe(System.out::println);
//		flux2.subscribe(System.out::println);
//		flux2.subscribe(System.out::println);

		// some minutes later...
//		many.tryEmitNext(1.0); //Vgl. in MQTT würde man Ereignisse hier weitergeben

//		Flux.interval(Duration.ofSeconds(1))
//			.sample(Duration.ofSeconds(3))
//			.subscribe(System.out::println);
//		
//		Flux.just("RED", "BLUE", "ORANGE", "RED").distinct().subscribe(System.out::println);
//		
//		Mono<Long> count = Flux.fromArray(new Integer[] {1, 2, 2, 1, 3, 2, 4}).count();
//		count
//		    .subscribe(System.out::println);
		
		
//		Flux.fromArray(new Integer[] {1, 2, 2, 1, 3, 2, 4})
//			.reduce((a, b) -> {
//				if(a > b) {
//					return a;
//				}
//				return b;
//			}).subscribe(System.out::println);
//		Flux.fromArray(new Integer[] {1, 2, 2, 1, 3, 2, 4})
//			.reduce(Math::max).subscribe(System.out::println);

//		Flux<Integer> a = Flux.interval(Duration.ofSeconds(1)).map(el -> el.intValue()); // <- nicht gut für concat, da kein "Complete"
//		Flux<Integer> a = Flux.fromArray(new Integer[] {1, 2, 2, 1});
//		Flux<Integer> b = Flux.fromArray(new Integer[] {3, 1, 4, 5});
//		Flux<Integer> aUndB = Flux.concat(a, b);
//		aUndB.subscribe(System.out::println);
//		Flux<Integer> aUndB = Flux.merge(a, b);
//		aUndB.subscribe(System.out::println);
//		Flux<Tuple2<Integer, Integer>> aUndB = Flux.zip(a, b);
//		aUndB.subscribe(System.out::println);
//		Flux<Tuple2<Integer, Integer>> aUndB = Flux.combineLatest(a, b, (x, y) -> Tuples.of(x, y));
//		aUndB.subscribe(System.out::println);
		
		
//		Flux.interval(Duration.ofSeconds(1))
//		    .buffer(Duration.ofSeconds(3))
//		    .subscribe(el -> {
//		    	Flux.fromIterable(el).reduce((x, y) -> x + y).subscribe(System.out::println);
//		    });
//		Flux.interval(Duration.ofSeconds(1))
//		    .window(3)
//		    .subscribe(win -> win
//		        .reduce((a, b) -> a + b)
//		        .subscribe(System.out::println));
		
//		Flux.<List<String>>create(sink -> sink.next(Arrays.asList(
//		        "https://www.google.com/", 
//		        "https://www.yahoo.com/", 
//		        "https://www.microsoft.com/en-us/")))
//		    .flatMap(list -> Flux.fromIterable(list))
//		    .subscribe(System.out::println);
		
//		Flux.interval(Duration.ofSeconds(1))
//		    .buffer(Duration.ofSeconds(3))
//		    .flatMap(list -> Flux.fromIterable(list))
//		    .subscribe(System.out::println);
		
//		Flux.interval(Duration.ofSeconds(1))
//		    .flatMap(el -> Math.random() > 0.5 ? 
//		        Mono.just(el) : 
//		        Mono.empty())
//		    .subscribe(System.out::println);
//		Flux.interval(Duration.ofSeconds(1))
//		    .map(el -> Math.random())
//		    .subscribe(System.out::println);
		
//		Flux<Integer> fromArray = Flux.fromArray(new Integer[] {1, 2, 2, 1, 3, 2, 4});
//		Flux.combineLatest(
//				fromArray.reduce((a, b) -> a + b), 
//				fromArray.count(), (a, b) -> a / b);
//		Flux<Integer> fromArray = Flux.fromArray(new Integer[] {1, 2, 2, 1, 3, 2, 4});
//		Mono<Integer> sum = fromArray.reduce((a, b) -> a + b);
//		Mono<Long> count = fromArray.count();
//		Flux.combineLatest(sum, count, (a, b) -> a / b);
		
//		Flux<Integer> fromArray = Flux.fromArray(new Integer[] {1, 2, 2, 1, 3, 2, 4});
//		Flux.combineLatest(
//				fromArray.reduce((a, b) -> a ++), 
//				fromArray.reduce((a, b) -> a + b), 
//				(a, b) -> a / b);
		
//		Flux.interval(Duration.ofSeconds(1))
//			.buffer(3, 1)
//			.flatMap(win -> 
//				Flux.fromIterable(win)
//					.map(el -> Tuples.of(el, 0))
//					.reduce((a, b) -> Tuples.of(a.getT1() + b.getT1(), a.getT2() + 1))
//					.map(el -> el.getT1() / el.getT2())
//			).subscribe(System.out::println);
		
		
		// Einen Durchschnitt berechnen?
		// -> anzahl
		// -> verrechnung / summe
		
		
		
//		Many<Double> many = Sinks.many().multicast().onBackpressureBuffer();
//		Flux<Double> flux = Flux.<Double>create(sink -> {
		
//		System.out.println("Start flux with thread...");
//		new Thread(() -> {
//			// Erzeugung
//			while(true) {
//				double value = Math.random();
//				//sink.next(value);
//				many.tryEmitNext(value);
//				try {
//					Thread.sleep(1000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
//		}).start();
		
//		});
		
//		Flux<Double> flux = many.asFlux();
//		// Weitere Verwendung
//		new Thread(() -> {
//			// Verwendung ohne direkten Bezug
//			flux.subscribe(System.out::println);
//		}).start();
//		new Thread(() -> {
//			// Verwendung ohne direkten Bezug
//			flux.subscribe(System.out::println);
//		}).start();
		
		
		Flux.interval(Duration.ofSeconds(1))
			.doOnEach(el -> System.out.println(el.get()))
			.flatMap(el -> Mono.error(new RuntimeException("do something")))
			.onErrorReturn("Hello Error")
			.subscribe(System.out::println);
		
		
		
		System.in.read();
		
//		String doOtherthing = doOtherthing("Test");
//		
//		Integer res = doOtherthing(Integer.valueOf(123));
	}
	
	
	
//	private T attr;
//	public T doSomething(T t) {return null;}
//	
//	
//	
//	public static <K> K doOtherthing(K obj) {
//		return (K) obj;
//	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
