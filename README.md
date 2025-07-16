# Cosmetics E-Commerce

A full-stack e-commerce web application for selling cosmetic products, built with Java, Spring Boot, and Thymeleaf.

## Table of Contents

- [Project Overview](#project-overview)
- [Tech Stack](#tech-stack)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
- [Features](#features)
- [API Endpoints](#api-endpoints)

## Project Overview

This project is a fully functional e-commerce platform designed for selling cosmetics. It includes features for both customers and administrators, providing a seamless shopping experience and powerful management tools.

## Tech Stack

- **Backend**: Java, Spring Boot
- **Frontend**: Thymeleaf, Tailwind CSS, JavaScript
- **Database**: MySQL
- **Build Tool**: Maven

## Getting Started

### Prerequisites

- Java 11 or higher
- Maven 3.2 or higher

### Installation

1.  **Clone the repository**:
    ```bash
    git clone https://github.com/svmmy-a/cosmetics-ecommerce.git
    ```
2.  **Navigate to the project directory**:
    ```bash
    cd cosmetics-ecommerce
    ```
3.  **Run the application**:
    ```bash
    ./mvnw spring-boot:run
    ```
The application will be available at `http://localhost:8080`.

### Database Setup

1.  Ensure you have MySQL installed and running.
2.  Create a new database named `cosmetics_db`.
3.  The application will automatically create the necessary tables when it starts up.

## Features

-   **Customer Features**:
    -   User registration and login
    -   Browse products by category
    -   Search for products
    -   Add products to cart and wishlist
    -   Checkout and payment processing
    -   View order history
-   **Admin Features**:
    -   Dashboard with sales analytics
    -   Manage products, categories, and inventory
    -   View and manage customer orders
    -   Manage suppliers and warehouses

## API Endpoints

### Products

-   `GET /api/products`: Get all products
-   `GET /api/products/{id}`: Get a product by ID
-   `POST /api/products`: Create a new product
-   `PUT /api/products/{id}`: Update a product
-   `DELETE /api/products/{id}`: Delete a product

### Cart

-   `GET /api/cart`: Get cart items
-   `POST /api/cart`: Add an item to the cart
-   `DELETE /api/cart/{productId}`: Remove an item from the cart

### Orders

-   `GET /api/orders`: Get all orders
-   `GET /api/orders/{id}`: Get an order by ID
-   `POST /api/orders`: Create a new order
