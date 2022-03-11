package uk.co.asepstrath.bank;

import io.jooby.exception.StartupException;
import org.junit.jupiter.api.Assertions;
import uk.co.asepstrath.bank.App;
import io.jooby.JoobyTest;
import io.jooby.StatusCode;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.BindException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JoobyTest(App.class)
class IntegrationTest {

    static OkHttpClient client = new OkHttpClient();

    @Test
    void testStatusCode(int serverPort) throws IOException {
        Request req = new Request.Builder().url("http://localhost:" + serverPort + "/").build();
        Response rsp = client.newCall(req).execute();
        assertEquals(StatusCode.OK.value(), rsp.code());
        
        req = new Request.Builder().url("http://localhost:" + serverPort + "/accountsData").build();
        rsp = client.newCall(req).execute();
        assertEquals(StatusCode.OK.value(), rsp.code());

        req = new Request.Builder().url("http://localhost:" + serverPort + "/transactionData").build();
        rsp = client.newCall(req).execute();
        assertEquals(StatusCode.OK.value(), rsp.code());

        req = new Request.Builder().url("http://localhost:" + serverPort + "/transactionData/byAccount").build();
        rsp = client.newCall(req).execute();
        assertEquals(StatusCode.OK.value(), rsp.code());
    }
}
