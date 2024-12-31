package src;

import org.mindrot.jbcrypt.BCrypt; // BCrypt for hashing
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Userlogin {
    private static final String UserInfo = System.getProperty("user.home") + "/Desktop/Ledger/UserInfo.csv";
    private static final File USER_FILE = new File(UserInfo);
    private static Writer writer;
    private static Scanner scan;
    private static final Scanner sc = new Scanner(System.in);
    public static String username, password, email, userID;

    public static void main(String[] args) throws IOException {
        createFileIfNotExist(UserInfo);
        initializeResources();
        if (USER_FILE.length() == 0) {
            writer.write("Username,Password,Email,UserID\n");
            writer.flush();
        }

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("== Ledger System ==");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    login();
                    break;
                case 2:
                    register();
                    break;
                case 3:
                    System.out.println("Thank you for using the Ledger System!");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    public static void createFileIfNotExist(String filePath) throws IOException {
        File fileToCreate = new File(filePath);
        File parentDir = fileToCreate.getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }
        if (!fileToCreate.exists()) {
            fileToCreate.createNewFile();
        }
    }

    public static void initializeResources() throws IOException {
        writer = new FileWriter(USER_FILE, true);
        scan = new Scanner(USER_FILE);
    }

    public static void login() throws FileNotFoundException {
        System.out.print("Enter username: ");
        username = sc.nextLine();
        System.out.print("Enter password: ");
        password = sc.nextLine();
        System.out.print("Enter email: ");
        email = sc.nextLine();

        if (isValidLogin(username, password, email, USER_FILE)) {
            System.out.println("Login successful");
            TransactionManager.dashboard(email);
        } else {
            System.out.println("Incorrect password, username, or email");
        }
    }

    public static void register() throws IOException {
        System.out.print("Enter username: ");
        username = sc.nextLine();
        System.out.print("Enter password: ");
        password = sc.nextLine();
        System.out.print("Enter email: ");
        email = sc.nextLine();

        if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            System.out.println("All fields are required");
            return;
        }

        if (username.length() <= 6 || username.length() >= 30 || !username.matches("^[a-zA-Z][a-zA-Z0-9]*$")) {
            System.out.println("Invalid username. Username should be 6 to 30 characters long and starts with a letter. No symbols allowed.");
            return;
        }

        if (password.length() <= 8 || password.matches("[a-zA-Z]+") || password.matches("\\d+")) {
            System.out.println("Invalid password. Password should be at least 8 characters and consist of both numbers and letters.");
            return;
        }

        if (!isValidEmail(email)) {
            System.out.println("Invalid email format. Please enter a valid email: (name@example.com)");
            return;
        }

        if (isUniqueRegistrationCheck(username, email, USER_FILE)) {
            String hashedPassword = hashPassword(password); // Hash the password
            userID = String.valueOf(userID(username, USER_FILE));
            writer.write(username + "," + hashedPassword + "," + email + "," + userID + "\n");
            writer.flush();
            System.out.println("Registration successful!");
        } else {
            System.out.println("Email or username already exists. Please try again.");
        }
    }

    private static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    private static boolean verifyPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }

    public static boolean isUniqueRegistrationCheck(String username, String email, File UserRegistrationData) throws FileNotFoundException {
        boolean isUnique = true;
        Scanner scan = new Scanner(UserRegistrationData);
        if (scan.hasNextLine()) {
            scan.nextLine();
        } // skip header

        String[] data;
        while (scan.hasNextLine()) {
            String fileContent = scan.nextLine();
            data = fileContent.split(",");

            if (data[0].equalsIgnoreCase(username) || data[2].equalsIgnoreCase(email)) {
                isUnique = false;
                break;
            }
        }

        scan.close();

        return isUnique;
    }

    public static boolean isValidLogin(String username, String password, String email, File UserRegistrationData) throws FileNotFoundException {
        Scanner scan = new Scanner(UserRegistrationData);
        boolean isValidLogin = false;

        if (scan.hasNextLine()) {
            scan.nextLine();
        }

        String[] data;
        while (scan.hasNextLine()) {
            String fileContent = scan.nextLine();
            data = fileContent.split(",");

            if (data[0].equalsIgnoreCase(username) && data[2].equalsIgnoreCase(email) && verifyPassword(password, data[1])) {
                isValidLogin = true;
                break;
            }
        }
        return isValidLogin;
    }

    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern p = Pattern.compile(emailRegex);
        return email != null && p.matcher(email).matches();
    }

    public static int userID(String username, File UserRegistrationData) throws FileNotFoundException {
        scan = new Scanner(UserRegistrationData);
        String fileContent = "";
        if (scan.hasNextLine()) {
            scan.nextLine();
        }

        int userID = 1;

        while (scan.hasNextLine()) {
            scan.nextLine();
            userID++;
        }
        scan.close();
        return userID;
    }
}
