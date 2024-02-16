package edu.brown.cs.student.main.server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * A Spark Route handler for HTTP requests related to broadband data retrieval. It interacts with
 * ACSDataSource to fetch information based on state and county parameters provided in the request.
 * This class implements the Spark Route interface and is designed to handle HTTP GET requests.
 */
public class BroadbandHandler implements Route {
  private LocationCodes codeHandler;
  private Map<String, String> statecodes;
  private ACSDataSource source;
  /**
   * Constructs a BroadbandHandler instance with the provided ACSDataSource.
   *
   * @param source The ACSDataSource used for retrieving data.
   */
  public BroadbandHandler(ACSDataSource source) {
    this.codeHandler = new LocationCodes();
    this.statecodes = this.codeHandler.getStateCodes();
    this.source = source;
  }
  /**
   * Handles HTTP requests related to broadband data retrieval.
   *
   * @param request The HTTP request containing parameters for data retrieval.
   * @param response The HTTP response to be populated with the result.
   * @return The JSON response containing broadband data or error information.
   */
  @Override
  public Object handle(Request request, Response response) {

    Date retrieved = Calendar.getInstance().getTime();
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    String strDate = dateFormat.format(retrieved);

    Map<String, Object> responseMap = new HashMap<>();
    Moshi moshi = new Moshi.Builder().build();
    Type type = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, Object>> adapter1 = moshi.adapter(type);

    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, String.class);
    JsonAdapter<Map<String, String>> adapter = moshi.adapter(mapStringObject);
    adapter = moshi.adapter(mapStringObject);

    try {
      if (this.statecodes == null) {
        responseMap.put("result", "error_datasource");
        responseMap.put("reason", "unable to retrieve state codes from API");
        return adapter1.toJson(responseMap);
      }
      String state = this.statecodes.get(request.queryParams("state"));
      Map<String, String> countycodes = this.codeHandler.getCountyCodes(state);
      if (countycodes == null) {
        responseMap.put("result", "error_bad_request");
        responseMap.put(
            "reason", "unable to retrieve county codes for API, there may be a spelling error");
        responseMap.put("state_arg", request.queryParams("state"));
        responseMap.put("county_arg", request.queryParams("county"));
        return adapter1.toJson(responseMap);
      }

      String county = countycodes.get(request.queryParams("county"));

      if (county == null) {
        county = "*";
      }

      if (state == null) {
        responseMap.put("state_arg", request.queryParams("state"));
        responseMap.put("county_arg", request.queryParams("county"));
        responseMap.put("result", "error_bad_request");
        responseMap.put("error_type", "missing parameter");
        return adapter1.toJson(responseMap);
      }

      List<List<String>> data = this.source.getData(state, county);
      responseMap.put("result", "success");
      responseMap.put("data retrieved at: ", strDate);
      for (List<String> list : data) {
        responseMap.put(list.get(0), list.get(1));
      }
      return adapter1.toJson(responseMap);

    } catch (DatasourceException | IOException | InterruptedException | URISyntaxException | JsonDataException e) {
      System.err.println(e.getMessage());
      responseMap.put("result", "error_bad_json");
      responseMap.put("reason", e.getMessage());
      return adapter1.toJson(responseMap);
    }
  }
}
