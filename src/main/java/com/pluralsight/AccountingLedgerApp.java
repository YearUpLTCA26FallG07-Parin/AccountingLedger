import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Scanner;
import java.util.function.Predicate;

public class AccountingLedgerApp {
    private static final File FILE = new File("transactions.csv");

    static void main(String[] args) {
        ArrayList<Transaction> transactions = new ArrayList<>();
        // Load existing transactions from CSV
        loadTransactions(transactions);

        try (Scanner keyboard = new Scanner(System.in)) {
            while (true) {
                //Display Menu
                System.out.println("\nHome Screen\nD) Add Deposit\nP) Make Payment (Debit)\nL) Ledger\nX) Exit");

                //Get User Choice
                String choice = readChoice(keyboard);

                //Switch Statement
                switch (choice) {
                    case "D":
                        addTransaction(keyboard, transactions, false);
                        break;
                    case "P":
                        addTransaction(keyboard, transactions, true);
                        break;
                    case "L":
                        showLedger(keyboard, transactions);
                        break;
                    case "X":
                        System.out.println("Goodbye");
                        return;
                    default:
                        System.out.println("Invalid Choice");
                }
            }
        }
    }

    private static String readChoice(Scanner keyboard) {
        System.out.print("Enter Choice: ");
        return keyboard.nextLine().trim().toUpperCase(Locale.ROOT);
    }

