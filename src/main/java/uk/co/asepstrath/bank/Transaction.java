package uk.co.asepstrath.bank;

public class Transaction {

    private String withdrawAccount;
    private String depositAccount;
    private String timestamp;
    private String id;
    private double amount;
    private String currency;

    public Transaction(String withdrawAccount, String depositAccount, String timestamp, String id, double amount, String currency){
        this.withdrawAccount = withdrawAccount;
        this.depositAccount = depositAccount;
        this.timestamp = timestamp;
        this.id = id;
        this.amount = amount;
        this.currency = currency;
    }

    public String getWidAcc(){return withdrawAccount;}

    public String getDepAcc(){return depositAccount;}

    public String getTimestamp(){return timestamp;}

    public String getId(){return id;}

    public double getAmount(){return amount;}

    public String getCurrency(){return currency;}


}
