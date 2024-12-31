package src.CreditLoan.CreditLoan;

import java.util.List;
import java.util.Scanner;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;
import javax.swing.JOptionPane;

public class CreditLoanMain {
    private static CreditLoan userLoan = null; // Current user's loan

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Welcome to the Credit Loan System");
            System.out.println("1. Apply for Credit Loan");
            System.out.println("2. Repay Loan");
            System.out.println("3. View Loan Details");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 1) {
                applyForLoan(scanner);
            } else if (choice == 2) {
                repayLoan(scanner);
            } else if (choice == 3) {
                viewLoanDetails();
            } else if (choice == 4) {
                System.out.println("Goodbye!");
                break;
            }
        }
        scanner.close();
    }

    // Apply for a new loan
    public static void applyForLoan(Scanner scanner) {
        // Check if the user already has an active loan
        if (userLoan != null && !userLoan.isLoanPaid()) {
            System.out.println("You already have an active loan. Repay your loan before applying for a new one.");
            return;
        }

        // Collect user input
        System.out.print("Enter your username: ");
        String username = scanner.next(); // Getting the username

        System.out.print("Enter loan amount: ");
        double loanAmount = scanner.nextDouble();

        System.out.print("Enter interest rate (%): ");
        double interestRate = scanner.nextDouble();

        System.out.print("Enter repayment period (months): ");
        int months = scanner.nextInt();

        // Create a new CreditLoan object
        userLoan = new CreditLoan(loanAmount, interestRate, months, username); // Pass username here

        userLoan.loan(); // Call loan method instead of applyLoan

        // Create a new loan record
        LoansRecord updatedLoanRecord = new LoansRecord(
                1, // Loan ID (you might want to implement an auto-increment feature for unique
                   // IDs)
                username, // Use the username from input
                userLoan.getLoanAmount(),
                userLoan.getInterestRate(),
                userLoan.getRepaymentPeriod(),
                userLoan.getRemainingLoanAmount(),
                userLoan.isLoanPaid() ? "Paid" : "Active",
                LocalDate.now().toString());

        // Export the loan record to CSV
        LoansCSV.exportLoans(updatedLoanRecord, username); // Export the updated loan record with the username
    }

    // Repay the loan
    public static void repayLoan(Scanner scanner) {
        if (userLoan == null) {
            System.out.println("No loan to repay.");
            return;
        }
        if (userLoan.isLoanPaid()) {
            System.out.println("Your loan is already fully paid.");
            return;
        }

        // Collect repayment amount from the user
        System.out.print("Enter repayment amount: ");
        double paymentAmount = scanner.nextDouble();

        // Process the loan repayment
        userLoan.repayLoan(paymentAmount);

        // Create a new loan record with the updated details
        LoansRecord newLoanRecord = new LoansRecord(
                1, // Loan ID (this could be incremented to make it unique, for simplicity we use
                   // '1')
                userLoan.getUsername(), // Get the username from the userLoan object
                userLoan.getLoanAmount(),
                userLoan.getInterestRate(),
                userLoan.getRepaymentPeriod(),
                userLoan.getRemainingLoanAmount(),
                userLoan.isLoanPaid() ? "Paid" : "Active",
                LocalDate.now().toString());

        // Export the updated loan record to CSV
        LoansCSV.exportLoans(newLoanRecord, userLoan.getUsername());
    }

    public static void viewLoanDetails() {
        if (userLoan == null) {
            System.out.println("No loan details available.");
        } else {
            userLoan.displayLoanDetails();
            userLoan.displayRepaymentReminder();
        }
    }

}