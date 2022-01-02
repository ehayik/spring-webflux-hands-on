package com.github.ehayik.playground.domain;

import lombok.NonNull;

import java.util.Objects;

public record Product(String id, String name, Double price) {

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof Product otherProduct) {
			return otherProduct.id != null && Objects.equals(otherProduct.id, id);
		}

		return false;
	}

	public Product update(@NonNull Product other) {

		if (other.name == null || other.name.isBlank()) {
			throw new IllegalArgumentException("Name cannot be blank");
		}

		if (other.price <= 0) {
			throw new IllegalArgumentException("Price cannot be less than or equal to zero");
		}

		return new Product(id, other.name, other.price);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
