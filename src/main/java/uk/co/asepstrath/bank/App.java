package uk.co.asepstrath.bank;

import io.jooby.Jooby;
import io.jooby.handlebars.HandlebarsModule;
import io.jooby.helper.UniRestExtension;
import io.jooby.hikari.HikariModule;
import org.slf4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class App extends Jooby {

    {
        /*
        This section is used for setting up the Jooby Framework modules
         */
        install(new UniRestExtension());
        install(new HandlebarsModule());
        install(new HikariModule("mem"));

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

    public static void main(final String[] args) {
        runApp(args, App::new);
    }

    /*
        This function will be called when the application starts up,
        it should be used to ensure that the DB is properly setup
     */

//    {
//        get("/", req -> {
//            //Line that throws the error:
//            return Results.html("home").put("model", new Object());
//        });
//    }

    public void onStart() {
        Logger log = getLog();
        log.info("Starting Up...");

        // Fetch DB Source
        DataSource ds = require(DataSource.class);
        // Open Connection to DB
        try (Connection connection = ds.getConnection()) {
            //Populate The Database
            Statement stmt = connection.createStatement();
            Object[][] dataSet = new Object[][]{
                    {"Rachel", 50.00},
                    {"Monica", 100.00},
                    {"Phoebe", 76.00},
                    {"Joey", 23.90},
                    {"Chandler", 3.00},
                    {"Ross", 54.32},
            };
            stmt.executeUpdate("CREATE TABLE AccountDataset (Name varchar(255), Balance float)");

            for(int i = 0; i <= 5; i++) {
                stmt.executeUpdate("INSERT INTO AccountDataset " + "VALUES ('"+ dataSet[i][0] +"', '"+ dataSet[i][1] +"')");
            }
        } catch (SQLException e) {
            log.error("Database Creation Error", e);
        }
    }

    /*
        This function will be called when the application shuts down
     */
    public void onStop() {
        System.out.println("Shutting Down...");
    }

}
