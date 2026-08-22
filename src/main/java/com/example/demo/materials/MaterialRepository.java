package com.example.demo.materials;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface  MaterialRepository extends JpaRepository<Material, Long> {
	  java.util.Optional<Material> findByCode(String code);
}
