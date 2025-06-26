package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.models.Product;
import org.yearup.data.ProductDao;

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

        if (categoryId != null && categoryId != -1) {
            sqlBuilder.append(" AND category_id = ?");
            params.add(categoryId);
        }

        if (minPrice != null && minPrice.compareTo(BigDecimal.valueOf(-1)) != 0) {
            sqlBuilder.append(" AND price >= ?");
            params.add(minPrice);
        }

        if (maxPrice != null && maxPrice.compareTo(BigDecimal.valueOf(-1)) != 0) {
            sqlBuilder.append(" AND price <= ?");
            params.add(maxPrice);
        }

        if (color != null && !color.isBlank()) {
            sqlBuilder.append(" AND color LIKE ?");
            params.add("%" + color + "%");
        }


        sqlBuilder.append(" ORDER BY product_id ASC");


        try (Connection connection = getConnection())
        {

            PreparedStatement statement = connection.prepareStatement(sqlBuilder.toString());
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof Integer) {
                    statement.setInt(i + 1, (Integer) param);
                } else if (param instanceof BigDecimal) {
                    statement.setBigDecimal(i + 1, (BigDecimal) param);
                } else if (param instanceof String) {
                    statement.setString(i + 1, (String) param);
                }
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

    @Override
    public List<Product> listByCategoryId(int categoryId)
    {
        List<Product> products = new ArrayList<>();

        String sql = "SELECT * FROM products " +
                " WHERE category_id = ? ";

        try (Connection connection = getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, categoryId);

            ResultSet row = statement.executeQuery();

            while (row.next())
            {
                Product product = mapRow(row);
                products.add(product);
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }

        return products;
    }


    @Override
    public Product getById(int productId)
    {
        String sql = "SELECT * FROM products WHERE product_id = ?";
        try (Connection connection = getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, productId);

            ResultSet row = statement.executeQuery();

            if (row.next())
            {
                return mapRow(row);
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Product create(Product product)
    {

        String sql = "INSERT INTO products(name, price, category_id, description, color, image_url, stock, featured) " +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

        try (Connection connection = getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setString(1, product.getName());
            statement.setBigDecimal(2, product.getPrice());
            statement.setInt(3, product.getCategoryId());
            statement.setString(4, product.getDescription());
            statement.setString(5, product.getColor());
            statement.setString(6, product.getImageUrl());
            statement.setInt(7, product.getStock());
            statement.setBoolean(8, product.isFeatured());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {

                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int newProductId = generatedKeys.getInt(1);

                        return getById(newProductId);
                    }
                }
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error creating product: " + product.getName(), e);
        }
        return null;
    }

    @Override
    public void update(int productId, Product product)
    {

        String sql = "UPDATE products" +
                " SET name = ? " +
                "   , price = ? " +
                "   , category_id = ? " +
                "   , description = ? " +
                "   , color = ? " +
                "   , image_url = ? " +
                "   , stock = ? " +
                "   , featured = ? " +
                " WHERE product_id = ?;";

        try (Connection connection = getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, product.getName());
            statement.setBigDecimal(2, product.getPrice());
            statement.setInt(3, product.getCategoryId());
            statement.setString(4, product.getDescription());
            statement.setString(5, product.getColor());
            statement.setString(6, product.getImageUrl());
            statement.setInt(7, product.getStock());
            statement.setBoolean(8, product.isFeatured());
            statement.setInt(9, productId);

            statement.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error updating product with ID: " + productId, e);
        }
    }

    @Override
    public void delete(int productId)
    {

        String sql = "DELETE FROM products WHERE product_id = ?;";

        try (Connection connection = getConnection())
        {

            String deleteShoppingCartSql = "DELETE FROM shopping_cart WHERE product_id = ?";
            try (PreparedStatement deleteCartStatement = connection.prepareStatement(deleteShoppingCartSql)) {
                deleteCartStatement.setInt(1, productId);
                deleteCartStatement.executeUpdate();
            }


            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, productId);

            statement.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error deleting product with ID: " + productId, e);
        }
    }

    protected static Product mapRow(ResultSet row) throws SQLException
    {
        int productId = row.getInt("product_id");
        String name = row.getString("name");
        BigDecimal price = row.getBigDecimal("price");
        int categoryId = row.getInt("category_id");
        String description = row.getString("description");
        String color = row.getString("color");
        int stock = row.getInt("stock");
        boolean isFeatured = row.getBoolean("featured");
        String imageUrl = row.getString("image_url");

        return new Product(productId, name, price, categoryId, description, color, stock, isFeatured, imageUrl);
    }
}