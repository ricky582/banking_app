package uk.co.asepstrath.bank;

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

    @Override
    public int compareTo(Transaction o) {
        return this.timestamp.compareTo(o.timestamp);
    }
}
