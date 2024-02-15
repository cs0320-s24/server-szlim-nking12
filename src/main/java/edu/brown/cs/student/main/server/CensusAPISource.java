package edu.brown.cs.student.main.server;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

/**
 * The CensusAPISource class implements the ACSDataSource interface to fetch ACS data from the
 * Census API. It sends HTTP requests to the API and deserializes the response to obtain information
 * based on state and county parameters.
 */
public class CensusAPISource implements ACSDataSource {
  /** Constructs a CensusAPISource instance. */
  public CensusAPISource() {}
  /**
   * Sends a request to the Census API and retrieves ACS data for a specific state and county.
   *
   * @param state The state code for data retrieval.
   * @param county The county code for data retrieval.
   * @return The retrieved data as a list of lists of strings.
   * @throws DatasourceException Thrown if there is an issue with the data source.
   * @throws IOException Thrown if there is an I/O error during data retrieval.
   * @throws InterruptedException Thrown if the data retrieval process is interrupted.
   * @throws URISyntaxException Thrown if there is an issue with the URI syntax for data retrieval.
   */
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
  /**
   * Retrieves data for a specific state and county.
   *
   * @param statenum The state number for retrieving data.
   * @param countynum The county number for retrieving data.
   * @return The retrieved data as a list of lists of strings.
   * @throws DatasourceException Thrown if there is an issue with the data source.
   * @throws IOException Thrown if there is an I/O error during data retrieval.
   * @throws InterruptedException Thrown if the data retrieval process is interrupted.
   * @throws URISyntaxException Thrown if there is an issue with the URI syntax for data retrieval.
   */
  @Override
  public List<List<String>> getData(String statenum, String countynum)
      throws DatasourceException, IOException, InterruptedException, URISyntaxException {
    return getCensusData(statenum, countynum);
  }
}
