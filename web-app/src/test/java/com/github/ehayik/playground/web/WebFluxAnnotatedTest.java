package com.github.ehayik.playground.web;

import com.github.ehayik.domain.mongodb.ProductDocumentRepository;
import com.github.ehayik.playground.domain.Product;
import com.github.ehayik.playground.domain.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static org.mockito.Mockito.when;
import static reactor.core.publisher.Flux.fromIterable;

@WebFluxTest(ProductController.class)
class WebFluxAnnotatedTest {

	@Autowired
	WebTestClient client;

	List<Product> expectedList;

	@MockBean
	ProductRepository repository;

	@MockBean
	ProductDocumentRepository productDocumentRepository;

	@BeforeEach
	void beforeEach() {
		expectedList = List.of(new Product("1", "Big Latte", 2.99));
	}

	@Test
	void testGetAllProducts() {
		when(repository.findAll()).thenReturn(fromIterable(expectedList));

		client.get().uri("/products").exchange().expectStatus().isOk().expectBodyList(Product.class)
				.isEqualTo(expectedList);
	}

}
