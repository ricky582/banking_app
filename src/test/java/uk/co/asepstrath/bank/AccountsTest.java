package uk.co.asepstrath.bank;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AccountsTest {
    @Test
    public void createAccount()
    {
        Account a = new Account("a", 0);
        Assertions.assertTrue(a.getBalance() == 0);
    }

    @Test
    public void addFunds()
    {
        Account a = new Account("a",20);
        a.deposit(50);
        Assertions.assertEquals(70, a.getBalance());
    }

    @Test
    public void spendingSpree()
    {
        Account a = new Account("a",40);
        a.withdraw(20);
        Assertions.assertEquals(20,a.getBalance());
    }

    @Test
    public void noOverdraft()
    {
        Account a = new Account("a",30);
        Assertions.assertThrows(ArithmeticException.class,() -> a.withdraw(100));
    }

    @Test
    public void superSaving()
    {
        Account a = new Account("a",20);
        for(int i = 0; i < 5; i++)
        {
            a.deposit(10);
        }

        for(int i = 0; i < 3; i++)
        {
            a.withdraw(20);
        }

        Assertions.assertEquals(10, a.getBalance());
    }

    @Test
    public void takingCareOfPennies()
    {
        Account a = new Account("a",5.45);
        a.deposit(17.56);
        Assertions.assertEquals(23.01, a.getBalance());
    }
}
