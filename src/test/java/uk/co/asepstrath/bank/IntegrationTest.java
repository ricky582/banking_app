package uk.co.asepstrath.bank;

import io.jooby.*;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.util.Map;

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

    @Test
    void index() {
        MockRouter router = new MockRouter(new App());
        MockContext context = new MockContext();

        router.get("/", context, rsp -> {
            ModelAndView modelAndView = (ModelAndView) rsp.value();
            Assertions.assertThat(modelAndView).isNotNull();
            String view = modelAndView.getView();
            Assertions.assertThat(view).isEqualTo("index.hbs");
        });
    }

    @Test
    void accountsData() {
        MockRouter router = new MockRouter(new App());
        MockContext context = new MockContext();

        router.get("/accountsData", context, rsp -> {
            ModelAndView modelAndView = (ModelAndView) rsp.value();
            Assertions.assertThat(modelAndView).isNotNull();
            String view = modelAndView.getView();
            Assertions.assertThat(view).isEqualTo("accountsData.hbs");
        });
    }

    @Test
    void transactionData() {
        MockRouter router = new MockRouter(new App());
        MockContext context = new MockContext();

        router.get("/transactionData", context, rsp -> {
            ModelAndView modelAndView = (ModelAndView) rsp.value();
            Assertions.assertThat(modelAndView).isNotNull();
            String view = modelAndView.getView();
            Assertions.assertThat(view).isEqualTo("transactionData.hbs");
        });
    }

    @Test
    void transactionDataAcc() {
        MockRouter router = new MockRouter(new App());
        MockContext context = new MockContext();

        router.get("/transactionData/byAccount", context, rsp -> {
            ModelAndView modelAndView = (ModelAndView) rsp.value();
            Assertions.assertThat(modelAndView).isNotNull();
            String view = modelAndView.getView();
            Assertions.assertThat(view).isEqualTo("transactionDataAcc.hbs");
        });
    }
}
