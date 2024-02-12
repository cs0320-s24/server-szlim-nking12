package edu.brown.cs.student.main.server;

import com.squareup.moshi.Moshi;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class LoadCSVHandler implements Route {

  private List<List<String>> state;

  public LoadCSVHandler(List<List<String>> state) {}

  @Override
  public Object handle(Request request, Response response) throws Exception {
    String csv = request.queryParams("csv");
    this.state = new CSVSource(csv).getData(csv);
    Map<String, List<String>> responseMap = new HashMap<>();

    for (int i = state.size() - 1; i >= 0; i--) {
      responseMap.put("Row " + i, state.get(i));
    }
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
