package com.chtrembl.petstoreapp.repository;

import org.springframework.stereotype.Repository;

import com.azure.spring.data.cosmos.repository.ReactiveCosmosRepository;
import com.chtrembl.petstoreapp.model.Order;

@Repository
public interface ECommerceRepository extends ReactiveCosmosRepository<Order, String> {
	
}