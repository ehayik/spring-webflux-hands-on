package com.github.ehayik.playground.web;

import com.github.ehayik.domain.mongodb.ProductMongoConfiguration;
import com.github.ehayik.playground.domain.Product;
import com.github.ehayik.playground.domain.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import reactor.core.publisher.Flux;

@Slf4j
@SpringBootApplication
@Import(ProductMongoConfiguration.class)
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	CommandLineRunner initDatabase(/* ReactiveMongoOperations operations, */ ProductRepository productRepository) {
		return args -> {
			Flux<Product> productFlux = Flux.just(new Product(null, "Big Latte", 2.99),
					new Product(null, "Big Decaf", 2.49), new Product(null, "Green Tea", 1.99))
					.flatMap(productRepository::save);

			productFlux.thenMany(productRepository.findAll())
					.subscribe(product -> LOG.info("Saved product: {}", product));

			/*
			 * operations.collectionExists(Product.class) .flatMap(exists -> exists ?
			 * operations.dropCollection(Product.class) : Mono.just(exists)) .thenMany(v
			 * -> operations.createCollection(Product.class)) .thenMany(productFlux)
			 * .thenMany(repository.findAll()) .subscribe(System.out::println);
			 */
		};
	}

}
