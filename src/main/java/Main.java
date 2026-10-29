import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * Inventory Management System using Java JDBC and MySQL.
 *
 * Configure DB_URL, DB_USER, and DB_PASSWORD for your MySQL installation.
 * This program expects a database named retail_db and a Product table.
 */
public class Main {
    private static final String DB_URL =
            "jdbc:mysql://localhost:3306/retail_db";
    private static final String DB_USER = "root";
    // Change this placeholder to your own MySQL password locally.
    private static final String DB_PASSWORD = "YOUR_MYSQL_PASSWORD";

    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            try (Connection con = DriverManager.getConnection(
                    DB_URL, DB_USER, DB_PASSWORD)) {
                System.out.println("Connected to MySQL successfully!");

                boolean running = true;
                while (running) {
                    printMenu();
                    System.out.print("Enter your choice: ");
                    String input = sc.nextLine().trim();

                    int choice;
                    try {
                        choice = Integer.parseInt(input);
                    } catch (NumberFormatException e) {
                        System.out.println("Please enter a number from 1 to 6.");
                        continue;
                    }

                    try {
                        switch (choice) {
                            case 1:
                                insertThreeProducts(con, sc);
                                break;
                            case 2:
                                searchByCategory(con, sc);
                                break;
                            case 3:
                                updateStock(con, sc);
                                break;
                            case 4:
                                updatePrice(con, sc);
                                break;
                            case 5:
                                inventoryValueAndDelete(con, sc);
                                break;
                            case 6:
                                running = false;
                                System.out.println("Exiting Inventory Management System.");
                                break;
                            default:
                                System.out.println("Invalid choice. Choose 1 to 6.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid numeric input. Please try again.");
                    } catch (SQLException e) {
                        if (isDuplicateKey(e)) {
                            System.out.println("A product with that ID already exists.");
                        } else {
                            System.out.println("Database error: " + e.getMessage());
                        }
                    }
                }
            } catch (SQLException e) {
                System.out.println("Could not connect to MySQL: " + e.getMessage());
                System.out.println("Check that MySQL is running, retail_db exists, and credentials are correct.");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n===== INVENTORY MANAGEMENT SYSTEM =====");
        System.out.println("1. Insert three products");
        System.out.println("2. Search products by category");
        System.out.println("3. Add stock to a product");
        System.out.println("4. Search and update product price");
        System.out.println("5. Calculate inventory value and delete zero-stock product");
        System.out.println("6. Exit");
    }

    // Part (a): Insert three products and handle duplicate product IDs.
    private static void insertThreeProducts(Connection con, Scanner sc)
            throws SQLException {
        String sql = "INSERT INTO Product " +
                "(product_id, product_name, category, quantity, price) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            int inserted = 0;
            while (inserted < 3) {
                System.out.println("\nEnter details for product " + (inserted + 1));

                int id = readInt(sc, "Product ID: ");
                System.out.print("Product name: ");
                String name = sc.nextLine().trim();
                System.out.print("Category: ");
                String category = sc.nextLine().trim();
                int quantity = readInt(sc, "Quantity: ");
                double price = readDouble(sc, "Price: ");

                if (name.isEmpty() || category.isEmpty()) {
                    System.out.println("Product name and category cannot be empty.");
                    continue;
                }
                if (quantity < 0 || price < 0) {
                    System.out.println("Quantity and price cannot be negative.");
                    continue;
                }

                ps.setInt(1, id);
                ps.setString(2, name);
                ps.setString(3, category);
                ps.setInt(4, quantity);
                ps.setDouble(5, price);

                try {
                    int rows = ps.executeUpdate();
                    if (rows > 0) {
                        inserted++;
                        System.out.println("Product inserted successfully!");
                    }
                } catch (SQLException e) {
                    if (isDuplicateKey(e)) {
                        System.out.println("Duplicate product ID. Enter a different ID.");
                    } else {
                        throw e;
                    }
                }
            }
        }
    }

