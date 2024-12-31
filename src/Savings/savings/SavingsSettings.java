package src.Savings.savings;

import java.io.*;
import java.util.*;

import src.TransactionHistory;

public class SavingsSettings {

    private int savingsId; // Savings ID field
    private boolean isSavingsActive = false; // Tracks if savings feature is active
    private int savingsPercentage = 0; // Default savings percentage
    private double totalSavings = 0.0; // Accumulated savings amount
    private TransactionHistory transactionHistory; // Links to TransactionHistory for balance updates
    private List<SavingsRecord> savingsRecords; // List to store savings records
    private String username; // Store the username
    private File savingsFile; // Reference to the savings file

    // Constructor to initialize SavingsSettings
    public SavingsSettings(TransactionHistory transactionHistory, String username) {
        this.transactionHistory = transactionHistory;
        this.username = username;

        // Initialize savings file
        this.savingsFile = new File(username + "_savings.csv");
        
        // Fetch the next available savings ID
        this.savingsId = getNextSavingsId(); // Get the next available savings ID

        this.savingsRecords = new ArrayList<>(); // Initialize the savings records list
        loadPreviousSavingsRecords(); // Load existing savings records (if any)
    }

    // Getter for savingsId
    public int getSavingsId() {
        return savingsId;
    }

    // Getter for username
    public String getUsername() {
        return username;
    }

    // Getter for total savings
    public double getTotalSavings() {
        return totalSavings;
    }

    // Flow to activate and set up savings
    public void activateSavingsFlow() {
        Scanner scanner = new Scanner(System.in);

        // Check if savings are already activated
        if (!isSavingsActive) {
            // Prompt to activate savings if not activated yet
            System.out.print("Do you want to activate savings? (Y/N): ");
            String choice = scanner.nextLine().trim().toUpperCase();

            if (choice.equals("Y")) {
                System.out.print("Enter default savings percentage (0-100): ");
                int percentage = getValidPercentage(scanner);
                activateSavings(percentage);
            } else {
                System.out.println("Savings not activated.");
            }
        } else {
            // Allow the user to change the percentage if savings are already activated
            System.out.print("Savings already activated with " + savingsPercentage
                    + "%. Do you want to change the percentage? (Y/N): ");
            String changeChoice = scanner.nextLine().trim().toUpperCase();

            if (changeChoice.equals("Y")) {
                System.out.print("Enter new savings percentage (0-100): ");
                int newPercentage = getValidPercentage(scanner);
                updateSavingsPercentage(newPercentage);
            } else {
                System.out.println("Savings percentage remains " + savingsPercentage + "%.");
            }
        }
    }

