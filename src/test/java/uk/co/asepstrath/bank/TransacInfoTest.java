package uk.co.asepstrath.bank;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

class TransacInfoTest {
    TransactionInfo a;
    Account targetAcc;
    ArrayList<Transaction> transacs;

    @BeforeEach
    void initialise() {
        Account ext = new Account("5346-9684");
        targetAcc = new Account("0000-0001", "Bill", 200.46, "Savings Account", "USD");
        Account test1 = new Account("4548-4533", "Robert", 1000.01, "Investment Account", "GBP");
        Account test2 = new Account("6798-4432", "Martin", 56.89, "Savings Account", "RUB");
        Account test3 = new Account("8756-8900", "Andrew", 78900.65, "Auto Loan Account", "EUR");

        transacs = new ArrayList<>();
        Transaction tr2 = new Transaction(test1, targetAcc, "6:50:43", "0002", 189.02, "GBP");
        Transaction tr1 = new Transaction(targetAcc, ext, "4:50:43", "0001", 50.21, "USD");
        Transaction tr0 = new Transaction(ext, targetAcc, "4:50:42", "0000", 10, "USD");
        Transaction tr4 = new Transaction(test3, targetAcc, "11:30:43", "0004", 7000.99, "EUR");
        Transaction tr3 = new Transaction(targetAcc, test2, "11:30:42", "0003", 45.65, "RUB");
        transacs.add(tr2);
        transacs.add(tr1);
        transacs.add(tr4);
        transacs.add(tr0);
        transacs.add(tr3);

        a = new TransactionInfo(targetAcc, transacs);
        a.getCurrentBal();

    }

    @Test
    void testNotNull() {
        Assertions.assertNotNull(a);
    }

    @Test
    void testInitialBal() {
        Assertions.assertEquals(200.46, a.getInitialBal());
    }

    @Test
    void testAccount() {
        Assertions.assertEquals(targetAcc, a.getAccount());
    }

    @Test
    void testTransactions() {
        Assertions.assertEquals(transacs, a.getTransactions());
    }

    @Test
    void testBalance() {
        Assertions.assertEquals(7304.61, a.getCurrentBal());
    }

    @Test
    void testAllSuccessful() {
        Assertions.assertEquals(5, a.getNumSuccessful());
        Assertions.assertEquals(0, a.getNumFailed());
    }

    @Test
    void rightOrder() {
        for (int i = 1; i < a.getTransactions().size(); i++) {
            Assertions.assertTrue(a.getTransactions().get(i - 1).compareTo(a.getTransactions().get(i)) <= -1);
        }
    }

    @Test
    void failingTransaction() {
        Account empty = new Account("9861-4329", 0);
        a.getTransactions().add(new Transaction(targetAcc, empty, "23:30:42", "0005", 7304.62, "BTC"));
        a.getTransactions().add(new Transaction(empty, targetAcc, "22:30:42", "0006", 0.01, "XRP"));
        testBalance();
        Assertions.assertEquals(0, empty.getBalance());
        Assertions.assertEquals(2, a.getNumFailed());
        Assertions.assertEquals(5, a.getNumSuccessful());
    }
}