    // Part (b): Filter by category and quantity, then sort by price descending.
    private static void searchByCategory(Connection con, Scanner sc)
            throws SQLException {
        System.out.print("Enter category: ");
        String category = sc.nextLine().trim();

        String sql = "SELECT product_id, product_name, category, quantity, price " +
                "FROM Product WHERE category = ? AND quantity < 20 " +
                "ORDER BY price DESC";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, category);
            try (ResultSet rs = ps.executeQuery()) {
                boolean found = false;
                System.out.println("\nMatching products:");
                while (rs.next()) {
                    found = true;
                    printProduct(rs);
                }
                if (!found) {
                    System.out.println("No matching products found.");
                }
            }
        }
    }

    // Part (c): Add a positive amount to the existing stock.
    private static void updateStock(Connection con, Scanner sc)
            throws SQLException {
        int id = readInt(sc, "Enter product ID: ");
        int amount = readInt(sc, "Enter quantity to add: ");

        if (amount <= 0) {
            System.out.println("Quantity to add must be positive.");
            return;
        }

        String sql = "UPDATE Product SET quantity = quantity + ? WHERE product_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, amount);
            ps.setInt(2, id);
            int rows = ps.executeUpdate();

            if (rows == 0) {
                System.out.println("Product ID not found.");
                return;
            }

            System.out.println("Stock updated successfully!");
            showProductById(con, id);
        }
    }

    // Part (d): Find a product, update its price, and display the updated record.
    private static void updatePrice(Connection con, Scanner sc)
            throws SQLException {
        int id = readInt(sc, "Enter product ID: ");

        if (!productExists(con, id)) {
            System.out.println("Product ID not found.");
            return;
        }

        showProductById(con, id);
        double newPrice = readDouble(sc, "Enter new price: ");

        if (newPrice <= 0) {
            System.out.println("Price must be greater than zero.");
            return;
        }

        String sql = "UPDATE Product SET price = ? WHERE product_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, newPrice);
            ps.setInt(2, id);
            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Price updated successfully!");
                System.out.println("Updated product details:");
                showProductById(con, id);
            } else {
                System.out.println("Price could not be updated.");
            }
        }
    }

    // Part (e): Calculate total inventory value, then conditionally delete a product.
    private static void inventoryValueAndDelete(Connection con, Scanner sc)
            throws SQLException {
        String totalSql =
                "SELECT COALESCE(SUM(quantity * price), 0) AS total_value FROM Product";

        try (PreparedStatement ps = con.prepareStatement(totalSql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                System.out.printf("Total inventory value: %.2f%n",
                        rs.getDouble("total_value"));
            }
        }

        int id = readInt(sc,
                "Enter product ID to delete (quantity must be zero): ");

        String deleteSql =
                "DELETE FROM Product WHERE product_id = ? AND quantity = 0";

        try (PreparedStatement ps = con.prepareStatement(deleteSql)) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Zero-stock product deleted successfully.");
            } else {
                System.out.println(
                        "Product not deleted: it may not exist or its quantity is not zero.");
            }
        }
    }

    private static boolean productExists(Connection con, int id)
            throws SQLException {
        String sql = "SELECT 1 FROM Product WHERE product_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private static void showProductById(Connection con, int id)
            throws SQLException {
        String sql = "SELECT product_id, product_name, category, quantity, price " +
                "FROM Product WHERE product_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    printProduct(rs);
                }
            }
        }
    }

    private static void printProduct(ResultSet rs) throws SQLException {
        System.out.printf("ID: %d | Name: %s | Category: %s | Quantity: %d | Price: %.2f%n",
                rs.getInt("product_id"),
                rs.getString("product_name"),
                rs.getString("category"),
                rs.getInt("quantity"),
                rs.getDouble("price"));
    }

    private static int readInt(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    private static double readDouble(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                double value = Double.parseDouble(sc.nextLine().trim());
                if (Double.isFinite(value)) {
                    return value;
                }
                System.out.println("Please enter a finite number.");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static boolean isDuplicateKey(SQLException e) {
        // MySQL duplicate primary/unique key error code is 1062.
        return e.getErrorCode() == 1062 || "23000".equals(e.getSQLState());
    }
}
