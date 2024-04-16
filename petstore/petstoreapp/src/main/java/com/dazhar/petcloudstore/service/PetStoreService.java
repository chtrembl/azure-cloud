package com.dazhar.petcloudstore.service;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Service;

import com.dazhar.petcloudstore.model.Order;
import com.dazhar.petcloudstore.model.Pet;
import com.dazhar.petcloudstore.model.Product;
import com.dazhar.petcloudstore.model.Tag;

@Service
public interface PetStoreService {
	public Collection<Pet> getPets(String category);

	public Collection<Product> getProducts(String category, List<Tag> tags);

	public void updateOrder(long productId, int quantity, boolean completeOrder);

	public Order retrieveOrder(String orderId);
}
