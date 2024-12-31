package src.CreditLoan.CreditLoan;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LoansCSV {

    public static void exportLoans(LoansRecord loanRecord, String username) {
        // Dynamically create the file path based on the username
        String filePath = "loans_" + username + ".csv";
    
        try (FileWriter writer = new FileWriter(filePath, true)) { // Open in append mode
            // If the file is new, write the header
            boolean isNewFile = new java.io.File(filePath).length() == 0;
            if (isNewFile) {
                writer.write("LoanId,UserId,LoanAmount,InterestRate,Months,RemainingAmount,Status,LoanStartDate\n");
            }
    
            // Append the new loan record
            String recordLine = String.format("%d,%s,%.2f,%.2f,%d,%.2f,%s,%s\n",
                    loanRecord.getLoanId(),
                    loanRecord.getUserId(),
                    loanRecord.getLoanAmount(),
                    loanRecord.getInterestRate(),
                    loanRecord.getMonths(),
                    loanRecord.getRemainingAmount(),
                    loanRecord.getStatus(),
                    loanRecord.getLoanStartDate());
            writer.write(recordLine);
    
            System.out.println("Loan record added successfully to " + filePath);
    
        } catch (IOException e) {
            System.out.println("Error exporting loans: " + e.getMessage());
        }
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


