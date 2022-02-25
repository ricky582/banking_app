package uk.co.asepstrath.bank;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jooby.ModelAndView;
import io.jooby.Jooby;
import io.jooby.annotations.GET;
import io.jooby.annotations.Path;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import org.slf4j.Logger;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Path = IP/argument, i.e("localhost:8080/accounts)
@Path("/")
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

    @GET("/")
    public ModelAndView index() {
        return new ModelAndView("index.hbs");
    }

    @GET("/api")
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

    @GET("/accounts")
    public ModelAndView accounts() throws IOException {
        String objectOutput = displayAccounts();

        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> listData = mapper.readValue(objectOutput, new TypeReference<List<Map<String, Object>>>(){});

        Map<String, Object> mapTest = new HashMap<>();
        mapTest.put("accounts", "accounts");
        mapTest.put("user", listData);

        return new ModelAndView("accounts.hbs", mapTest);
    }

    @GET("/accountsData")
    public ModelAndView accountsData() {
        ArrayList<Account> arrayListAccount = retrieveData();
        Map<String, Object> mapTest = new HashMap<>();

        mapTest.put("accounts", "accounts");
        mapTest.put("user", arrayListAccount);

        return new ModelAndView("accountsData.hbs", mapTest);
    }

    public ArrayList<Account> fetchData() {
        String jsonResult = String.valueOf(Unirest.get("https://api.asep-strath.co.uk/api/Team1/accounts")
                .asJson()
                .getBody());

        return parseJson(jsonResult);
    }

    public ArrayList<Account> parseJson(String responseBody) {
        ArrayList<Account> accounts = new ArrayList<>();
        JSONArray accountsData = new JSONArray(responseBody);
        for (int i = 0; i < accountsData.length(); i++) {
            JSONObject accountData = accountsData.getJSONObject(i);
            accounts.add(new Account(accountData.getString("id"), accountData.getString("name"), accountData.getDouble("balance"), accountData.getString("accountType"), accountData.getString("currency")));
        }

        return accounts;
    }

    public ArrayList<Account> retrieveData() {
        ArrayList<Account> accounts = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            Statement stmt = connection.createStatement();
            String sql = "SELECT * FROM accounts";
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                double balance = rs.getDouble("balance");
                String accountType = rs.getString("accountType");
                String currency = rs.getString("currency");

                Account bankUser = new Account(id, name, balance, accountType, currency);
                accounts.add(bankUser);
            }
            rs.close();
        } catch (SQLException e) {}
        return accounts;
    }

    @GET("/transactionData")
    public ModelAndView transactionData() {
        ArrayList<Transaction> arrayListTransaction = retrieveDataTransaction();
        Map<String, Object> mapTest = new HashMap<>();

        mapTest.put("transaction", "transaction");
        mapTest.put("transac", arrayListTransaction);

        return new ModelAndView("transactionData.hbs", mapTest);
    }

    public ArrayList<Transaction> fetchDataTransaction() {
        String jsonResult = String.valueOf(Unirest.get("https://api.asep-strath.co.uk/api/team1/transactions?PageSize=10000")
                .asJson()
                .getBody());

        return parseJsonTransaction(jsonResult);
    }

    public ArrayList<Transaction> parseJsonTransaction(String responseBody) {
        ArrayList<Transaction> accounts = new ArrayList<>();
        JSONArray accountsData = new JSONArray(responseBody);
        for (int i = 0; i < accountsData.length(); i++) {
            JSONObject accountData = accountsData.getJSONObject(i);
            accounts.add(new Transaction(accountData.getString("withdrawAccount"), accountData.getString("depositAccount"), accountData.getString("timestamp"), accountData.getString("id"), accountData.getDouble("amount"), accountData.getString("currency")));
        }

        return accounts;
    }

    public ArrayList<Transaction> retrieveDataTransaction() {
        ArrayList<Transaction> transactions = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            Statement stmt = connection.createStatement();
            String sql = "SELECT * FROM transactions";
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String withdrawAccount = rs.getString("withdrawAccount");
                String depositAccount = rs.getString("depositAccount");
                String timestamp = rs.getString("timestamp");
                String id = rs.getString("id");
                double amount = rs.getDouble("amount");
                String currency = rs.getString("currency");

                Transaction bankTransaction = new Transaction(withdrawAccount, depositAccount, timestamp, id, amount, currency);
                transactions.add(bankTransaction);
            }
            rs.close();
        } catch (SQLException e) {}
        return transactions;
    }

    //transaction data by account giving information required in user story
    @GET("/transactionData/byAccount")
    public ModelAndView transactionDataAcc() {
        ArrayList<TransactionInfo> arrayListTransactionAcc = retrieveDataTransactionAcc();
        Map<String, Object> mapTest = new HashMap<>();

        mapTest.put("transaction", "transaction"); //CHANGE TO RIGHT DB
        mapTest.put("user", arrayListTransactionAcc); //user shows in hbs file

        return new ModelAndView("transactionDataAcc.hbs", mapTest);
    }


    public ArrayList<TransactionInfo> retrieveDataTransactionAcc() {
        ArrayList<Transaction> transactions = retrieveDataTransaction();
        ArrayList<Account> accounts = retrieveData();
        ArrayList<TransactionInfo> transactionInfo = new ArrayList<>();

        for (Account account : accounts){
            ArrayList<Transaction> temp = new ArrayList<>();
            for (Transaction transaction : transactions){
                if (transaction.getWidAcc() == account.getID()){
                    temp.add(transaction);
                }
            }
            transactionInfo.add(new TransactionInfo(account, temp));
        }

        return transactionInfo;
    }
}