package src;

import org.mindrot.jbcrypt.BCrypt; // BCrypt for hashing

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

// Main system imports
import src.Transaction.Credit;
import src.Transaction.Debit;
import src.Transaction.Transaction;
import src.Savings.savings.SavingsCSV;
import src.Savings.savings.SavingsSettings;
import src.TransactionHistory;
import src.CreditLoan.CreditLoan.CreditLoan;
import src.CreditLoan.CreditLoan.LoansRecord;
import Visualize.DataVisualization;

public class Main {
    private static final String UserInfo = System.getProperty("user.home") + "/Desktop/Ledger/UserInfo.csv";
    private static final File USER_FILE = new File(UserInfo);
    private static Writer writer;
    private static Scanner scan;
    private static String username;

    public static void main(String[] args) throws IOException {
        // Setup user authentication
        createFileIfNotExist(UserInfo);
        initializeResources();

        if (USER_FILE.length() == 0) {
            writer.write("Username,Password,Email,UserID\n");
            writer.flush();
        }

        Scanner scanner = new Scanner(System.in);
        String username = null, email = null;

        // User Login/Registration flow
        while (true) {
            System.out.println("== Ledger System Authentication ==");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    String[] loginDetails = login(scanner);
                    if (loginDetails != null) {
                        username = loginDetails[0];
                        email = loginDetails[1];
                        break;
                    } else {
                        System.out.println("Login failed. Try again.");
                    }
                    break;
                case 2:
                    register(scanner);
                    break;
                case 3:
                    System.out.println("Thank you for using the Ledger System!");
                    scanner.close(); // Close the scanner before exiting
                    return; // Exit the program after closing the scanner
                default:
                    System.out.println("Invalid choice. Please try again.");
            }

            // Exit the loop if login is successful
            if (username != null) {
                break;
            }
        }

        // Proceed to Main Ledger System after successful login
        System.out.println("\n== Welcome, " + username + " ==");

        boolean loop = true;
        String key = "";
        TransactionHistory transactionHistory = new TransactionHistory(0.00, username);

        // Now, initialize the SavingsSettings object with the required parameters
        // Initialize SavingsSettings with transactionHistory and username
        SavingsSettings savingsSettings = new SavingsSettings(transactionHistory, username);

        CreditLoan creditLoan = null;
        displayAccountSummary(transactionHistory, savingsSettings, creditLoan);
        displayLoanAmount(username);

        do {
            System.out.println("\n== Transaction Menu ==");
            System.out.println("1. Debit");
            System.out.println("2. Credit");
            System.out.println("3. History");
            System.out.println("4. Savings");
            System.out.println("5. Credit Loan");
            System.out.println("6. Deposit Interest Predictor");
            System.out.println("7. Visualize Data");
            System.out.println("8. Logout");

            System.out.print("\n> ");
            key = scanner.next();

            switch (key) {
                case "1":
                    Debit debit = new Debit(savingsSettings);
                    Transaction debitTransaction = debit.recordDebit();
                    if (debitTransaction != null) {
                        transactionHistory.addTransaction(debitTransaction);
                        System.out.println("Debit successfully recorded!");
                    } else {
                        System.out.println("Debit transaction failed.");
                    }
                    displayAccountSummary(transactionHistory, savingsSettings, creditLoan);
                    break;

                case "2":
                    Credit credit = new Credit();
                    Transaction creditTransaction = credit.recordCredit();
                    if (creditTransaction != null) {
                        transactionHistory.addTransaction(creditTransaction);
                        System.out.println("Credit successfully recorded!");
                    } else {
                        System.out.println("Credit transaction failed.");
                    }
                    displayAccountSummary(transactionHistory, savingsSettings, creditLoan);
                    break;

                case "3":
                    transactionHistory.viewTransactionHistory(scanner);
                    break;

                case "4":
                    savingsSettings.activateSavingsFlow();
                    break;

                case "5":
                    if (creditLoan == null) {
                        creditLoan = handleCreditLoanFlow(scanner);
                    } else {
                        handleCreditLoanOptions(scanner, creditLoan);
                    }
                    break;

                case "6":
                    DepositInterestPredictor.main(new String[] {});
                    break;

                case "7":
                    visualizeData(username);
                    break;

                case "8":
                    System.out.println("\nThank you for using \"Ledger System AlgoNauts\".");
                    loop = false;
                    break;

                default:
                    System.out.println("\nError. Invalid selection, please choose again.");
                    break;
            }

            transactionHistory.saveTransactionHistory();
        } while (loop);

