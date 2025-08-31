# E-Commerce Backend API

A comprehensive e-commerce backend application built with Spring Boot, providing RESTful APIs for online shopping functionality with multi-role user management.

## 🚀 Features

### Authentication & Authorization

- **JWT-based Authentication** - Secure token-based authentication system
- **Multi-role Support** - BUYER, SELLER, and ADMIN roles with role-based access control
- **Email Verification** - Account activation via email verification
- **Password Reset** - Secure password reset functionality via email
- **Cookie-based Session Management** - Automatic JWT cookie handling

### User Management

- **User Registration & Login** - Complete user authentication flow
- **Profile Management** - Update user information and account details
- **Soft Delete** - Users are soft deleted to maintain data integrity
- **Admin Panel** - Administrative functions for user management

### Product Management

- **Product CRUD** - Complete product lifecycle management (Sellers only)
- **Product Search** - Advanced search functionality with filters
- **Category Management** - Organize products by categories (Admin controlled)
- **Pagination** - Efficient data loading with paginated responses

### Shopping Cart

- **Cart Management** - Create, view, and delete shopping carts (Buyers only)
- **Cart Items** - Add, update, remove, and view items in cart
- **User-specific Carts** - Each buyer has their own shopping cart

### Technical Features

- **Redis Caching** - Improved performance with Redis integration
- **Email Service** - SMTP integration for notifications
- **Global Exception Handling** - Centralized error management
- **Data Validation** - Comprehensive input validation
- **Audit Trail** - Track creation and modification timestamps
- **API Documentation** - Interactive Swagger UI documentation
- **Logging** - AOP-based logging for monitoring

## 🛠 Technology Stack

- **Framework**: Spring Boot 3.3.2
- **Language**: Java 22
- **Database**: PostgreSQL
- **Cache**: Redis
- **Security**: Spring Security with JWT
- **Documentation**: SpringDoc OpenAPI (Swagger)
- **Email**: Spring Boot Mail
- **Build Tool**: Maven
- **Additional Libraries**:
  - Lombok - Reduce boilerplate code
  - ModelMapper - Object mapping
  - JavaFaker - Test data generation

## 📋 Prerequisites

Before running the application, ensure you have the following installed:

- **Java 22** or higher
- **Maven 3.6+**
- **PostgreSQL 12+**
- **Redis Server**
- **SMTP Email Account** (for email features)

## ⚙️ Environment Configuration

Create a `.env` file in the project root with the following variables:

```env
# Server Configuration
SERVER_PORT=8080
CONTEXT_PATH=/api

# Database Configuration
DB_URL=jdbc:postgresql://localhost:5432/ecommerce_db
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password

# JWT Configuration
JWT_SECRET=your_jwt_secret_key_here_make_it_long_and_secure
JWT_EXPIRE=86400000

# Email Configuration
EMAIL_USERNAME=your_email@gmail.com
EMAIL_PASSWORD=your_app_password
```

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone <repository-url>
cd E-commerce_demo/backend
```

### 2. Database Setup

```sql
-- Create database
CREATE DATABASE ecommerce_db;

-- The application will automatically create tables using JPA/Hibernate
```

### 3. Start Redis Server

```bash
# On Windows
redis-server

# On Linux/Mac
sudo service redis-server start
```

### 4. Install Dependencies

```bash
mvn clean install
```

### 5. Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080/api`

## 📚 API Documentation

Once the application is running, access the interactive API documentation at:

- **Swagger UI**: `http://localhost:8080/api/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/api/v3/api-docs`

### Authentication

To access protected endpoints, include the JWT token in the Authorization header:

```http
Authorization: Bearer <your-jwt-token>
```

## 🔐 API Endpoints Overview

### Public Endpoints (No Authentication Required)

```http
POST /api/login              - User login
POST /api/register           - User registration
POST /api/verify-email       - Email verification
POST /api/forget-password    - Request password reset
POST /api/reset-password     - Reset password
GET  /api/logout             - User logout
```

### Product Endpoints

```http
GET    /api/product          - Get all products (paginated)
GET    /api/product/{id}     - Get product by ID
POST   /api/product/search   - Search products with filters
POST   /api/product          - Add product (SELLER only)
PATCH  /api/product/{id}     - Update product (SELLER only)
DELETE /api/product/{id}     - Delete product (SELLER only)
```

### Category Endpoints

```http
GET    /api/category         - Get all categories
GET    /api/category/{id}    - Get category by ID
POST   /api/category         - Add category (ADMIN only)
PATCH  /api/category/{id}    - Update category (ADMIN only)
DELETE /api/category/{id}    - Delete category (ADMIN only)
```

### Cart Endpoints (BUYER only)

```http
POST   /api/cart             - Create cart
GET    /api/cart/cartItems   - Get cart items
DELETE /api/cart             - Delete cart
```

### Cart Item Endpoints (BUYER only)

```http
POST   /api/cart-item        - Add item to cart
GET    /api/cart-item/{id}   - Get cart item
PATCH  /api/cart-item/{id}   - Update cart item quantity
DELETE /api/cart-item/{id}   - Remove item from cart
```

### User Endpoints

```http
GET    /api/user             - Get current user profile
PATCH  /api/user             - Update user profile
DELETE /api/user             - Delete user account
```

### Admin Endpoints (ADMIN only)

```http
GET    /api/admin/all-sellers-buyers    - Get all users (paginated)
GET    /api/admin/user-details/{id}     - Get user details by ID
DELETE /api/admin/delete-user/{id}      - Delete user by ID
```

## 🏗 Project Structure

```
src/
├── main/
│   ├── java/org/ecommerce/backend/
│   │   ├── aspect/           # AOP logging aspects
│   │   ├── config/           # Configuration classes
│   │   │   ├── CorsConfig.java
│   │   │   ├── SecurityConfiguration.java
│   │   │   ├── RedisConfiguration.java
│   │   │   └── SwaggerConfig.java
│   │   ├── controller/       # REST Controllers
│   │   ├── dto/             # Data Transfer Objects
│   │   ├── exception/       # Exception handling
│   │   ├── model/           # JPA Entities
│   │   ├── repository/      # Data Access Layer
│   │   ├── service/         # Business Logic Layer
│   │   ├── util/            # Utility classes
│   │   └── validator/       # Custom validators
│   └── resources/
│       └── application.properties
└── test/                    # Test cases
```

## 🔒 Security Features

- **JWT Authentication** - Stateless authentication with secure tokens
- **Role-based Authorization** - Method-level security with @PreAuthorize
- **CORS Configuration** - Cross-origin request handling
- **Password Encryption** - BCrypt password hashing
- **Input Validation** - Comprehensive request validation
- **SQL Injection Prevention** - JPA/Hibernate parameterized queries

## 📊 Database Schema

### Core Entities

- **Users** - User account information with roles
- **Products** - Product catalog with seller relationships
- **Categories** - Product categorization
- **Carts** - Shopping cart for buyers
- **CartItems** - Items within shopping carts

### Key Relationships

- User (SELLER) → Products (One-to-Many)
- User (BUYER) → Cart (One-to-One)
- Cart → CartItems (One-to-Many)
- Category → Products (One-to-Many)


## 📝 Logging

Application logs are written to:

- **Console** - Development logging
- **File** - `logs/application.log`

---

**Note**: This is a demo project for educational purposes. For production use, additional security measures and optimizations should be implemented.
