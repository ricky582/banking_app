package uk.co.asepstrath.bank;

public class Transaction implements Comparable<Transaction> {

    private final Account withdrawAccount;  //Account to take funds from
    private final Account depositAccount;   //Recipient of funds
    private final String timestamp;         //Time at which transaction occurred
    private final String id;                //unique identifier for transaction
    private final double amount;            //amount of money to be moved
    private final String currency;          //string to represent currency (e.g "GBP")
    private boolean done;                   //starts at false, switches to true once funds have been updated in BOTH accounts

    //constructor for transactions
    public Transaction(Account withdrawAccount, Account depositAccount, String timestamp, String id, double amount, String currency){
        this.withdrawAccount = withdrawAccount;
        this.depositAccount = depositAccount;
        this.timestamp = timestamp;
        this.id = id;
        this.amount = amount;
        this.currency = currency;
        this.done = false;
    }

    //getter for withdrawAccount
    public Account getWidAcc(){return withdrawAccount;}

    //getter for depositAccount
    public Account getDepAcc(){return depositAccount;}

    //getter for timestamp
    public String getTimestamp(){return timestamp;}

    //getter for ID
    public String getId(){return id;}

    //getter for amount
    public double getAmount(){return amount;}

    //getter for currency
    public String getCurrency(){return currency;}

    //getter for done
    public boolean getDone(){return done;}

    //marks transaction as complete
    public void finished(){done = true;}

    //compareTo() now orders by timestamp so transactions can be applied in order of which they occured
    @Override
    public int compareTo(Transaction o) {
        return this.timestamp.compareTo(o.timestamp);
    }
}
