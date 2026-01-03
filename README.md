# Bank-Management-System

🏦 Java Console Banking SystemA robust, console-based banking simulation built with Java and MySQL. This application replicates core banking functionalities including secure user authentication, fund transfers, transaction history, and loan eligibility checks, engineered with a focus on data integrity and security.🚀 Key FeaturesUser Onboarding: Secure registration capturing KYC details (Name, DOB, Address) and Account Type (Savings/Current).Secure Authentication: Login system protected by SHA-256 hashing (passwords are never stored in plain text).Banking Operations:Credit/Deposit: Add funds to the account.Fund Transfer: Transfer money to other accounts with 12-digit account number validation.Balance Check: Real-time fetching from the database (Single Source of Truth).Mini-Statement: View the last 10 transactions with timestamps.Loan Eligibility: smart algorithm that checks balance and transaction history to determine loan approval.🛠️ Tech Stack & Concepts UsedThis project goes beyond basic coding to implement industry-standard software engineering practices:ConceptImplementationLanguageJava (Core, OOPs, Collections, Regex)DatabaseMySQL (Relational Data Persistence)ConnectivityJDBC (Java Database Connectivity)Build ToolApache MavenDesign PatternsSingleton (DB Connection), DAO (Data Access Object)SecuritySHA-256 Hashing for credentialsData IntegrityACID Properties (Commit/Rollback) for safe money transfers⚙️ How to Run This ProjectPrerequisitesJava Development Kit (JDK) 8 or higher.MySQL Server installed locally.Maven (or an IDE like IntelliJ IDEA/Eclipse).Step 1: Database SetupOpen your MySQL Workbench or Terminal and run the following script to initialize the database:SQLCREATE DATABASE bank_db;
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
Step 2: Configure Database ConnectionNavigate to src/main/java/com/bank/util/DBConnection.java.Update your MySQL credentials:Javaprivate static final String USER = "root";
private static final String PASSWORD = "YOUR_MYSQL_PASSWORD";
Step 3: Run the ApplicationOpen the project in IntelliJ IDEA.Navigate to src/main/java/com/bank/main/BankingApp.java.Right-click and select Run 'BankingApp.main()'.📸 Usage Example1. Registration:PlaintextEnter Name: John Doe
Enter Email: john@example.com
Enter Password: *****
Enter Date of Birth: 1999-01-01
Enter Address: 123 Baker St
...
🎉 YOUR ACCOUNT NUMBER IS: 100000000012
2. Money Transfer:PlaintextEnter Receiver Account No (12 Digits): 100000000015
Enter Amount: 5000
✅ Transfer Successful! Rs. 5000 sent to 100000000015
🛡️ "Standout" Engineering HighlightsACID Transactions: The transfer money feature uses connection.setAutoCommit(false). If the money leaves the sender but fails to reach the receiver (e.g., system crash), the database automatically Rolls Back to prevent data inconsistency.Input Validation: Uses Regex for email validation and strict checks for 12-digit account numbers to prevent bad data entry.Security: Utilizes MessageDigest to encrypt passwords before storing them in the database.
