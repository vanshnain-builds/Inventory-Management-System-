-- Run this script in MySQL Workbench or the MySQL command-line client.
-- If retail_db and Product already exist, do not rerun duplicate sample inserts.
CREATE DATABASE IF NOT EXISTS retail_db;
USE retail_db;

CREATE TABLE IF NOT EXISTS Product (
    product_id INT PRIMARY KEY,
    product_name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    quantity INT NOT NULL DEFAULT 0,
    price DOUBLE NOT NULL DEFAULT 0
);

-- Optional sample data: run only if you need sample records.
-- IDs must not already exist in Product.
INSERT INTO Product (product_id, product_name, category, quantity, price) VALUES
(101, 'Laptop', 'Electronics', 10, 50000),
(102, 'Mouse', 'Electronics', 15, 500),
(103, 'Keyboard', 'Electronics', 25, 1200),
(104, 'Chair', 'Furniture', 5, 2500),
(105, 'Desk', 'Furniture', 0, 7000);
