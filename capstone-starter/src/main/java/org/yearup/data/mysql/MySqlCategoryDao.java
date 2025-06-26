package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.CategoryDao;
import org.yearup.models.Category;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlCategoryDao extends MySqlDaoBase implements CategoryDao
{
    public MySqlCategoryDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public List<Category> getAllCategories()
    {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT category_id, name, description FROM categories";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet row = statement.executeQuery())
        {
            while (row.next())
            {
                categories.add(mapRow(row));
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
        return categories;
    }

    @Override
    public Category getById(int categoryId)
    {
        String sql = "SELECT category_id, name, description FROM categories WHERE category_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.setInt(1, categoryId);
            try (ResultSet row = statement.executeQuery())
            {
                if (row.next())
                {
                    return mapRow(row);
                }
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Category create(Category category)
    {
        String sql = "INSERT INTO categories (name, description) VALUES (?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            statement.setString(1, category.getName());
            statement.setString(2, category.getDescription());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int newCategoryId = generatedKeys.getInt(1);
                        return getById(newCategoryId);
                    }
                }
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public void update(int categoryId, Category category)
    {
        String sql = "UPDATE categories SET name = ?, description = ? WHERE category_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.setString(1, category.getName());
            statement.setString(2, category.getDescription());
            statement.setInt(3, categoryId);
            statement.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int categoryId)
    {
        try (Connection connection = getConnection())
        {
            // --- NEW: Delete from shopping_cart first ---
            String deleteShoppingCartItemsSql = "DELETE FROM shopping_cart WHERE product_id IN (SELECT product_id FROM products WHERE category_id = ?)";
            try (PreparedStatement deleteShoppingCartItemsStatement = connection.prepareStatement(deleteShoppingCartItemsSql)) {
                deleteShoppingCartItemsStatement.setInt(1, categoryId);
                deleteShoppingCartItemsStatement.executeUpdate();
            }

            // Then, delete all products that belong to this category
            String deleteProductsSql = "DELETE FROM products WHERE category_id = ?";
            try (PreparedStatement deleteProductsStatement = connection.prepareStatement(deleteProductsSql)) {
                deleteProductsStatement.setInt(1, categoryId);
                deleteProductsStatement.executeUpdate();
            }

            // Finally, delete the category itself
            String deleteCategorySql = "DELETE FROM categories WHERE category_id = ?";
            try (PreparedStatement deleteCategoryStatement = connection.prepareStatement(deleteCategorySql)) {
                deleteCategoryStatement.setInt(1, categoryId);
                deleteCategoryStatement.executeUpdate();
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error deleting category with ID: " + categoryId + " and its associated data.", e);
        }
    }

    private Category mapRow(ResultSet row) throws SQLException
    {
        int categoryId = row.getInt("category_id");
        String name = row.getString("name");
        String description = row.getString("description");

        Category category = new Category()
        {{
            setCategoryId(categoryId);
            setName(name);
            setDescription(description);
        }};

        return category;
    }
}