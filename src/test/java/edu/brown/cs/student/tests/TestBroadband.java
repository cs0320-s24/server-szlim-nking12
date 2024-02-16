package edu.brown.cs.student.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.ACSDataSource;
import edu.brown.cs.student.main.server.BroadbandHandler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
import spark.Spark;

public class TestBroadband {

  @BeforeAll
  public static void setupOnce() {
    // Pick an arbitrary free port
    Spark.port(0);
    // Eliminate logger spam in console for test suite
    Logger.getLogger("").setLevel(Level.WARNING); // empty name = root
  }

  //private JsonAdapter<List<List<String>>> adapter;
  private JsonAdapter<Map<String, Object>> adapter;
  private final Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);

  @BeforeEach
  public void setup() {
    // Re-initialize parser, state, etc. for every test method

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
    Type listType = Types.newParameterizedType(List.class, List.class, String.class);
    //adapter = moshi.adapter(listType);
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

    // Get the expected response: an error
    //List<List<String>> responseBody = adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    Map<String, Object> body = this.adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));

    showDetailsIfError(body);
    assertEquals(" failure", body.get("status:"));
    loadConnection.disconnect(); // close gracefully
  }

  @Test
  public void testSuccessfulDataRetrieval() {
    List<List<String>> mockedData = new ArrayList<>();
    mockedData.add(Arrays.asList("variable1", "value1"));
    mockedData.add(Arrays.asList("variable2", "value2"));

    Map<String, String> expected = new HashMap<>();
    expected.put("type", "success");
    expected.put("data retrieved at: ", String.valueOf(LocalDateTime.now()));
    expected.put("variable1", "value1");
    expected.put("variable2", "value2");

    //nope im confused

  }

  @Test
  public void testCountyCodeRetrievalFailure() {

  }

  @Test
  public void testStateCodeRetrievalFailure(){

  }

  @Test
  public void testCachingHit(){
    //check that if the same data is requested again, it's retrieved from the cache rather than making a new API call.
  }

  @Test
  public void testCachingMiss(){
    //check that when the data is not present in the cache, and ensure that the ACSCaching class fetches data from the CensusAPISource.
  }

  @Test
  public void testInvalidParameters(){

  }

  @Test
  public void testMissingParameters(){

  }



  private void showDetailsIfError(Map<String, Object> body) {
    if(body.containsKey("type") && "error".equals(body.get("type"))) {
      System.out.println(body);
    }
  }


}
