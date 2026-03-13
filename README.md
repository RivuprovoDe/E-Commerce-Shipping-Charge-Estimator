# E-Commerce Shipping Charge Estimator

A RESTful Spring Boot API that calculates shipping charges for a B2B e-commerce platform. Built as part of the Jumbotail SDE Intern Assignment.

Given a seller, product, customer, and delivery speed — the API finds the nearest warehouse, selects the appropriate transport mode based on distance, and computes the total shipping charge.

---

## How It Works

**Shipping charge formula:**

```
Total = Base Courier Charge + Transport Charge [+ Express Surcharge]

Transport Charge = Distance (km) × Rate (₹/km/kg) × Chargeable Weight (kg)
```

**Transport mode is selected automatically by distance:**

| Distance | Mode | Rate |
|---|---|---|
| 0 – 100 km | Mini Van | ₹3 / km / kg |
| 100 – 500 km | Truck | ₹2 / km / kg |
| 500 km+ | Aeroplane | ₹1 / km / kg |

**Delivery speed adds a surcharge:**

| Speed | Base Charge | Express Surcharge |
|---|---|---|
| Standard | ₹10 | None |
| Express | ₹10 | ₹1.2 / kg |

Distance between two coordinates is calculated using the **Haversine formula**.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.2.0 |
| Database | H2 (in-memory) |
| ORM | Spring Data JPA + Hibernate |
| Caching | Caffeine (via Spring Cache) |
| Validation | Spring Boot Validation |
| Utilities | Lombok |
| Testing | JUnit 5, Spring Boot Test |

---

## Project Structure

```
src/main/java/com/jumbotail/shipping/
├── controller/
│   ├── ShippingController.java       # Shipping charge endpoints
│   └── WarehouseController.java      # Nearest warehouse endpoint
├── service/
│   ├── ShippingChargeService.java    # Core shipping logic
│   ├── WarehouseService.java         # Nearest warehouse finder
│   ├── ShippingChargeStrategy.java   # Strategy interface
│   ├── StandardShippingStrategy.java # Standard delivery impl
│   ├── ExpressShippingStrategy.java  # Express delivery impl
│   └── ShippingStrategyFactory.java  # Strategy selector
├── entity/
│   ├── Warehouse.java
│   ├── Customer.java
│   ├── Seller.java
│   ├── Product.java
│   ├── DeliverySpeed.java            # STANDARD / EXPRESS enum
│   └── TransportMode.java            # MINI_VAN / TRUCK / AEROPLANE enum
├── dto/                              # Request/Response objects
├── repository/                       # Spring Data JPA repositories
├── config/
│   └── CacheConfig.java              # Caffeine cache (10 min TTL, 500 entries)
├── exception/                        # Global exception handler + custom exceptions
└── util/
    └── DistanceCalculator.java       # Haversine formula implementation
```

---

## Getting Started

### Prerequisites

- Java 17
- Maven

### Run

```bash
cd ecommerce-shipping
./mvnw spring-boot:run
```

The API starts at `http://localhost:8080`.

The H2 database is seeded automatically with 3 warehouses, 3 customers, 3 sellers, and 4 products on startup via `data.sql`.

### H2 Console

Available at `http://localhost:8080/h2-console`

- JDBC URL: `jdbc:h2:mem:shippingdb`
- Username: `sa`
- Password: *(leave blank)*

---

## API Endpoints

### 1. Get Shipping Charge (Warehouse → Customer)

```
GET /api/v1/shipping-charge
```

| Parameter | Type | Required | Description |
|---|---|---|---|
| `warehouseId` | Long | Yes | ID of the source warehouse |
| `customerId` | String | Yes | Customer ID (e.g. `Cust-123`) |
| `deliverySpeed` | String | Yes | `standard` or `express` |
| `productId` | String | No | Product ID for weight-based pricing |

**Example:**
```
GET /api/v1/shipping-charge?warehouseId=1&customerId=Cust-123&deliverySpeed=standard&productId=Prod-001
```

**Response:**
```json
{
  "shippingCharge": 45.50,
  "transportMode": "TRUCK",
  "distanceKm": 210.30,
  "deliverySpeed": "standard",
  "baseCourierCharge": 10.0,
  "transportCharge": 35.50,
  "expressCharge": 0.0
}
```

---

### 2. Calculate Full Shipping (Seller → Nearest Warehouse → Customer)

```
POST /api/v1/shipping-charge/calculate
```

**Request body:**
```json
{
  "sellerId": "Seller-001",
  "customerId": "Cust-123",
  "productId": "Prod-001",
  "quantity": 5,
  "deliverySpeed": "express"
}
```

**Response:**
```json
{
  "shippingCharge": 89.25,
  "nearestWarehouse": {
    "warehouseId": 1,
    "warehouseCode": "BLR_Warehouse",
    "warehouseName": "Bangalore Fulfillment Center",
    "warehouseLocation": { "lat": 12.99999, "lng": 37.923273 },
    "distanceFromSellerKm": 156.40
  },
  "transportMode": "TRUCK",
  "distanceKm": 312.50,
  "deliverySpeed": "express",
  "baseCourierCharge": 10.0,
  "transportCharge": 56.25,
  "expressCharge": 3.0
}
```

---

### 3. Get Nearest Warehouse for a Seller

```
GET /api/v1/warehouse/nearest?sellerId=Seller-001&productId=Prod-001
```

**Response:**
```json
{
  "warehouseId": 1,
  "warehouseCode": "BLR_Warehouse",
  "warehouseName": "Bangalore Fulfillment Center",
  "warehouseLocation": { "lat": 12.99999, "lng": 37.923273 },
  "distanceFromSellerKm": 156.40
}
```

---

## Seed Data

| Entity | IDs |
|---|---|
| Warehouses | `BLR_Warehouse` (Bangalore), `MUMB_Warehouse` (Mumbai), `DEL_Warehouse` (Delhi NCR) |
| Customers | `Cust-123` (Mysuru), `Cust-124` (Mumbai), `Cust-125` (New Delhi) |
| Sellers | `Seller-001` (Pune), `Seller-002` (Hassan), `Seller-003` (Belagavi) |
| Products | `Prod-001` (Maggi 500g), `Prod-002` (Rice 10kg), `Prod-003` (Sugar 25kg), `Prod-004` (Tata Salt 1kg) |

---

## Design Decisions

**Strategy Pattern for delivery speed** — `StandardShippingStrategy` and `ExpressShippingStrategy` each implement `ShippingChargeStrategy`. The `ShippingStrategyFactory` auto-discovers all strategy beans via Spring injection, so adding a new delivery speed only requires a new class — no factory changes needed.

**Caffeine caching** — Nearest warehouse lookups and shipping charges are cached for 10 minutes with a max size of 500 entries. Warehouse proximity depends only on seller location, so the cache key for nearest-warehouse is just `sellerId`.

**Haversine distance** — All distance calculations use the Haversine formula for great-circle distance between two lat/lng coordinates.

**H2 in-memory database** — No setup required. Schema is created by Hibernate on startup and seeded via `data.sql`.

---

## Running Tests

```bash
./mvnw test
```

Test coverage includes unit tests for distance calculation, delivery speed parsing, transport mode selection, shipping strategies, and full API integration tests.
