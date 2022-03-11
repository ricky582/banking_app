package uk.co.asepstrath.bank;

import io.jooby.Jooby;
import io.jooby.OpenAPIModule;
import io.jooby.handlebars.HandlebarsModule;
import io.jooby.helper.UniRestExtension;
import io.jooby.hikari.HikariModule;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
@OpenAPIDefinition(
        info = @Info(
                title = "your-bank",
                description = "A banking app that allows for display, process and management of accounts and transactions",
                contact = @Contact(
                        url = "bank.com",
                        email = "admin@bank.com"
                ),

                version = "1.00"
        ),
        tags = @Tag(name = "Accounts")
)
public class App extends Jooby {

    {
        /*
        This section is used for setting up the Jooby Framework modules
         */
        install(new UniRestExtension());
        install(new HandlebarsModule());
        install(new HikariModule("mem"));
        install(new OpenAPIModule());

        /*
        This will host any files in src/main/resources/assets on <host>/assets
        For example in the dice template (dice.hbs) it references "assets/dice.png" which is in resources/assets folder
         */
        assets("/assets/*", "/assets");

        /*
        Now we set up our controllers and their dependencies
         */
        DataSource ds = require(DataSource.class);
        Logger log = getLog();

        mvc(new Controller(ds,log));

        /*
        Finally we register our application lifecycle methods
         */
        onStarted(() -> onStart());
        onStop(() -> onStop());
    }
    @Tag(name = "Start App", description = "Basic app start operations")
    @ApiResponse(description = "This is the default response")
    public static void main(final String[] args) {
        runApp(args, App::new);
    }

    public void onStart() {
        Logger log = getLog();
        log.info("Starting Up...");
        DataSource ds = require(DataSource.class);
        Controller control1 = new Controller(ds,log);
        ArrayList<Account> acc = control1.fetchData();
        // Open Connection to DB
        try (Connection connection = ds.getConnection()) {
            //Populate The Database
            Statement stmt = connection.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS accounts (\n"
                    + " id varchar(50) PRIMARY KEY,\n"
                    + " name text NOT NULL,\n"
                    + " balance decimal NOT NULL,\n"
                    + " accountType text NOT NULL,\n"
                    + " currency text NOT NULL);";
            stmt.execute(sql);
            sql = "INSERT INTO accounts (id, name, balance, accountType, currency) "
                    + "VALUES (?,?,?,?,?)";
            PreparedStatement prep = connection.prepareStatement(sql);
            for(int x = 0; x < acc.size() ;x++) {
                prep.setString(1, acc.get(x).getID());
                prep.setString(2, acc.get(x).getName());
                prep.setDouble(3, acc.get(x).getBalance());
                prep.setString(4, acc.get(x).getAccountType());
                prep.setString(5, acc.get(x).getCurrency());
                prep.executeUpdate();
            }
            prep.close();
            stmt.close();
        } catch (SQLException e) {
            log.error("Database Creation Error", e);
        }


        ArrayList<Transaction> transac = control1.fetchDataTransaction();
        // Open Connection to DB
        try (Connection connection = ds.getConnection()) {
            //Populate The Database
            Statement stmt = connection.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS transactions (\n"
                    + " withdrawAccount varchar(50) NOT NULL, \n"
                    + " depositAccount varchar(50) NOT NULL, \n"
                    + " timestamp text NOT NULL, \n"
                    + " id varchar(50) PRIMARY KEY,\n"
                    + " amount decimal NOT NULL,\n"
                    + " currency text NOT NULL);";
            stmt.execute(sql);
            sql = "INSERT INTO transactions (withdrawAccount, depositAccount, timestamp, id, amount, currency) "
                    + "VALUES (?,?,?,?,?,?)";
            PreparedStatement prep = connection.prepareStatement(sql);
            for(int x = 0; x < transac.size() ;x++) {
                prep.setString(1, transac.get(x).getWidAcc().getID());
                prep.setString(2, transac.get(x).getDepAcc().getID());
                prep.setString(3, transac.get(x).getTimestamp());
                prep.setString(4, transac.get(x).getId());
                prep.setDouble(5, transac.get(x).getAmount());
                prep.setString(6, transac.get(x).getCurrency());
                prep.executeUpdate();
            }
            prep.close();
            stmt.close();
        } catch (SQLException e) {
            log.error("Database Creation Error", e);
        }
    }

    public void onStop() {
        System.out.println("Shutting Down...");
    }

}