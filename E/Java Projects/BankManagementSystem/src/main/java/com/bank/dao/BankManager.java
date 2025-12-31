package com.bank.dao;

import com.bank.model.User;
import com.bank.util.DBConnection;
import com.bank.util.SecurityUtil;
import java.sql.*;

public class BankManager {


    // Inside com.bank.dao.BankManager.java

    public double getBalance(long accountNumber) {
        String sql = "SELECT balance FROM users WHERE account_number = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("balance");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Error code
    }
    // 1. REGISTER USER
    public void registerUser(String name, String email, String password) {
        String sql = "INSERT INTO users (full_name, email, password_hash, balance) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, SecurityUtil.hashPassword(password)); // Hashing!
            pstmt.setDouble(4, 0.0);

            int rows = pstmt.executeUpdate();
            if (rows > 0) System.out.println("Registration Successful!");
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

    // 3. CREDIT MONEY (DEPOSIT)
    public void credit(long accountNum, double amount) {
        // Logic to update balance and log transaction
        String updateSql = "UPDATE users SET balance = balance + ? WHERE account_number = ?";
        String logSql = "INSERT INTO transactions (account_number, type, amount) VALUES (?, 'CREDIT', ?)";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false); // Start Transaction

            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                 PreparedStatement logStmt = conn.prepareStatement(logSql)) {

                updateStmt.setDouble(1, amount);
                updateStmt.setLong(2, accountNum);
                updateStmt.executeUpdate();

                logStmt.setLong(1, accountNum);
                logStmt.setDouble(2, amount);
                logStmt.executeUpdate();

                conn.commit(); // Commit Changes
                System.out.println("Rs. " + amount + " Credited Successfully.");
            } catch (SQLException e) {
                conn.rollback(); // Rollback if error
                System.out.println("Transaction Failed!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 4. TRANSFER MONEY (The "Standout" Feature)
    public void transferMoney(long senderAcc, long receiverAcc, double amount) {
        String checkBalanceSql = "SELECT balance FROM users WHERE account_number = ?";
        String debitSql = "UPDATE users SET balance = balance - ? WHERE account_number = ?";
        String creditSql = "UPDATE users SET balance = balance + ? WHERE account_number = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false); // ACID Property: All or Nothing

            // Check Sender Balance
            PreparedStatement checkStmt = conn.prepareStatement(checkBalanceSql);
            checkStmt.setLong(1, senderAcc);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                double currentBalance = rs.getDouble("balance");
                if (currentBalance < amount) {
                    System.out.println("Insufficient Balance!");
                    return;
                }
            }

            // Debit Sender
            PreparedStatement debitStmt = conn.prepareStatement(debitSql);
            debitStmt.setDouble(1, amount);
            debitStmt.setLong(2, senderAcc);
            debitStmt.executeUpdate();

            // Credit Receiver
            PreparedStatement creditStmt = conn.prepareStatement(creditSql);
            creditStmt.setDouble(1, amount);
            creditStmt.setLong(2, receiverAcc);
            creditStmt.executeUpdate();

            conn.commit();
            System.out.println("Transfer Successful!");

        } catch (SQLException e) {
            System.out.println("Transfer Failed. Money refunded.");
            e.printStackTrace();
        }
    }
}