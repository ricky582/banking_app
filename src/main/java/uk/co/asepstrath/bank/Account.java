package uk.co.asepstrath.bank;

public class Account {

    private String name;            //name on account
    private double balance;         //account balance
    private String id;              //unique ID for account
    private String accountType;     //string to represent account type (e.g "Investment Account")
    private String currency;        //string to represent currency (e.g "GBP")
    private final boolean localAcc; //represents whether account is in bank or not
    private final double initialBal;

    //simple account constructor mainly used for testing
    public Account(String n, double amount) {
        this.name = n;
        this.balance = amount;
        this.initialBal = amount;
        this.localAcc = true;
    }

    //main constructor for account
    public Account(String id, String name, double balance, String accountType, String currency) {
        this.id = id;
        this.name = name;
        this.balance = balance;
        this.initialBal = balance;
        this.accountType = accountType;
        this.currency = currency;
        this.localAcc = true;
    }

    //constructor for non-local accounts (accounts that appear in transactions but are not under our bank
    public Account(String id){
        this.id = id;
        this.localAcc = false;
        this.initialBal = -1;
    }

    //better formatted toString function
    @Override
    public String toString()
    {
        if (localAcc) return id + " " + name + " " + getBalance() + " " + accountType + " " + currency;
        else return id + " " + "false";
    }

    //deposits amount to account
    public void deposit(double amount) {
        balance += amount;
    }

    //withdraws amount from account
    public void withdraw(double amount) throws ArithmeticException {
        if (amount > balance) throw new ArithmeticException();
        balance -= amount;
    }

    public double getInitialBal() {
        return initialBal;
    }

    //balance getter
    public double getBalance() {
        if(localAcc) return Math.round(balance*100.00)/100.00;
        else return -1;
    }

    //balance setter
    public void setBalance(double bal){
        balance = bal;
    }

    //name getter
    public String getName() {
        if (localAcc) return name;
        else return "";
    }

    //id getter
    public String getID() {
        return id;
    }

    //accountType getter
    public String getAccountType() {
        if (localAcc) return accountType;
        else return "External";
    }

    //currency getter
    public String getCurrency() {
        if (localAcc) return currency;
        else return "";
    }

    //localAcc getter
    public boolean getLocal() {
        return localAcc;
    }
}