package com.github.ehayik.playground.web;

import com.github.ehayik.playground.domain.Product;
import com.github.ehayik.playground.domain.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

@SpringBootTest
@AutoConfigureWebTestClient
class AutoConfigureWebTestClientTest {

	@Autowired
	WebTestClient client;

	List<Product> expectedList;

	@Autowired
	ProductRepository repository;

	@BeforeEach
	void beforeEach() {
		expectedList = repository.findAll().collectList().block();
		client = client.mutate().baseUrl("/products").build();
	}

	@Test
	void testGetAllProducts() {
		client.get().uri("/").exchange().expectStatus().isOk().expectBodyList(Product.class).isEqualTo(expectedList);
	}

}
