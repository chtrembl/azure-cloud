package com.chtrembl.petstoreapp.service;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Service;

import com.chtrembl.petstoreapp.model.Order;
import com.chtrembl.petstoreapp.model.Pet;
import com.chtrembl.petstoreapp.model.Product;
import com.chtrembl.petstoreapp.model.Tag;

@Service
public interface PetStoreService {
	public Collection<Pet> getPets(String category);

	public Collection<Product> getProducts(String category, List<Tag> tags);

	public void updateOrder(long productId, int quantity, boolean completeOrder);

	public Order retrieveOrder(String orderId);
}
