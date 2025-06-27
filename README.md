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



![es](https://github.com/user-attachments/assets/698159df-f13e-41b7-9140-909d6658118b)
![es2](https://github.com/user-attachments/assets/bf5cf423-32ba-4330-93c9-58f4ddea8484)
![es3](https://github.com/user-attachments/assets/7c53d1e7-6f73-499a-857f-01c89361ec53)
![es4](https://github.com/user-attachments/assets/2a386d39-efef-4479-a359-47b0da1f1a8e)
![es5](https://github.com/user-attachments/assets/6921f26c-4bec-48c6-899e-2d7a3d666e33)
![es6](https://github.com/user-attachments/assets/0b0cd329-0846-464a-b265-1d127e323956)
![es7](https://github.com/user-attachments/assets/903a76a1-48a5-45d6-bb70-f0051c164de5)
![es8](https://github.com/user-attachments/assets/ee754a9b-d659-4280-8c02-ec2467baafa1)
![es9](https://github.com/user-attachments/assets/739e655d-b216-463b-89b6-5778480373a4)
![es10](https://github.com/user-attachments/assets/0a48f639-162c-4ee0-a9f7-567e2a5a5e95)
![es11](https://github.com/user-attachments/assets/6b15dd1f-3a68-4613-81e2-2bff71044226)
![es12](https://github.com/user-attachments/assets/a93fbf46-6023-4231-b16b-b0474bede8ca)
![es13](https://github.com/user-attachments/assets/3913e441-3d70-4b34-9a47-b550d6e45168)
![es14](https://github.com/user-attachments/assets/bd1732e9-594e-4e4d-8918-45e85236f11c)
![es15](https://github.com/user-attachments/assets/71513061-4dfd-481d-a78f-35ca4e28d9a2)
![es16](https://github.com/user-attachments/assets/94c03c8b-e565-448f-a645-fff9c0ac18cf)
![es17](https://github.com/user-attachments/assets/69571c08-fbee-491e-932e-ebf451c1885f)
![es18](https://github.com/user-attachments/assets/89139fad-43cb-402f-bf7e-76c65ade0383)
![es19](https://github.com/user-attachments/assets/4c4704be-ff94-4a18-b69e-3fcdabf0db37)
![es20](https://github.com/user-attachments/assets/99ffdccb-6114-44b6-b043-0d44f0cc20ff)













