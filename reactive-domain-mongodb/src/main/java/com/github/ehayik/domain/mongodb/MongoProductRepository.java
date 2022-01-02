package com.github.ehayik.domain.mongodb;

import com.github.ehayik.playground.domain.Product;
import com.github.ehayik.playground.domain.ProductRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class MongoProductRepository implements ProductRepository {

	@NonNull
	private final ProductDocumentRepository repository;

	@Override
	public Flux<Product> findAll() {
		return repository.findAll().map(ProductDocument::toProduct);
	}

	@Override
	public Mono<Product> findById(String id) {
		return repository.findById(id).map(ProductDocument::toProduct);
	}

	@Override
	public Mono<Product> save(@NonNull Product product) {
		return Mono.just(product).map(ProductDocument::of).flatMap(repository::save).map(ProductDocument::toProduct);
	}

	@Override
	public Mono<Void> delete(@NonNull String id) {
		var productMono = repository.findById(id);
		return productMono.flatMap(repository::delete);
	}

}
