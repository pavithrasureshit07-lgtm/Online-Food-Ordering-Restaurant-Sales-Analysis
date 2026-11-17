USE restaurant_db;
INSERT INTO menu_items(item_name,category,price,available) VALUES
 ('Masala Dosa','Breakfast',90.00,TRUE),
 ('Idli Set','Breakfast',50.00,TRUE),
 ('Veg Meals','Lunch',120.00,TRUE),
 ('Paneer Fried Rice','Main Course',150.00,TRUE),
 ('Chicken Biryani','Main Course',180.00,TRUE),
 ('Fresh Lime Juice','Beverage',40.00,TRUE);

INSERT INTO customers(customer_name,phone,email) VALUES
 ('Anu Kumar','9000000001','anu@example.com'),
 ('Bala Raj','9000000002','bala@example.com'),
 ('Cathy Devi','9000000003','cathy@example.com');
