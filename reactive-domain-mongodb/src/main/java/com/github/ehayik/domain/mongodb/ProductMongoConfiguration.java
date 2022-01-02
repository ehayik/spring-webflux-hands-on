package com.github.ehayik.domain.mongodb;

import com.github.ehayik.playground.domain.ProductRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@Configuration
@EnableReactiveMongoRepositories
public class ProductMongoConfiguration {

	@Bean
	ProductRepository productRepository(ProductDocumentRepository productDocumentRepository) {
		return new MongoProductRepository(productDocumentRepository);
	}

}
