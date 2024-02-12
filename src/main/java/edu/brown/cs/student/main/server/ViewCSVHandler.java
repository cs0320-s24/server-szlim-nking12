package edu.brown.cs.student.main.server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class ViewCSVHandler implements Route {
  private final Datasource state;

  public ViewCSVHandler(Datasource state) {
    this.state = state;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    ArrayList<List<String>> responseList = new ArrayList<>();
    if (state != null) {
      for (int i = state.getData().size() - 1; i >= 0; i--) {
        responseList.add(state.getData().get(i));
      }
      return new ViewSuccessResponse(responseList).serialize();
    }
    return new ViewFailureResponse(
            "CSV has not been loaded."
                + "Please use the load endpoint before attempting to view a CSV.")
        .serialize();
  }

  public record ViewSuccessResponse(String response_type, ArrayList<List<String>> responseMap) {
    public ViewSuccessResponse(ArrayList<List<String>> responseMap) {
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
