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
