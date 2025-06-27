EasyShop E-Commerce API - Capstone Project
Project Description
This project is the backend API for an e-commerce application named EasyShop. It's built using Spring Boot and interacts with a MySQL database. The API provides essential functionalities for an online store, including:

User Authentication & Authorization: Secure user login and registration using JWT (JSON Web Tokens) with distinct ROLE_USER and ROLE_ADMIN roles.

Product Management:

Retrieving lists of all products.

Retrieving individual product details by ID.

Advanced Search & Filtering: Products can be searched and filtered by category, price range (minimum and maximum), and color.

Admin-only CRUD operations: Administrators can create, update, and delete product listings.

Category Management:

Retrieving lists of all product categories.

Retrieving individual category details by ID.

Retrieving products within a specific category.

Admin-only CRUD operations: Administrators can create, update, and delete product categories. Includes cascading deletion for associated products and shopping cart items.

User Profile Management: Basic profile creation upon user registration.

The primary goals of this capstone were to:

Implement new features for Category management (CRUD operations).

Identify and fix existing bugs in the Product search and update functionalities.

Ensure robust security by properly implementing role-based authorization using Spring Security and JWT.






 Interesting Piece of Code:
One interesting piece of code in this project is the dynamic product search functionality implemented in the MySqlProductDao class. This method efficiently handles various optional filtering parameters (category, minimum price, maximum price, and color) by dynamically constructing the SQL query.


Code Snippet: MySqlProductDao.java search method

package com.easyshop.data.mysql;

import org.springframework.stereotype.Component;
import com.easyshop.model.Product;
import com.easyshop.dao.ProductDao;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlProductDao extends MySqlDaoBase implements ProductDao
{
    public MySqlProductDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public List<Product> search(Integer categoryId, BigDecimal minPrice, BigDecimal maxPrice, String color)
    {
        List<Product> products = new ArrayList<>();
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM products WHERE 1=1");
        List<Object> params = new ArrayList<>();

        // Conditionally append filter clauses based on provided parameters
        if (categoryId != null && categoryId != -1) { // -1 acts as a "no filter" flag for int
            sqlBuilder.append(" AND category_id = ?");
            params.add(categoryId);
        }

        if (minPrice != null && minPrice.compareTo(BigDecimal.valueOf(-1)) != 0) { // -1 acts as a "no filter" flag for BigDecimal
            sqlBuilder.append(" AND price >= ?");
            params.add(minPrice);
        }

        if (maxPrice != null && maxPrice.compareTo(BigDecimal.valueOf(-1)) != 0) {
            sqlBuilder.append(" AND price <= ?");
            params.add(maxPrice);
        }

        if (color != null && !color.isBlank()) { // Check for non-null and non-empty string
            sqlBuilder.append(" AND color LIKE ?"); // Use LIKE for partial matching
            params.add("%" + color + "%"); // Add wildcards for flexible search
        }

        sqlBuilder.append(" ORDER BY product_id ASC"); // Ensure consistent order

        try (Connection connection = getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(sqlBuilder.toString());
            // Dynamically bind parameters based on their type and order of addition
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof Integer) {
                    statement.setInt(i + 1, (Integer) param);
                } else if (param instanceof BigDecimal) {
                    statement.setBigDecimal(i + 1, (BigDecimal) param);
                } else if (param instanceof String) {
                    statement.setString(i + 1, (String) param);
                }
                // Add more type checks if other parameter types are used
            }

            ResultSet row = statement.executeQuery();

            while (row.next())
            {
                Product product = mapRow(row);
                products.add(product);
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error during product search", e);
        }

        return products;
    }

    // ... other methods (listByCategoryId, getById, create, update, delete, mapRow)
}











