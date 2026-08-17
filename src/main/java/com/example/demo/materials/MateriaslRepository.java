package com.example.demo.materials;

import org.springframework.data.jpa.repository.JpaRepository;

public interface  MateriaslRepository extends JpaRepository<Materials, Integer> {
	  java.util.Optional<Materials> findByCode(String code);
}
