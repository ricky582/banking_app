package uk.co.asepstrath.bank;

public class Account {

    private String name;
    private double balance;
    private String id;
    private String accountType;
    private String currency;

    public Account(String n, double amount) {
        name = n;
        balance = amount;
    }

    public Account(String id, String name, double balance, String accountType, String currency) {
        this.id = id;
        this.name = name;
        this.balance = balance;
        this.accountType = accountType;
        this.currency = currency;
    }

    @Override
    public String toString()
    {
        return String.valueOf(id + " " + name + " " + getBalance() + " " + accountType + " " + currency);
    }


    public void deposit(double amount) {
        balance += amount;
    }

    public void withdraw(double amount) throws ArithmeticException {
        if (amount > balance) {
            throw new ArithmeticException();
        }

        balance -= amount;
    }

    public double getBalance() {
        balance = balance * 100;
        balance = Math.round(balance);
        balance = balance / 100;
        return balance;

        //Doing Math.round(bal*100)/100 would return 1dp, and just returning bal had some funky math (double math not accurate)
    }

    public String getName() {
        return name;
    }

    public String getID() {
        return id;
    }

    public String getAccountType() {
        return accountType;
    }

    public String getCurrency() {
        return currency;
    }
}