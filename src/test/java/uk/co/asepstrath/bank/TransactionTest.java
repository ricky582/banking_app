package uk.co.asepstrath.bank;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TransactionTest {
    Account test1 = new Account("4548-4533", "Robert", 1000.01, "Investment Account", "GBP");
    Account test2 = new Account("6798-4432", "Martin", 56.89, "Savings Account", "RUB");
    @Test
    void testUniqueIds(){
        Transaction t = new Transaction(test1, test2, "4:50:43", "",30, "GBP");
        Transaction u = new Transaction(test1, test2, "4:50:43", "", 30, "GBP");
        Assertions.assertNotEquals(t.generateId(), u.generateId());
    }

    @Test
    void testStatus(){
        Transaction t = new Transaction(test1, test2, "4:50:43", "",1000.01, "GBP");
        Assertions.assertEquals("Incomplete", t.getDone());
        t.doTransaction();
        Assertions.assertEquals("Done", t.getDone());
        Transaction u = new Transaction(test1, test2, "4:50:44", "",0.01, "GBP");
        u.doTransaction();
        Assertions.assertEquals("Failed", u.getDone());
    }

    @Test
    void testSetStatus(){
        Transaction t = new Transaction(test1, test2, "4:50:43", "",1000.01, "GBP");
        t.setStatus(1);
        Assertions.assertEquals(t.getStatus(), 1);
        t.setStatus(2);
        Assertions.assertEquals(t.getStatus(), 1);
    }
}