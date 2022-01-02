package com.github.ehayik.playground.web;

import com.github.ehayik.playground.domain.Product;
import com.github.ehayik.playground.domain.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static org.mockito.Mockito.when;
import static reactor.core.publisher.Flux.fromIterable;

@ExtendWith(SpringExtension.class)
class ProductControllerBondedTest {

	WebTestClient client;

	List<Product> expectedList;

	@MockBean
	private ProductRepository repository;

	@BeforeEach
	void beforeEach() {
		client = WebTestClient.bindToController(new ProductController(repository)).configureClient()
				.baseUrl("/products").build();

		expectedList = List.of(new Product("1", "Big Latte", 2.99));
	}

	@Test
	void testGetAllProducts() {
		when(repository.findAll()).thenReturn(fromIterable(expectedList));

		client.get().uri("/").exchange().expectStatus().isOk().expectBodyList(Product.class).isEqualTo(expectedList);
	}

}
