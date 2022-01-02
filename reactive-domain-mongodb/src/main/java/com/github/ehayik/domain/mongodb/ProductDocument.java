package com.github.ehayik.domain.mongodb;

import com.github.ehayik.playground.domain.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ProductDocument {

	@Id
	@EqualsAndHashCode.Include
	private String id;

	private String name;

	private Double price;

	Product toProduct() {
		return new Product(id, name, price);
	}

	static ProductDocument of(@NonNull Product product) {
		return new ProductDocument(product.id(), product.name(), product.price());
	}

}
