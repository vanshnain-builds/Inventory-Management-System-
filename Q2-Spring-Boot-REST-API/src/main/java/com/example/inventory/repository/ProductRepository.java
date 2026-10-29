package com.example.inventory.repository;

import com.example.inventory.model.Product;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ProductRepository {
    private final JdbcTemplate jdbc;
    private final RowMapper<Product> mapper = (rs, rowNum) -> new Product(
            rs.getInt("product_id"), rs.getString("product_name"), rs.getString("category"),
            rs.getInt("quantity"), rs.getDouble("price"));

    public ProductRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public List<Product> findAll() {
        return jdbc.query("SELECT product_id, product_name, category, quantity, price FROM Product ORDER BY product_id", mapper);
    }
    public Optional<Product> findById(int id) {
        List<Product> result = jdbc.query("SELECT product_id, product_name, category, quantity, price FROM Product WHERE product_id = ?", mapper, id);
        return result.stream().findFirst();
    }
    public int insert(Product p) {
        return jdbc.update("INSERT INTO Product (product_id, product_name, category, quantity, price) VALUES (?, ?, ?, ?, ?)",
                p.getProductId(), p.getProductName(), p.getCategory(), p.getQuantity(), p.getPrice());
    }
    public int update(int id, Product p) {
        return jdbc.update("UPDATE Product SET product_name = ?, category = ?, quantity = ?, price = ? WHERE product_id = ?",
                p.getProductName(), p.getCategory(), p.getQuantity(), p.getPrice(), id);
    }
    public int delete(int id) { return jdbc.update("DELETE FROM Product WHERE product_id = ?", id); }
    public List<Product> findLowStock() {
        return jdbc.query("SELECT product_id, product_name, category, quantity, price FROM Product WHERE quantity < 20 ORDER BY price DESC", mapper);
    }
    public double inventoryValue() {
        Double value = jdbc.queryForObject("SELECT COALESCE(SUM(quantity * price), 0) FROM Product", Double.class);
        return value == null ? 0.0 : value;
    }
}
