package uk.co.asepstrath.bank;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AccountsTest {
    @Test
    void testGetBalance()
    {
        Account a = new Account("Fred", 45.3);
        Account b = new Account("1232", "Garry", 56.3, "Investment Account", "GBP");
        Account c = new Account("1234");
        Assertions.assertEquals(45.3, a.getBalance());
        Assertions.assertEquals(56.3, b.getBalance());
        Assertions.assertEquals(-1, c.getBalance());
    }

    @Test
    void testGetName(){
        Account a = new Account("Fred", 45.3);
        Account b = new Account("1232", "Garry", 56.3, "Investment Account", "GBP");
        Account c = new Account("1234");
        Assertions.assertEquals("Fred", a.getName());
        Assertions.assertEquals("Garry", b.getName());
        Assertions.assertEquals("", c.getName());
    }

    @Test
    void testGetAccountType(){
        Account a = new Account("Fred", 45.3);
        Account b = new Account("1232", "Garry", 56.3, "Investment Account", "GBP");
        Account c = new Account("1234");
        Assertions.assertNull(a.getAccountType());
        Assertions.assertEquals("Investment Account", b.getAccountType());
        Assertions.assertEquals("External", c.getAccountType());
    }

    @Test
    void testGetCurrency(){
        Account a = new Account("Fred", 45.3);
        Account b = new Account("1232", "Garry", 56.3, "Investment Account", "GBP");
        Account c = new Account("1234");
        Assertions.assertNull(a.getCurrency());
        Assertions.assertEquals("GBP", b.getCurrency());
        Assertions.assertEquals("", c.getCurrency());
    }

    @Test
    void testNotNull() {
        Account a = new Account("Fred", 45.3);
        Account b = new Account("1232", "Garry", 56.3, "Investment Account", "GBP");
        Account c = new Account("1234");
        Assertions.assertNotNull(a);
        Assertions.assertNotNull(b);
        Assertions.assertNotNull(c);

    }

    @Test
    void testDeposit() {
        Account a = new Account("a",20);
        a.deposit(50);
        Assertions.assertEquals(70, a.getBalance());
    }

    @Test
    void testWithdraw() {
        Account a = new Account("a",40);
        a.withdraw(20);
        Assertions.assertEquals(20,a.getBalance());
    }

    @Test
    void testNoOverdraft() {
        Account a = new Account("a",30);
        Assertions.assertThrows(ArithmeticException.class,() -> a.withdraw(100));
    }

    @Test
    void testMultipleTransactions() {
        Account a = new Account("a",20);
        for(int i = 0; i < 5; i++) {
            a.deposit(10);
        }

        for(int i = 0; i < 3; i++) {
            a.withdraw(20);
        }

        Assertions.assertEquals(10, a.getBalance());
    }

    @Test
    void testDecimal() {
        Account a = new Account("a",5.45);
        a.deposit(17.56);
        Assertions.assertEquals(23.01, a.getBalance());
    }

    @Test
    void testNewToString(){
        Account a = new Account("1234");
        Account b = new Account("5678", "test", 100.76, "Test", "GBP");
        Account NULL = null;
        Assertions.assertEquals(a.toString(), "1234 false", a.toString());
        Assertions.assertEquals(b.toString(), "5678 test 100.76 Test GBP",b.toString());
        Assertions.assertThrows(NullPointerException.class, () -> NULL.toString());
    }
}