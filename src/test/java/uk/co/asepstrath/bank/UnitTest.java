package uk.co.asepstrath.bank;

import org.junit.jupiter.api.*;
import org.slf4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UnitTest {
    /*
    Unit tests should be here
    Example can be found in example/UnitTest.java
     */


    //Account Tests
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
