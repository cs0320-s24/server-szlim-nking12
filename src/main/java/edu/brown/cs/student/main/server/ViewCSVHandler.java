package edu.brown.cs.student.main.server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;
/**
 * A Spark Route handler for handling viewing a CSV dataset.
 * This class implements the Spark Route interface and is designed to handle HTTP GET requests.
 */
public class ViewCSVHandler implements Route {
  private final CSVSource state;
  /**
   * Constructs a new instance of ViewCSVHandler.
   * @param state The CSV data source to be viewed.
   */
  public ViewCSVHandler(CSVSource state) {
    this.state = state;
  }
  /**
   * Handles an HTTP GET request for viewing the CSV dataset.
   *
   * @param request  The HTTP request object.
   * @param response The HTTP response object.
   * @return A serialized response, either success or failure, in JSON format.
   * @throws Exception Thrown if an error occurs during request handling.
   */
  @Override
  public Object handle(Request request, Response response) throws Exception {
    Map<String, List<String>> responseMap = new HashMap<>();
    if (state != null) {
      for (int i = state.getData().size() - 1; i >= 0; i--) {
        responseMap.put("Row" + i, state.getData().get(i));
      }
      return new ViewSuccessResponse(responseMap).serialize();
    }
    return new ViewFailureResponse(
            "CSV has not been loaded."
                + "Please use the load endpoint before attempting to view a CSV.")
        .serialize();
  }
  /** Response object to send */
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
  /**
   * A record representing a failure response when viewing the CSV.
   */
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
