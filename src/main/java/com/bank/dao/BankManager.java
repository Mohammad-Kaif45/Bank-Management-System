package com.bank.dao;

import com.bank.model.User;
import com.bank.util.DBConnection;
import com.bank.util.SecurityUtil;
import java.sql.*;

public class BankManager {

    // 1. REGISTER USER (Now with Account Type)
    // Inside com.bank.dao.BankManager.java

    public void registerUser(String name, String email, String password, String accountType, String dob, String address) {
        // Updated SQL to include dob and address
        String sql = "INSERT INTO users (full_name, email, password_hash, balance, account_type, dob, address) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, SecurityUtil.hashPassword(password));
            pstmt.setDouble(4, 0.0);
            pstmt.setString(5, accountType);
            pstmt.setString(6, dob);     // New Field
            pstmt.setString(7, address); // New Field

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        long accountNumber = generatedKeys.getLong(1);
                        System.out.println("✅ Registration Successful!");
                        System.out.println("🎉 YOUR ACCOUNT NUMBER IS: " + accountNumber);
                        System.out.println("⚠️  Please save this number for login.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 2. LOGIN USER
    public User loginUser(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ? AND password_hash = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            pstmt.setString(2, SecurityUtil.hashPassword(password));
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new User(rs.getLong("account_number"), rs.getString("full_name"), rs.getString("email"), rs.getDouble("balance"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 3. GET BALANCE
    public double getBalance(long accountNumber) {
        String sql = "SELECT balance FROM users WHERE account_number = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getDouble("balance");
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }

    // 4. CREDIT MONEY
    public void credit(long accountNum, double amount) {
        String updateSql = "UPDATE users SET balance = balance + ? WHERE account_number = ?";
        String logSql = "INSERT INTO transactions (account_number, type, amount) VALUES (?, 'CREDIT', ?)";
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                 PreparedStatement logStmt = conn.prepareStatement(logSql)) {

                updateStmt.setDouble(1, amount);
                updateStmt.setLong(2, accountNum);
                updateStmt.executeUpdate();

                logStmt.setLong(1, accountNum);
                logStmt.setDouble(2, amount);
                logStmt.executeUpdate();

                conn.commit();
                System.out.println("✅ Rs. " + amount + " Credited Successfully.");
            } catch (SQLException e) { conn.rollback(); e.printStackTrace(); }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // 5. TRANSFER MONEY
    // 5. TRANSFER MONEY (Fixed)
    public void transferMoney(long senderAcc, long receiverAcc, double amount) {
        // Check 1: Sender Balance
        if (getBalance(senderAcc) < amount) {
            System.out.println("❌ Insufficient Balance!");
            return;
        }

        // Check 2: Receiver Exists (THE FIX)
        if (!userExists(receiverAcc)) {
            System.out.println("❌ Transfer Failed: Account " + receiverAcc + " does not exist!");
            return;
        }

        // If checks pass, proceed with ACID Transaction
        String debitSql = "UPDATE users SET balance = balance - ? WHERE account_number = ?";
        String creditSql = "UPDATE users SET balance = balance + ? WHERE account_number = ?";
        String logSender = "INSERT INTO transactions (account_number, type, amount) VALUES (?, 'DEBIT', ?)";
        String logReceiver = "INSERT INTO transactions (account_number, type, amount) VALUES (?, 'CREDIT', ?)";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false); // Begin Transaction

            try (PreparedStatement debitStmt = conn.prepareStatement(debitSql);
                 PreparedStatement creditStmt = conn.prepareStatement(creditSql);
                 PreparedStatement logSendStmt = conn.prepareStatement(logSender);
                 PreparedStatement logRecStmt = conn.prepareStatement(logReceiver)) {

                // 1. Debit Sender
                debitStmt.setDouble(1, amount);
                debitStmt.setLong(2, senderAcc);
                debitStmt.executeUpdate();

                // 2. Log Sender Transaction
                logSendStmt.setLong(1, senderAcc);
                logSendStmt.setDouble(2, amount);
                logSendStmt.executeUpdate();

                // 3. Credit Receiver
                creditStmt.setDouble(1, amount);
                creditStmt.setLong(2, receiverAcc);
                creditStmt.executeUpdate();

                // 4. Log Receiver Transaction
                logRecStmt.setLong(1, receiverAcc);
                logRecStmt.setDouble(2, amount);
                logRecStmt.executeUpdate();

                conn.commit(); // Commit all changes
                System.out.println("✅ Transfer Successful! Rs." + amount + " sent to " + receiverAcc);

            } catch (SQLException e) {
                conn.rollback(); // Rollback on error
                System.out.println("❌ Transaction Failed! Money refunded.");
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 6. SHOW TRANSACTION HISTORY (New Feature)
    public void printTransactionHistory(long accountNum) {
        String sql = "SELECT * FROM transactions WHERE account_number = ? ORDER BY timestamp DESC LIMIT 10";
        System.out.println("\n--- 📜 LAST 10 TRANSACTIONS ---");
        System.out.printf("%-15s %-10s %-10s %-20s%n", "ID", "Type", "Amount", "Date");
        System.out.println("------------------------------------------------------");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, accountNum);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                System.out.printf("%-15d %-10s Rs.%-9.2f %-20s%n",
                        rs.getInt("transaction_id"),
                        rs.getString("type"),
                        rs.getDouble("amount"),
                        rs.getString("timestamp").substring(0, 19)); // Cut off milliseconds
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public boolean userExists(long accountNum) {
        String sql = "SELECT account_number FROM users WHERE account_number = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, accountNum);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    // 7. CHECK LOAN ELIGIBILITY (New Feature)
    public void checkLoanEligibility(long accountNum) {
        // Condition: Balance > 10,000 AND Transactions > 10
        String countSql = "SELECT COUNT(*) AS total FROM transactions WHERE account_number = ?";
        double currentBalance = getBalance(accountNum);
        int transactionCount = 0;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(countSql)) {
            pstmt.setLong(1, accountNum);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) transactionCount = rs.getInt("total");
        } catch (SQLException e) { e.printStackTrace(); }

        System.out.println("\n--- 🏦 LOAN ELIGIBILITY CHECK ---");
        System.out.println("Current Balance: " + currentBalance);
        System.out.println("Total Transactions: " + transactionCount);

        if (currentBalance >= 10000 && transactionCount > 10) {
            System.out.println("✅ CONGRATULATIONS! You are eligible for a loan.");
            System.out.println("Pre-approved amount: Rs. " + (currentBalance * 2)); // Example logic
        } else {
            System.out.println("❌ NOT ELIGIBLE.");
            if (currentBalance < 10000) System.out.println("- Reason: Balance must be > Rs. 10,000");
            if (transactionCount <= 10) System.out.println("- Reason: You need more than 10 transactions.");
        }
    }
}