    private static void addTransaction(Scanner keyboard, ArrayList<Transaction> transactions,
                                       boolean isPayment) {
        System.out.println(isPayment ? "Make Payment" : "Add Deposit");

        //Ask for Description
        System.out.print("Description: ");
        String description = keyboard.nextLine();

        //Ask for Vendor
        System.out.print("Vendor: ");
        String vendor = keyboard.nextLine();

        //Ask for Amount
        System.out.print("Amount: ");
        double amount;
        try {
            amount = Double.parseDouble(keyboard.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Enter a valid number.");
            return;
        }
        if (!Double.isFinite(amount) || amount == 0 || (!isPayment && amount < 0)) {
            System.out.println(isPayment ? "Enter a finite, nonzero amount." : "Enter a positive deposit amount.");
            return;
        }

        if (isPayment) {
            //Make Amount Negative
            amount = -Math.abs(amount);
        }

        LocalDateTime now = LocalDateTime.now();
        //Date
        //Get Date
        String date = now.toLocalDate().toString();
        //Time
        //Get Time
        String time = now.toLocalTime().toString();
        //New Transaction
        //Create Transaction
        Transaction transaction = new Transaction(date, time, description, vendor, amount);
        if (saveTransaction(transaction)) {
            transactions.add(transaction);
            if (isPayment) {
                //Confirmation
                System.out.println("Payment Added!");
            } else {
                //Confirm
                System.out.println("Transaction Added!");
            }
            System.out.println("Total Transactions: " + transactions.size());
        }
    }

    private static void showLedger(Scanner keyboard, ArrayList<Transaction> transactions) {
        while (true) {
            System.out.println("\nLedger\nA) All\nD) Deposits\nP) Payments\nR) Reports\nH) Home");
            switch (readChoice(keyboard)) {
                case "A":
                    displayEntries(transactions, transaction -> true);
                    break;
                case "D":
                    displayEntries(transactions, transaction -> transaction.getAmount() > 0);
                    break;
                case "P":
                    displayEntries(transactions, transaction -> transaction.getAmount() < 0);
                    break;
                case "R":
                    showReports(keyboard, transactions);
                    break;
                case "H":
                    return;
                default:
                    System.out.println("Invalid Choice");
            }
        }
    }

    private static void showReports(Scanner keyboard, ArrayList<Transaction> transactions) {
        while (true) {
            System.out.println("\nReports\n1) Month To Date\n2) Previous Month\n3) Year To Date");
            System.out.println("4) Previous Year\n5) Search by Vendor\n6) Custom Search\n0) Back");
            LocalDate today = LocalDate.now();
            LocalDate monthStart = today.withDayOfMonth(1);
            LocalDate yearStart = today.withDayOfYear(1);

            switch (readChoice(keyboard)) {
                case "1":
                    showDateRange(transactions, monthStart, today);
                    break;
                case "2":
                    showDateRange(transactions, monthStart.minusMonths(1), monthStart.minusDays(1));
                    break;
                case "3":
                    showDateRange(transactions, yearStart, today);
                    break;
                case "4":
                    showDateRange(transactions, yearStart.minusYears(1), yearStart.minusDays(1));
                    break;
                case "5":
                    System.out.print("Vendor Name: ");
                    String vendor = keyboard.nextLine().trim();
                    displayEntries(transactions, transaction -> transaction.getVendor().equalsIgnoreCase(vendor));
                    break;
                case "6":
                    customSearch(keyboard, transactions);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid Choice");
            }
        }
    }

    private static void showDateRange(ArrayList<Transaction> transactions, LocalDate start, LocalDate end) {
        displayEntries(transactions, transaction -> matchesDates(transaction, start, end));
    }

    private static boolean matchesDates(Transaction transaction, LocalDate start, LocalDate end) {
        LocalDate date = LocalDate.parse(transaction.getDate());
        return (start == null || !date.isBefore(start)) && (end == null || !date.isAfter(end));
    }

    private static void customSearch(Scanner keyboard, ArrayList<Transaction> transactions) {
        System.out.println("Press Enter to skip a field. Use a negative amount to search for a payment.");
        System.out.print("Start Date (YYYY-MM-DD): ");
        String startText = keyboard.nextLine().trim();
        System.out.print("End Date (YYYY-MM-DD): ");
        String endText = keyboard.nextLine().trim();
        System.out.print("Description: ");
        String description = keyboard.nextLine().trim();
        System.out.print("Vendor: ");
        String vendor = keyboard.nextLine().trim();
        System.out.print("Amount: ");
        String amountText = keyboard.nextLine().trim();

        try {
            LocalDate start = startText.isEmpty() ? null : LocalDate.parse(startText);
            LocalDate end = endText.isEmpty() ? null : LocalDate.parse(endText);
            Double amount = amountText.isEmpty() ? null : Double.parseDouble(amountText);

            if (start != null && end != null && start.isAfter(end)) {
                System.out.println("Start date must be on or before end date.");
                return;
            }
            if (amount != null && !Double.isFinite(amount)) {
                System.out.println("Enter a finite amount.");
                return;
            }
            displayEntries(transactions, transaction ->
                    matchesDates(transaction, start, end)
                            && (description.isEmpty() || transaction.getDescription().equalsIgnoreCase(description))
                            && (vendor.isEmpty() || transaction.getVendor().equalsIgnoreCase(vendor))
                            && (amount == null || transaction.getAmount() == amount));
        } catch (DateTimeParseException | NumberFormatException e) {
            System.out.println("Enter dates as YYYY-MM-DD and use a number for the amount.");
        }
    }

    private static void displayEntries(ArrayList<Transaction> transactions, Predicate<Transaction> filter) {
        ArrayList<Transaction> matches = new ArrayList<>();
        for (Transaction transaction : transactions) {
            if (filter.test(transaction)) {
                matches.add(transaction);
            }
        }
        Collections.reverse(matches);
        matches.sort((first, second) -> timestamp(second).compareTo(timestamp(first)));

        if (matches.isEmpty()) {
            System.out.println("No matching transactions.");
        }
        for (Transaction transaction : matches) {
            System.out.println(transaction.getDate() + " | " + transaction.getTime() + " | "
                    + transaction.getDescription() + " | " + transaction.getVendor() + " | "
                    + transaction.getAmount());
        }
    }

    private static LocalDateTime timestamp(Transaction transaction) {
        return LocalDateTime.parse(transaction.getDate() + "T" + transaction.getTime());
    }

    // Load Transactions Method
    public static void loadTransactions(ArrayList<Transaction> transactions) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE))) {
            String line = reader.readLine();
            if ("date|time|description|vendor|amount".equalsIgnoreCase(line)) {
                line = reader.readLine(); // Skip header row
            }
            while (line != null) {
                String[] parts = line.split("\\|", -1);
                if (parts.length == 5) {
                    try {
                        double amount = Double.parseDouble(parts[4]);
                        Transaction transaction = new Transaction(parts[0], parts[1], parts[2], parts[3], amount);
                        timestamp(transaction);
                        if (Double.isFinite(amount)) {
                            transactions.add(transaction);
                        }
                    } catch (DateTimeParseException | NumberFormatException e) {
                        System.out.println("Skipped a transaction with an invalid date, time, or amount.");
                    }
                }
                line = reader.readLine();
            }
        } catch (FileNotFoundException e) {
            if (FILE.exists()) {
                System.out.println("Could not load transactions.");
            }
        } catch (IOException e) {
            System.out.println("Could not load transactions.");
        }
    }

    // Save Transaction Method
    public static boolean saveTransaction(Transaction transaction) {
        if (transaction.getDescription().contains("|") || transaction.getVendor().contains("|")) {
            System.out.println("Description and vendor cannot contain the | character.");
            return false;
        }
        try (FileWriter writer = new FileWriter(FILE, true)) {
            writer.write(transaction.getDate() + "|" + transaction.getTime() + "|"
                    + transaction.getDescription() + "|" + transaction.getVendor() + "|"
                    + transaction.getAmount() + System.lineSeparator());
        } catch (IOException e) {
            System.out.println("Error saving transaction.");
            return false;
        }
        return true;
    }
}
