package src.Transaction;

import java.util.Scanner;

public class TransactionMainMenu {

    // show the main menu, give hint and select the option
    public static void main(String[] args) {
        
        boolean loop = true;
        Scanner k = new Scanner(System.in);
        String key = "";
        do {
            
            System.out.println("== Transaction ==");
            System.out.println("1.Debit");
            System.out.println("2.Credit");
            System.out.println("3.History");
            System.out.println("4.Savings");
            System.out.println("5.Credit loan");
            System.out.println("6.Deposit Interest Predictor");
            System.out.println("7.Logout");

            System.out.println();
            System.out.print(">");
            key = k.next();

            // use switch control
            switch (key) {
                case "1":
                    System.out.println("== Debit ==");
                    break;
                case "2":
                    System.out.println("== Credit ==");
                    break;
                case "3":
                    System.out.println("== Savings ==");
                    break; 
                case "4":
                    System.out.println("== History ==");
                    break; 
                case "5":
                    System.out.println("== Credit loan ==");
                    break;
                case "6":
                    System.out.println("== Deposit Interest Predictor ==");
                    break;
                case "7":
                    System.out.println("Thank you for using \" Ledger System AlgoNauts \"");
                    loop = false;
                    break;      
                default:
                    System.out.println("Error. Please select again.");
                    break;
            }

        } while (loop);
    }
}
