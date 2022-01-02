package com.github.ehayik.playground.web;

import com.github.ehayik.playground.domain.Product;
import com.github.ehayik.playground.domain.ProductEvent;
import com.github.ehayik.playground.domain.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static java.time.Duration.ofSeconds;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;
import static reactor.core.publisher.Flux.interval;
import static reactor.core.publisher.Mono.just;

@RestController
@RequiredArgsConstructor
@SuppressWarnings("unused")
@RequestMapping("/products")
class ProductController {

	private final ProductRepository productRepository;

	@GetMapping
	Flux<Product> get() {
		return productRepository.findAll();
	}

	@GetMapping("{id}")
	Mono<ResponseEntity<Product>> get(@PathVariable String id) {
		return productRepository.findById(id).map(ResponseEntity::ok).defaultIfEmpty(notFound().build());
	}

	@PostMapping
	@ResponseStatus(CREATED)
	Mono<Product> create(@RequestBody Product product) {
		return productRepository.save(product);
	}

	@PutMapping("{id}")
	Mono<ResponseEntity<Product>> update(@PathVariable String id, @RequestBody Product product) {
		return productRepository.findById(id).flatMap(existing -> productRepository.save(existing.update(product)))
				.map(ResponseEntity::ok).defaultIfEmpty(notFound().build());
	}

	@DeleteMapping("{id}")
	Mono<ResponseEntity<Void>> delete(@PathVariable String id) {
		return productRepository.findById(id)
				.flatMap(existing -> productRepository.delete(existing.id()).then(just(ok().<Void>build())))
				.defaultIfEmpty(notFound().build());
	}

	@GetMapping(value = "/events", produces = TEXT_EVENT_STREAM_VALUE)
	Flux<ProductEvent> getProductEvents() {
		return interval(ofSeconds(1)).map(val -> new ProductEvent(val, "Product Event"));
	}

}
