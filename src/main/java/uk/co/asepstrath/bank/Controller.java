package uk.co.asepstrath.bank;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.jooby.StatusCode;
import io.jooby.annotations.*;
import io.jooby.exception.StatusCodeException;
import org.slf4j.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

//Path = IP/argument, i.e("localhost:8080/accounts)
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

    @GET //@path + any extra, in this case since no argument with @get, just at @path
    public String displayAccounts() {
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

            for(int i = 0; i <= 5; i++) {
                ResultSet set = statement.executeQuery("SELECT * FROM AccountDataset Where Name = '" + accountOwners.get(i) + "'");
                set.next();
                Account newAccount = new Account(set.getString("Name"), set.getFloat("Balance"));
                accounts.add(newAccount);
            }

            ObjectMapper objectMapper = new ObjectMapper();
            String objectOutput = objectMapper.writeValueAsString(accounts);

            return objectOutput.substring(1, objectOutput.length() -1);
        } catch (SQLException e) {
            logger.error("Database Error Occurred", e);
            throw new StatusCodeException(StatusCode.SERVER_ERROR, "Database Error Occurred");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }
}
