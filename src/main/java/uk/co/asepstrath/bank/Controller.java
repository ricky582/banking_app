package uk.co.asepstrath.bank;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jooby.ModelAndView;
import io.jooby.annotations.GET;
import io.jooby.annotations.Path;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import org.slf4j.Logger;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Path = IP/argument, i.e("localhost:8080/accounts)
@Path("/accounts")
public class Controller {

    private static HttpURLConnection urlConn;
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

    @GET("/test")
    public int fetchData() throws IOException {
        BufferedReader connectionReader;
        StringBuffer responseContent = new StringBuffer();
        String line;
        int status;

        try {
            URL url = new URL("https://api.asep-strath.co.uk/api/Team1/accounts");
            urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setRequestMethod("GET");

            status = urlConn.getResponseCode();

            if (status == 200) {
                connectionReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
                while((line = connectionReader.readLine()) != null) {
                    responseContent.append(line);
                }
                connectionReader.close();
            } else {
                connectionReader = new BufferedReader(new InputStreamReader(urlConn.getErrorStream()));
                while((line = connectionReader.readLine()) != null) {
                    responseContent.append(line);
                }
                connectionReader.close();
            }
            parseJson(responseContent.toString());
        }  catch (IOException e) {} finally {
            urlConn.disconnect();
        }

        return 0;
    }

    public static String parseJson(String responseBody) {
        JSONArray accountsData = new JSONArray(responseBody);
        for (int i = 0; i < accountsData.length(); i++) {
            JSONObject accountData = accountsData.getJSONObject(i);
            System.out.println(accountData);
        }
        return "a";
    }
}
