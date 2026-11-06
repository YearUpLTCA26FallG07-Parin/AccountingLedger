package com.pluralsight;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Scanner;

public class AccountingLedgerApp {

    public static void main(String[] args) {

        Scanner keyboard = new Scanner(System.in);
        String choice = "";

        ArrayList<Transaction> transactions = new ArrayList<>();

        // Load existing transactions from CSV
        loadTransactions(transactions);

        while (!choice.equalsIgnoreCase("X")) {

            //Display Menu
            System.out.println("\nHome Screen");
            System.out.println("D) Add Deposit");
            System.out.println("P) Make Payment");
            System.out.println("L) Ledger");
            System.out.println("X) Exit");

            //Get User Choice
            System.out.print("Enter Choice: ");
            choice = keyboard.nextLine();

            //Switch Statement
            switch (choice.toUpperCase()) {

                case "D": {

                    System.out.println("Add Deposit");

                    //Ask for Description
                    System.out.print("Description: ");
                    String description = keyboard.nextLine();

                    //Ask for Vendor
                    System.out.print("Vendor: ");
                    String vendor = keyboard.nextLine();

                    //Ask for Amount
                    System.out.print("Amount: ");
                    double amount = Double.parseDouble(keyboard.nextLine());

                    //Date
                    String date = LocalDate.now().toString();

                    //Time
                    String time = LocalTime.now().toString();

                    //New Transaction
                    Transaction deposit = new Transaction(
                            date,
                            time,
                            description,
                            vendor,
                            amount
                    );

                    transactions.add(deposit);
                    saveTransaction(deposit);

                    //Confirm
                    System.out.println("Transaction Added!");
                    System.out.println("Total Transactions: " + transactions.size());

                    break;
                }

                case "P": {

                    System.out.println("Make Payment");

                    //Ask for Description
                    System.out.print("Description: ");
                    String description = keyboard.nextLine();

                    //Ask for Vendor
                    System.out.print("Vendor: ");
                    String vendor = keyboard.nextLine();

                    //Ask for Amount
                    System.out.print("Amount: ");
                    double amount = Double.parseDouble(keyboard.nextLine());

                    //Make Amount Negative
                    amount = -Math.abs(amount);

                    //Get Date
                    String date = LocalDate.now().toString();

                    //Get Time
                    String time = LocalTime.now().toString();

                    //Create Transaction
                    Transaction payment = new Transaction(
                            date,
                            time,
                            description,
                            vendor,
                            amount
                    );

                    transactions.add(payment);
                    saveTransaction(payment);

                    //Confirmation
                    System.out.println("Payment Added!");
                    System.out.println("Total Transactions: " + transactions.size());

                    break;
                }

                case "L":

                    System.out.println("\nLedger");
                    System.out.println("A) All");
                    System.out.println("D) Deposits");
                    System.out.println("P) Payments");
                    System.out.println("H) Home");

                    System.out.print("Enter Choice: ");
                    String ledgerChoice = keyboard.nextLine();

                    switch(ledgerChoice.toUpperCase()){
                        case "A":

                            for (Transaction t : transactions) {
                                System.out.println(
                                        t.getDate() + " | " +
                                                t.getTime() + " | " +
                                                t.getDescription() + " | " +
                                                t.getVendor() + " | " +
                                                t.getAmount()
                                );
                            }
                            break;

                        case "D":

                            for (Transaction t : transactions) {
                                if (t.getAmount() > 0) {
                                    System.out.println(
                                            t.getDate() + " | " +
                                                    t.getTime() + " | " +
                                                    t.getDescription() + " | " +
                                                    t.getVendor() + " | " +
                                                    t.getAmount()
                                    );
                                }
                            }
                            break;

                        case "P":

                            for (Transaction t : transactions) {
                                if (t.getAmount() < 0) {
                                    System.out.println(
                                            t.getDate() + " | " +
                                                    t.getTime() + " | " +
                                                    t.getDescription() + " | " +
                                                    t.getVendor() + " | " +
                                                    t.getAmount()
                                    );
                                }
                            }
                            break;

                        case "H":
                            break;

                        default:
                            System.out.println("Invalid Choice");
                    }
                    break;


                case "X":
                    System.out.println("Goodbye");
                    break;

                default:
                    System.out.println("Invalid Choice");
            }
        }

        keyboard.close();
    }

    // Load Transactions Method
    public static void loadTransactions(ArrayList<Transaction> transactions) {

        try {

            BufferedReader reader =
                    new BufferedReader(new FileReader("transactions.csv"));

            reader.readLine(); // Skip header row

            String line;

            while ((line = reader.readLine()) != null) {

                String[] parts = line.split("\\|");

                String date = parts[0];
                String time = parts[1];
                String description = parts[2];
                String vendor = parts[3];
                double amount = Double.parseDouble(parts[4]);

                Transaction transaction = new Transaction(
                        date,
                        time,
                        description,
                        vendor,
                        amount
                );

                transactions.add(transaction);
            }

            reader.close();

        } catch (IOException e) {
            System.out.println("Could not load transactions.");
        }
    }

    // Save Transaction Method
    public static void saveTransaction(Transaction transaction) {

        try {

            FileWriter writer = new FileWriter("transactions.csv", true);

            writer.write(
                    transaction.getDate() + "|" +
                            transaction.getTime() + "|" +
                            transaction.getDescription() + "|" +
                            transaction.getVendor() + "|" +
                            transaction.getAmount() + "\n"
            );

            writer.close();

        } catch (IOException e) {
            System.out.println("Error saving transaction.");
        }
    }
}