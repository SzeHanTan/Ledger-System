package src.Transaction;

import java.time.LocalDate;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Credit {

    private double credit;
    private String description;
    private Scanner scanner = new Scanner(System.in);

    public Credit() {
        this.credit = 0;
        this.description = "";
    }

    public double getCredit() {
        return credit;
    }

    public void setCredit(double credit) {
        this.credit = credit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Method to record a credit transaction
    public Transaction recordCredit() {
        double creditAmount;
        String transactionDescription;

        // Get credit amount with validation
        while (true) {
            System.out.print("Enter credit amount: ");
            try {
                creditAmount = scanner.nextDouble();
                if (creditAmount <= 0) {
                    System.out.println("Credit amount must be positive. Please try again.");
                } else {
                    break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a numeric value.");
                scanner.nextLine(); // Clear invalid input
            }
        }

        // Get transaction description
        scanner.nextLine(); // Clear buffer
        while (true) {
            System.out.print("Enter description: ");
            transactionDescription = scanner.nextLine().trim();
            if (transactionDescription.isEmpty()) {
                System.out.println("Description cannot be empty. Please try again.");
            } else {
                break;
            }
        }

        // Create and return the transaction
        System.out.println("Credit recorded successfully!");
        return new Transaction(LocalDate.now(), transactionDescription, creditAmount,
                Transaction.TransactionType.CREDIT);
    }

}
