# 🏩 Product GO GO Spring Boot API Documentation

This document provides a comprehensive overview of all the available REST API endpoints in the AliScraper backend application. Each section corresponds to a specific controller.

---

## 🧠 AuthController - `/api/auth`
Authentication-related endpoints for user registration and login.

### 🔐 Register User
```http
POST /api/auth/register
```
**Request Body**
```json
{
  "username": "user@example.com",
  "password": "securepassword"
}
```
**Sample Response**
```json
{
  "message": "User registered successfully"
}
```

### 🔐 Authenticate User
```http
POST /api/auth/login
```
**Request Body**
```json
{
  "username": "user@example.com",
  "password": "securepassword"
}
```
**Sample Response**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR..."
}
```

---

## 📊 DashboardDataController - `/api/dashboard`
Provides dashboard data for authenticated users.

### 📈 Get Dashboard Data
```http
GET /api/dashboard
```
**Headers**
```
Authorization: Bearer <JWT_TOKEN>
```
**Sample Response**
```json
{
  "totalProducts": 23,
  "dailyUploads": 5
}
```

---

## 📝 DescriptionController - `/api/description`
Generate marketing descriptions based on product data.

### 🪄 Generate Product Description
```http
POST /api/description/generateDescription
```
**Request Body**
```json
{
  "userPrompt": "Create a compelling product description.",
  "productData": {"Color": "Red", "Material": "Cotton"},
  "productName": "Red Cotton T-Shirt"
}
```
**Sample Response**
```json
{
  "description": "This Red Cotton T-Shirt blends comfort with casual style..."
}
```

---

## 🌐 ExtentionDataController - `/api/scrape`
Handles incoming data scraped from the browser extension.

### 📨 Receive Scraped Data (Extension)
```http
POST /api/scrape/receive
```
**Headers**
```
Authorization: Bearer <JWT_TOKEN>
```
**Request Body**: complex product JSON

**Sample Response**
```json
{
  "message": "Scraped data received successfully."
}
```

---

## 🌐 InAppWebScraperController - `/api/scrape`
Allows scraping directly within the app given a URL.

### 🔍 Scrape AliExpress In-App
```http
POST /api/scrape/aliexpress?url=<product_url>
```
**Headers**
```
Authorization: Bearer <JWT_TOKEN>
```
**Sample Response**
```json
{
  "title": "Product Title",
  "price": "$9.99",
  "...":"..."
}
```

---

## 📦 ProductController - `/api/products`
Manage user products, including create, read, update, and delete operations.

### 📦 Get User Products
```http
GET /api/products
```

### 📦 Get Product By ID
```http
GET /api/products/{id}
```

### 📦 Create Product
```http
POST /api/products/create
```

### 📦 Update Product
```http
PUT /api/products/{id}
```

### 🗑️ Delete Product
```http
DELETE /api/products/{id}
```

### 📊 Get User Product Count
```http
GET /api/products/count
```

### 📆 Get Daily Product Count
```http
GET /api/products/daily-count
```

### 📆 Get All Users Daily Count (ADMIN)
```http
GET /api/products/all-users/daily-count
```

### 📦 Get All Products (ADMIN)
```http
GET /api/products/all
```

---

## ⭐ ReviewController - `/api/reviews`
Fetch product reviews directly from AliExpress.

### ⭐ Get Product Reviews
```http
GET /api/reviews?productId=<product_id>
```
**Sample Response**
```json
[
  {
    "buyerName": "John",
    "buyerCountry": "US",
    "feedback": "Great product!",
    "evalDate": "2023-01-10",
    "images": ["https://image1.jpg"]
  }
]
```

---

## 👤 UserDataController - `/api/user`
Endpoints to manage user profile and password.

### 👤 Get Current User
```http
GET /api/user/me
```
**Headers**
```
Authorization: Bearer <JWT_TOKEN>
```
**Sample Response**
```json
{
  "username": "user@example.com",
  "role": "USER"
}
```

### ❌ Delete Account
```http
POST /api/user/deleteAccount
```
**Headers**
```
Authorization: Bearer <JWT_TOKEN>
```
**Sample Response**
```json
true
```

### 🔐 Change Password
```http
POST /api/user/change-password
```
**Headers**
```
Authorization: Bearer <JWT_TOKEN>
```
**Request Body**
```json
{
  "currentPassword": "oldPass",
  "newPassword": "newPass"
}
```
**Sample Response**
```json
{
  "message": "Password changed successfully"
}
```

---

## 🛒 WooCommerceController - `/api/woocommerce`
Imports AliExpress products into a WooCommerce store.

### 📄 Import Product to WooCommerce
```http
POST /api/woocommerce/import-product?storeId=<store_id>
```
**Headers**
```
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```
**Request Body**
```json
"{...raw AliExpress product JSON...}"
```
**Sample Response**
```json
{
  "message": "Product imported successfully"
}
```

---

## 📊 AdminController - `/api/admin`
Provides administrative endpoints for analytics and user/store/product management.

### 🔹 Get Total Users Count
```http
GET /api/admin/total-users
```

### 🔹 Get Total Stores Count
```http
GET /api/admin/total-stores
```

### 🔹 Get All Users
```http
GET /api/admin/users
```

### 🔹 Get All Stores
```http
GET /api/admin/stores
```

### 🔹 Get All Products
```http
GET /api/admin/products
```

### 🔹 Delete Store By ID
```http
DELETE /api/admin/store/{id}
```

---

## ⚙️ Application Configuration
Before running the application, make sure to configure your `application.properties`:

```properties
spring.application.name=AliScapper
server.port=8080
spring.data.mongodb.uri=mongodb+srv://<username>:<password>@cluster.mongodb.net
spring.data.mongodb.database=Cluster0dropshipping

jwt.secret=39f0aea7-ff2e-4001-9dd8-ea981a9ee6bf
jwt.expiration=3600000

api.url=https://openrouter.ai/api/v1/chat/completions
api.key=sk-<your-key>
```

---




## 🧩 Chrome Extension

This project includes a Chrome extension for scraping AliExpress product data. It communicates with the backend via the `/api/scrape/receive` endpoint.

> 🔧 The extension is maintained separately in its own repository.
> https://github.com/Shashi-Madushan/AliScraperExtention

---


