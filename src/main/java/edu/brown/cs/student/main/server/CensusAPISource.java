package edu.brown.cs.student.main.server;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class CensusAPISource implements ACSDataSource {

  public CensusAPISource() {}

  private static List<CensusResponse> getCensusData(String state, String county) {
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

      List<CensusResponse> body =
          CensusAPIUtilities.deserializeCensusData(sentCensusResponse.body());

      // sentCensusResponse.statusCode();
      //
      //      if (body == null || body.NAME() == null || body.S2802_C03_022E() == null)
      //        throw new DatasourceException("Malformed response from NWS");
      //
      //      return new CensusResponse(body.NAME(), body.S2802_C03_022E());
      return body;
    } catch (IOException | InterruptedException | URISyntaxException e) {
      return null;
    }
  }

  //  @Override
  //  public String toString() {
  //    return this.percentage
  //        + "of households in"
  //        + this.county
  //        + " ,"
  //        + this.state
  //        + "have broadband access.";
  //  }

  @Override
  public List<CensusResponse> getData(String statenum, String countynum)
      throws DatasourceException {
    return getCensusData(statenum, countynum);
  }
}
