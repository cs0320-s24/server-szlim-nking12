package edu.brown.cs.student.main.server;

import com.squareup.moshi.Moshi;
import spark.Request;
import spark.Response;
import spark.Route;

public class LoadCSVHandler implements Route {

  private Datasource state;

  public LoadCSVHandler(Datasource state) {
    this.state = state;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    String csv = request.queryParams("csv");
    this.state.cleanData(csv);
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
