package uk.co.asepstrath.bank;
import java.util.Random;
public class Transaction implements Comparable<Transaction> {

    private Account withdrawAccount;
    private Account depositAccount;
    private String timestamp;
    private String id;
    private double amount;
    private String currency;
    private boolean done;

    public Transaction(Account withdrawAccount, Account depositAccount, String timestamp, String id, double amount, String currency){
        this.withdrawAccount = withdrawAccount;
        this.depositAccount = depositAccount;
        this.timestamp = timestamp;
        this.id = id;
        this.amount = amount;
        this.currency = currency;
        this.done = false;
    }

    public Account getWidAcc(){return withdrawAccount;}

    public Account getDepAcc(){return depositAccount;}

    public String getTimestamp(){return timestamp;}

    public String getId(){return id;}

    public double getAmount(){return amount;}

    public String getCurrency(){return currency;}

    public boolean getDone(){return done;}

    public void finished(){done = true;}

    public String generateId() {
        int n1 = 7;
        int n2 = 4;
        int n3 = 4;
        int n4 = 4;
        int n5 = 12;
        String result = "";
        String AlphaNumericString = "abcdefg"
                + "0123456789";
        StringBuilder sb1 = new StringBuilder(n1);
        StringBuilder sb2 = new StringBuilder(n2);
        StringBuilder sb3 = new StringBuilder(n3);
        StringBuilder sb4 = new StringBuilder(n4);
        StringBuilder sb5 = new StringBuilder(n5);

        Random rand = new Random();
        int upperBound = 6;
        int randInt = rand.nextInt(upperBound);

        for (int i = 0; i < n1; i++) {
            int index = (int)(AlphaNumericString.length() * Math.random());
            sb1.append(AlphaNumericString.charAt(index));
        }
        for (int i = 0; i < n2; i++) {
            int index = (int)(AlphaNumericString.length() * Math.random());
            sb2.append(AlphaNumericString.charAt(index));
        }
        for (int i = 0; i < n3; i++) {
            int index = (int)(AlphaNumericString.length() * Math.random());
            sb3.append(AlphaNumericString.charAt(index));
        }
        for (int i = 0; i < n4; i++) {
            int index = (int)(AlphaNumericString.length() * Math.random());
            sb4.append(AlphaNumericString.charAt(index));
        }
        for (int i = 0; i < n5; i++) {
            int index = (int)(AlphaNumericString.length() * Math.random());
            sb5.append(AlphaNumericString.charAt(index));
        }

        result = (randInt + "" + sb1 + "-" + sb2 + "-" + sb3 + "-" + sb4 + "-" + sb5);
        return result;
    }

    @Override
    public int compareTo(Transaction o) {
        return this.timestamp.compareTo(o.timestamp);
    }
}
