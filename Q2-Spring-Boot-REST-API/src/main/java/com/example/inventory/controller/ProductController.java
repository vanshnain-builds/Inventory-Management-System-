package com.example.inventory.controller;

import com.example.inventory.model.Product;
import com.example.inventory.repository.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductRepository repository;
    public ProductController(ProductRepository repository) { this.repository = repository; }

    @GetMapping
    public List<Product> getAllProducts() { return repository.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable int id) {
        return repository.findById(id).<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Product not found")));
    }

    @PostMapping
    public ResponseEntity<?> createProduct(@Valid @RequestBody Product product) {
        if (product.getProductId() == null) return ResponseEntity.badRequest().body(Map.of("error", "productId is required"));
        try {
            repository.insert(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(product);
        } catch (DuplicateKeyException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "A product with this productId already exists"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable int id, @Valid @RequestBody Product product) {
        if (repository.findById(id).isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Product not found"));
        product.setProductId(id);
        repository.update(id, product);
        return ResponseEntity.ok(product);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable int id) {
        if (repository.findById(id).isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Product not found"));
        repository.delete(id);
        return ResponseEntity.ok(Map.of("message", "Product deleted successfully"));
    }

    @GetMapping("/low-stock")
    public List<Product> getLowStockProducts() { return repository.findLowStock(); }

    @GetMapping("/inventory-value")
    public Map<String, Double> getInventoryValue() { return Map.of("totalInventoryValue", repository.inventoryValue()); }
}
