package edu.brown.cs.student.main.server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.io.IOException;
import okio.Buffer;
import java.util.Map;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class CensusAPISource implements ACSDataSource {

  private String county;
  private String state;
  private String percentage;

  public CensusAPISource() {

  }


  private static HttpURLConnection connect(URL requestURL) throws DatasourceException, IOException {
    URLConnection urlConnection = requestURL.openConnection();
    if(! (urlConnection instanceof HttpURLConnection))
      throw new DatasourceException("unexpected: result of connection wasn't HTTP");
    HttpURLConnection clientConnection = (HttpURLConnection) urlConnection;
    clientConnection.connect(); // GET
    if(clientConnection.getResponseCode() != 200)
      throw new DatasourceException("unexpected: API connection not success status "+clientConnection.getResponseMessage());
    return clientConnection;
  }

  private static CensusResponse getCensusData(String state, String county)
      throws DatasourceException {

    try {

      URL requestURL = new URL("https://api.census.gov/data/2021/acs/acs1/subject/"
          + "variables?get="
          + "NAME,S2802_C03_022E&for=county:"
          + county
          + "&in=state:"
          + state);
      HttpURLConnection clientConnection = connect(requestURL);
      Moshi moshi = new Moshi.Builder().build();

      JsonAdapter<CensusResponse> adapter = moshi.adapter(CensusResponse.class).nonNull();

      CensusResponse body = adapter.fromJson(
          new Buffer().readFrom(clientConnection.getInputStream()));

      System.out.println(body); // records are nice for giving auto toString

      clientConnection.disconnect();

      if (body == null || body.NAME() == null || body.S2802_C03_022E() == null)
        throw new DatasourceException("Malformed response from NWS");

      return new CensusResponse(body.NAME, body.S2802_C03_022E);
    } catch (IOException e) {
      throw new DatasourceException(e.getMessage());
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
