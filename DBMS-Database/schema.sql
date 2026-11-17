CREATE DATABASE IF NOT EXISTS restaurant_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE restaurant_db;

CREATE TABLE IF NOT EXISTS menu_items (
 item_id INT AUTO_INCREMENT PRIMARY KEY,
 item_name VARCHAR(100) NOT NULL,
 category VARCHAR(60) NOT NULL,
 price DECIMAL(10,2) NOT NULL,
 available BOOLEAN NOT NULL DEFAULT TRUE,
 CONSTRAINT chk_menu_price CHECK (price >= 0),
 INDEX idx_menu_category(category)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS customers (
 customer_id INT AUTO_INCREMENT PRIMARY KEY,
 customer_name VARCHAR(100) NOT NULL,
 phone VARCHAR(20) NOT NULL UNIQUE,
 email VARCHAR(120) UNIQUE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS orders (
 order_id INT AUTO_INCREMENT PRIMARY KEY,
 customer_id INT NOT NULL,
 order_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
 status ENUM('Placed','Preparing','Completed','Cancelled') NOT NULL DEFAULT 'Placed',
 total_amount DECIMAL(12,2) NOT NULL DEFAULT 0,
 CONSTRAINT chk_order_total CHECK (total_amount >= 0),
 CONSTRAINT fk_orders_customer FOREIGN KEY(customer_id) REFERENCES customers(customer_id)
  ON UPDATE CASCADE ON DELETE RESTRICT,
 INDEX idx_orders_date(order_date),
 INDEX idx_orders_status(status)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS order_items (
 order_item_id INT AUTO_INCREMENT PRIMARY KEY,
 order_id INT NOT NULL,
 item_id INT NOT NULL,
 quantity INT NOT NULL,
 unit_price DECIMAL(10,2) NOT NULL,
 line_total DECIMAL(12,2) NOT NULL,
 CONSTRAINT chk_item_quantity CHECK (quantity > 0),
 CONSTRAINT chk_item_price CHECK (unit_price >= 0),
 CONSTRAINT chk_line_total CHECK (line_total >= 0),
 CONSTRAINT fk_items_order FOREIGN KEY(order_id) REFERENCES orders(order_id)
  ON UPDATE CASCADE ON DELETE CASCADE,
 CONSTRAINT fk_items_menu FOREIGN KEY(item_id) REFERENCES menu_items(item_id)
  ON UPDATE CASCADE ON DELETE RESTRICT,
 INDEX idx_order_items_item(item_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS payments (
 payment_id INT AUTO_INCREMENT PRIMARY KEY,
 order_id INT NOT NULL,
 amount DECIMAL(12,2) NOT NULL,
 payment_method ENUM('Cash','UPI','Card','Other') NOT NULL,
 payment_status ENUM('Pending','Partial','Paid') NOT NULL DEFAULT 'Pending',
 payment_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
 CONSTRAINT chk_payment_amount CHECK (amount >= 0),
 CONSTRAINT fk_payments_order FOREIGN KEY(order_id) REFERENCES orders(order_id)
  ON UPDATE CASCADE ON DELETE CASCADE,
 INDEX idx_payments_order(order_id)
) ENGINE=InnoDB;
