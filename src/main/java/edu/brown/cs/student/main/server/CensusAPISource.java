package edu.brown.cs.student.main.server;

import com.squareup.moshi.Moshi;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class CensusAPISource implements ACSDataSource {

  private String county;
  private String state;
  private String percentage;

  public CensusAPISource() {}

  private static HttpURLConnection connect(URL requestURL) throws DatasourceException, IOException {
    URLConnection urlConnection = requestURL.openConnection();
    if (!(urlConnection instanceof HttpURLConnection clientConnection))
      throw new DatasourceException("unexpected: result of connection wasn't HTTP");
    clientConnection.connect(); // GET
    if (clientConnection.getResponseCode() != 200)
      throw new DatasourceException(
          "unexpected: API connection not success status " + clientConnection.getResponseMessage());
    return clientConnection;
  }

  private static CensusResponse getCensusData(String state, String county) {
    try {
      HttpRequest buildCensusRequest =
          HttpRequest.newBuilder()
              .uri(
                  new URI(
                      "https://api.census.gov/data/2021/acs/acs1/subject/"
                          + "variables?get="
                          + "NAME,S2802_C03_022E&for=county:"
                          + county
                          + "&in=state:"
                          + state))
              .GET()
              .build();

      // Send that API request then store the response in this variable. Note the generic type.
      HttpResponse<String> sentCensusResponse =
          HttpClient.newBuilder()
              .build()
              .send(buildCensusRequest, HttpResponse.BodyHandlers.ofString());

      Moshi moshi = new Moshi.Builder().build();

      CensusResponse body = CensusAPIUtilities.deserializeCensusData(sentCensusResponse.body());

      // sentCensusResponse.statusCode();

      if (body == null || body.NAME() == null || body.S2802_C03_022E() == null)
        throw new DatasourceException("Malformed response from NWS");

      return new CensusResponse(body.NAME, body.S2802_C03_022E);
    } catch (DatasourceException | IOException | InterruptedException | URISyntaxException e) {
      return null;
    }
  }

  @Override
  public String toString() {
    return this.percentage
        + "of households in"
        + this.county
        + " ,"
        + this.state
        + "have broadband access.";
  }

  @Override
  public CensusResponse getData(String statenum, String countynum) throws DatasourceException {
    return getCensusData(statenum, countynum);
  }

  public record CensusResponse(String NAME, String S2802_C03_022E) {}
}
