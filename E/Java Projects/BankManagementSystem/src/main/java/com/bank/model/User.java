package com.bank.model;

public class User {
    private long accountNumber;
    private String fullName;
    private String email;
    private double balance;

    // Constructors, Getters, and Setters
    public User(long accountNumber, String fullName, String email, double balance) {
        this.accountNumber = accountNumber;
        this.fullName = fullName;
        this.email = email;
        this.balance = balance;
    }

    public long getAccountNumber() { return accountNumber; }
    public String getFullName() { return fullName; }
    public double getBalance() { return balance; }
}