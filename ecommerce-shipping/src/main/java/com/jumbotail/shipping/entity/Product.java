package com.jumbotail.shipping.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String productId;

    @Column(nullable = false)
    private String name;

    private String description;
    private String category;
    private String brand;

    @Column(nullable = false)
    private Double sellingPrice;

    @Column(nullable = false)
    private Double weightKg;

    // dimensions in cm
    private Double dimensionLengthCm;
    private Double dimensionWidthCm;
    private Double dimensionHeightCm;

    private String skuCode;

    @Builder.Default
    private Integer minimumOrderQuantity = 1;

    @Builder.Default
    private Integer stockQuantity = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;

    @Builder.Default
    private Boolean active = true;

    // volumetric weight = L*W*H / 5000 (standard courier formula)
    public Double getVolumetricWeightKg() {
        if (dimensionLengthCm != null && dimensionWidthCm != null && dimensionHeightCm != null) {
            return (dimensionLengthCm * dimensionWidthCm * dimensionHeightCm) / 5000.0;
        }
        return weightKg;
    }

    // billing is based on whichever is higher: actual or volumetric weight
    public Double getChargeableWeightKg() {
        return Math.max(weightKg, getVolumetricWeightKg());
    }
}
