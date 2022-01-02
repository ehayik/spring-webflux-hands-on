package com.github.ehayik.domain.mongodb;

import com.github.ehayik.playground.domain.Product;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Flux;

@NoArgsConstructor
final class ProductMother {

	static final Product BIG_LATTE = new Product(null, "Big Latte", 2.99);
	static final Product BIG_DECAF = new Product(null, "Big Decaf", 2.49);
	static final Product GREEN_TEA = new Product(null, "Green Tea", 1.99);

	static Flux<Product> productFlux() {
		return Flux.just(BIG_LATTE, BIG_DECAF, GREEN_TEA);
	}

}
