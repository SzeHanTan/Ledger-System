package src;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import src.Transaction.Transaction;
import java.time.LocalDate;

public class TransactionHistory {

    private List<Transaction> transactions; // List to store transactions
    private double initialBalance;          // Initial balance of the account
    private double currentBalance;          // Current balance of the account
    private String username;                // Username for user-specific transactions

    // Constructor to initialize transaction history with an initial balance and username
    public TransactionHistory(double initialBalance, String username) {
        this.transactions = new ArrayList<>();
        this.initialBalance = initialBalance;
        this.currentBalance = initialBalance; // Set the starting balance
        this.username = username;
        loadTransactionHistory();             // Attempt to load existing transaction history
    }

    public static List<Transaction> filterByDateRange(LocalDate startDate, LocalDate endDate, List<Transaction> transactions) {
        return transactions.stream()
                .filter(t -> !t.getDate().isBefore(startDate) && !t.getDate().isAfter(endDate))
                .collect(Collectors.toList());
    }

    public static List<Transaction> filterByType(String type, List<Transaction> transactions) {
        Transaction.TransactionType transactionType = Transaction.TransactionType.valueOf(type.toUpperCase());
        return transactions.stream()
                .filter(t -> t.getType() == transactionType)
                .collect(Collectors.toList());
    }

    public static List<Transaction> filterByAmountRange(double minAmount, double maxAmount, List<Transaction> transactions) {
        return transactions.stream()
                .filter(t -> t.getAmount() >= minAmount && t.getAmount() <= maxAmount)
                .collect(Collectors.toList());
    }

    public static List<Transaction> sortByDate(boolean newestFirst, List<Transaction> transactions) {
        return transactions.stream()
                .sorted((t1, t2) -> newestFirst ? t2.getDate().compareTo(t1.getDate()) : t1.getDate().compareTo(t2.getDate()))
                .collect(Collectors.toList());
    }

    public static List<Transaction> sortByAmount(boolean highestFirst, List<Transaction> transactions) {
        return transactions.stream()
                .sorted((t1, t2) -> highestFirst ? Double.compare(t2.getAmount(), t1.getAmount()) : Double.compare(t1.getAmount(), t2.getAmount()))
                .collect(Collectors.toList());
    }

