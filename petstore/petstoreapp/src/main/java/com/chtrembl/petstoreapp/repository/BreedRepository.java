package com.chtrembl.petstoreapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.chtrembl.petstoreapp.model.Breed;

@Repository
public interface BreedRepository extends JpaRepository<Breed, Integer> {

}
