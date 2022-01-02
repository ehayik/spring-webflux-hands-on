package com.github.ehayik.playground.web;

import com.github.ehayik.playground.domain.Product;
import com.github.ehayik.playground.domain.ProductEvent;
import com.github.ehayik.playground.domain.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static java.time.Duration.ofSeconds;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM;
import static org.springframework.web.reactive.function.BodyInserters.fromValue;
import static org.springframework.web.reactive.function.server.ServerResponse.notFound;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
@RequiredArgsConstructor
class ProductHandler {

	private final ProductRepository repository;

	Mono<ServerResponse> getAllProducts(ServerRequest request) {
		var products = repository.findAll();

		return ok().contentType(APPLICATION_JSON).body(products, Product.class);
	}

	Mono<ServerResponse> getProduct(ServerRequest request) {
		var id = request.pathVariable("id");
		var productMono = repository.findById(id);
		var notFound = notFound().build();

		return productMono.flatMap(product -> ok().contentType(APPLICATION_JSON).body(fromValue(product)))
				.switchIfEmpty(notFound);
	}

	Mono<ServerResponse> saveProduct(ServerRequest request) {
		var productMono = request.bodyToMono(Product.class);

		return productMono.flatMap(product -> ServerResponse.status(CREATED).contentType(APPLICATION_JSON)
				.body(repository.save(product), Product.class));
	}

	Mono<ServerResponse> updateProduct(ServerRequest request) {
		var id = request.pathVariable("id");
		var existingProductMono = repository.findById(id);
		var productMono = request.bodyToMono(Product.class);
		var notFound = notFound().build();

		return productMono
				.zipWith(existingProductMono,
						(product, existingProduct) -> new Product(existingProduct.id(), product.name(),
								product.price()))
				.flatMap(product -> ok().contentType(APPLICATION_JSON).body(repository.save(product), Product.class))
				.switchIfEmpty(notFound);
	}

	Mono<ServerResponse> deleteProduct(ServerRequest request) {
		var id = request.pathVariable("id");
		var productMono = repository.findById(id);
		var notFound = notFound().build();

		return productMono.flatMap(existingProduct -> ok().build(repository.delete(existingProduct.id())))
				.switchIfEmpty(notFound);
	}

	Mono<ServerResponse> getProductEvents(ServerRequest request) {
		var eventsFlux = Flux.interval(ofSeconds(1)).map(val -> new ProductEvent(val, "Product Event"));

		return ServerResponse.ok().contentType(TEXT_EVENT_STREAM).body(eventsFlux, ProductEvent.class);
	}

}
