package src.Savings.savings;

public class SavingsRecord {

    private int savingsId;
    private String username; // Using username instead of userId for user-specific record
    private String status;
    private int percentage;
    private double totalSavings; // Add a field for the savings amount

    // Constructor to initialize the savings record
    public SavingsRecord(int savingsId, String username, String status, int percentage, double totalSavings) {
        this.savingsId = savingsId;
        this.username = username;
        this.status = status;
        this.percentage = percentage;
        this.totalSavings = totalSavings; // Initialize with zero savings or calculate if needed
    }

    // Getter methods
    public int getSavingsId() {
        return savingsId;
    }

    public String getUsername() {
        return username;
    }

    public String getStatus() {
        return status;
    }

    public int getPercentage() {
        return percentage;
    }

    public double getTotalSavings() {
        return totalSavings;
    }

    // Setter for totalSavings
    public void setTotalSavings(double totalSavings) {
        this.totalSavings = totalSavings;
    }

    // Add this setter method to modify the percentage
    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    // Method to update the savings record if needed
    public void addSavings(double amount) {
        this.totalSavings += amount;
    }

    // Optional: Override toString for easier debugging or display
    @Override
    public String toString() {
        return "SavingsRecord [savingsId=" + savingsId + ", username=" + username + ", status=" + status
                + ", percentage=" + percentage + ", totalSavings=" + totalSavings + "]";
    }
}
