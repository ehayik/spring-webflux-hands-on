package com.github.ehayik.playground.web;

import com.github.ehayik.playground.domain.Product;
import com.github.ehayik.playground.domain.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.util.List;

@SpringBootTest
class RouterFunctionTest {

	WebTestClient client;

	List<Product> expectedList;

	@Autowired
	ProductRepository repository;

	@Autowired
	RouterFunction<ServerResponse> routes;

	@BeforeEach
	public void beforeEach() {
		client = WebTestClient.bindToRouterFunction(routes).configureClient().baseUrl("/products").build();

		expectedList = repository.findAll().collectList().block();
	}

	@Test
	public void testGetAllProducts() {
		client.get().uri("/").exchange().expectStatus().isOk().expectBodyList(Product.class).isEqualTo(expectedList);
	}

}
