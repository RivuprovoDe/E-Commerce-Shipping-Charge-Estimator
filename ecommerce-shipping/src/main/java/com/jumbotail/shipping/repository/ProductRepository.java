package com.jumbotail.shipping.repository;

import com.jumbotail.shipping.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByProductIdAndActiveTrue(String productId);
}
