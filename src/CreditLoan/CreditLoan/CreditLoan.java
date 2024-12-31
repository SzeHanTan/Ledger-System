package src.CreditLoan.CreditLoan;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Scanner;
import javax.swing.JOptionPane;

public class CreditLoan {
    private double loanAmount; // Loan amount
    private double interestRate; // Interest rate
    private int months; // Loan period (months)
    private double monthlyPayment; // Monthly payment
    private double totalRepayment; // Total repayment amount
    private double amountPaid; // Amount paid so far
    private boolean isLoanPaid; // Whether the loan is paid off
    private LocalDate loanStartDate; // Loan start date
    private LocalDate nextPaymentDate; // Next payment date
    private String username;
    private static final DecimalFormat df = new DecimalFormat("0.00");

    // Constructor accepting loanAmount, interestRate, months, and username
    public CreditLoan(double loanAmount, double interestRate, int months, String username) {
        this.loanAmount = loanAmount;
        this.interestRate = interestRate;
        this.months = months;
        this.username = username;
        this.amountPaid = 0;
        this.isLoanPaid = false;
        this.monthlyPayment = calculateMonthlyPayment();
        this.totalRepayment = monthlyPayment * months;
        this.loanStartDate = LocalDate.now(); // Default to current date
        this.nextPaymentDate = loanStartDate.plusDays(5); // Next payment is in 5 days
    }

    // Calculate monthly payment using loan amortization formula
    public double calculateMonthlyPayment() {
        double rate = interestRate / 100 / 12; // Monthly interest rate
        return loanAmount * rate / (1 - Math.pow(1 + rate, -months)); // Loan amortization formula
    }

    // Apply for a loan (display loan details)
    public void loan() {
        System.out.println("\n--- Loan Details ---");
        System.out.println("Loan Amount: " + df.format(loanAmount));
        System.out.println("Interest Rate: " + df.format(interestRate) + "%");
        System.out.println("Loan Period: " + months + " months");
        System.out.println("Monthly Payment: " + df.format(monthlyPayment));
        System.out.println("Total Repayment: " + df.format(totalRepayment));
        System.out.println("----------------------\n");

        // Create a LoansRecord for the new loan application
        LoansRecord loanRecord = new LoansRecord(1, "user1", loanAmount, interestRate, months,
                totalRepayment - amountPaid, isLoanPaid ? "Paid" : "Active", LocalDate.now().toString());

        // Export the loan to CSV
        LoansCSV.exportLoans(loanRecord, getUsername()); // Update loan information in CSV
    }

    // Repay loan
    public void repayLoan(double paymentAmount) {
        if (isLoanPaid) {
            JOptionPane.showMessageDialog(null, "Your loan is already fully paid.", "Loan Status",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        amountPaid += paymentAmount;
        if (amountPaid >= totalRepayment) {
            isLoanPaid = true;
            amountPaid = totalRepayment;
            JOptionPane.showMessageDialog(null, "Loan fully repaid!", "Loan Status", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null,
                    "Amount Paid: " + df.format(amountPaid) + "\nRemaining Amount: "
                            + df.format(totalRepayment - amountPaid),
                    "Repayment Status", JOptionPane.INFORMATION_MESSAGE);
        }

        // Update the next payment date for next month
        nextPaymentDate = nextPaymentDate.plusMonths(1);

        // Create an updated LoansRecord for the repaid loan
        LoansRecord loanRecord = new LoansRecord(1, "user1", loanAmount, interestRate, months,
                totalRepayment - amountPaid, isLoanPaid ? "Paid" : "Active", LocalDate.now().toString());

        // Export the updated loan status to CSV
        LoansCSV.exportLoans(loanRecord, getUsername()); // Update loan information in CSV
    }

    // Check if the loan is fully paid
    public boolean isLoanPaid() {
        return isLoanPaid;
    }

    // Display loan details
    public void displayLoanDetails() {
        if (isLoanPaid) {
            System.out.println("\n--- Loan Paid Off ---");
        } else {
            System.out.println("\n--- Loan Details ---");
        }

        System.out.println("Loan Amount: " + df.format(loanAmount));
        System.out.println("Interest Rate: " + df.format(interestRate) + "%");
        System.out.println("Loan Period: " + months + " months");
        System.out.println("Monthly Payment: " + df.format(monthlyPayment));
        System.out.println("Total Repayment: " + df.format(totalRepayment));
        System.out.println("Amount Paid: " + df.format(amountPaid));
        System.out.println("Remaining Balance: " + df.format(totalRepayment - amountPaid));
        System.out.println("----------------------\n");
    }

    public void displayRepaymentReminder() {
        if (!isLoanPaid) {
            long daysUntilRepayment = ChronoUnit.DAYS.between(LocalDate.now(), nextPaymentDate);
            if (daysUntilRepayment <= 7 && daysUntilRepayment >= 0) {
                String reminderMessage = "Reminder: Your loan repayment is due in " + daysUntilRepayment + " days!\n"
                        + "Next payment date: " + nextPaymentDate;
                JOptionPane.showMessageDialog(null, reminderMessage, "Loan Repayment Reminder",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                System.out.println("Reminder not triggered. Days until repayment: " + daysUntilRepayment); // Debug
            }
        } else {
            System.out.println("Loan is already paid. No reminder needed."); // Debug
        }
    }

    // Loan method (placeholder)
    public void applyLoan() {
        System.out.println("Loan applied successfully!");
    }

    public double getRemainingLoanAmount() {
        return totalRepayment - amountPaid;
    }

    public double getLoanAmount() {
        return loanAmount;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public int getRepaymentPeriod() {
        return months;
    }

    // Getter for username
    public String getUsername() {
        return this.username;
    }

    public int getLoanId() {
        return (String.valueOf(loanAmount) + username).hashCode(); // Convert loanAmount to String and combine with username
    }
    

    // Getter for months (loan period)
    public int getMonths() {
        return months;
    }

    // Getter for loan status (Active or Paid)
    public String getStatus() {
        return isLoanPaid ? "Paid" : "Active";
    }

    // Getter for loanStartDate
    public LocalDate getLoanStartDate() {
        return loanStartDate;
    }
    

}
