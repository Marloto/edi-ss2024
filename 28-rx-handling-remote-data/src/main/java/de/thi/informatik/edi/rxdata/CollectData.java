package de.thi.informatik.edi.rxdata;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CollectData {
	
	@Value("${temp:http://localhost:3000/temperature}")
	private String tempEndpoint;
	@Value("${temp:http://localhost:3000/humidity}")
	private String humiEndpoint;
	
	private RestTemplateBuilder builder;

	public CollectData(@Autowired RestTemplateBuilder builder) {
		this.builder = builder;
	}
	
	/*
	 * Example for HTTP-Call: MeasurementValue[] values = 
	 *   builder.build().getForObject(url, MeasurementValue[].class); 
	 */
	
//	public Flux<MeasurementValue> load(String url) {
//		MeasurementValue[] values = builder.build().getForObject(url, MeasurementValue[].class);
//		// -> irgend eine form von on next, od. pollen um es in einen flux zu schmeißen
//		return Flux.fromArray(values);
//	}
	
	private Mono<MeasurementValue[]> load(String url) {
		return Mono.create(sink -> sink.success(builder.build().getForObject(url, MeasurementValue[].class)));
	}
	
	public Flux<Measurement> find() {
		AtomicLong lastTemp = new AtomicLong();
		AtomicLong lastHumi = new AtomicLong();
		Flux<MeasurementValue> temp = Flux.interval(Duration.ofSeconds(3))
			.map(el -> tempEndpoint)
			.flatMap(this::load)
			.flatMap(el -> Flux.fromArray(el))
			.filter(el -> el.getTime() > lastTemp.get())
			.doOnNext(el -> lastTemp.getAndSet(el.getTime()));
		Flux<MeasurementValue> humi = Flux.interval(Duration.ofSeconds(3))
				.map(el -> humiEndpoint)
				.flatMap(this::load)
				.flatMap(el -> Flux.fromArray(el))
				.filter(el -> el.getTime() > lastHumi.get())
				.doOnNext(el -> lastHumi.getAndSet(el.getTime()));
		return Flux.combineLatest(
				(a) -> Measurement.fromMeasurementValue((MeasurementValue)a[0], (MeasurementValue)a[1]), 
				temp, 
				humi
		);
	}
	
	
}
