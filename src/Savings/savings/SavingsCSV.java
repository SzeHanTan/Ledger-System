package src.Savings.savings;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SavingsCSV {

    private File savingsFile; // Reference to the savings file

    public SavingsCSV(String username) {
        // Initialize the file name based on the username
        this.savingsFile = new File("savings_" + username + ".csv");

        // Ensure the file exists with a proper header
        if (!savingsFile.exists()) {
            initializeSavingsFile();
        } else {
            ensureHeaderExists();
        }
    }

    // Initialize the savings file with a header
    private void initializeSavingsFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(savingsFile))) {
            writer.write("Savings_id,Username,Status,Percentage,Total_Savings\n");
        } catch (IOException e) {
            System.out.println("Error initializing savings file: " + e.getMessage());
        }
    }

    // Ensure the header exists in the file
    private void ensureHeaderExists() {
        try (BufferedReader reader = new BufferedReader(new FileReader(savingsFile))) {
            String firstLine = reader.readLine();
            if (firstLine == null || !firstLine.equals("Savings_id,Username,Status,Percentage,Total_Savings")) {
                initializeSavingsFile(); // Rewrite the header if missing or incorrect
            }
        } catch (IOException e) {
            System.out.println("Error ensuring header in savings file: " + e.getMessage());
        }
    }

    // Add a new savings record and write it to the file
    public void addSavingsRecord(String username, String status, int percentage, double totalSavings) {
        int nextId = getNextSavingsId();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(savingsFile, true))) { // Append mode
            writer.write(String.format("%d,%s,%s,%d,%.2f%n", nextId, username, status, percentage, totalSavings));
            System.out.println("New savings record added successfully!");
        } catch (IOException e) {
            System.out.println("Error adding new savings record: " + e.getMessage());
        }
    }

    // Load all savings records from the CSV file
    public List<SavingsRecord> loadSavingsRecords() {
        List<SavingsRecord> records = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(savingsFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Savings_id")) continue; // Skip the header

                String[] fields = line.split(",");
                int savingsId = Integer.parseInt(fields[0]);
                String username = fields[1];
                String status = fields[2];
                int percentage = Integer.parseInt(fields[3]);
                double totalSavings = Double.parseDouble(fields[4]);

                records.add(new SavingsRecord(savingsId, username, status, percentage, totalSavings));
            }
        } catch (IOException e) {
            System.out.println("Error loading savings records: " + e.getMessage());
        }
        return records;
    }

    // Get the next Savings_id for a new record
    private int getNextSavingsId() {
        int nextId = 1;
        try (BufferedReader reader = new BufferedReader(new FileReader(savingsFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Savings_id")) continue; // Skip the header

                String[] fields = line.split(",");
                int currentId = Integer.parseInt(fields[0]);
                nextId = Math.max(nextId, currentId + 1); // Increment the highest ID
            }
        } catch (IOException e) {
            System.out.println("Error reading file for next Savings_id: " + e.getMessage());
        }
        return nextId;
    }
}


