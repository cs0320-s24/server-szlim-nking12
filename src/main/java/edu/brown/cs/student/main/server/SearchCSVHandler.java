package edu.brown.cs.student.main.server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.CSVParser.exceptions.FactoryFailureException;
import edu.brown.cs.student.main.CSVParser.utility.Search;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * A Spark Route handler for handling search requests on a CSV dataset. This class implements the
 * Spark Route interface and is designed to handle HTTP GET requests.
 */
public class SearchCSVHandler implements Route {

  private final CSVSource state;

  /**
   * Constructs a new instance of SearchCSVHandler.
   *
   * @param state The CSV data source to be searched.
   */
  public SearchCSVHandler(CSVSource state) {
    this.state = state;
  }

  /**
   * Handles an HTTP GET request for searching the CSV dataset based on query parameters.
   *
   * @param request The HTTP request object.
   * @param response The HTTP response object.
   * @return A serialized response, either success or failure, in JSON format.
   * @throws IOException Thrown if an error occurs during request handling.
   * @throws FactoryFailureException if an error occurs during CSV parsing
   */
  @Override
  public Object handle(Request request, Response response)
      throws IOException, FactoryFailureException {
    Moshi moshi = new Moshi.Builder().build();
    Type type = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, Object>> adapter1 = moshi.adapter(type);
    Map<String, Object> responseMap = new HashMap<>();
    if (state.getData() != null) {
      List<List<String>> searchResults = this.searchCSV(request);
      if (searchResults.isEmpty()) {
        responseMap.put("result", "no matches found");
        return adapter1.toJson(responseMap);
      }
      for (int i = searchResults.size() - 1; i >= 0; i--) {
        responseMap.put("Row " + i, searchResults.get(i));
      }
      responseMap.put("result", "success");
      return adapter1.toJson(responseMap);
    }

    responseMap.put("result", "error");
    responseMap.put(
        "error",
        "CSV has not been loaded."
            + "Please use the load endpoint before attempting to view a CSV.");
    return adapter1.toJson(responseMap);
  }

  /**
   * Performs a search on the CSV based on parameters.
   *
   * @param request The HTTP request object containing search parameters.
   * @return A list of search results as rows.
   * @throws IOException Thrown if an I/O error occurs during search.
   * @throws FactoryFailureException Thrown if there is a failure during search.
   */
  public List<List<String>> searchCSV(Request request) throws IOException, FactoryFailureException {
    String target = request.queryParams("target");
    String col = request.queryParams("col");
    Search searcher = new Search(this.state.getData(), this.state.getHeaderRow());
    return searcher.search(target, col);
  }
}
