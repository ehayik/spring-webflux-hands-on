package com.github.ehayik.domain.mongodb;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@SpringBootApplication
@EnableReactiveMongoRepositories(basePackages = "com.github.ehayik.domain.mongodb")
public class ReactiveMongoApplication {

}
