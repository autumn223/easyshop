
package org.yearup.controllers;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.CategoryDao;

import org.yearup.data.ProductDao;

import org.yearup.models.Category;

import org.yearup.models.Product;



import java.util.List;



import java.util.List;

@RestController

// http://localhost:8080/categories
@RequestMapping("/categories")
// add annotation to allow cross site origin requests
@CrossOrigin
public class CategoriesController
{
    private final CategoryDao categoryDao; // Using final for injected dependencies
    private final ProductDao productDao;   // Using final for injected dependencies


    // create an Autowired controller to inject the categoryDao and ProductDao
    @Autowired 
    public CategoriesController(CategoryDao categoryDao, ProductDao productDao)
    {
        this.categoryDao = categoryDao;
        this.productDao = productDao;
    }

    // add the appropriate annotation for a get action (all categories)
    @GetMapping("") // Maps to GET http://localhost:8080/categories
    public List<Category> getAll()
    {
        // find and return all categories
        try {
            return categoryDao.getAllCategories();
        } catch (Exception e) {
            
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving all categories", e);
        }
    }

    // add the appropriate annotation for a get action (category by ID)
    @GetMapping("{id}") // Maps to GET http://localhost:8080/categories/{id}
    public Category getById(@PathVariable int id)
    {
        // get the category by id
        Category category = categoryDao.getById(id);
        if (category == null) {
            // If category is not found, return 404 Not Found
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found with ID: " + id);
        }
        return category;
    }



    @GetMapping("{categoryId}/products") // Maps to GET http://localhost:8080/categories/{categoryId}/products
    public List<Product> getProductsById(@PathVariable int categoryId)
    {
        // get a list of product by categoryId
        try {
            
            return productDao.listByCategoryId(categoryId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving products for category ID: " + categoryId, e);
        }
    }

    // add annotation to call this method for a POST action
    @PostMapping("") // Maps to POST http://localhost:8080/categories
    // add annotation to ensure that only an ADMIN can call this function
    @PreAuthorize("hasRole('ADMIN')") // Only users with the 'ADMIN' role can access this method
    @ResponseStatus(HttpStatus.CREATED) // Returns 201 Created on success
    public Category addCategory(@RequestBody Category category)
    {
        // insert the category
        try {
            // The create method in the DAO should return the newly created category, potentially with its generated ID
            return categoryDao.create(category);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error adding category", e);
        }
    }

    // add annotation to call this method for a PUT (update) action - the url path must include the categoryId
    @PutMapping("{id}") // Maps to PUT http://localhost:8080/categories/{id}
    // add annotation to ensure that only an ADMIN can call this function
    @PreAuthorize("hasRole('ADMIN')") // Only users with the 'ADMIN' role can access this method
    @ResponseStatus(HttpStatus.NO_CONTENT) // Returns 204 No Content on successful update
    public void updateCategory(@PathVariable int id, @RequestBody Category category)
    {
        // update the category by id
        
        if (categoryDao.getById(id) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found with ID: " + id);
        }
        // Ensure the ID from the path variable is passed to the DAO for the update operation
        
        category.setCategoryId(id);
        try {
            categoryDao.update(id, category);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error updating category with ID: " + id, e);
        }
    }


   
    @DeleteMapping("{id}") // Maps to DELETE http://localhost:8080/categories/{id}
    // add annotation to ensure that only an ADMIN can call this function
    @PreAuthorize("hasRole('ADMIN')") // Only users with the 'ADMIN' role can access this method
    @ResponseStatus(HttpStatus.NO_CONTENT) // Returns 204 No Content on successful deletion
    public void deleteCategory(@PathVariable int id)
    {
        // delete the category by id
        // First, check if the category exists
        if (categoryDao.getById(id) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found with ID: " + id);
        }
        try {
            categoryDao.delete(id);
        } catch (Exception e) {
            
            
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error deleting category with ID: " + id + ". Check for dependent products.", e);
        }
    }
}

