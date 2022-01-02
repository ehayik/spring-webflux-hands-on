package com.github.ehayik.playground.web;

import com.github.ehayik.playground.domain.Product;
import com.github.ehayik.playground.domain.ProductEvent;
import com.github.ehayik.playground.domain.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM;

@SpringBootTest
class ProductControllerTests {

	WebTestClient client;

	List<Product> expectedList;

	@Autowired
	ProductRepository repository;

	@BeforeEach
	void beforeEach() {
		client = WebTestClient.bindToController(new ProductController(repository)).configureClient()
				.baseUrl("/products").build();

		expectedList = repository.findAll().collectList().block();
	}

	@Test
	void testGetAllProducts() {
		client.get().uri("/").exchange().expectStatus().isOk().expectBodyList(Product.class).isEqualTo(expectedList);
	}

	@Test
	void testProductInvalidIdNotFound() {
		client.get().uri("/aaa").exchange().expectStatus().isNotFound();
	}

	@Test
	void testProductIdFound() {
		var expectedProduct = expectedList.get(0);
		client.get().uri("/{id}", expectedProduct.id()).exchange().expectStatus().isOk().expectBody(Product.class)
				.isEqualTo(expectedProduct);
	}

	@Test
	void testProductEvents() {
		var expectedEvent = new ProductEvent(0L, "Product Event");

		FluxExchangeResult<ProductEvent> result = client.get().uri("/events").accept(TEXT_EVENT_STREAM).exchange()
				.expectStatus().isOk().returnResult(ProductEvent.class);

		StepVerifier.create(result.getResponseBody()).expectNext(expectedEvent).expectNextCount(2)
				.consumeNextWith(event -> assertThat(event.eventId()).isEqualTo(3)).thenCancel().verify();
	}

}
