package com.chtrembl.petstore.order.model;

import java.util.ArrayList;
import java.util.List;

public class ProductList {
	private List<Product> products;

	public ProductList() {
		this.products = new ArrayList<>();
	}

	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}
}
