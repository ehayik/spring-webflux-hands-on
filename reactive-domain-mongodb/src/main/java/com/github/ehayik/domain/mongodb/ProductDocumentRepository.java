package com.github.ehayik.domain.mongodb;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ProductDocumentRepository extends ReactiveMongoRepository<ProductDocument, String> {

}
