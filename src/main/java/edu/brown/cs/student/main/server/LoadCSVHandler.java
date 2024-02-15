package edu.brown.cs.student.main.server;

import com.squareup.moshi.Moshi;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * A Spark Route handler for loading a CSV dataset. This class implements the Spark Route interface
 * and is designed to handle HTTP GET requests.
 */
public class LoadCSVHandler implements Route {

  private CSVSource state;
  /**
   * Constructs a new instance of LoadCSVHandler.
   *
   * @param state The CSV data source to be searched.
   */
  public LoadCSVHandler(CSVSource state) {
    this.state = state;
    if (!(this.state.getData() == null)) {
      if (!this.state.getData().isEmpty()) {
        this.state.getData().clear();
      }
    }
  }
  /**
   * Handles an HTTP GET request for loading the CSV dataset.
   *
   * @param request The HTTP request object.
   * @param response The HTTP response object.
   * @return A serialized response, either success or failure, in JSON format.
   * @throws Exception Thrown if an error occurs during request handling.
   */
  @Override
  public Object handle(Request request, Response response) throws Exception {
    String csv = request.queryParams("csv");
    String headers = request.queryParams("headers");

    // directory protection
    if (!csv.contains("data/")) {
      throw new IllegalArgumentException();
    }

    boolean hasHeaders;
    if (headers == null) {
      hasHeaders = false;
    } else {
      hasHeaders = headers.equalsIgnoreCase("true");
    }
    System.out.println(hasHeaders);
    this.state.cleanData(csv, hasHeaders);
    return new LoadSuccessResponse().serialize();
  } // need to add error response

  /** A record representing a success response when loading a CSV file. */
  public record LoadSuccessResponse(String response_type) {
    /** Constructs a LoadSuccessResponse with the success message. */
    public LoadSuccessResponse() {
      this("CSV file successfully loaded.");
    }

    /**
     * @return this response, serialized as Json
     */
    String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      return moshi
          .adapter(edu.brown.cs.student.main.server.LoadCSVHandler.LoadSuccessResponse.class)
          .toJson(this);
    }
  }
}
