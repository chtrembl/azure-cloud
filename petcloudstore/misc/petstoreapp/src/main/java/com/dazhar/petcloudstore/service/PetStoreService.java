package com.dazhar.petcloudstore.service;

import java.util.Collection;

import org.springframework.stereotype.Service;

import com.dazhar.petcloudstore.model.Pet;

@Service
public interface PetStoreService {
	public Collection<Pet> getPets();
}
