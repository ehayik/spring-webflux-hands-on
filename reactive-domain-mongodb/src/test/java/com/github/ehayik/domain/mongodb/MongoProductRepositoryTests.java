package com.github.ehayik.domain.mongodb;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import reactor.test.StepVerifier;

import static com.github.ehayik.domain.mongodb.ProductMother.BIG_DECAF;
import static com.github.ehayik.domain.mongodb.ProductMother.productFlux;
import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class MongoProductRepositoryTests {

	@Autowired
	MongoProductRepository repository;

	@Test
	void findAll() {
		// Given
		var productFlux = productFlux().flatMap(repository::save);

		// When
		productFlux = productFlux.thenMany(repository.findAll()).log();

		// Then
		StepVerifier.create(productFlux).expectNextCount(3).verifyComplete();
	}

	@Test
	void findById() {
		// Given
		var productMono = repository.save(BIG_DECAF);

		// When
		productMono = productMono.flatMap(p -> repository.findById(p.id())).log();

		// Then
		StepVerifier.create(productMono).assertNext(p -> assertThat(p.name()).isEqualTo(BIG_DECAF.name()))
				.verifyComplete();
	}

	@Test
	void delete() {
		// Given
		var productMono = repository.save(BIG_DECAF);

		// When
		var emptyMono = productMono.flatMap(p -> repository.delete(p.id())).thenMany(repository.findAll()).log();

		// Then
		StepVerifier.create(emptyMono).expectNextCount(0).verifyComplete();
	}

	@TestConfiguration
	static class MongoProductRepositoryConfig {

		@Bean
		MongoProductRepository mongoProductRepository(ProductDocumentRepository repository) {
			return new MongoProductRepository(repository);
		}

	}

}