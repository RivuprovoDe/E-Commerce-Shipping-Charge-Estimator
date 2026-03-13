-- Seed data for E-Commerce Shipping Charge Estimator

-- WAREHOUSES
INSERT INTO warehouses (warehouse_code, name, address, city, state, pincode, latitude, longitude, storage_capacity_cbm, contact_person, contact_phone, operational)
VALUES
    ('BLR_Warehouse',  'Bangalore Fulfillment Center',  'Whitefield Industrial Area',  'Bangalore', 'Karnataka',    '560066', 12.99999, 37.923273, 5000.0,  'Ravi Kumar',   '9876543210', TRUE),
    ('MUMB_Warehouse', 'Mumbai Fulfillment Center',     'Bhiwandi Logistics Park',     'Mumbai',    'Maharashtra',  '421302', 11.99999, 27.923273, 8000.0,  'Suresh Mehta', '9876543211', TRUE),
    ('DEL_Warehouse',  'Delhi NCR Fulfillment Center',  'Gurgaon Industrial Area',     'Gurugram',  'Haryana',      '122001', 28.45950, 77.026600, 10000.0, 'Amit Sharma',  '9876543212', TRUE);

-- CUSTOMERS
INSERT INTO customers (customer_id, name, phone_number, email, gst_number, address, city, state, pincode, latitude, longitude, credit_limit, active)
VALUES
    ('Cust-123', 'Shree Kirana Store',   '9847000001', 'shree.kirana@example.com',  '29ABCDE1234F1Z5', '123 Market Street',    'Mysuru',    'Karnataka',   '570001', 11.2320, 23.445495, 50000.0,  TRUE),
    ('Cust-124', 'Andheri Mini Mart',    '9101000002', 'andheri.mart@example.com',  '27ABCDE5678G1Z3', 'Shop 45, Andheri West', 'Mumbai',    'Maharashtra', '400058', 17.2320, 33.445495, 75000.0,  TRUE),
    ('Cust-125', 'Sharma General Store', '9876543001', 'sharma.general@example.com','07ABCDE9012H1Z1', 'Block B, Lajpat Nagar', 'New Delhi', 'Delhi',       '110024', 28.5700, 77.240000, 100000.0, TRUE);

-- SELLERS
INSERT INTO sellers (seller_id, name, contact_person, phone_number, email, gst_number, address, city, state, pincode, latitude, longitude, active)
VALUES
    ('Seller-001', 'Nestle Seller', 'John D''souza',  '9811000001', 'nestle.seller@example.com', '27NESTLE1234A1Z2', 'Nestle Distribution Center, Pune', 'Pune',     'Maharashtra', '411001', 18.5204, 73.8567, TRUE),
    ('Seller-002', 'Rice Seller',   'Rajesh Patil',   '9822000002', 'rice.seller@example.com',   '29RICE5678B1Z4',   'Grain Market, Hassan',             'Hassan',   'Karnataka',   '573201', 13.0068, 76.1004, TRUE),
    ('Seller-003', 'Sugar Seller',  'Vikram Joshi',   '9833000003', 'sugar.seller@example.com',  '29SUGAR9012C1Z6',  'Sugar Estate, Belgaum',            'Belagavi', 'Karnataka',   '590001', 15.8497, 74.4977, TRUE);

-- PRODUCTS
INSERT INTO products (product_id, name, description, category, brand, selling_price, weight_kg, dimension_length_cm, dimension_width_cm, dimension_height_cm, sku_code, minimum_order_quantity, stock_quantity, seller_id, active)
VALUES
    ('Prod-001', 'Maggie 500g Packet', 'Nestle Maggi 2-Minute Noodles 500g pack', 'Instant Food',       'Nestle', 10.0,  0.5,  10.0,   10.0,  10.0,  'NESTLE-MAGGI-500G', 10, 5000,  (SELECT id FROM sellers WHERE seller_id = 'Seller-001'), TRUE),
    ('Prod-002', 'Rice Bag 10Kg',      'Premium Basmati Rice 10kg bag',           'Grains & Cereals',   'Daawat', 500.0, 10.0, 1000.0, 800.0, 500.0, 'RICE-BASMATI-10KG', 1,  500,   (SELECT id FROM sellers WHERE seller_id = 'Seller-002'), TRUE),
    ('Prod-003', 'Sugar Bag 25kg',     'Refined White Sugar 25kg bag',            'Sugar & Sweeteners', 'Madhur', 700.0, 25.0, 1000.0, 900.0, 600.0, 'SUGAR-WHITE-25KG',  1,  200,   (SELECT id FROM sellers WHERE seller_id = 'Seller-003'), TRUE),
    ('Prod-004', 'Tata Salt 1kg',      'Tata Iodised Salt 1kg pack',              'Salt & Spices',      'Tata',   22.0,  1.0,  12.0,   8.0,   4.0,   'TATA-SALT-1KG',     5,  10000, (SELECT id FROM sellers WHERE seller_id = 'Seller-001'), TRUE);
