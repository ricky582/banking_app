package uk.co.asepstrath.bank;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AccountsTest {
    @Test
    public void testGetBalance()
    {
        Account a = new Account("Fred", 45.3);
        Account b = new Account("1232", "Garry", 56.3, "Investment Account", "GBP");
        Account c = new Account("1234");
        Assertions.assertEquals(a.getBalance(), 45.3);
        Assertions.assertEquals(b.getBalance(), 56.3);
        Assertions.assertEquals(c.getBalance(), -1);
    }

    @Test
    public void testGetName(){
        Account a = new Account("Fred", 45.3);
        Account b = new Account("1232", "Garry", 56.3, "Investment Account", "GBP");
        Account c = new Account("1234");
        Assertions.assertEquals(a.getName(), "Fred");
        Assertions.assertEquals(b.getName(), "Garry");
        Assertions.assertEquals(c.getName(), "");
    }

    @Test
    public void testGetAccountType(){
        Account a = new Account("Fred", 45.3);
        Account b = new Account("1232", "Garry", 56.3, "Investment Account", "GBP");
        Account c = new Account("1234");
        Assertions.assertNull(a.getAccountType());
        Assertions.assertEquals(b.getAccountType(), "Investment Account");
        Assertions.assertEquals(c.getAccountType(), "External");
    }

    @Test
    public void testGetCurrency(){
        Account a = new Account("Fred", 45.3);
        Account b = new Account("1232", "Garry", 56.3, "Investment Account", "GBP");
        Account c = new Account("1234");
        Assertions.assertNull(a.getCurrency());
        Assertions.assertEquals(b.getCurrency(), "GBP");
        Assertions.assertEquals(c.getCurrency(), "");
    }

    @Test
    public void testNotNull()
    {
        Account a = new Account("Fred", 45.3);
        Account b = new Account("1232", "Garry", 56.3, "Investment Account", "GBP");
        Account c = new Account("1234");
        Assertions.assertNotNull(a);
        Assertions.assertNotNull(b);
        Assertions.assertNotNull(c);

    }

    @Test
    public void testDeposit()
    {
        Account a = new Account("a",20);
        a.deposit(50);
        Assertions.assertEquals(70, a.getBalance());
    }

    @Test
    public void testWithdraw()
    {
        Account a = new Account("a",40);
        a.withdraw(20);
        Assertions.assertEquals(20,a.getBalance());
    }

    @Test
    public void testNoOverdraft()
    {
        Account a = new Account("a",30);
        Assertions.assertThrows(ArithmeticException.class,() -> a.withdraw(100));
    }

    @Test
    public void testMultipleTransactions()
    {
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
    public void testDecimal()
    {
        Account a = new Account("a",5.45);
        a.deposit(17.56);
        Assertions.assertEquals(23.01, a.getBalance());
    }

    @Test
    public void testNewToString(){
        Account a = new Account("1234");
        Account b = new Account("5678", "test", 100.76, "Test", "GBP");
        Account NULL = null;
        Assertions.assertEquals(a.toString(), "1234 false");
        Assertions.assertEquals(b.toString(), "5678 test 100.76 Test GBP");
        Assertions.assertThrows(NullPointerException.class, () -> NULL.toString());
    }
}