package uk.co.asepstrath.bank;

import uk.co.asepstrath.bank.App;
import io.jooby.JoobyTest;
import io.jooby.StatusCode;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JoobyTest(App.class)
public class IntegrationTest {

    static OkHttpClient client = new OkHttpClient();

    @Test
    public void shouldDisplayValue(int serverPort) throws IOException {
        Request req = new Request.Builder()
                .url("http://localhost:" + serverPort + "/accounts")
                .build();

        try (Response rsp = client.newCall(req).execute()) {
            assertEquals("[{\"name\":\"Rachel\",\"balance\":50.0},{\"name\":\"Monica\",\"balance\":100.0},{\"name\":\"Phoebe\",\"balance\":76.0},{\"name\":\"Joey\",\"balance\":23.9},{\"name\":\"Chandler\",\"balance\":3.0},{\"name\":\"Ross\",\"balance\":54.32}]", rsp.body().string());
            assertEquals(StatusCode.OK.value(), rsp.code());
        }
    }

    @Test
    public void showAccount(int serverPort) throws IOException {
        Request req = new Request.Builder()
                .url("http://localhost:" + serverPort + "/accounts/get")
                .build();

        try (Response rsp = client.newCall(req).execute()) {
            assertEquals("<!DOCTYPE html><head> <meta charset=\"utf-8\"> <title></title> <meta name=\"description\" content=\"\"> <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"> <title>GET Endpoint — Test</title></head><body> <table> <tr> <th>First Name</th> <th>Balance</th> </tr><tr> <td>Rachel</td><td>50.0</td></tr><tr> <td>Monica</td><td>100.0</td></tr><tr> <td>Phoebe</td><td>76.0</td></tr><tr> <td>Joey</td><td>23.9</td></tr><tr> <td>Chandler</td><td>3.0</td></tr><tr> <td>Ross</td><td>54.32</td></tr></table></body></html>", rsp.body().string());
            assertEquals(StatusCode.OK.value(), rsp.code());
        }
    }
}
