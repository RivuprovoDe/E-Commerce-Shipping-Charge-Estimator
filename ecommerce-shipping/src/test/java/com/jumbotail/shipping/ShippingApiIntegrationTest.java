package com.jumbotail.shipping;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for all shipping APIs.
 * Uses the sample data loaded by DataInitializer.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ShippingApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // ===== Warehouse API Tests =====

    @Test
    void testGetNearestWarehouse_ValidSellerAndProduct() throws Exception {
        mockMvc.perform(get("/api/v1/warehouse/nearest")
                        .param("sellerId", "Seller-001")
                        .param("productId", "Prod-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.warehouseId").exists())
                .andExpect(jsonPath("$.warehouseCode").exists())
                .andExpect(jsonPath("$.warehouseLocation").exists())
                .andExpect(jsonPath("$.distanceFromSellerKm").isNumber());
    }

    @Test
    void testGetNearestWarehouse_InvalidSellerId() throws Exception {
        mockMvc.perform(get("/api/v1/warehouse/nearest")
                        .param("sellerId", "INVALID-999")
                        .param("productId", "Prod-001"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetNearestWarehouse_InvalidProductId() throws Exception {
        mockMvc.perform(get("/api/v1/warehouse/nearest")
                        .param("sellerId", "Seller-001")
                        .param("productId", "INVALID-PROD"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetNearestWarehouse_MissingSellerParam() throws Exception {
        mockMvc.perform(get("/api/v1/warehouse/nearest")
                        .param("productId", "Prod-001"))
                .andExpect(status().isBadRequest());
    }

    // ===== Shipping Charge API Tests =====

    @Test
    void testGetShippingCharge_StandardDelivery() throws Exception {
        mockMvc.perform(get("/api/v1/shipping-charge")
                        .param("warehouseId", "1")
                        .param("customerId", "Cust-123")
                        .param("deliverySpeed", "standard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shippingCharge").isNumber())
                .andExpect(jsonPath("$.transportMode").exists())
                .andExpect(jsonPath("$.deliverySpeed").value("standard"));
    }

    @Test
    void testGetShippingCharge_ExpressDelivery() throws Exception {
        mockMvc.perform(get("/api/v1/shipping-charge")
                        .param("warehouseId", "1")
                        .param("customerId", "Cust-123")
                        .param("deliverySpeed", "express"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shippingCharge").isNumber())
                .andExpect(jsonPath("$.deliverySpeed").value("express"))
                .andExpect(jsonPath("$.expressCharge").isNumber());
    }

    @Test
    void testGetShippingCharge_InvalidDeliverySpeed() throws Exception {
        mockMvc.perform(get("/api/v1/shipping-charge")
                        .param("warehouseId", "1")
                        .param("customerId", "Cust-123")
                        .param("deliverySpeed", "rocket"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetShippingCharge_InvalidCustomerId() throws Exception {
        mockMvc.perform(get("/api/v1/shipping-charge")
                        .param("warehouseId", "1")
                        .param("customerId", "NONEXISTENT")
                        .param("deliverySpeed", "standard"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetShippingCharge_InvalidWarehouseId() throws Exception {
        mockMvc.perform(get("/api/v1/shipping-charge")
                        .param("warehouseId", "9999")
                        .param("customerId", "Cust-123")
                        .param("deliverySpeed", "standard"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetShippingCharge_WithProductId() throws Exception {
        mockMvc.perform(get("/api/v1/shipping-charge")
                        .param("warehouseId", "1")
                        .param("customerId", "Cust-124")
                        .param("deliverySpeed", "standard")
                        .param("productId", "Prod-002"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shippingCharge").isNumber());
    }

    // ===== Calculate Shipping API Tests =====

    @Test
    void testCalculateShipping_StandardDelivery() throws Exception {
        String requestBody = """
                {
                    "sellerId": "Seller-001",
                    "customerId": "Cust-123",
                    "deliverySpeed": "standard",
                    "productId": "Prod-001",
                    "quantity": 1
                }
                """;

        mockMvc.perform(post("/api/v1/shipping-charge/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shippingCharge").isNumber())
                .andExpect(jsonPath("$.nearestWarehouse").exists())
                .andExpect(jsonPath("$.nearestWarehouse.warehouseId").isNumber())
                .andExpect(jsonPath("$.transportMode").exists());
    }

    @Test
    void testCalculateShipping_ExpressDelivery() throws Exception {
        String requestBody = """
                {
                    "sellerId": "Seller-002",
                    "customerId": "Cust-124",
                    "deliverySpeed": "express",
                    "productId": "Prod-002"
                }
                """;

        mockMvc.perform(post("/api/v1/shipping-charge/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shippingCharge").isNumber())
                .andExpect(jsonPath("$.expressCharge").isNumber())
                .andExpect(jsonPath("$.deliverySpeed").value("express"));
    }

    @Test
    void testCalculateShipping_MissingFields() throws Exception {
        String requestBody = """
                {
                    "sellerId": "Seller-001"
                }
                """;

        mockMvc.perform(post("/api/v1/shipping-charge/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCalculateShipping_InvalidSeller() throws Exception {
        String requestBody = """
                {
                    "sellerId": "INVALID",
                    "customerId": "Cust-123",
                    "deliverySpeed": "standard"
                }
                """;

        mockMvc.perform(post("/api/v1/shipping-charge/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCalculateShipping_MultipleQuantity() throws Exception {
        String requestBody = """
                {
                    "sellerId": "Seller-003",
                    "customerId": "Cust-125",
                    "deliverySpeed": "express",
                    "productId": "Prod-003",
                    "quantity": 5
                }
                """;

        mockMvc.perform(post("/api/v1/shipping-charge/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shippingCharge").isNumber());
    }
}
