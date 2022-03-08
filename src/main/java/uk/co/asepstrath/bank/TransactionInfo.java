package uk.co.asepstrath.bank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

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
        Collections.sort(this.transactions);
        this.initialBal = account.getBalance();
    }

    public Account getAccount() {return account;}

    public ArrayList<Transaction> getTransactions() {return transactions;}

    public double getInitialBal() {return initialBal;}

    public double getCurrentBal() {
        double currentBal = initialBal;
        for(Transaction trns : transactions){
            if (account.getID().equals(trns.getWidAcc().getID())){
                if (currentBal-trns.getAmount() >= 0) {
                    currentBal -= trns.getAmount();
                    if (!trns.getDone()) {
                        numSuccessful++;
                        trns.finished();
                    }
                }
                else numFailed++;
            }
            else {
                if (trns.getWidAcc().getBalance()-trns.getAmount() >= 0 || !trns.getWidAcc().getLocal()) {
                    currentBal += trns.getAmount();
                    if (!trns.getDone()) {
                        numSuccessful++;
                        trns.finished();
                    }
                }
                else numFailed++;
            }
        }
        //to avoid same error from accounts
        currentBal = currentBal * 100;
        currentBal = Math.round(currentBal);
        currentBal = currentBal / 100;
        return currentBal;
    }

    public int getNumFailed() {
        return numFailed;
    }

    public int getNumSuccessful() {
        return numSuccessful;
    }
}

