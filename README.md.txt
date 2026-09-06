# 🏦 Bank Management System

A full-stack banking application built with Java, Spring Boot, and MySQL, featuring separate customer and admin portals with secure authentication, real-time transactions, and administrative oversight.

## 📋 Features

### Customer Portal
- **Registration & Login** — secure account creation with profile photo upload
- **Dashboard** — view profile, account details, and current balance
- **Deposit & Withdraw** — real-time balance updates with validation
- **Transaction History** — complete record of all deposits and withdrawals
- **Profile Management** — update name and phone number
- **Forgot/Reset Password** — token-based password recovery

### Admin Portal
- **Secure Login** — email-based authentication
- **Dashboard Overview** — live statistics (total customers, accounts, transactions, bank balance)
- **Customer Management** — search and view detailed customer profiles
- **Account Management** — view all bank accounts with linked customer and transaction data
- **Transaction Oversight** — complete log of all transactions across the bank

### Security
- **Password Hashing** — BCrypt encryption for all customer and admin passwords
- **Session-based Authentication** — secure login state management
- **Input Validation** — server-side validation for registration, updates, and password resets
- **Proper Error Handling** — meaningful HTTP status codes (400, 401, 404) instead of generic errors

## 🛠️ Tech Stack

- **Backend:** Java 17, Spring Boot, Spring MVC, Spring Data JPA
- **Frontend:** Thymeleaf, HTML, CSS, JavaScript
- **Database:** MySQL
- **Security:** Spring Security Crypto (BCrypt)
- **Build Tool:** Maven
- **IDE:** Eclipse

## 🏗️ Architecture

The project follows a clean layered architecture:

Controller → Service → Repository → Entity → MySQL

## 📂 Project Structure

com.bank.management
├── controller     → REST controllers (Customer, Admin, Bank Account, Page)
├── entity          → JPA entities (Customer, BankAccount, Transaction, Admin)
├── repository      → Spring Data JPA repositories
├── service         → Business logic layer
├── dto             → Data transfer objects for validated requests
└── config          → Configuration classes (Password Encoder)

## ⚙️ Setup Instructions

### Prerequisites
- Java 17
- MySQL Server
- Maven (or use the included mvnw wrapper)

### Steps

1. Clone the repository
   git clone https://github.com/vaishnavi-p-mali/bank-management-system.git
   cd bank-management-system

2. Create the MySQL database
   CREATE DATABASE bank_management;

3. Configure application properties
   - Copy src/main/resources/application.properties.example to src/main/resources/application.properties
   - Update the spring.datasource.password field with your MySQL password

4. Run the application
   ./mvnw spring-boot:run

5. Access the application
   - Customer Portal: http://localhost:8080/
   - Admin Login: http://localhost:8080/admin-login

## 🔑 Default Admin Credentials

Set up your own admin account directly in the database:

INSERT INTO admin (email, password) VALUES ('admin@example.com', 'your-bcrypt-hashed-password');

## 📸 Screenshots

(Add screenshots of your customer dashboard, admin dashboard, etc. here if you'd like)

## 🚀 Future Improvements

- Email-based password reset (currently token-based, displayed on-screen for demo purposes)
- Fund transfers between accounts
- PDF/CSV export of transaction history
- Admin ability to freeze/unfreeze accounts

## 👩‍💻 Author

Built by Vaishnavi Mali as a full-stack learning project using Spring Boot and MySQL.