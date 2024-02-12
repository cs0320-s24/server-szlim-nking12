package edu.brown.cs.student.main.server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class ViewCSVHandler implements Route {
  private final List<List<String>> state;


  public ViewCSVHandler(List<List<String>> state){
    this.state = state;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    Map<String, List<String>> responseMap = new HashMap<>();
    if (state != null) {
      for (int i = state.size() - 1; i >= 0; i--) {
        responseMap.put("Row " + i, state.get(i));
      }
      return new ViewSuccessResponse(responseMap).serialize();
    }

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
        // For debugging purposes, show in the console _why_ this fails
        // Otherwise we'll just get an error 500 from the API in integration
        // testing.
        e.printStackTrace();
        System.err.println("Error: " + e.getMessage());
        throw e;
      }
    }
  }
}
