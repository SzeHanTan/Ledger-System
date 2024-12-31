package src.CreditLoan.CreditLoan;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LoansRecord {
    private int loanId;
    private String userId;
    private double loanAmount;
    private double interestRate;
    private int months;
    private double remainingAmount;
    private String status; // "Active" or "Paid"
    private String loanStartDate;

    // Constructor
    public LoansRecord(int loanId, String userId, double loanAmount, double interestRate, int months,
            double remainingAmount, String status, String loanStartDate) {
        this.loanId = loanId;
        this.userId = userId;
        this.loanAmount = loanAmount;
        this.interestRate = interestRate;
        this.months = months;
        this.remainingAmount = remainingAmount;
        this.status = status;
        this.loanStartDate = loanStartDate;
    }

    // Getters for the fields
    public int getLoanId() {
        return loanId;
    }

    public String getUserId() {
        return userId;
    }

    public double getLoanAmount() {
        return loanAmount;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public int getMonths() {
        return months;
    }

    public double getRemainingAmount() {
        return remainingAmount;
    }

    public String getStatus() {
        return status;
    }

    public String getLoanStartDate() {
        return loanStartDate;
    }

    public static List<LoansRecord> loadLoans(String username) {
        String filePath = "loans_" + username + ".csv";
        List<LoansRecord> loanRecords = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                int loanId = Integer.parseInt(data[0]);
                String userId = data[1];
                double loanAmount = Double.parseDouble(data[2]);
                double interestRate = Double.parseDouble(data[3]);
                int months = Integer.parseInt(data[4]);
                double remainingAmount = Double.parseDouble(data[5]);
                String status = data[6];
                String loanStartDate = data[7];

                LoansRecord record = new LoansRecord(loanId, userId, loanAmount, interestRate, months, remainingAmount,
                        status, loanStartDate);
                loanRecords.add(record);
            }

        } catch (IOException e) {
            System.out.println("Error loading loans: " + e.getMessage());
        }

        return loanRecords;
    }
}
