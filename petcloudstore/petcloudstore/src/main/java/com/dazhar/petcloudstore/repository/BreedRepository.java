package com.dazhar.petcloudstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dazhar.petcloudstore.model.Breed;

@Repository
public interface BreedRepository extends JpaRepository<Breed, Integer> {

}
