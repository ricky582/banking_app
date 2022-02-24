package uk.co.asepstrath.bank;

import io.jooby.Jooby;
import io.jooby.handlebars.HandlebarsModule;
import io.jooby.helper.UniRestExtension;
import io.jooby.hikari.HikariModule;
import org.slf4j.Logger;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;

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

    public void onStart() {
        Logger log = getLog();
        log.info("Starting Up...");






        // Fetch DB Source
        DataSource ds = require(DataSource.class);

        Controller control1 = new Controller(ds,log);

        ArrayList<Account> acc = control1.fetchData();

        // Open Connection to DB
        try (Connection connection = ds.getConnection()) {
            //Populate The Database
            Statement stmt = connection.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS accounts (\n"
                    + " id integer PRIMARY KEY,\n"
                    + " name text NOT NULL,\n"
                    + " balance decimal NOT NULL);";

            stmt.execute(sql);

            sql = "INSERT INTO accounts (id,name,balance) "
                    + "VALUES (?,?,?)";

            PreparedStatement prep = connection.prepareStatement(sql);

            for(int x=0;x<acc.size();x++) {

                prep.setInt(1,x);
                prep.setString(2,acc.get(x).getName());
                prep.setDouble(3,acc.get(x).getBalance());
                prep.executeUpdate();

            }



            stmt = connection.createStatement();
            sql = "SELECT * FROM accounts";
            ResultSet rs = stmt.executeQuery(sql);



            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                double balance = rs.getDouble("balance");


                Account employee = new Account(name,balance);
                System.out.println(employee.toString());
            }
            rs.close();

            connection.close();

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
