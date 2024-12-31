package src;

import java.text.DecimalFormat;
import java.util.Scanner;

public class DepositInterestPredictor {

    // Bank interest rates
    private static final double RHB_RATE = 2.6;
    private static final double MAYBANK_RATE = 2.5;
    private static final double HONG_LEONG_RATE = 2.3;
    private static final double ALLIANCE_RATE = 2.85;
    private static final double AMBANK_RATE = 2.55;
    private static final double STANDARD_CHARTERED_RATE = 2.65;

    // Decimal format for two decimal places
    private static final DecimalFormat df = new DecimalFormat("0.00");

    // Method to calculate monthly interest
    private static double calculateMonthlyInterest(double deposit, double interestRate) {
        return (deposit * interestRate) / 12 / 100; // Convert rate to decimal
    }

    // Display bank options
    private static void displayBankOptions() {
        System.out.println("\nChoose a bank:");
        System.out.println("1. RHB (2.6%)");
        System.out.println("2. Maybank (2.5%)");
        System.out.println("3. Hong Leong (2.3%)");
        System.out.println("4. Alliance (2.85%)");
        System.out.println("5. AmBank (2.55%)");
        System.out.println("6. Standard Chartered (2.65%)");
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("\nEnter your deposit amount: ");
            double deposit = scanner.nextDouble();

            // Display bank options
            displayBankOptions();
            System.out.print("Enter the number corresponding to your chosen bank: ");
            int bankChoice = scanner.nextInt();

            double interestRate = 0.0;
            String bankName = "";

            // Determine interest rate based on user choice
            switch (bankChoice) {
                case 1:
                    interestRate = RHB_RATE;
                    bankName = "RHB";
                    break;
                case 2:
                    interestRate = MAYBANK_RATE;
                    bankName = "Maybank";
                    break;
                case 3:
                    interestRate = HONG_LEONG_RATE;
                    bankName = "Hong Leong";
                    break;
                case 4:
                    interestRate = ALLIANCE_RATE;
                    bankName = "Alliance";
                    break;
                case 5:
                    interestRate = AMBANK_RATE;
                    bankName = "AmBank";
                    break;
                case 6:
                    interestRate = STANDARD_CHARTERED_RATE;
                    bankName = "Standard Chartered";
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    continue;
            }

            // Calculate monthly interest
            double monthlyInterest = calculateMonthlyInterest(deposit, interestRate);

            // Display results
            System.out.println("\n--- Deposit Interest Prediction ---");
            System.out.println("Bank: " + bankName);
            System.out.println("Deposit Amount: RM" + df.format(deposit));
            System.out.println("Interest Rate: " + df.format(interestRate) + "%");
            System.out.println("Monthly Interest Earned: RM" + df.format(monthlyInterest));
            System.out.println("-----------------------------------");

            // Ask if the user wants another calculation
            System.out.print("Do you want to make another prediction? (y/n): ");
            String choice = scanner.next();
            if (!choice.equalsIgnoreCase("y")) {
                System.out.println("Goodbye!");
                break;
            }
        }
    }
}