        scanner.close();
    }

    // User authentication methods
    private static void createFileIfNotExist(String filePath) throws IOException {
        File fileToCreate = new File(filePath);
        File parentDir = fileToCreate.getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }
        if (!fileToCreate.exists()) {
            fileToCreate.createNewFile();
        }
    }

    private static void initializeResources() throws IOException {
        writer = new FileWriter(USER_FILE, true);
        scan = new Scanner(USER_FILE);
    }

    private static String[] login(Scanner scanner) throws FileNotFoundException {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();

        if (isValidLogin(username, password, email)) {
            System.out.println("Login successful");
            return new String[] { username, email };
        }
        return null;
    }

    private static void register(Scanner scanner) throws IOException {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();

        if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            System.out.println("All fields are required");
            return;
        }

        if (isUniqueRegistration(username, email)) {
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            String userID = String.valueOf(getNextUserID());
            writer.write(username + "," + hashedPassword + "," + email + "," + userID + "\n");
            writer.flush();
            System.out.println("Registration successful!");
        } else {
            System.out.println("Email or username already exists.");
        }
    }

    private static boolean isValidLogin(String username, String password, String email) throws FileNotFoundException {
        Scanner scan = new Scanner(USER_FILE);
        if (scan.hasNextLine())
            scan.nextLine();

        while (scan.hasNextLine()) {
            String[] data = scan.nextLine().split(",");
            if (data[0].equalsIgnoreCase(username) && data[2].equalsIgnoreCase(email)
                    && BCrypt.checkpw(password, data[1])) {
                return true;
            }
        }
        return false;
    }

    private static boolean isUniqueRegistration(String username, String email) throws FileNotFoundException {
        Scanner scan = new Scanner(USER_FILE);
        if (scan.hasNextLine())
            scan.nextLine();

        while (scan.hasNextLine()) {
            String[] data = scan.nextLine().split(",");
            if (data[0].equalsIgnoreCase(username) || data[2].equalsIgnoreCase(email)) {
                return false;
            }
        }
        return true;
    }

    private static int getNextUserID() throws FileNotFoundException {
        Scanner scan = new Scanner(USER_FILE);
        int userID = 1;
        if (scan.hasNextLine())
            scan.nextLine();

        while (scan.hasNextLine()) {
            scan.nextLine();
            userID++;
        }
        return userID;
    }

    // In handleCreditLoanFlow
    public static CreditLoan handleCreditLoanFlow(Scanner scanner) {
        System.out.println("You are applying for a new Credit Loan.");
        System.out.print("Enter your username: ");
        String username = scanner.next(); // Get username from the user

        System.out.print("Enter loan amount: ");
        double loanAmount = scanner.nextDouble();
        System.out.print("Enter interest rate (%): ");
        double interestRate = scanner.nextDouble();
        System.out.print("Enter repayment period (months): ");
        int months = scanner.nextInt();

        CreditLoan newLoan = new CreditLoan(loanAmount, interestRate, months, username);
        newLoan.loan();

        return newLoan;
    }

    

    // Handles existing loan repayment and viewing loan details
    public static void handleCreditLoanOptions(Scanner scanner, CreditLoan loan) {
        while (true) {
            System.out.println("\n== Loan Options ==");
            System.out.println("1. Repay Loan");
            System.out.println("2. View Loan Details");
            System.out.println("3. Exit Loan Options");
            System.out.print("> ");

            int loanChoice = scanner.nextInt();
            if (loanChoice == 1) {
                System.out.print("Enter repayment amount: ");
                double repayment = scanner.nextDouble();
                loan.repayLoan(repayment);
            } else if (loanChoice == 2) {
                loan.displayLoanDetails();
            } else if (loanChoice == 3) {
                break;
            } else {
                System.out.println("Invalid option. Try again.");
            }
        }
    }

    

    // Display the account balance summary
    public static void displayAccountSummary(TransactionHistory transactionHistory, SavingsSettings savings,
            CreditLoan loan) {
        System.out.println("\nBalance: " + String.format("%.2f", transactionHistory.getCurrentBalance()));
        System.out.println("Savings: " + String.format("%.2f", savings.getSavingsBalance()));

        // if (loan != null) {
        //     System.out.println("Loan: " + String.format("%.2f", loan.getRemainingLoanAmount()));
        // } else {
        //     System.out.println("Loan: 0.00");
        // }
        
    }

    public static void displayLoanAmount(String username) {
        String filePath = "loans_" + username + ".csv";
    
        // Check if the file exists before attempting to read it
        if (!Files.exists(Paths.get(filePath))) {
            System.out.println("Loan: 0.00"); // Default display for no loans
            return;
        }
    
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            double latestRemainingAmount = 0.0;
    
            br.readLine(); // Skip the header
    
            while ((line = br.readLine()) != null) {
                String[] loanDetails = line.split(",");
                if (loanDetails.length < 6) { // Ensure the line has enough columns
                    continue;
                }
                try {
                    latestRemainingAmount = Double.parseDouble(loanDetails[5]); // Assuming RemainingAmount is in column 6
                } catch (NumberFormatException e) {
                    System.out.println("Skipping invalid loan amount in line: " + line);
                }
            }
    
            // Display the latest remaining amount (from the last row) formatted to two decimal places
            System.out.println("Loan: " + String.format("%.2f", latestRemainingAmount));
        } catch (IOException e) {
            System.out.println("Error loading loans for user: " + username);
            e.printStackTrace();
        }
    }
    

    // Integrate Data Visualization
    public static void visualizeData(String username) {
        try {
            // Paths to user-specific CSV files
            String savingsPath = "savings_" + username + ".csv";
            String loansPath = "loans_" + username + ".csv";
            String transactionsPath = "transaction_history_" + username + ".csv";

            // Call DataVisualization methods
            DataVisualization.main(new String[] { savingsPath, loansPath, transactionsPath });
        } catch (Exception e) {
            System.out.println("Error visualizing data: " + e.getMessage());
        }
    }

}
