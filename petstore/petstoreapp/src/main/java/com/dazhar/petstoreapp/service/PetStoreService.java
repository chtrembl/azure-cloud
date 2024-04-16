package com.dazhar.petstoreapp.service;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Service;

import com.dazhar.petstoreapp.model.Order;
import com.dazhar.petstoreapp.model.Pet;
import com.dazhar.petstoreapp.model.Product;
import com.dazhar.petstoreapp.model.Tag;

@Service
public interface PetStoreService {
	public Collection<Pet> getPets(String category);

	public Collection<Product> getProducts(String category, List<Tag> tags);

	public void updateOrder(long productId, int quantity, boolean completeOrder);

	public Order retrieveOrder(String orderId);
}
