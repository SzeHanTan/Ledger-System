package src.Transaction;

import java.time.LocalDate;
import java.util.List;
import java.io.Serializable;

public class Transaction implements Serializable {

    private static final long serialVersionUID = 1L;
    private LocalDate date;
    private String description;
    private double amount;  // Single field for amount (positive for credit, negative for debit)
    private TransactionType type;

    public enum TransactionType {
        DEBIT, CREDIT
    }

    public Transaction(LocalDate date, String description, double amount, TransactionType type) {
        this.date = date;
        this.description = description;
        this.amount = amount;
        this.type = type;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }

    public TransactionType getType() {
        return type;
    }

    // For balance calculation, ensure we handle debits and credits correctly
    public static double getBalance(List<Transaction> transactions) {
        double balance = 0.0;
        for (Transaction t : transactions) {
            if (t.getType() == TransactionType.DEBIT) {
                balance += t.getAmount();  // Debit increases balance
            } else if (t.getType() == TransactionType.CREDIT) {
                balance -= t.getAmount();  // Credit reduces balance
            }
        }
        return balance;
    }
}





