package uk.co.asepstrath.bank;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class TransacInfoTest {
    TransactionInfo a;
    Account targetAcc;
    ArrayList<Transaction> transacs;

    @BeforeEach
    public void initialise() {
        Account ext = new Account("5346-9684");
        targetAcc = new Account("0000-0001", "Bill", 200.46, "Savings Account", "USD");
        Account test1 = new Account("4548-4533", "Robert", 1000.01, "Investment Account", "GBP");
        Account test2 = new Account("6798-4432", "Martin", 56.89, "Savings Account", "RUB");
        Account test3 = new Account("8756-8900", "Andrew", 78900.65, "Auto Loan Account", "EUR");

        transacs = new ArrayList<>();
        Transaction tr2 = new Transaction(test1, targetAcc, "6:50:43", "0002", 189.02, "GBP");
        Transaction tr1 = new Transaction(targetAcc, ext, "4:50:43", "0001", 50.21, "USD");
        Transaction tr4 = new Transaction(test3, targetAcc, "11:30:43", "0004", 7000.99, "EUR");
        Transaction tr3 = new Transaction(targetAcc, test2, "11:30:42", "0003", 45.65, "RUB");
        transacs.add(tr2);
        transacs.add(tr1);
        transacs.add(tr4);
        transacs.add(tr3);

        a = new TransactionInfo(targetAcc, transacs);
        a.getCurrentBal();

    }

    @Test
    public void testNotNull() {
        Assertions.assertNotNull(a);
    }

    @Test
    public void testInitialBal() {
        Assertions.assertEquals(a.getInitialBal(), 200.46);
    }

    @Test
    public void testAccount() {
        Assertions.assertEquals(a.getAccount(), targetAcc);
    }

    @Test
    public void testTransactions() {
        Assertions.assertEquals(a.getTransactions(), transacs);
    }

    @Test
    public void testBalance() {
        Assertions.assertEquals(a.getCurrentBal(), 7294.61);
    }

    @Test
    public void testAllSuccessful() {
        Assertions.assertEquals(a.getNumSuccessful(), 4);
        Assertions.assertEquals(a.getNumFailed(), 0);
    }

    @Test
    public void rightOrder() {
        for (int i = 1; i < a.getTransactions().size(); i++) {
            Assertions.assertTrue(a.getTransactions().get(i - 1).compareTo(a.getTransactions().get(i)) <= -1);
        }
    }

    @Test
    public void failingTransaction() {
        Account empty = new Account("9861-4329", 0);
        a.getTransactions().add(new Transaction(targetAcc, empty, "23:30:42", "0005", 7294.62, "BTC"));
        a.getTransactions().add(new Transaction(empty, targetAcc, "22:30:42", "0006", 0.01, "XRP"));
        testBalance();
        Assertions.assertEquals(empty.getBalance(), 0);
        Assertions.assertEquals(a.getNumFailed(), 2);
        Assertions.assertEquals(a.getNumSuccessful(), 4);
    }
}

