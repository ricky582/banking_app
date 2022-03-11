package uk.co.asepstrath.bank;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jooby.ModelAndView;
import io.jooby.Jooby;
import io.jooby.annotations.GET;
import io.jooby.annotations.Path;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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


    @GET("/accountsData")
    @ApiResponses({
            @ApiResponse(description = "Success",responseCode = "200"),
            @ApiResponse(description = "Not Found",responseCode = "404")
    })
    @Operation(
            summary = "Display All Accounts",
            description = "Display all accounts in the bank collection, kept on a table of ten which is scrollable/searchable"
    )
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
    @ApiResponses({
            @ApiResponse(description = "Success",responseCode = "200"),
            @ApiResponse(description = "Not Found",responseCode = "404")
    })
    @Operation(
            summary = "Display All Transactions",
            description = "Display all transactions in the bank collection, kept on a table of ten which is scrollable/searchable"
    )
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
    @ApiResponses({
            @ApiResponse(description = "Success",responseCode = "200"),
            @ApiResponse(description = "Not Found",responseCode = "404")
    })
    @Operation(
            summary = "Display All Transactions sorted by account",
            description = "Display all transactions in the bank collection, kept on a table of ten which is scrollable/searchable and which is sorted by account"
    )
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
}