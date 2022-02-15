package uk.co.asepstrath.bank;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jooby.ModelAndView;
import io.jooby.annotations.GET;
import io.jooby.annotations.Path;
import org.slf4j.Logger;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
    public String displayAccounts() throws JsonProcessingException {
        ArrayList<Account> accounts = gatherAccounts();
        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.writeValueAsString(accounts);
    }

    public ArrayList<Account> gatherAccounts() {
        ArrayList<Account> accounts = new ArrayList<>();
        accounts.add(new Account("Rachel", 50));
        accounts.add(new Account("Monica", 100.00));
        accounts.add(new Account("Phoebe", 76));
        accounts.add(new Account("Joey", 23.90));
        accounts.add(new Account("Chandler", 3.00));
        accounts.add(new Account("Ross", 54.32));

        return accounts;
    }

    @GET("/get")
    public ModelAndView accounts() throws IOException {
        String objectOutput = displayAccounts();

        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> listData = mapper.readValue(objectOutput, new TypeReference<List<Map<String, Object>>>(){});

        Map<String, Object> mapTest = new HashMap<>();
        mapTest.put("accounts", "accounts");
        mapTest.put("user", listData);

        return new ModelAndView("accounts.hbs", mapTest);
    }
}
