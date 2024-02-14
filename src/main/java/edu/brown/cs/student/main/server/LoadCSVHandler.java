package edu.brown.cs.student.main.server;

import com.squareup.moshi.Moshi;
import spark.Request;
import spark.Response;
import spark.Route;

public class LoadCSVHandler implements Route {

  private CSVSource state;

  public LoadCSVHandler(CSVSource state) {
    this.state = state;
    if (!(this.state.getData() == null)) {
      if (!this.state.getData().isEmpty()) {
        this.state.getData().clear();
      }
    }
  }

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

  public record LoadSuccessResponse(String response_type) {
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
