package com.github.ehayik.playground.webclient;

import com.github.ehayik.playground.domain.Product;
import com.github.ehayik.playground.domain.ProductEvent;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.CountDownLatch;

import static java.util.concurrent.TimeUnit.SECONDS;
import static reactor.core.publisher.Mono.just;

@Slf4j
public class ProductWebClient {

	private final WebClient webClient;

	public ProductWebClient() {
		webClient = WebClient.create("http://localhost:8080/products");
	}

	@SneakyThrows
	public static void main(String[] args) {
		var latch = new CountDownLatch(5);
		var api = new ProductWebClient();

		api.postNewProduct() //
				.thenMany(api.getAllProducts()).take(1) //
				.flatMap(p -> api.updateProduct(p.id(), "White Tea", 0.99)) //
				.flatMap(p -> api.deleteProduct(p.id()) //
						.thenMany(api.getAllProducts()) //
						.thenMany(api.getAllEvents())) //
				// By default in reactor, operators run on the thread in which the
				// subscribe call was made.
				// Specify that the request should be executed in another thread
				// .subscribeOn(Schedulers.newSingle("web-client"))
				.subscribe(event -> LOG.info(event.toString())); //

		latch.await(10, SECONDS);
		System.exit(0);
	}

	private Mono<ResponseEntity<Product>> postNewProduct() {
		return webClient.post().body(just(new Product(null, "Jasmine Tea", 1.99)), Product.class)
				.exchangeToMono(response -> response.toEntity(Product.class))
				.doOnSuccess(o -> LOG.debug("**********POST {}", o));
	}

	private Flux<Product> getAllProducts() {
		return webClient.get().retrieve().bodyToFlux(Product.class).doOnNext(o -> LOG.debug("**********GET: {}", o));
	}

	private Mono<Product> updateProduct(String id, String name, double price) {
		return webClient.put().uri("/{id}", id).body(just(new Product(null, name, price)), Product.class).retrieve()
				.bodyToMono(Product.class).doOnSuccess(o -> LOG.debug("**********PUT {}", o));
	}

	private Mono<Void> deleteProduct(String id) {
		return webClient.delete().uri("/{id}", id).retrieve().bodyToMono(Void.class)
				.doOnSuccess(o -> LOG.debug("**********DELETE {}", o));
	}

	private Flux<ProductEvent> getAllEvents() {
		return webClient.get().uri("/events").retrieve().bodyToFlux(ProductEvent.class);
	}

}
