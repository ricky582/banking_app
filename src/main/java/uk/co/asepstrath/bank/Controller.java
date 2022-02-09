package uk.co.asepstrath.bank;
import io.jooby.StatusCode;
import io.jooby.annotations.*;
import io.jooby.exception.StatusCodeException;
import org.slf4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

@Path("/accounts")
public class Controller {

    private final DataSource dataSource;
    private final Logger logger;

    /*
        This constructor can take in any dependencies the controller may need to respond to a request
     */

    public Controller(DataSource ds, Logger log) {
        dataSource = ds;
        logger = log;
    }

    /*
        This request makes a call to the passed in data source (The Database) which has been set up in App.java
     */

    @GET
    public ArrayList displayAccounts() {
        ArrayList<Account> accounts = new ArrayList<>();
        ArrayList<String> accountOwners = new ArrayList<>();
        accountOwners.add("Rachel");
        accountOwners.add("Monica");
        accountOwners.add("Phoebe");
        accountOwners.add("Joey");
        accountOwners.add("Chandler");
        accountOwners.add("Ross");

        try (Connection connection = dataSource.getConnection()) {
            Statement statement = connection.createStatement();

            for(int i = 0; i <= 1; i++) {
                ResultSet set = statement.executeQuery("SELECT * FROM AccountDataset Where Name = '" + accountOwners.get(i) + "'");
                set.next();
                System.out.println(set.getString("Name"));
                System.out.println(set.getFloat("Balance"));
                Account newAccount = new Account(set.getString("Name"), set.getFloat("Balance"));
                accounts.add(newAccount);
            }

            return accounts;
        } catch (SQLException e) {
            logger.error("Database Error Occurred", e);
            throw new StatusCodeException(StatusCode.SERVER_ERROR, "Database Error Occurred");
        }
    }
}