# Inventory Management System

A menu-driven inventory management application built with **Java, JDBC, and MySQL**.

## Features

1. Insert three products, with duplicate product-ID handling.
2. Search by category for products with quantity below 20, sorted by price in descending order.
3. Add stock to an existing product.
4. Search for a product, update its price, and display the updated details.
5. Calculate total inventory value and delete a product only when its quantity is zero.
6. Exit through the menu.

## Technologies

- Java
- JDBC
- MySQL
- MySQL Connector/J
- IntelliJ IDEA

## Requirements

- Java JDK
- MySQL Server
- MySQL Connector/J added to the project classpath

## Setup

1. Start MySQL Server.
2. Run the database and table setup in [database.sql](database.sql). If the database and table already exist, do not rerun sample INSERT statements for IDs that are already present.
3. Open `src/main/java/Main.java` in IntelliJ IDEA.
4. Add MySQL Connector/J to the project dependencies.
5. Update the local database settings in `Main.java` if needed:
   - URL: `jdbc:mysql://localhost:3306/retail_db`
   - Username: `root`
   - Password: `YOUR_MYSQL_PASSWORD` (replace this placeholder locally with your own MySQL password; do not commit the real password).
6. Run `Main.main()`.

## Database

The `Product` table contains:

- `product_id` — primary key
- `product_name`
- `category`
- `quantity`
- `price`

## Security

The committed source intentionally uses a placeholder for the database password. Never commit real passwords, tokens, or other secrets to a public repository.
