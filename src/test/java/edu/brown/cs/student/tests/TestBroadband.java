package edu.brown.cs.student.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.sources.ACSDataSource;
import edu.brown.cs.student.main.server.handler.BroadbandHandler;
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
/**
 * The TestBroadband class tests the functionality of the Broadband API endpoint in a Spark web server. It includes tests for requesting broadband data based on state
 * and county parameters.
 */
public class TestBroadband {
  /**
   * Sets up the configuration once before all test cases.
   */
  @BeforeAll
  public static void setupOnce() {
    // Pick an arbitrary free port
    Spark.port(0);
    // Eliminate logger spam in console for test suite
    Logger.getLogger("").setLevel(Level.WARNING); // empty name = root
  }

  private JsonAdapter<Map<String, Object>> adapter;
  private final Type mapStringObject =
      Types.newParameterizedType(Map.class, String.class, Object.class);
  /**
   * Sets up the Spark server and initializes necessary objects before each test case.
   */
  @BeforeEach
  public void setup() {

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
  /**
   * Tears down the server and cleans up resources after each test case.
   */
  @AfterEach
  public void tearDown() {
    // Gracefully stop Spark listening on both endpoints
    Spark.unmap("broadband");
    Spark.awaitStop(); // don't proceed until the server is stopped
  }
  /**
   * Attempts to make a request to the specified API call.
   *
   * @param apiCall  The API endpoint to call.
   * @return The HttpURLConnection for the request.
   * @throws IOException If an I/O error occurs.
   */
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
  /**
   * Tests the Broadband API endpoint with missing census parameters and expects a failure response.
   *
   * @throws IOException If an I/O error occurs during the test.
   */
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
  /**
   * Tests successful retrieval of broadband data based on state and county parameters.
   *
   * @throws IOException If an I/O error occurs during the test.
   */
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
  /**
   * Tests retrieval failure when an invalid state code is provided.
   *
   * @throws IOException If an I/O error occurs during the test.
   */
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
  /**
   * Tests failure due to invalid parameters provided in the request.
   *
   * @throws IOException If an I/O error occurs during the test.
   */
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
  /**
   * Prints details of the error response if the response type is 'error'.
   *
   * @param body The response body.
   */
  private void showDetailsIfError(Map<String, Object> body) {
    if (body.containsKey("type") && "error".equals(body.get("type"))) {
      System.out.println(body);
    }
  }
}
