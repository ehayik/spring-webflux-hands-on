package com.github.ehayik.playground.web;

import com.github.ehayik.playground.domain.Product;
import com.github.ehayik.playground.domain.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class BindToServerTest {

	@Autowired
	WebTestClient client;

	List<Product> expectedList;

	@Autowired
	ProductRepository repository;

	@LocalServerPort
	int port;

	@BeforeEach
	public void beforeEach() {
		client = WebTestClient.bindToServer().baseUrl("http://localhost:" + port + "/products").build();

		expectedList = repository.findAll().collectList().block();
	}

	@Test
	void testGetAllProducts() {
		client.get().uri("/").exchange().expectStatus().isOk().expectBodyList(Product.class).isEqualTo(expectedList);
	}

}
