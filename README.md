# Cotton Dealer Management App

A web application for cotton dealers to manage customers, cotton purchases, and financial transactions.

## Features
- **Customer Management**: Add and view customers.
- **Cotton Entries**: Record cotton purchases (Weight, Rate, Total Amount).
- **Transactions**: Record payments (TAKE/GIVE).
- **Balance Calculation**: Automatically calculates net balance for each customer.
- **Dashboard**: High-level summary of business.

## Tech Stack
- **Backend**: Java, Spring Boot (Web, Data JPA)
- **Database**: MySQL
- **Frontend**: HTML5, Bootstrap 5, JavaScript (Fetch API)

## Prerequisites
1. **Java JDK 17+** installed.
2. **Maven** installed.
3. **MySQL Server** installed and running.

## Setup Instructions

1. **Database Setup**:
   - Create a database named `cotton_db` in MySQL.
     ```sql
     CREATE DATABASE cotton_db;
     ```
   - Update `src/main/resources/application.properties` if your MySQL username/password is different from `root`/`root`.

2. **Build the Project**:
   ```sh
   mvn clean install
   ```

3. **Run the Application**:
   ```sh
   mvn spring-boot:run
   ```
   The application will start on `http://localhost:8080`.

## Application Structure
- **Backend**: `src/main/java/com/cottondealer/app`
  - `model`: JPA Entites
  - `repository`: Data access
  - `service`: Business logic
  - `controller`: REST APIs
- **Frontend**: `src/main/resources/static`
  - `*.html`: Pages
  - `js/app.js`: Logic
  - `css/styles.css`: Styles

## APIs
- `GET /api/customers` - List all customers with balance.
- `GET /api/customers/{id}` - Get single customer details.
- `POST /api/customers` - Create customer.
- `POST /api/entries` - Add cotton entry.
- `POST /api/transactions` - Add payment transaction.
