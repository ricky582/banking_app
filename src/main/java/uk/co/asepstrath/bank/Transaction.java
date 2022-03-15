package uk.co.asepstrath.bank;

import java.util.Objects;
import java.util.Random;
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

    public void doTransaction(){
        if (this.withdrawAccount.getLocal() && this.status == 0) {
            if (withdrawAccount.getBalance() - amount >= 0) {
                withdrawAccount.withdraw(amount);
                depositAccount.deposit(amount);
                status = 1;
            }
            else this.status = -1;
        }
        else if (status == 0){
            depositAccount.deposit(amount);
            this.status = 1;
        }
    }

    //compareTo() now orders by timestamp so transactions can be applied in order of which they occured
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
