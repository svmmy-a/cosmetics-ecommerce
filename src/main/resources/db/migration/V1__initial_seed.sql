-- Insert categopries (for produtcs)
INSERT INTO category (name)
VALUES 
('Skincare'),
('Haircare'),
('Makeup'),
('Fragrance');

-- Insert products (one per category)
INSERT INTO product (name, description, price, size, image_url, category_id, is_new)
VALUES 
('Glow Cleanser', 'Gentle cleanser for refreshed, glowing skin.', 12.99, '150ml', '/assets/glow-cleanser.jpg', 1, FALSE),
('Smooth Shampoo', 'Nourishing shampoo for silky, healthy hair.', 10.50, '250ml', '/assets/smooth-shampoo.jpg', 2, TRUE),
('Velvet Lipstick', 'Long-lasting matte lipstick for daily wear.', 9.99, '5g', '/assets/velvet-lipstick.jpg', 3, TRUE),
('Bloom Mist', 'Light floral fragrance for all-day freshness.', 15.75, '50ml', '/assets/bloom-mist.jpg', 4, TRUE);

-- Insert admin user
-- Note: the password  should be replaced with a BCrypt hashed value of 'cosmetics'
INSERT INTO admin (name, email, password)
VALUES 
('Samin', 'sam@admin.com', 'cosmetics');

-- Insert warehouse
INSERT INTO warehouse (name, address)
VALUES 
('Main Warehouse', '123 Cosmetics Lane, London, UK');

-- Insert suppliers
INSERT INTO supplier (name, email, phone, address)
VALUES 
('Beauty Supplies Ltd', 'contact@beautysupplies.com', '020-1234-5678', '456 Supplier Road, London, UK');

-- Insert Inventory
INSERT INTO inventory (product_id, warehouse_id, quantity, buy_price, supplier_id, last_updated)
VALUES 
(1, 1, 0, 0, 1, CURRENT_TIMESTAMP),
(2, 1, 0, 0, 1, CURRENT_TIMESTAMP),
(3, 1, 0, 0, 1, CURRENT_TIMESTAMP),
(4, 1, 0, 0, 1, CURRENT_TIMESTAMP);

-- Insert product supplier records (linking products to suppliers with transaction details)
-- INSERT INTO product_supplier (product_id, supplier_id, quantity, buy_price, supply_date)
-- VALUES 
-- (1, 1, 5, 8.00, CURRENT_TIMESTAMP),
-- (2, 1, 9, 6.50, CURRENT_TIMESTAMP),
-- (3, 1, 5, 5.00, CURRENT_TIMESTAMP),
-- (4, 1, 9, 10.00, CURRENT_TIMESTAMP);
