package src.Transaction;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import src.Savings.savings.SavingsCSV;
import src.Savings.savings.SavingsRecord;
import src.Savings.savings.SavingsSettings; // Import the SavingsSettings class from the savings package

public class Debit {

    private double debit;
    private String description;
    private Scanner scanner = new Scanner(System.in);
    private SavingsSettings savingsSettings; // Link to SavingsSettings

    // Default constructor
    public Debit() {
        this.debit = 0;
        this.description = "";
        this.savingsSettings = null; // No savingsSettings object yet
    }

    // Constructor to link the SavingsSettings object
    public Debit(SavingsSettings savingsSettings) {
        this.debit = 0;
        this.description = "";
        this.savingsSettings = savingsSettings;
    }

    public double getDebit() {
        return debit;
    }

    public void setDebit(double debit) {
        this.debit = debit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Transaction recordDebit() {
        // Check if savings feature is active
        if (savingsSettings != null && savingsSettings.getSavingsPercentage() > 0) {
            System.out.println("Savings feature is active.");
        } else {
            System.out.println("Savings Settings not initialized or inactive. Proceeding with normal debit.");
        }

        // Input debit amount
        System.out.print("Enter debit amount: ");
        debit = scanner.nextDouble();
        if (debit <= 0) {
            System.out.println("Debit amount must be positive!");
            return null;
        }

        // Input description
        System.out.print("Enter description: ");
        scanner.nextLine(); // Clear buffer
        description = scanner.nextLine();

        double remainingDebit = debit;

        // Apply savings deduction only for debits
        if (savingsSettings != null && savingsSettings.getSavingsPercentage() > 0) {
            double savingsDeduction = (debit * savingsSettings.getSavingsPercentage()) / 100;
            remainingDebit -= savingsDeduction;

            // Update the savings balance
            savingsSettings.addToSavingsBalance(savingsDeduction);

            System.out.printf("Savings Deducted: %.2f%n", savingsDeduction);

            // Now, update the savings record in the CSV file
            String username = savingsSettings.getUsername(); // Get the username from savingsSettings
            SavingsCSV savingsCSV = new SavingsCSV(username); // Pass the username to the constructor
            List<SavingsRecord> updatedRecords = savingsCSV.loadSavingsRecords();

            // Iterate through records and update the one that matches the savings ID
            for (SavingsRecord record : updatedRecords) {
                if (record.getSavingsId() == savingsSettings.getSavingsId()) { // Match the savings ID
                    record.setTotalSavings(savingsSettings.getTotalSavings()); // Update total savings
                    record.setPercentage(savingsSettings.getSavingsPercentage()); // Update percentage
                }
            }

            // Write the updated records back to the CSV
            savingsCSV.addSavingsRecord(username, "Active", savingsSettings.getSavingsPercentage(), savingsSettings.getTotalSavings()); // Pass the list of updated records
        }

        // Record the debit transaction
        return new Transaction(LocalDate.now(), description, remainingDebit, Transaction.TransactionType.DEBIT);
    }

}
