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
            assertEquals("[Rachel 50.0, Monica 100.0, Phoebe 76.0, Joey 23.9, Chandler 3.0, Ross 54.32]", rsp.body().string());
            assertEquals(StatusCode.OK.value(), rsp.code());
        }
    }
}