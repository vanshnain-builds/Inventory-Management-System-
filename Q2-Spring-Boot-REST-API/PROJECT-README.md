# Inventory Management REST API (Spring Boot + JDBC + MySQL)

A REST API for the OOPJ CS336B MSE2 Q2 requirement. It uses Spring Boot, Spring JDBC (JdbcTemplate), and MySQL. Test the endpoints with Postman.

## Requirements
- JDK 17 or newer
- Maven 3.6+ (or IntelliJ IDEA Maven support)
- MySQL Server
- Postman

## Setup
1. Run `database.sql` in MySQL Workbench or MySQL command line.
2. Open `src/main/resources/application.properties` and replace `YOUR_MYSQL_PASSWORD` with your local MySQL password. Never commit a real password to GitHub. Alternatively, set `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` environment variables.
3. Open this folder in IntelliJ IDEA (the folder containing `pom.xml`).
4. Let Maven download dependencies and run `InventoryRestApiApplication.java`.
5. API base URL: `http://localhost:8080`.

## Endpoints
| Method | URL | Purpose |
|---|---|---|
| GET | `/api/products` | List products |
| GET | `/api/products/101` | Find by ID |
| POST | `/api/products` | Add product |
| PUT | `/api/products/101` | Update product |
| DELETE | `/api/products/101` | Delete product |
| GET | `/api/products/low-stock` | Products with quantity below 20, price descending |
| GET | `/api/products/inventory-value` | Sum of quantity × price |

### POST example
```json
{
  "productId": 104,
  "productName": "USB Cable",
  "category": "Electronics",
  "quantity": 12,
  "price": 199.00
}
```

### PUT example
```json
{
  "productName": "Wireless Mouse",
  "category": "Electronics",
  "quantity": 18,
  "price": 699.00
}
```

In Postman, choose Body → raw → JSON for POST and PUT. Capture successful GET, POST, PUT and DELETE responses for the submission. For DELETE, use a test product or re-run the sample inserts before capturing screenshots.

## GitHub and cloud notes
The repository intentionally contains a password placeholder. For cloud hosting, set environment variables and use a hosted MySQL database reachable from the hosting provider. A database on your own computer at localhost is not reachable from a remote host.
