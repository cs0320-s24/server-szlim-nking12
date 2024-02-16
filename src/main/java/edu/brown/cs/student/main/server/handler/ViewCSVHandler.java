package edu.brown.cs.student.main.server.handler;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.sources.CSVSource;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * A Spark Route handler for handling viewing a CSV dataset. This class implements the Spark Route
 * interface and is designed to handle HTTP GET requests.
 */
public class ViewCSVHandler implements Route {

  private final CSVSource state;

  /**
   * Constructs a new instance of ViewCSVHandler.
   *
   * @param state The CSV data source to be viewed.
   */
  public ViewCSVHandler(CSVSource state) {
    this.state = state;
  }

  /**
   * Handles an HTTP GET request for viewing the CSV dataset.
   *
   * @param request The HTTP request object.
   * @param response The HTTP response object.
   * @return A serialized response, either success or failure, in JSON format.
   */
  @Override
  public Object handle(Request request, Response response) {
    Map<String, Object> responseMap = new HashMap<>();
    Moshi moshi = new Moshi.Builder().build();
    Type type = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, Object>> adapter1 = moshi.adapter(type);
    if (this.state.getData() != null) {
      for (int i = state.getData().size() - 1; i >= 0; i--) {
        responseMap.put("Row" + i, state.getData().get(i));
      }
      responseMap.put("result", "success");
      return adapter1.toJson(responseMap);
    }
    responseMap.put(
        "error", "CSV has not been loaded. you must load a CSV before you can view it.");
    return adapter1.toJson(responseMap);
  }
}
