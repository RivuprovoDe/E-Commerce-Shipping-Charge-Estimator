# Shipping Charge Estimator

This is my solution for the Jumbotail backend assignment. The idea is to build APIs that calculate shipping charges for a B2B marketplace where Kirana stores order products from sellers.

## How to run

You need Java 17 and Maven installed.

```bash
cd ecommerce-shipping
mvn spring-boot:run
```

Server starts at `http://localhost:8080`. Some sample data (sellers, products, customers, warehouses) gets loaded automatically so you can test the APIs right away.

```bash
mvn test
```

There's also an H2 console at `http://localhost:8080/h2-console` if you want to poke around the database.
- JDBC URL: `jdbc:h2:mem:shippingdb`
- Username: `sa`, Password: (leave empty)

## Tech stack

- Java 17, Spring Boot 3.2
- Spring Data JPA with H2 (in-memory)
- Caffeine for caching
- JUnit 5 for tests
- Lombok

## APIs

### 1. Find nearest warehouse for a seller

```
GET /api/v1/warehouse/nearest?sellerId=Seller-001&productId=Prod-001
```

```json
{
  "warehouseId": 2,
  "warehouseCode": "MUMB_Warehouse",
  "warehouseName": "Mumbai Fulfillment Center",
  "warehouseLocation": { "lat": 11.99999, "lng": 27.923273 },
  "distanceFromSellerKm": 312.45
}
```

### 2. Get shipping charge from a warehouse to a customer

```
GET /api/v1/shipping-charge?warehouseId=1&customerId=Cust-123&deliverySpeed=standard
```

`productId` is optional — if you pass it, the product's actual weight is used for the calculation, otherwise it defaults to 1kg.

```json
{
  "shippingCharge": 150.00,
  "transportMode": "TRUCK",
  "distanceKm": 234.5,
  "deliverySpeed": "standard",
  "baseCourierCharge": 10.0,
  "transportCharge": 140.0,
  "expressCharge": 0.0
}
```

### 3. Calculate full shipping charge (seller to customer)

This one combines both steps — it finds the nearest warehouse to the seller and then calculates the charge to the customer.

```
POST /api/v1/shipping-charge/calculate
Content-Type: application/json

{
  "sellerId": "Seller-001",
  "customerId": "Cust-123",
  "deliverySpeed": "express",
  "productId": "Prod-001",
  "quantity": 10
}
```

```json
{
  "shippingCharge": 180.00,
  "nearestWarehouse": {
    "warehouseId": 2,
    "warehouseCode": "MUMB_Warehouse",
    "warehouseLocation": { "lat": 11.99999, "lng": 27.923273 },
    "distanceFromSellerKm": 312.45
  },
  "transportMode": "TRUCK",
  "distanceKm": 450.0,
  "deliverySpeed": "express",
  "baseCourierCharge": 10.0,
  "transportCharge": 158.0,
  "expressCharge": 12.0
}
```

## Pricing

Transport mode is picked based on distance:

| Mode | Distance | Rate |
|------|----------|------|
| Mini Van | 0–100 km | Rs 3/km/kg |
| Truck | 100–500 km | Rs 2/km/kg |
| Aeroplane | 500+ km | Rs 1/km/kg |

Delivery speed affects the final charge:
- **Standard** → Rs 10 + transport charge
- **Express** → Rs 10 + Rs 1.2/kg + transport charge

For products with dimensions, chargeable weight = max(actual weight, volumetric weight). Volumetric weight is calculated as L×W×H / 5000, which is the standard courier formula.

## Sample data

| | IDs |
|--|-----|
| Sellers | `Seller-001`, `Seller-002`, `Seller-003` |
| Products | `Prod-001` (Maggi 500g), `Prod-002` (Rice 10kg), `Prod-003` (Sugar 25kg), `Prod-004` (Salt 1kg) |
| Customers | `Cust-123`, `Cust-124`, `Cust-125` |
| Warehouses | ID `1` (BLR), ID `2` (MUMB), ID `3` (DEL) |

## A few notes on the implementation

**Caching** — nearest warehouse lookups and shipping charge calculations are cached using Caffeine (10 min TTL). The warehouse cache key is just the `sellerId` since the nearest warehouse doesn't change based on what product is being shipped.

**Strategy pattern** — each delivery speed (standard, express) has its own class implementing `ShippingChargeStrategy`. Adding a new speed type in the future just means adding a new class, nothing else needs to change.

**Error responses** always come back in the same format:
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Seller not found with ID: INVALID"
}
```
