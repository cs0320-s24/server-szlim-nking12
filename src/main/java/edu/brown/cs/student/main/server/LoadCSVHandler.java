package edu.brown.cs.student.main.server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.CSVParser.exceptions.FactoryFailureException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
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
  public Object handle(Request request, Response response)
      throws IllegalArgumentException, IOException, FactoryFailureException {
    Moshi moshi = new Moshi.Builder().build();
    Type type = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, Object>> adapter1 = moshi.adapter(type);
    Map<String, Object> responseMap = new HashMap<>();
    try{
    String csv = request.queryParams("csv");
    String headers = request.queryParams("headers");

    // directory protection
    if (!csv.contains("data/")) {
      responseMap.put("result", "error");
      responseMap.put("message", "File is outside protected directory. CSV files must be within the data/ directory.");
      responseMap.put("CSV entered", csv);
      return adapter1.toJson(responseMap);
    }

    boolean hasHeaders;
    if (headers == null) {
      hasHeaders = false;
    } else {
      hasHeaders = headers.equalsIgnoreCase("true");
    }
    this.state.cleanData(csv, hasHeaders);
    responseMap.put("result", "success");
    responseMap.put("message", "CSV file successfully loaded.");
    return adapter1.toJson(responseMap);
  } catch (IllegalArgumentException | IOException | FactoryFailureException e){
      responseMap.put("result", "error");
      responseMap.put("error", e.getMessage());
      responseMap.put("csv entered", request.queryParams("csv"));
      return adapter1.toJson(responseMap);
    }
}}
