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

            // Fix: Handle non-integer inputs gracefully
            if (!sc.hasNextInt()) {
                System.out.println("Invalid input! Please enter a number.");
                sc.nextLine(); // Clear buffer
                continue;
            }
            int choice = sc.nextInt();
            sc.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    // --- REGISTRATION VALIDATION LOGIC START ---
                    String name, email, pass;

                    // 1. Validate Name (Cannot be empty)
                    while (true) {
                        System.out.print("Enter Name: ");
                        name = sc.nextLine().trim(); // .trim() removes spaces
                        if (!name.isEmpty()) break;
                        System.out.println("❌ Name cannot be empty!");
                    }

                    // 2. Validate Email (Must look like an email)
                    while (true) {
                        System.out.print("Enter Email: ");
                        email = sc.nextLine().trim();
                        if (isValidEmail(email)) break;
                        System.out.println("❌ Invalid Email! (Format: example@mail.com)");
                    }

                    // 3. Validate Password (Cannot be empty)
                    while (true) {
                        System.out.print("Enter Password: ");
                        pass = sc.nextLine().trim();
                        if (!pass.isEmpty()) break;
                        System.out.println("❌ Password cannot be empty!");
                    }
                    // --- REGISTRATION VALIDATION LOGIC END ---

                    bankManager.registerUser(name, email, pass);
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
                            System.out.println("\n1. Credit Money\n2. Transfer Money\n3. Check Balance\n4. Logout");
                            System.out.print("Enter choice: ");

                            if (!sc.hasNextInt()) {
                                System.out.println("Invalid input! Enter a number.");
                                sc.nextLine();
                                continue;
                            }
                            int userChoice = sc.nextInt();
                            sc.nextLine(); // consume newline after int

                            switch (userChoice) {
                                case 1:
                                    System.out.print("Enter Amount: ");
                                    if(sc.hasNextDouble()) {
                                        double amount = sc.nextDouble();
                                        bankManager.credit(user.getAccountNumber(), amount);
                                    } else {
                                        System.out.println("Invalid Amount!");
                                        sc.next(); // clear buffer
                                    }
                                    break;

                                case 2:
                                    // --- VALIDATION FOR 12-DIGIT ACCOUNT NUMBER ---
                                    long receiverAcc;
                                    while (true) {
                                        System.out.print("Enter Receiver Account No (12 Digits): ");
                                        if (sc.hasNextLong()) {
                                            receiverAcc = sc.nextLong();
                                            // Check length by converting to String
                                            if (String.valueOf(receiverAcc).length() == 12) {
                                                break; // Input is valid, exit loop
                                            } else {
                                                System.out.println("❌ Invalid Length! Account number must be exactly 12 digits.");
                                            }
                                        } else {
                                            System.out.println("❌ Invalid input! Please enter numbers only.");
                                            sc.next(); // Clear invalid input from scanner buffer
                                        }
                                    }

                                    System.out.print("Enter Amount: ");
                                    if(sc.hasNextDouble()) {
                                        double transferAmt = sc.nextDouble();
                                        bankManager.transferMoney(user.getAccountNumber(), receiverAcc, transferAmt);
                                    } else {
                                        System.out.println("Invalid Amount!");
                                        sc.next();
                                    }
                                    break;

                                case 3:
                                    // FETCH FRESH BALANCE FROM DB (Single Source of Truth)
                                    // Ensure you added getBalance() to BankManager.java per previous instructions
                                    double currentBalance = bankManager.getBalance(user.getAccountNumber());
                                    System.out.printf("✅ Current Balance: Rs. %.2f%n", currentBalance);
                                    break;

                                case 4:
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
                    System.out.println("Thank you for using Java Bank.");
                    sc.close();
                    return;

                default:
                    System.out.println("Invalid option! Try again.");
            }
        }
    }

    // --- HELPER METHOD FOR EMAIL VALIDATION ---
    private static boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        Pattern pattern = Pattern.compile(emailRegex);
        return email != null && pattern.matcher(email).matches();
    }
}