package com.github.ehayik.playground.domain;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductRepository {

	Flux<Product> findAll();

	Mono<Product> findById(String id);

	Mono<Product> save(Product product);

	Mono<Void> delete(String id);

}
