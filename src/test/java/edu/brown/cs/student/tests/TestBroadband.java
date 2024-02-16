package edu.brown.cs.student.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.ACSDataSource;
import edu.brown.cs.student.main.server.BroadbandHandler;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testng.Assert;
import spark.Spark;

public class TestBroadband {

  @BeforeAll
  public static void setupOnce() {
    // Pick an arbitrary free port
    Spark.port(0);
    // Eliminate logger spam in console for test suite
    Logger.getLogger("").setLevel(Level.WARNING); // empty name = root
  }

  // private JsonAdapter<List<List<String>>> adapter;
  private JsonAdapter<Map<String, Object>> adapter;
  private final Type mapStringObject =
      Types.newParameterizedType(Map.class, String.class, Object.class);

  @BeforeEach
  public void setup() {

    // Use *MOCKED* data when in this test environment.
    // Notice that the WeatherHandler code doesn't need to care whether it has
    // "real" data or "fake" data. Good separation of concerns enables better testing.

    ArrayList<List<String>> list = new ArrayList<List<String>>();
    HashSet<String> coll = new HashSet<>();
    coll.add("Los Angeles County, California");
    coll.add("89.9");

    List<String> strList = new ArrayList<>();
    strList.addAll(coll);
    list.add(strList);

    ACSDataSource mockedSource = new MockedCensusSource(list);
    Spark.get("broadband", new BroadbandHandler(mockedSource));
    Spark.awaitInitialization(); // don't continue until the server is listening

    // New Moshi adapter for responses (and requests, too; see a few lines below)
    //   For more on this, see the Server gearup.
    Moshi moshi = new Moshi.Builder().build();
    this.adapter = moshi.adapter(this.mapStringObject);
  }

  @AfterEach
  public void tearDown() {
    // Gracefully stop Spark listening on both endpoints
    Spark.unmap("broadband");
    Spark.awaitStop(); // don't proceed until the server is stopped
  }

  private HttpURLConnection tryRequest(String apiCall) throws IOException {
    // Configure the connection (but don't actually send a request yet)
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/" + apiCall);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();
    // The request body contains a Json object
    clientConnection.setRequestProperty("Content-Type", "application/json");
    // We're expecting a Json object in the response body
    clientConnection.setRequestProperty("Accept", "application/json");
    clientConnection.connect();
    return clientConnection;
  }

  @Test
  public void testMissingCensusRequestFail() throws IOException {
    // Setup without any parameters (oops!)
    HttpURLConnection loadConnection = tryRequest("broadband");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, loadConnection.getResponseCode());

    Map<String, Object> body =
        this.adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));

    showDetailsIfError(body);
    assertEquals("error_bad_request", body.get("result"));
    loadConnection.disconnect(); // close gracefully
  }

  @Test
  public void testSuccessfulDataRetrieval() throws IOException {
    HttpURLConnection loadConnection =
        tryRequest("broadband?state=California&county=Los%20Angeles%20County,%20California");
    assertEquals(200, loadConnection.getResponseCode());

    Map<String, Object> body =
        this.adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));

    Assert.assertEquals(body.get("89.9"), "Los Angeles County, California");
    assertEquals("success", body.get("result"));
    loadConnection.disconnect();
  }

  /**
   * If an invalid county code is inputted, the Server will respond with all counties in the state
   *
   * @throws IOException
   */
  @Test
  public void testCountyCodeRetrievalFailure() throws IOException {
    HttpURLConnection loadConnection =
        tryRequest("broadband?state=California&county=Los%20Anles%20County,%20California");
    assertEquals(200, loadConnection.getResponseCode());

    Map<String, Object> body =
        this.adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));

    Assert.assertEquals(body.get("89.9"), "Los Angeles County, California");
    assertEquals("success", body.get("result"));
    loadConnection.disconnect();
  }

  @Test
  public void testStateCodeRetrievalFailure() throws IOException {
    HttpURLConnection loadConnection =
        tryRequest("broadband?state=Cafornia&county=Los%20Angeles%20County,%20California");
    assertEquals(200, loadConnection.getResponseCode());

    Map<String, Object> body =
        this.adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));

    Assert.assertEquals(body.get("result"), "error_bad_request");
    Assert.assertEquals(body.get("state_arg"), "Cafornia");
    loadConnection.disconnect();
  }

  @Test
  public void testInvalidParameters() throws IOException {
    HttpURLConnection loadConnection =
        tryRequest("broadband?state=Cafornia&county=Los%20Angeles%20County,%20California");
    assertEquals(200, loadConnection.getResponseCode());

    Map<String, Object> body =
        this.adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));

    Assert.assertEquals(body.get("result"), "error_bad_request");
    Assert.assertEquals(body.get("state_arg"), "Cafornia");
    loadConnection.disconnect();
  }

  private void showDetailsIfError(Map<String, Object> body) {
    if (body.containsKey("type") && "error".equals(body.get("type"))) {
      System.out.println(body);
    }
  }
}
