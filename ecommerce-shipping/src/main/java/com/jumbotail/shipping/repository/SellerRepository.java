package com.jumbotail.shipping.repository;

import com.jumbotail.shipping.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Long> {
    Optional<Seller> findBySellerIdAndActiveTrue(String sellerId);
}
