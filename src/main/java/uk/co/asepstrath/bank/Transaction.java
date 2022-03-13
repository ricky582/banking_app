package uk.co.asepstrath.bank;

public class Transaction implements Comparable<Transaction> {

    private final Account withdrawAccount;  //Account to take funds from
    private final Account depositAccount;   //Recipient of funds
    private final String timestamp;         //Time at which transaction occurred
    private final String id;                //unique identifier for transaction
    private final double amount;            //amount of money to be moved
    private final String currency;          //string to represent currency (e.g "GBP")
    private int status;                   //starts at 0, will switch to 1 if transactions is successful or -1 if it fails

    //constructor for transactions
    public Transaction(Account withdrawAccount, Account depositAccount, String timestamp, String id, double amount, String currency){
        this.withdrawAccount = withdrawAccount;
        this.depositAccount = depositAccount;
        this.timestamp = timestamp;
        this.id = id;
        this.amount = amount;
        this.currency = currency;
        this.status = 0;
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

    //getter for status
    public int getStatus(){return status;}

    public String getDone(){
        if (this.status == 0){
            return "Incomplete";
        }
        else if (this.status == 1){
            return "Done";
        }
        else {
            return "Failed";
        }
    }

    //marks transaction as complete
    public void setStatus(int s){
        if(s <= 1 && s >=-1) {
            status = s;
        }
    }

    //compareTo() now orders by timestamp so transactions can be applied in order of which they occured
    @Override
    public int compareTo(Transaction o) {
        return this.timestamp.compareTo(o.timestamp);
    }

}
