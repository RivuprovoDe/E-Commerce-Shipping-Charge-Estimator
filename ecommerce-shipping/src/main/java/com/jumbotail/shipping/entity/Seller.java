package com.jumbotail.shipping.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sellers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Seller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String sellerId;

    @Column(nullable = false)
    private String name;

    private String contactPerson;
    private String phoneNumber;
    private String email;
    private String gstNumber;
    private String address;
    private String city;
    private String state;
    private String pincode;

    // seller's dispatch location, used to find nearest warehouse
    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Builder.Default
    private Boolean active = true;
}
