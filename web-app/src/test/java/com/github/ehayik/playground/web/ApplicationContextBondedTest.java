package com.github.ehayik.playground.web;

import com.github.ehayik.playground.domain.Product;
import com.github.ehayik.playground.domain.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

@SpringBootTest
class ApplicationContextBondedTest {

	WebTestClient client;

	List<Product> expectedList;

	@Autowired
	ProductRepository repository;

	@Autowired
	ApplicationContext context;

	@BeforeEach
	void beforeEach() {
		client = WebTestClient.bindToApplicationContext(context).configureClient().baseUrl("/products").build();

		expectedList = repository.findAll().collectList().block();
	}

	@Test
	void testGetAllProducts() {
		client.get().uri("/").exchange().expectStatus().isOk().expectBodyList(Product.class).isEqualTo(expectedList);
	}

}