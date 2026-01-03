package com.bank.main;

import com.bank.dao.BankManager;
import com.bank.model.User;
import java.util.Scanner;
import java.util.regex.Pattern;

public class BankingApp {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        BankManager bankManager = new BankManager();

        while (true) {
            System.out.println("\n--- JAVA BANKING SYSTEM ---");
            System.out.println("1. Register\n2. Login\n3. Exit");
            System.out.print("Enter choice: ");

            if (!sc.hasNextInt()) { System.out.println("Invalid input!"); sc.nextLine(); continue; }
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                // Inside com.bank.main.BankingApp.java

                case 1:
                    // --- REGISTRATION ---
                    String name, email, pass, dob, address, accountType = "Savings";

                    // 1. Name
                    while (true) {
                        System.out.print("Enter Full Name: ");
                        name = sc.nextLine().trim();
                        if (!name.isEmpty()) break;
                    }

                    // 2. Email (Existing logic)
                    while (true) {
                        System.out.print("Enter Email: ");
                        email = sc.nextLine().trim();
                        if (isValidEmail(email)) break;
                        System.out.println("❌ Invalid Email!");
                    }

                    // 3. Password
                    while (true) {
                        System.out.print("Enter Password: ");
                        pass = sc.nextLine().trim();
                        if (!pass.isEmpty()) break;
                    }

                    // 4. Date of Birth (NEW)
                    while (true) {
                        System.out.print("Enter Date of Birth (YYYY-MM-DD): ");
                        dob = sc.nextLine().trim();
                        if (!dob.isEmpty()) break;
                        // Note: You can add strict Regex validation for date here if you want later
                    }

                    // 5. Address (NEW)
                    while (true) {
                        System.out.print("Enter Address: ");
                        address = sc.nextLine().trim();
                        if (!address.isEmpty()) break;
                    }

                    // 6. Account Type
                    System.out.println("Select Account Type:\n1. Savings\n2. Current");
                    System.out.print("Enter Choice: ");
                    if (sc.hasNextInt()) {
                        int typeChoice = sc.nextInt();
                        sc.nextLine(); // Consume newline
                        if (typeChoice == 2) accountType = "Current";
                    } else {
                        sc.nextLine(); // clear invalid input
                    }

                    // Call the updated register method
                    bankManager.registerUser(name, email, pass, accountType, dob, address);
                    break;

                case 2:
                    System.out.print("Enter Email: ");
                    String loginEmail = sc.nextLine().trim();
                    System.out.print("Enter Password: ");
                    String loginPass = sc.nextLine().trim();
                    User user = bankManager.loginUser(loginEmail, loginPass);

                    if (user != null) {
                        System.out.println("✅ Login Successful! Welcome, " + user.getFullName());
                        boolean loggedIn = true;
                        while (loggedIn) {
                            System.out.println("\n--- DASHBOARD ---");
                            System.out.println("1. Credit Money");
                            System.out.println("2. Transfer Money");
                            System.out.println("3. Check Balance");
                            System.out.println("4. Transaction History"); // NEW
                            System.out.println("5. Apply for Loan"); // NEW
                            System.out.println("6. Logout");
                            System.out.print("Enter choice: ");

                            if (!sc.hasNextInt()) { sc.nextLine(); continue; }
                            int userChoice = sc.nextInt();
                            sc.nextLine();

                            switch (userChoice) {
                                case 1:
                                    System.out.print("Enter Amount: ");
                                    if(sc.hasNextDouble()) bankManager.credit(user.getAccountNumber(), sc.nextDouble());
                                    break;
                                case 2:
                                    // --- SIMPLIFIED TRANSFER (NO 12-DIGIT CHECK) ---
                                    System.out.print("Enter Receiver Account No: ");
                                    long receiverAcc = sc.nextLong(); // Just accept whatever number they type

                                    System.out.print("Enter Amount: ");
                                    double transferAmt = sc.nextDouble();

                                    bankManager.transferMoney(user.getAccountNumber(), receiverAcc, transferAmt);
                                    break;
                                case 3:
                                    System.out.printf("✅ Current Balance: Rs. %.2f%n", bankManager.getBalance(user.getAccountNumber()));
                                    break;
                                case 4:
                                    // NEW: Show History
                                    bankManager.printTransactionHistory(user.getAccountNumber());
                                    break;
                                case 5:
                                    // NEW: Check Loan Logic
                                    bankManager.checkLoanEligibility(user.getAccountNumber());
                                    break;
                                case 6:
                                    loggedIn = false;
                                    System.out.println("Logged out.");
                                    break;
                                default:
                                    System.out.println("Invalid Choice!");
                            }
                        }
                    } else {
                        System.out.println("❌ Invalid Credentials!");
                    }
                    break;
                case 3:
                    System.out.println("Exiting...");
                    sc.close();
                    return;
                default:
                    System.out.println("Invalid option!");
            }
        }
    }

    private static boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        Pattern pattern = Pattern.compile(emailRegex);
        return email != null && pattern.matcher(email).matches();
    }
}