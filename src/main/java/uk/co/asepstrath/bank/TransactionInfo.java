package uk.co.asepstrath.bank;

import java.util.ArrayList;

public class TransactionInfo {
    private double initialBal;
    private Account account;
    private ArrayList<Transaction> transactions;
    //for second half of user story
    private int numSuccessful;
    private int numFailed;

    public TransactionInfo(Account account, ArrayList<Transaction> transactions){
        this.account = account;
        this.transactions = transactions;
        this.initialBal = account.getBalance();
    }

    public Account getAccount() {return account;}

    public ArrayList<Transaction> getTransactions() {return transactions;}

    public double getInitialBal() {return initialBal;}

    public double getCurrentBal() {
        double currentBal = initialBal;
        for(Transaction trns : transactions){
            if (account.getID() == trns.getWidAcc()){
                currentBal -= trns.getAmount();
            }
            else {
                currentBal += trns.getAmount();
            }
        }
        //to avoid same error from accounts
        currentBal = currentBal * 100;
        currentBal = Math.round(currentBal);
        currentBal = currentBal / 100;
        return currentBal;
    }
}

