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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;

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
        ArrayList<Transaction> transactions = new ArrayList<>();
        JSONArray transactionsData = new JSONArray(responseBody);
        ArrayList<String> fraud = new ArrayList<>();
        fraud = fraudData();

        for (int i = 0; i < transactionsData.length(); i++) {
            JSONObject accountData = transactionsData.getJSONObject(i);

           if(!fraud.contains(accountData.getString("id"))) {
               transactions.add(new Transaction(getAccountById(accountData.getString("withdrawAccount")), getAccountById(accountData.getString("depositAccount")), accountData.getString("timestamp"), accountData.getString("id"), accountData.getDouble("amount"), accountData.getString("currency")));
           }
        }

        return transactions;
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

                Transaction bankTransaction = new Transaction(getAccountById(withdrawAccount), getAccountById(depositAccount), timestamp, id, amount, currency);
                transactions.add(bankTransaction);
            }
            rs.close();
        } catch (SQLException e) {}
        return transactions;
    }

    public Account getAccountById(String id){
        ArrayList<Account> accounts = retrieveData();
        for (Account a : accounts){
            if (id.equals(a.getID())){
                return a;
            }
        }
        return new Account(id);
    }

    //transaction data by account giving information required in user story
    @GET("/transactionData/byAccount")
    public ModelAndView transactionDataAcc() {
        ArrayList<TransactionInfo> arrayListTransactionAcc = retrieveDataTransactionAcc();
        Map<String, Object> mapTest = new HashMap<>();
        int totalSuccessful = 0;
        for (TransactionInfo t : arrayListTransactionAcc){
            t.getCurrentBal();
            totalSuccessful += t.getNumSuccessful();
        }
        mapTest.put("transaction", "transaction");

        mapTest.put("user", arrayListTransactionAcc); //users show in hbs file
        mapTest.put("total", Integer.toString(totalSuccessful)); //total successful transactions is passed in


        return new ModelAndView("transactionDataAcc.hbs", mapTest);
    }


    public ArrayList<TransactionInfo> retrieveDataTransactionAcc() {
        ArrayList<Transaction> transactions = retrieveDataTransaction();
        ArrayList<Account> accounts = retrieveData();
        ArrayList<TransactionInfo> transactionInfo = new ArrayList<>();

        for (Account account : accounts){
            ArrayList<Transaction> temp = new ArrayList<>();
            for (Transaction transaction : transactions){
                if (transaction.getWidAcc().getID().equals(account.getID()) || transaction.getDepAcc().getID().equals(account.getID())){
                    temp.add(transaction);
                }
            }
            transactionInfo.add(new TransactionInfo(account, temp));
        }

        return transactionInfo;
    }

    public ArrayList<String> fraudData() {

        String jsonResult = String.valueOf(Unirest.get("http://api.asep-strath.co.uk/api/Team1/fraud")
                .header("accept", "application/json")
                .asJson()
                .getBody());

        return parseJsonId(jsonResult);
    }

    public ArrayList<String> parseJsonId(String responseBody) {
        ArrayList<String> fraudId = new ArrayList<>();
        JSONArray jFraudID = new JSONArray(responseBody);

        for (int i = 0; i < jFraudID.length(); i++) {
            fraudId.add(jFraudID.getString(i));
        }

        return fraudId;
    }

    public ArrayList<Transaction> repeatTransaction() {
        String tempId = "12ac7766-c511-400d-9651-d85166e3eab2";
        SimpleDateFormat newDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date newDate = new Date();
        ArrayList<Transaction> tranList = retrieveDataTransaction();

        for (int i = 0; i < tranList.size(); i++) {
            if (tranList.get(i).getId().equals(tempId)) {
                try (Connection connection = dataSource.getConnection()) {

                    Statement stmt = connection.createStatement();

                    String sql = "INSERT INTO transactions (withdrawAccount, depositAccount, timestamp, id, amount, currency) "
                            + "VALUES (?,?,?,?,?,?)";

                    PreparedStatement prep = connection.prepareStatement(sql);

                    prep.setString(1, tranList.get(i).getWidAcc().getID());
                    prep.setString(2, tranList.get(i).getDepAcc().getID());
                    prep.setString(3, newDateFormat.format(newDate));
                    prep.setString(4,tranList.get(i).getId()+1);
                    prep.setDouble(5, tranList.get(i).getAmount());
                    prep.setString(6, tranList.get(i).getCurrency());

                    prep.executeUpdate();

                    prep.close();
                    stmt.close();
                } catch (SQLException e) {
                }
            }
        }

        return tranList;
    }


}