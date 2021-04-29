package com.chtrembl.petstoreapp.service;

import java.util.Collection;

import org.springframework.stereotype.Service;

import com.chtrembl.petstoreapp.model.Pet;

@Service
public interface PetStoreService {
	public Collection<Pet> getPets();
}
