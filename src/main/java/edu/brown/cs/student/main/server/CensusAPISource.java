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

  private static List<List<String>> getCensusData(String state, String county)
      throws DatasourceException, IOException, InterruptedException, URISyntaxException {
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

    HttpResponse<String> sentCensusResponse =
        HttpClient.newBuilder()
            .build()
            .send(buildCensusRequest, HttpResponse.BodyHandlers.ofString());

    List<List<String>> body = CensusAPIUtilities.deserializeCensusData(sentCensusResponse.body());
    sentCensusResponse.statusCode();

    if (body == null) throw new DatasourceException("Malformed response from NWS");

    return body;
  }

  @Override
  public List<List<String>> getData(String statenum, String countynum)
      throws DatasourceException, IOException, InterruptedException, URISyntaxException {
    return getCensusData(statenum, countynum);
  }
}
