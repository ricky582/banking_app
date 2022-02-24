package uk.co.asepstrath.bank;

public class Account {

    private String name;
    private double bal;
    private String id;
    private String accountType;
    private String currency;

    public Account(String n, double amount) {
        name = n;
        bal = amount;
    }

    public Account(String id, String n, double amount, String accountType, String currency) {
        id = id;
        name = n;
        bal = amount;
        accountType = accountType;
        currency = currency;
    }

    @Override
    public String toString()
    {
        return String.valueOf(name + " " + getBalance());
    }


    public void deposit(double amount) {
        bal += amount;
    }

    public void withdraw(double amount) throws ArithmeticException {
        if (amount > bal) {
            throw new ArithmeticException();
        }

        bal -= amount;
    }

    public double getBalance() {
        bal = bal * 100;
        bal = Math.round(bal);
        bal = bal / 100;
        return bal;

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