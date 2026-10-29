CREATE DATABASE IF NOT EXISTS retail_db;
USE retail_db;

CREATE TABLE IF NOT EXISTS Product (
    product_id INT PRIMARY KEY,
    product_name VARCHAR(100) NOT NULL,
    category VARCHAR(60) NOT NULL,
    quantity INT NOT NULL DEFAULT 0 CHECK (quantity >= 0),
    price DECIMAL(10,2) NOT NULL CHECK (price >= 0)
);

INSERT INTO Product (product_id, product_name, category, quantity, price) VALUES
(101, 'Keyboard', 'Electronics', 15, 799.00),
(102, 'Notebook', 'Stationery', 50, 45.00),
(103, 'Mouse', 'Electronics', 10, 499.00)
ON DUPLICATE KEY UPDATE product_name = VALUES(product_name);
