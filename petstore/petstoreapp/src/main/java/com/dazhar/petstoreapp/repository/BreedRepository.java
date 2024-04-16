package com.dazhar.petstoreapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dazhar.petstoreapp.model.Breed;

@Repository
public interface BreedRepository extends JpaRepository<Breed, Integer> {

}
