package edu.brown.cs.student.main.server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.CSVParser.exceptions.FactoryFailureException;
import edu.brown.cs.student.main.CSVParser.utility.Search;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class SearchCSVHandler implements Route {
  private final Datasource state;

  public SearchCSVHandler(Datasource state) {
    this.state = state;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    Map<String, List<String>> responseMap = new HashMap<>();
    if (state != null) {
      List<List<String>> searchResults = this.searchCSV(request);
      for (int i = searchResults.size() - 1; i >= 0; i--) {
        responseMap.put("Row " + i, searchResults.get(i));
      }
      return new ViewSuccessResponse(responseMap).serialize();
    }
    return new ViewFailureResponse(
            "CSV has not been loaded."
                + "Please use the load endpoint before attempting to view a CSV.")
        .serialize(); // change this to a
    // failure response
  }

  public List<List<String>> searchCSV(Request request) throws IOException, FactoryFailureException {
    String target = request.queryParams("target");
    String col = request.queryParams("col");
    System.out.println("col" + col);
    Search searcher = new Search(this.state.getData(), this.state.getHeaderRow());
    return searcher.search(target, col);
  }

  public record ViewSuccessResponse(String response_type, Map<String, List<String>> responseMap) {
    public ViewSuccessResponse(Map<String, List<String>> responseMap) {
      this("success", responseMap);
    }

    /**
     * @return this response, serialized as Json
     */
    String serialize() {
      try {
        // Initialize Moshi which takes in this class and returns it as JSON!
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<ViewSuccessResponse> adapter = moshi.adapter(ViewSuccessResponse.class);
        return adapter.toJson(this);
      } catch (Exception e) {
        e.printStackTrace();
        System.err.println("Error: " + e.getMessage());
        throw e;
      }
    }
  }

  public record ViewFailureResponse(String resp) {

    /**
     * @return this response, serialized as Json
     */
    String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      return moshi.adapter(ViewFailureResponse.class).toJson(this);
    }
  }
}
