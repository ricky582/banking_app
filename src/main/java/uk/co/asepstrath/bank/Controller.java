package uk.co.asepstrath.bank;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jooby.Jooby;
import io.jooby.MediaType;
import io.jooby.ModelAndView;
import io.jooby.annotations.*;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import org.slf4j.Logger;

import javax.sql.DataSource;
import java.net.HttpURLConnection;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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

    /*
        This request makes a call to the data for each account via Swagger and lists all accounts
    */
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
        retrieveDataTransaction();
        ArrayList<Account> arrayListAccount = retrieveData();
        Map<String, Object> mapTest = new HashMap<>();
        mapTest.put("accounts", "accounts");
        mapTest.put("user", arrayListAccount);
        return new ModelAndView("accountsData.hbs", mapTest);
    }

    //gets all accounts data from swagger and calls a parsing method
    public ArrayList<Account> fetchData() {
        String jsonResult = String.valueOf(Unirest.get("https://api.asep-strath.co.uk/api/Team1/accounts") //get request for all accounts data
                .asJson()
                .getBody());
        return parseJson(jsonResult);
    }

    //parses json data for accounts into a list of accounts and returns accounts
    public ArrayList<Account> parseJson(String responseBody) {
        ArrayList<Account> accounts = new ArrayList<>();    //array list to store accounts
        JSONArray accountsData = new JSONArray(responseBody);   //adds account data from parameter to a JSON array
        for (int i = 0; i < accountsData.length(); i++) {       //adds account data from JSON array to an Array of objects (Accounts)
            JSONObject accountData = accountsData.getJSONObject(i);
            accounts.add(new Account(accountData.getString("id"),
                                     accountData.getString("name"),
                                     accountData.getDouble("balance"),
                                     accountData.getString("accountType"),
                                     accountData.getString("currency")));
        }
        return accounts;
    }

    //retrieve accounts from database created in App
    public ArrayList<Account> retrieveData() {
        ArrayList<Account> accounts = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) { //creates connection to database
            Statement stmt = connection.createStatement();  //creates statement object
            String sql = "SELECT * FROM accounts";  //query search for all data of accounts
            ResultSet rs = stmt.executeQuery(sql);  //creates result object of executed query results
            while (rs.next()) {     //assigns each piece of data to its corresponding data type
                String id = rs.getString("id");
                String name = rs.getString("name");
                double balance = rs.getDouble("balance");
                String accountType = rs.getString("accountType");
                String currency = rs.getString("currency");
                double initialBal = rs.getDouble("initialBal");
                Account bankUser = new Account(id, name, initialBal, accountType, currency);//assigns all data to one account
                bankUser.setBalance(balance);
                accounts.add(bankUser);//adds each account to account arrayList
            }
            rs.close();
        } catch (SQLException e) {}
        return accounts;
    }


    /*
        This request makes a call to the transaction data via Swagger and lists all transactions
    */
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

    //gets all transaction data from Swagger and calls a parsing method for this data
    public ArrayList<Transaction> fetchDataTransaction() {
        String jsonResult = String.valueOf(Unirest.get("https://api.asep-strath.co.uk/api/team1/transactions?PageSize=10000")//get request for all transaction data
                .asJson()
                .getBody());
        return parseJsonTransaction(jsonResult);
    }

    //parses transaction data into transaction objects
    public ArrayList<Transaction> parseJsonTransaction(String responseBody) {
        ArrayList<Transaction> transactions = new ArrayList<>();    //arraylist to store each transaction
        JSONArray transactionsData = new JSONArray(responseBody);   //storing all transaction in a JSON Array
        for (int i = 0; i < transactionsData.length(); i++) {
            JSONObject accountData = transactionsData.getJSONObject(i);
            transactions.add(new Transaction(getAccountById(accountData.getString("withdrawAccount")),
                    getAccountById(accountData.getString("depositAccount")),
                    accountData.getString("timestamp"),
                    accountData.getString("id"),
                    accountData.getDouble("amount"),
                    accountData.getString("currency")));   //stores each transaction to transactions arrayList

        }
        return transactions;
    }

    //retrieve transactions from database created in App
    public ArrayList<Transaction> retrieveDataTransaction() {
        ArrayList<Transaction> transactions = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) { //creates connection to database
            Statement stmt = connection.createStatement(); //creates statement object
            String sql = "SELECT * FROM transactions";  //query search for all data of transactions
            ResultSet rs = stmt.executeQuery(sql);  //creates result object of executed query results
            while (rs.next()) { //assigns each piece of data to its corresponding data type
                String withdrawAccount = rs.getString("withdrawAccount");
                String depositAccount = rs.getString("depositAccount");
                String timestamp = rs.getString("timestamp");
                String id = rs.getString("id");
                double amount = rs.getDouble("amount");
                String currency = rs.getString("currency");
                Transaction bankTransaction = new Transaction(getAccountById(withdrawAccount),
                                                              getAccountById(depositAccount),
                                                              timestamp, id, amount, currency); //assigns all data to one transaction
                bankTransaction.setStatus(rs.getInt("status"));
                transactions.add(bankTransaction);  //adds each account to account arrayList
            }
            rs.close();
        } catch (SQLException e) {}
        applyTransactions(transactions);
        return transactions;
    }

    public void applyTransactions(ArrayList<Transaction> arr) {
        try {
            Connection connection = dataSource.getConnection(); //connect to DB
            Collections.sort(arr); //make sure transactions are sorted chronologically
            for (Transaction t : arr) { //
                if (t.getStatus() == 0) {
                    Account dep = getAccountById(t.getDepAcc().getID());
                    Account wid = getAccountById(t.getWidAcc().getID());
                    t.getDepAcc().setBalance(dep.getBalance());
                    t.getWidAcc().setBalance(wid.getBalance());
                    PreparedStatement prep = connection.prepareStatement("UPDATE transactions SET status = ? WHERE id = ?;");
                    t.doTransaction();
                    prep.setInt(1, t.getStatus());
                    prep.setString(2, t.getId());
                    prep.executeUpdate();
                    prep = connection.prepareStatement("UPDATE accounts SET balance = ? WHERE id = ?;");
                    prep.setDouble(1, t.getDepAcc().getBalance());
                    prep.setString(2, t.getDepAcc().getID());
                    prep.executeUpdate();
                    prep.setDouble(1, t.getWidAcc().getBalance());
                    prep.setString(2, t.getWidAcc().getID());
                    prep.executeUpdate();
                    prep.close();
                }
            }
        }
        catch (SQLException e){}
    }

    //finds account by id or creates a new account if it is a non-local account
    public Account getAccountById(String id){
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement prep = connection.prepareStatement("SELECT * FROM accounts WHERE id = ?;");
            prep.setString(1, id);
            ResultSet rs = prep.executeQuery();
            if (!rs.next()){
                return new Account(id);
            }
            String name = rs.getString("name");
            double balance = rs.getDouble("balance");
            String accountType = rs.getString("accountType");
            String currency = rs.getString("currency");
            double initialBal = rs.getDouble("initialBal");
            Account bankUser = new Account(id, name, initialBal, accountType, currency);
            bankUser.setBalance(balance);
            rs.close();
            return bankUser;
        }
        catch (SQLException e){
            return null;
        }
    }


    /*
        This request makes a call to the data for each account and transactions via Swagger to list transaction data for each account
    */
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
        ArrayList<Transaction> arr = retrieveDataTransaction();
        Map<String, Object> mapTest = new HashMap<>();
        int totalSuccessful = 0;
        for (Transaction t : arr){
            if (t.getStatus() == 1){
                totalSuccessful ++;
            }
        }
        mapTest.put("transaction", "transaction");
        mapTest.put("user", arrayListTransactionAcc); //users show in hbs file
        mapTest.put("total", Integer.toString(totalSuccessful)); //total successful transactions is passed in
        return new ModelAndView("transactionDataAcc.hbs", mapTest);
    }

    //grabs data for transaction and accounts and creates transactionInfo objects out of them
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

    //gets fraud data from Swagger and removes all transactions with matching IDs
    public void removeFraudulentTransactions() {
        String jsonResult = String.valueOf(Unirest.get("http://api.asep-strath.co.uk/api/Team1/fraud")
                .header("accept", "application/json") // get request returns data in xml thus .header returns data in a JSON format
                .asJson()
                .getBody());
        JSONArray jFraudID = new JSONArray(jsonResult);
        try (Connection connection = dataSource.getConnection()){ //deletes fraudulent transactions
            for (int i = 0; i < jFraudID.length(); i++) {
                PreparedStatement prep = connection.prepareStatement("DELETE FROM transactions WHERE id = ?;");
                prep.setString(1, jFraudID.getString(i));
                prep.executeUpdate();
            }
        }
        catch (SQLException e) {}
    }
    //displays success page or error page based on the success of the transaction
    @GET("/transactionData/")
    public ModelAndView repeatSuccess(@QueryParam("id") String transactionid) {
        if (repeatTransaction(transactionid) == true) {
            return new ModelAndView("RepeatTransactionSuccess.hbs");
        }
        return new ModelAndView("RepeatTransactionError.hbs");
    }
    //repeats transaction from user inputted id
    public boolean repeatTransaction(String transactionid) {
        SimpleDateFormat newDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSS");
        Date newDate = new Date();
        ArrayList<Transaction> tranList = retrieveDataTransaction();
        for (int i = 0; i < tranList.size(); i++) {
            if (tranList.get(i).getId().equals(transactionid)) {
                try (Connection connection = dataSource.getConnection()) {

                    Statement stmt = connection.createStatement();

                    String sql = "INSERT INTO transactions (withdrawAccount, depositAccount, timestamp, id, amount, currency, status) "
                                 + "VALUES (?,?,?,?,?,?,?)";

                    PreparedStatement prep = connection.prepareStatement(sql);

                    prep.setString(1, tranList.get(i).getWidAcc().getID());
                    prep.setString(2, tranList.get(i).getDepAcc().getID());
                    prep.setString(3, newDateFormat.format(newDate));
                    prep.setString(4, tranList.get(i).generateId());
                    prep.setDouble(5, tranList.get(i).getAmount());
                    prep.setString(6, tranList.get(i).getCurrency());
                    prep.setInt(7, 0);

                    prep.executeUpdate();

                    prep.close();
                    stmt.close();
                    return true;
                } catch (SQLException e) {
                    logger.error("Database insertion error", e);
                    return false;
                }
            }
        }
        return false;
    }
}