    // Helper method to validate percentage input
    private int getValidPercentage(Scanner scanner) {
        int percentage = -1;
        while (percentage < 0 || percentage > 100) {
            try {
                percentage = Integer.parseInt(scanner.nextLine().trim());
                if (percentage < 0 || percentage > 100) {
                    System.out.println("Please enter a valid percentage between 0 and 100.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number between 0 and 100.");
            }
        }
        return percentage;
    }

    public void activateSavings(int percentage) {
        //System.out.println("Before activation: savingsRecords = " + savingsRecords);
        isSavingsActive = true;
        savingsPercentage = percentage;
    
        // Update the total savings based on the new percentage
        double totalSavings = (transactionHistory.getCurrentBalance() * percentage) / 100;
    
        System.out.println("Savings activated successfully at " + percentage + "%!");
    
        // Create a new savings record with a unique Savings_id
        SavingsRecord newRecord = new SavingsRecord(
                getNextSavingsId(), // Unique Savings_id using the next available id
                username,
                "Active",
                percentage,
                totalSavings);
    
        // Check if the record with the same username and percentage already exists
        boolean recordExists = false;
        for (SavingsRecord record : savingsRecords) {
            if (record.getUsername().equals(username) && record.getPercentage() == percentage) {
                // If the record exists, update it instead of adding a new one
                record.setTotalSavings(totalSavings);
                record.setPercentage(percentage);
                recordExists = true;
                break;
            }
        }
    
        // If the record does not exist, add it to the list
        if (!recordExists) {
            savingsRecords.add(newRecord);
        }
    
        // Export the updated list of records to the CSV file
        //System.out.println("Before exporting: savingsRecords = " + savingsRecords);
        SavingsCSV savingsCSV = new SavingsCSV(username);
        //savingsCSV.exportSavings(savingsRecords); // Export all records, ensuring only unique and correct data is written to the file
    }
    

    // Automatically transfer savings to balance at the end of the month
    public void autoTransferSavingsToBalance() {
        System.out.println("\nEnd of the month. Transferring savings to main balance...");
        System.out.printf("Total Savings: %.2f%n", totalSavings);

        if (totalSavings > 0.0) {
            transactionHistory.updateBalance(transactionHistory.getCurrentBalance() + totalSavings);
            System.out.printf("Total Savings Transferred: %.2f%n", totalSavings);
            totalSavings = 0.0; // Reset savings after transfer
        } else {
            System.out.println("No savings to transfer.");
        }
        System.out.printf("New Balance: %.2f%n", transactionHistory.getCurrentBalance());
    }

    // Getter for savings percentage
    public int getSavingsPercentage() {
        return savingsPercentage;
    }

    // Getter for total savings balance
    public double getSavingsBalance() {
        return totalSavings;
    }

    // Getter for active status
    public boolean isSavingsActive() {
        return isSavingsActive;
    }

    private void loadPreviousSavingsRecords() {
        SavingsCSV savingsCSV = new SavingsCSV(username);
        this.savingsRecords = savingsCSV.loadSavingsRecords();
        if (!savingsRecords.isEmpty()) {
            // Load the latest record to update settings
            SavingsRecord latestRecord = savingsRecords.get(savingsRecords.size() - 1);
            this.isSavingsActive = latestRecord.getStatus().equals("Active");
            this.savingsPercentage = latestRecord.getPercentage();
            this.totalSavings = latestRecord.getTotalSavings(); // Ensure total savings is updated
        } else {
            //System.out.println("No previous savings records found. Initializing new savings settings.");
        }
    }

    public void updateSavingsPercentage(int newPercentage) {
        savingsPercentage = newPercentage;
        System.out.println("Savings percentage updated to " + newPercentage + "%!");

        // Calculate new total savings
        double newTotalSavings = (transactionHistory.getCurrentBalance() * newPercentage) / 100;

        // Create a new record with an incremented ID
        SavingsRecord updatedRecord = new SavingsRecord(
                getNextSavingsId(), // Get next unique ID
                username,
                "Active",
                newPercentage,
                newTotalSavings);

        // Append the new record
        SavingsCSV savingsCSV = new SavingsCSV(username);
        //savingsCSV.exportSavings(Collections.singletonList(updatedRecord)); // Wrap the record in a list

        // Add the new record to in-memory list
        savingsRecords.add(updatedRecord);
    }

    public void addToSavingsBalance(double amount) {
        if (amount > 0) {
            totalSavings += amount;
            System.out.printf("Total Savings Updated: %.2f%n", totalSavings);
        } else {
            System.out.println("Invalid amount. Cannot add to savings balance.");
        }
    }

    private int getNextSavingsId() {
        int nextId = 1;
        
        if (savingsFile == null || !savingsFile.exists()) {
            //System.out.println("Error: savingsFile is null or does not exist.");
            return nextId;
        }
    
        try (BufferedReader reader = new BufferedReader(new FileReader(savingsFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("Savings_id")) {
                    String[] values = line.split(",");
                    int id = Integer.parseInt(values[0]);
                    if (id >= nextId) {
                        nextId = id + 1; // Increment for the next record
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file for next Savings_id: " + e.getMessage());
        }
        
        return nextId;
    }
}