    // Add a new transaction and update the current balance
    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);

        // Update balance based on transaction type
        if (transaction.getType() == Transaction.TransactionType.DEBIT) {
            currentBalance += transaction.getAmount();
        } else if (transaction.getType() == Transaction.TransactionType.CREDIT) {
            currentBalance -= transaction.getAmount();
        }

        // Save updated transaction list to file
        saveTransactionHistory();
    }

    // View the complete transaction history with running balance
    public void viewTransactionHistory(Scanner scanner) {
        System.out.println("\n== History ==");
        System.out.printf("%-15s %-20s %-10s %-10s %-10s\n", "Date", "Description", "Debit", "Credit", "Balance");
        
        if (transactions.isEmpty()) {
            System.out.println("No transaction history available.");
        } else {
            double currentBalance = 0.0;  // Variable to hold current balance
            for (Transaction t : transactions) {
                // Update the current balance based on transaction type
                if (t.getType() == Transaction.TransactionType.DEBIT) {
                    currentBalance += t.getAmount();
                } else if (t.getType() == Transaction.TransactionType.CREDIT) {
                    currentBalance -= t.getAmount();
                }
                // Print each transaction with the updated balance
                System.out.printf("%-15s %-20s %-10.2f %-10.2f %-10.2f\n",
                        t.getDate(), t.getDescription(),
                        t.getType() == Transaction.TransactionType.DEBIT ? t.getAmount() : 0.0,
                        t.getType() == Transaction.TransactionType.CREDIT ? t.getAmount() : 0.0,
                        currentBalance);  // Use the running balance
            }
        }
        exportHistoryToCSV();

        while (true) {
            System.out.println("\n1. Filter by Date Range");
            System.out.println("2. Filter by Transaction Type");
            System.out.println("3. Filter by Amount Range");
            System.out.println("4. Sort by Date");
            System.out.println("5. Sort by Amount");
            System.out.println("6. Reset Filters");
            System.out.println("7. Back to Main Menu");
            System.out.print("> ");

            int choice = scanner.nextInt();
            List<Transaction> filteredTransactions = transactions; // Default show all transactions

            switch (choice) {
                case 1:
                    System.out.print("Enter start date (YYYY-MM-DD): ");
                    LocalDate startDate = LocalDate.parse(scanner.next());
                    System.out.print("Enter end date (YYYY-MM-DD): ");
                    LocalDate endDate = LocalDate.parse(scanner.next());
                    filteredTransactions = TransactionHistory.filterByDateRange(startDate, endDate, transactions);
                    break;

                case 2:
                    System.out.print("Enter transaction type (debit/credit): ");
                    String type = scanner.next();
                    filteredTransactions = TransactionHistory.filterByType(type, transactions);
                    break;

                case 3:
                    System.out.print("Enter minimum amount: ");
                    double minAmount = scanner.nextDouble();
                    System.out.print("Enter maximum amount: ");
                    double maxAmount = scanner.nextDouble();
                    filteredTransactions = TransactionHistory.filterByAmountRange(minAmount, maxAmount, transactions);
                    break;

                case 4:
                    System.out.print("Sort newest first? (true/false): ");
                    boolean newestFirst = scanner.nextBoolean();
                    filteredTransactions = TransactionHistory.sortByDate(newestFirst, transactions);
                    break;

                case 5:
                    System.out.print("Sort highest first? (true/false): ");
                    boolean highestFirst = scanner.nextBoolean();
                    filteredTransactions = TransactionHistory.sortByAmount(highestFirst, transactions);
                    break;

                case 6:
                    filteredTransactions = transactions; // Reset filters
                    break;

                case 7:
                    return;

                default:
                    System.out.println("Invalid option. Please try again.");
                    continue;
            }

            if (filteredTransactions.isEmpty()) {
                System.out.println("No transactions found.");
            } else {
                double currentBalance = 0.0;  // Variable to hold current balance
                for (Transaction t : filteredTransactions) {
                    if (t.getType() == Transaction.TransactionType.DEBIT) {
                        currentBalance += t.getAmount();
                    } else if (t.getType() == Transaction.TransactionType.CREDIT) {
                        currentBalance -= t.getAmount();
                    }
                    System.out.printf("%-15s %-20s %-10.2f %-10.2f %-10.2f\n",
                            t.getDate(), t.getDescription(),
                            t.getType() == Transaction.TransactionType.DEBIT ? t.getAmount() : 0.0,
                            t.getType() == Transaction.TransactionType.CREDIT ? t.getAmount() : 0.0,
                            currentBalance);  // Use the running balance
                }
            }
        }
    }

    // Save transaction history to a binary file (user-specific)
    public void saveTransactionHistory() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("transactions_" + username + ".dat"))) {
            oos.writeObject(transactions); // Serialize transactions to file
        } catch (IOException e) {
            System.err.println("Error saving transaction history: " + e.getMessage());
        }
    }

    // Load transaction history from a binary file (user-specific)
    @SuppressWarnings("unchecked")
    public void loadTransactionHistory() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("transactions_" + username + ".dat"))) {
            transactions = (List<Transaction>) ois.readObject(); // Deserialize transactions
            recalculateBalance(); // Recalculate balance after loading transactions
        } catch (IOException | ClassNotFoundException e) {
            //System.out.println("No previous transaction history found. Starting fresh.");
            transactions = new ArrayList<>();
        }
    }

    // Export transaction history to a CSV file (user-specific)
    private void exportHistoryToCSV() {
        try (PrintWriter writer = new PrintWriter(new File("transaction_history_" + username + ".csv"))) {
            writer.println("Date,Description,Debit,Credit,Balance");
            double runningBalance = initialBalance;

            for (Transaction t : transactions) {
                if (t.getType() == Transaction.TransactionType.DEBIT) {
                    runningBalance += t.getAmount();
                } else if (t.getType() == Transaction.TransactionType.CREDIT) {
                    runningBalance -= t.getAmount();
                }

                writer.printf("%s,%s,%.2f,%.2f,%.2f\n",
                        t.getDate(), t.getDescription(),
                        t.getType() == Transaction.TransactionType.DEBIT ? t.getAmount() : 0.0,
                        t.getType() == Transaction.TransactionType.CREDIT ? t.getAmount() : 0.0,
                        runningBalance);
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error exporting transaction history: " + e.getMessage());
        }
    }

    // Recalculate balance after loading history
    private void recalculateBalance() {
        currentBalance = initialBalance;  // Start with initial balance
        for (Transaction t : transactions) {
            if (t.getType() == Transaction.TransactionType.DEBIT) {
                currentBalance += t.getAmount();
            } else if (t.getType() == Transaction.TransactionType.CREDIT) {
                currentBalance -= t.getAmount();
            }
        }
    }

    // Getters and Setters for balance and transaction list
    public double getInitialBalance() {
        return initialBalance;
    }

    public double getCurrentBalance() {
        return currentBalance;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    // Update the current balance (used by SavingsSettings)
    public void updateBalance(double newBalance) {
        this.currentBalance = newBalance;
        System.out.printf("Updated Current Balance: %.2f%n", currentBalance);
    }

    // Update the initial balance (e.g., on account reset)
    public void setInitialBalance(double newInitialBalance) {
        this.initialBalance = newInitialBalance;
        recalculateBalance(); // Recalculate balance based on new initial balance
    }
}








