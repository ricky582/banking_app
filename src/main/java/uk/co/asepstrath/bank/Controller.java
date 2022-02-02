package uk.co.asepstrath.bank;
import io.jooby.ModelAndView;
import io.jooby.StatusCode;
import io.jooby.annotations.*;
import io.jooby.exception.StatusCodeException;
import kong.unirest.Unirest;
import org.slf4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/*
    Example Controller is a Controller from the MVC paradigm.
    The @Path Annotation will tell Jooby what /path this Controller can respond to,
    in this case the controller will respond to requests from <host>/example
 */
@Path("/page")
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
    public String welcomeFromDB() {
        String welcomeMessageKey = "Pheobe";
        // Create a connection
        try (Connection connection = dataSource.getConnection()) {
            // Create Statement (batch of SQL Commands)
            Statement statement = connection.createStatement();
            // Perform SQL Query
            ResultSet set = statement.executeQuery("SELECT * FROM AccountDataset Where Name = '"+welcomeMessageKey+"'");
            // Read First Result
            set.next();
            // Extract value from Result
            Account newAccount = new Account(set.getFloat("Balance"));
            // Return value
            return newAccount.toString();
        } catch (SQLException e) {
            // If something does go wrong this will log the stack trace
            logger.error("Database Error Occurred",e);
            // And return a HTTP 500 error to the requester
            throw new StatusCodeException(StatusCode.SERVER_ERROR, "Database Error Occurred");
        }
    }

    /*
    The dice endpoint displays two features of the Jooby framework, Parameters and Templates

    You can see that this function takes in a String name, the annotation @QueryParam tells the framework that
    the value of name should come from the URL Query String (<host>/example/dice?name=<value>)

    The function then uses this value and others to create a Map of values to be injected into a template.
    The ModelAndView constructor takes a template name and the model.
    The Template name is the name of the file containing the template, this name is relative to the folder src/main/resources/views

    We have set the Jooby framework up to use the Handlebars templating system which you can read more on here:
    https://handlebarsjs.com/guide/
     */

}
