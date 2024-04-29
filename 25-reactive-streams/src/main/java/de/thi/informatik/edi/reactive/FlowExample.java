package de.thi.informatik.edi.reactive;

import java.util.List;
import java.util.concurrent.Flow.Processor;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.SubmissionPublisher;
import java.util.function.Function;
import java.util.function.Predicate;

public class FlowExample {
	public static class ToStringSubscriber<T> implements Subscriber<T> {
		private Subscription subscription;
		private boolean done;
		public void onSubscribe(Subscription subscription) {
			System.out.println("onSubscribe");
			this.subscription = subscription;
			subscription.request(1);
		}
		public void onNext(T item) {
			System.out.println("onNext: " + item.toString() + "(" + item.getClass().getSimpleName() + ")");
			subscription.request(1);
		}
		public void onError(Throwable err) {
			err.printStackTrace();
		}
		public void onComplete() {
			System.out.println("onComplete");
			this.done = true;
		}
		public boolean isDone() {
			return this.done;
		}
		
	}
	
	public static class TransformProcessor<T, R> extends SubmissionPublisher<R> implements Processor<T, R> {

		private Subscription subscription;
		private Function<T, R> transform;
		
		public TransformProcessor(Function<T, R> transform) {
			this.transform = transform;
		}

		public void onSubscribe(Subscription subscription) {
			System.out.println("onSubscribe(map)");
			this.subscription = subscription;
			subscription.request(1);
		}

		public void onNext(T item) {
			System.out.println("onNext(map): " + item.toString() + "(" + item.getClass().getSimpleName() + ")");
			
			// Verwenden der Funktion zur Transformation
			R apply = transform.apply(item);
			this.submit(apply);
			
			// Naechstes Element anfragen
			subscription.request(1);
		}

		public void onError(Throwable err) {
			err.printStackTrace();
		}

		public void onComplete() {
			System.out.println("onComplete(map)");
			close();
		}
	}
	
	public static class FilterProcessor<T> extends SubmissionPublisher<T> implements Processor<T, T> {

		private Subscription subscription;
		private Predicate<T> condition;
		
		public FilterProcessor(Predicate<T> transform) {
			this.condition = transform;
		}

		public void onSubscribe(Subscription subscription) {
			System.out.println("onSubscribe(filter)");
			this.subscription = subscription;
			subscription.request(1);
		}

		public void onNext(T item) {
			System.out.println("onNext(filter): " + item.toString() + "(" + item.getClass().getSimpleName() + ")");
			
			// Bedingtes Weitergeben des Element
			if(condition.test(item)) {				
				this.submit(item);
			}

			// Naechstes Element anfragen
			subscription.request(1);
		}

		public void onError(Throwable err) {
			err.printStackTrace();
		}

		public void onComplete() {
			System.out.println("onComplete(filter)");
			close();
		}
	}
	
	public static void main(String[] args) throws Exception {
		// Publisher 1 erzeugt Strings
		SubmissionPublisher<String> publisher = new SubmissionPublisher<String>();
		
		// Transformer ist ein Publisher + Subsriber -> wandelt String zu int
		Processor<String, Integer> processor = new TransformProcessor<>((el) -> Integer.valueOf(el));
		publisher.subscribe(processor);
		
		// Transformer ist ein Publisher + Subsriber -> wandelt String zu int
		Processor<Integer, Integer> processor2 = new TransformProcessor<>((el) -> el + 1);
		processor.subscribe(processor2);
		
		Processor<Integer, Integer> processor3 = new FilterProcessor<>((el) -> el > 20);
		processor2.subscribe(processor3);
		
		// An dem Prozessor wird anschließend subscribed, um die Informationen weiter zu verarbeiten (ausgabe)
		ToStringSubscriber<Integer> subscriber = new ToStringSubscriber<>();
		processor3.subscribe(subscriber);
		
		List.of("1", "21", "2", "4", "3", "42").forEach(publisher::submit);
		publisher.close();

		while (!subscriber.isDone()) {
			Thread.sleep(100);
		}
	}
}





