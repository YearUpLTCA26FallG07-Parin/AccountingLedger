package com.pluralsight;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Scanner;


public class AccountingLedgerApp {


    public static void main(String[] args) {

        Scanner keyboard = new Scanner(System.in);
        String choice = "";


        ArrayList<Transaction> transactions = new ArrayList<>();


        while (!choice.equalsIgnoreCase("X")) {

            // display menu
            System.out.println("\nHome Screen");
            System.out.println("D) Add Deposit");
            System.out.println("P) Make Payment");
            System.out.println("L) Ledger");
            System.out.println("X) Exit");

            // get user choice
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

                    //Ask for amount
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
                    //Ask Amount
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
                    //Confirmation
                    transactions.add(payment);
                    System.out.println("Payment Added!");
                    break;
                }
                case "L":
                    System.out.println("Ledger");

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

                case "X":
                    System.out.println("Goodbye");
                    break;

                default:
                    System.out.println("Invalid Choice");


            }
        }

    }
}