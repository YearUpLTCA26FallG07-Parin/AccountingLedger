public class Transaction {

    private final String date;
    private final String time;
    private final String description;
    private final String vendor;
    private final double amount;

    public Transaction(String date,
                       String time,
                       String description,
                       String vendor,
                       double amount) {

        this.date = date;
        this.time = time;
        this.description = description;
        this.vendor = vendor;
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getDescription() {
        return description;
    }

    public String getVendor() {
        return vendor;
    }

    public double getAmount() {
        return amount;
    }
}