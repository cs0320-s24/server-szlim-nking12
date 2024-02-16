package edu.brown.cs.student.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.CSVSource;
import edu.brown.cs.student.main.server.LoadCSVHandler;
import edu.brown.cs.student.main.server.SearchCSVHandler;
import edu.brown.cs.student.main.server.ViewCSVHandler;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

public class TestCSV {

  private JsonAdapter<Map<String, Object>> adapter;
  private final Type mapStringObject =
      Types.newParameterizedType(Map.class, String.class, Object.class);
  private CSVSource state;

  @BeforeAll
  public static void setupOnce() {
    // Pick an arbitrary free port
    Spark.port(0);
    // Eliminate logger spam in console for test suite
    Logger.getLogger("").setLevel(Level.WARNING); // empty name = root
  }

  @BeforeEach
  public void setup() {

    // Use *MOCKED* data when in this test environment.
    // Notice that the WeatherHandler code doesn't need to care whether it has
    // "real" data or "fake" data. Good separation of concerns enables better testing.

    this.state = new CSVSource();

    Spark.get("loadcsv", new LoadCSVHandler(state));
    Spark.get("viewcsv", new ViewCSVHandler(state));
    Spark.get("searchcsv", new SearchCSVHandler(state));
    Spark.awaitInitialization(); // don't continue until the server is listening

    // New Moshi adapter for responses (and requests, too; see a few lines below)
    //   For more on this, see the Server gearup.
    Moshi moshi = new Moshi.Builder().build();
    this.adapter = moshi.adapter(this.mapStringObject);
  }

  @AfterEach
  public void tearDown() {
    // Gracefully stop Spark listening on both endpoints
    Spark.unmap("loadcsv");
    Spark.unmap("viewcsv");
    Spark.unmap("searchcsv");
    Spark.awaitStop(); // don't proceed until the server is stopped
  }

  private static HttpURLConnection tryRequest(String apiCall, String filePath) throws IOException {
    // Configure the connection (but don't actually send the request yet)
    URL requestURL =
        new URL("http://localhost:" + Spark.port() + "/" + apiCall + "?csv=" + filePath);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();
    clientConnection.connect();
    return clientConnection;
  }

  private static HttpURLConnection trySearchRequest(String apiCall, String target, String col)
      throws IOException {
    // Configure the connection (but don't actually send the request yet)
    URL requestURL =
        new URL(
            "http://localhost:"
                + Spark.port()
                + "/"
                + apiCall
                + "?target="
                + target
                + "&col="
                + col);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();
    clientConnection.connect();
    return clientConnection;
  }

  @Test
  public void testLoadHandlerCorrect() throws IOException {
    // tests on RI_Data csv file with correct inputs
    HttpURLConnection loadConnection =
        tryRequest(
            "loadcsv",
            "/Users/natalie/Desktop/CS32/projects/server-szlim-nking12/data/census/dol_ri_earnings_disparity.csv");
    assertEquals(200, loadConnection.getResponseCode());

    Map<String, Object> body =
        this.adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));

    assertEquals("success", body.get("result"));
  }

  @Test
  public void testLoadHandlerProtectedFile() throws IOException {
    // tests on RI_Data csv file with correct inputs
    HttpURLConnection loadConnection = tryRequest("loadcsv", "/stardata");
    assertEquals(200, loadConnection.getResponseCode());

    Map<String, Object> body =
        this.adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));

    assertEquals("error", body.get("result"));
  }

  @Test
  public void testLoadHandlerNonexistent() throws IOException {
    // tests on RI_Data csv file with correct inputs
    HttpURLConnection loadConnection =
        tryRequest(
            "loadcsv",
            "/Users/natalie/Desktop/CS32/projects/server-szlim-nking12/data/census/dol_ri_earnings_dispty.csv");
    assertEquals(200, loadConnection.getResponseCode());

    Map<String, Object> body =
        this.adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));

    assertEquals("error", body.get("result"));
  }

  @Test
  public void testViewNotLoaded() throws IOException {
    // tests on RI_Data csv file without loading

    HttpURLConnection viewConnection =
        tryRequest(
            "viewcsv",
            "/Users/sophialim/Desktop/cs32/server-szlim-nking12/data/census/dol_ri_earnings_disparity.csv");
    assertEquals(200, viewConnection.getResponseCode());

    Map<String, Object> body =
        this.adapter.fromJson(new Buffer().readFrom(viewConnection.getInputStream()));

    assertEquals(
        "CSV has not been loaded. you must load a CSV before you can view it.", body.get("error"));
  }

  @Test
  public void testViewSuccess() throws IOException {
    // successful view
    HttpURLConnection loadConnection =
        tryRequest(
            "loadcsv",
            "/Users/sophialim/Desktop/cs32/server-szlim-nking12/data/census/dol_ri_earnings_disparity.csv");
    assertEquals(200, loadConnection.getResponseCode());

    HttpURLConnection viewConnection =
        tryRequest(
            "viewcsv",
            "/Users/sophialim/Desktop/cs32/server-szlim-nking12/data/census/dol_ri_earnings_disparity.csv");
    assertEquals(200, loadConnection.getResponseCode());

    Map<String, Object> body =
        this.adapter.fromJson(new Buffer().readFrom(viewConnection.getInputStream()));

    assertEquals("success", body.get("result"));
  }

  @Test
  public void testSearchNoResults() throws IOException {
    HttpURLConnection loadConnection =
        tryRequest(
            "loadcsv",
            "/Users/sophialim/Desktop/cs32/server-szlim-nking12/data/census/dol_ri_earnings_disparity.csv");
    assertEquals(200, loadConnection.getResponseCode());

    HttpURLConnection searchConnection = trySearchRequest("searchcsv", "xyz", "1");
    assertEquals(200, loadConnection.getResponseCode());

    Map<String, Object> body =
        this.adapter.fromJson(new Buffer().readFrom(searchConnection.getInputStream()));

    assertEquals("no matches found", body.get("result"));
  }

  @Test
  public void testSearchNotLoaded() throws IOException {
    HttpURLConnection loadConnection =
        tryRequest(
            "loadcsv",
            "/Users/sophialim/Desktop/cs32/server-szlim-nking12/data/census/dol_ri_earnings_disparity.csv");
    assertEquals(200, loadConnection.getResponseCode());

    HttpURLConnection searchConnection = trySearchRequest("searchcsv", "White", "1");
    assertEquals(200, loadConnection.getResponseCode());

    Map<String, Object> body =
        this.adapter.fromJson(new Buffer().readFrom(searchConnection.getInputStream()));

    assertEquals("success", body.get("result"));
    assertEquals(
        "[RI, White, \" $1,058.47 \", 395773.6521,  $1.00 , 75%]", body.get("Row 0").toString());
  }

  @Test
  public void testSearchSuccessOneResult() throws IOException {
    HttpURLConnection loadConnection =
        tryRequest(
            "loadcsv",
            "/Users/sophialim/Desktop/cs32/server-szlim-nking12/data/census/dol_ri_earnings_disparity.csv");
    assertEquals(200, loadConnection.getResponseCode());

    HttpURLConnection searchConnection = trySearchRequest("searchcsv", "White", "1");
    assertEquals(200, loadConnection.getResponseCode());

    Map<String, Object> body =
        this.adapter.fromJson(new Buffer().readFrom(searchConnection.getInputStream()));

    assertEquals("success", body.get("result"));
    assertEquals(
        "[RI, White, \" $1,058.47 \", 395773.6521,  $1.00 , 75%]", body.get("Row 0").toString());
  }

  @Test
  public void testSearchMultipleResults() {}
}
