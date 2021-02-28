package com.fideljoser.demo_ReactiveSpring;

import java.time.Duration;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.jupiter.api.Disabled;
//import org.junit.Ignore;
import org.junit.jupiter.api.Test;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Log4j2
public class OperatorsTest {

	private static final org.apache.logging.log4j.Logger logger = org.apache.logging.log4j.LogManager.getLogger(OperatorsTest.class);
	
	@Disabled
	@Test
	public void transform() {
		var finished = new AtomicBoolean();
		var letters = Flux.just("A", "B", "C")
						.log()
						.transform(stringFlux -> stringFlux.doFinally(
										signal -> finished.lazySet(true)));
						
		StepVerifier.create(letters).expectNextCount(3).verifyComplete();
		Assert.assertTrue("", finished.get());
	}
	
	@Disabled
	@Test
	public void thenManyTest() {
		var letters = new AtomicInteger();
		var numbers = new AtomicInteger();
		Flux<String> letterPublisher = Flux.just("a", "b", "c")
											.doOnNext(value -> letters.incrementAndGet());
		Flux<Integer> numbersPublisher = Flux.just(1, 2, 3)
											.doOnNext(value -> numbers.incrementAndGet());
		Flux<Integer> thisBeforeThat = letterPublisher.log()
											.thenMany(numbersPublisher).log()
											.thenMany(Flux.just(9,8,7)).log();
		StepVerifier.create(thisBeforeThat).expectNext(9,8,7).verifyComplete();
		Assert.assertEquals(letters.get(), 3);
		Assert.assertEquals(numbers.get(), 3);
	}
	
	@Disabled
	@Test
	public void mapsTest() {
		Flux<String> maps = Flux.just("f", "i", "d", "e", "l")
								.map(s -> s.toUpperCase())
								.log();
		StepVerifier.create(maps).expectNext("F", "I", "D", "E", "L").verifyComplete();
		StepVerifier.create(maps).expectComplete();
	}
	
	@Disabled
	@Test
	public void flatMapTest() {
		Flux<String> maps = Flux.just("T", "o", "l", "ú")
//								.flatMap(i -> this.returnFluxTest(i));
								.flatMap(i -> {
									return Flux.just(i).delayElements(Duration.ofMillis(500));
								} );
		StepVerifier.create(maps).expectNext("T", "o", "l", "ú").verifyComplete();
		StepVerifier.create(maps).expectComplete();
	}
	
	public Flux<String> returnFluxTest(String letter){
		return Flux.just(letter).delayElements(Duration.ofMillis(1000));
	}
	
	// The concatMAp perserves the order of items - disadventage lose asynchronicity
	@Disabled
	@Test
	public void concatMapTest() {
		Flux<Integer> c = Flux.just(1, 2, 3, 4, 5, 6, 7)
							.concatMap(i -> {
								return Flux.just(this.anyFunction(i));
							});
		StepVerifier.create(c).expectNext(2,4,6,8,10,12,14).expectComplete();
		StepVerifier.create(c).expectComplete();
	}
	
	private Integer anyFunction(Integer number) {
		System.out.println("Function to each item - perserves order " + number);
		return number * number;
	}
	
	@Test
	@Disabled
	public void swithMapTest() {
		Flux<String> typed = Flux.just("F", "FI", "FID", "FIDE", "FIDEL")
								.delayElements(Duration.ofMillis(100))
								.log()
								.switchMap(this::lookup);
		StepVerifier.create(typed).expectNext("word is -> FIDEL").verifyComplete();
		
	}
	
	private Flux<String> lookup(String word){
		return Flux.just( "word is -> " + word)
				.delayElements(Duration.ofMillis(500));
	}
	
	@Test
	@Disabled
	public void takeTest() {
//		Flux<Integer> listNumbers = (Flux<Integer>) Flux.range(4, 1700)
//										.doOnEach(System.out::println)					
//										.take(10)
//										.subscribe();
		Flux<Integer> listNumbers = Flux.range(4, 1700)
										.doOnEach(System.out::println)					
										.take(100);
		StepVerifier.create(listNumbers).expectNextCount(100).verifyComplete();
	}
	
	@Test
	@Disabled
	public void takeUntilTest() {
//		Flux<Integer> takeUntil = (Flux<Integer>) Flux.range(0, 100)
//									.takeUntil(i -> i > 20)
//									.log()
//									.subscribe(System.out::println);
		Flux<Integer> takeUntil = Flux.range(0, 100)
									.takeUntil(i -> i > 20)
									.log();
		StepVerifier.create(takeUntil).expectNextCount(22).verifyComplete();
	}
	
	@Test
	public void filterTest() {
		Flux<Integer> filtered =  Flux.range(2, 14)
									.filter(i -> i % 2 == 0)
									.doOnNext(System.out::println)
									.doOnEach(System.out::println);
//									.log();
		StepVerifier.create(filtered).expectNext(2,4,6,8,10,12,14).verifyComplete();							
	}
	
	
	
}

