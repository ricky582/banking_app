package uk.co.asepstrath.bank;

import com.typesafe.config.ConfigException;

public class Account {

    private String name;
    private double balance;
    private String id;
    private String accountType;
    private String currency;
    private boolean localAcc;

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
        localAcc = true;
    }

    public Account(String id){
        this.id = id;
        localAcc = false;
    }

    @Override
    public String toString()
    {
        try {
            if (localAcc) {
                return String.valueOf(id + " " + name + " " + getBalance() + " " + accountType + " " + currency);
            }
            else return String.valueOf(id + " " + "false");
        }
        catch(NullPointerException e){return "";}
    }


    public void deposit(double amount) {
        try{
            balance += amount;
        }
        catch(NullPointerException e){}
    }

    public void withdraw(double amount) throws ArithmeticException {
        try {
            if (amount > balance) {
                throw new ArithmeticException();
            }
            balance -= amount;
        }
        catch(NullPointerException e){}
    }

    public double getBalance() {
        try {
            balance = balance * 100;
            balance = Math.round(balance);
            balance = balance / 100;
            return balance;
            //Doing Math.round(bal*100)/100 would return 1dp, and just returning bal had some funky math (double math not accurate)
        }
        catch (NullPointerException e){
            return -1;
        }
    }

    public String getName() {
        if (localAcc) return name;
        else return "";
    }

    public String getID() {
        return id;
    }

    public String getAccountType() {
        if (localAcc) return accountType;
        else return "External";
    }

    public String getCurrency() {
        if (localAcc) return currency;
        else return "";
    }

    public boolean getLocal() {return localAcc;}
}