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
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Slf4j
@SpringBootApplication
@Import(ProductMongoConfiguration.class)
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	CommandLineRunner initDatabase(ProductRepository productRepository) {
		return args -> {
			Flux<Product> productFlux = Flux.just(new Product(null, "Big Latte", 2.99),
					new Product(null, "Big Decaf", 2.49), new Product(null, "Green Tea", 1.99))
					.flatMap(productRepository::save);

			productFlux.thenMany(productRepository.findAll())
					.subscribe(product -> LOG.info("Saved product: {}", product));
		};
	}

	@Bean
	RouterFunction<ServerResponse> routes(ProductHandler handler) {
		// return route()
		// .GET("/products/events", accept(TEXT_EVENT_STREAM), handler::getProductEvents)
		// .GET("/products/{id}", accept(APPLICATION_JSON), handler::getProduct)
		// .GET("/products", accept(APPLICATION_JSON), handler::getAllProducts)
		// .PUT("/products/{id}", accept(APPLICATION_JSON), handler::updateProduct)
		// .POST("/products", contentType(APPLICATION_JSON), handler::saveProduct)
		// .DELETE("/products/{id}", accept(APPLICATION_JSON), handler::deleteProduct)
		// .DELETE("/products", accept(APPLICATION_JSON), handler::deleteAllProducts)
		// .build();

		return route().path("/products",
				builder -> builder
						.nest(accept(APPLICATION_JSON).or(contentType(APPLICATION_JSON)).or(accept(TEXT_EVENT_STREAM)),
								nestedBuilder -> nestedBuilder.GET("/events", handler::getProductEvents)
										.GET("/{id}", handler::getProduct).GET(handler::getAllProducts)
										.PUT("/{id}", handler::updateProduct).POST(handler::saveProduct))
						.DELETE("/{id}", handler::deleteProduct))
				.build();
	}

}
