# Bank-Management-System

# 🏦 Java Console Banking System

A robust, console-based banking simulation built with **Java** and **MySQL**. This application replicates core banking functionalities including secure user authentication, fund transfers, transaction history, and loan eligibility checks, engineered with a focus on data integrity and security.

## 🚀 Key Features

* **User Onboarding:** Secure registration capturing KYC details (Name, DOB, Address) and Account Type (Savings/Current).
* **Secure Authentication:** Login system protected by **SHA-256 hashing** (passwords are never stored in plain text).
* **Banking Operations:**
    * **Credit/Deposit:** Add funds to the account.
    * **Fund Transfer:** Transfer money to other accounts with **12-digit account number validation**.
    * **Balance Check:** Real-time fetching from the database (Single Source of Truth).
* **Mini-Statement:** View the last 10 transactions with timestamps.
* **Loan Eligibility:** Smart algorithm that checks balance and transaction history to determine loan approval.

## 🛠️ Tech Stack & Concepts Used

This project goes beyond basic coding to implement industry-standard software engineering practices:

| Concept | Implementation |
| :--- | :--- |
| **Language** | Java (Core, OOPs, Collections, Regex) |
| **Database** | MySQL (Relational Data Persistence) |
| **Connectivity** | JDBC (Java Database Connectivity) |
| **Build Tool** | Apache Maven |
| **Design Patterns** | **Singleton** (DB Connection), **DAO** (Data Access Object) |
| **Security** | **SHA-256 Hashing** for credentials |
| **Data Integrity** | **ACID Properties** (Commit/Rollback) for safe money transfers |

## ⚙️ How to Run This Project

### Prerequisites
* Java Development Kit (JDK) 8 or higher.
* MySQL Server installed locally.
* Maven (or an IDE like IntelliJ IDEA/Eclipse).

### Step 1: Database Setup
Open your MySQL Workbench or Terminal and run the following script to initialize the database:

```sql
CREATE DATABASE bank_db;
USE bank_db;

-- Create Users Table (Starts from 12-digit Account Number)
CREATE TABLE users (
    account_number BIGINT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(100),
    email VARCHAR(100) UNIQUE,
    password_hash VARCHAR(256),
    balance DECIMAL(10,2) DEFAULT 0.00,
    account_type VARCHAR(20),
    dob VARCHAR(20),
    address VARCHAR(100)
) AUTO_INCREMENT = 100000000000;

-- Create Transactions Table
CREATE TABLE transactions (
    transaction_id INT PRIMARY KEY AUTO_INCREMENT,
    account_number BIGINT,
    type VARCHAR(10),
    amount DECIMAL(10,2),
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_number) REFERENCES users(account_number)
);

Step 2: Configure Database Connection
Navigate to src/main/java/com/bank/util/DBConnection.java.

Update your MySQL credentials:

private static final String USER = "root";
private static final String PASSWORD = "YOUR_MYSQL_PASSWORD";

Step 3: Run the Application
Open the project in IntelliJ IDEA.

Navigate to src/main/java/com/bank/main/BankingApp.java.

Right-click and select Run 'BankingApp.main()'.

"Standout" Engineering Highlights
ACID Transactions: The transfer money feature uses connection.setAutoCommit(false). If the money leaves the sender but fails to reach the receiver (e.g., system crash), the database automatically Rolls Back to prevent data inconsistency.

Input Validation: Uses Regex for email validation and strict checks for 12-digit account numbers to prevent bad data entry.

Security: Utilizes MessageDigest to encrypt passwords before storing them in the database.

Created by Mohammad Kaif
