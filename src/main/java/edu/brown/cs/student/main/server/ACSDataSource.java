package edu.brown.cs.student.main.server;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

/**
 * An interface for classes that serve as a data source for retrieving information related to ACS
 */
public interface ACSDataSource {
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
  List<List<String>> getData(String statenum, String countynum)
      throws DatasourceException, IOException, InterruptedException, URISyntaxException;
}
