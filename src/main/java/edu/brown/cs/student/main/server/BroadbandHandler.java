package edu.brown.cs.student.main.server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.CensusAPISource.CensusResponse;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class BroadbandHandler implements Route {
  private LocationCodes codeHandler;
  private Map<String, String> statecodes;
  private final CensusAPISource source;

  public BroadbandHandler() {
    this.codeHandler = new LocationCodes();
    this.statecodes = this.codeHandler.getStateCodes();
    this.source = new CensusAPISource();
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    Map<String, Object> responseMap = new HashMap<>();
    if (this.statecodes == null) {
      responseMap.put("status:", " failure");
      responseMap.put("reason", " unable to retrieve state codes for API");
      return responseMap;
    }
    String state = this.statecodes.get(request.queryParams("state"));
    Map<String, String> countycodes = this.codeHandler.getCountyCodes(state);
    if (countycodes == null) {
      responseMap.put("status:", " failure");
      responseMap.put(
          "reason", " unable to retrieve county codes for API, there may be a spelling error");
      responseMap.put("state input", state);
      return responseMap;
    }

    String county = countycodes.get(request.queryParams("county"));

    if (county == null) {
      county = "*";
    }

    // Sends a request to the API and receives JSON back
    Moshi moshi = new Moshi.Builder().build();
    // Replies will be Maps from String to Object. This isn't ideal; see reflection...
    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
    JsonAdapter<CensusResponse> censusAdapter = moshi.adapter(CensusResponse.class);

    if (state == null) {
      responseMap.put("state_arg", state);
      responseMap.put("county_arg", county);
      responseMap.put("type", "error");
      responseMap.put("error_type", "missing parameter");
      return adapter.toJson(responseMap);
    }

    try {
      CensusResponse data = this.source.getData(state, county);
      responseMap.put("type", "success"); // STOPPED HERE NEED TO CONTINUE FOLLOWING TEMPLATE
      responseMap.put("place", data.NAME());
      responseMap.put("percentage", data.S2802_C03_022E());
      return adapter.toJson(responseMap);
    } catch (DatasourceException e) {
    }

    //      String infoJson = this.sendRequest(state, county, responseMap);
    //      List<List<String>> list = CensusAPIUtilities.deserializeCensusData(infoJson);
    //      responseMap.put("result", "success");
    //      responseMap.put("time retrieved", LocalDateTime.now().toString());
    //      for (List<String> strings : list) {
    //        responseMap.put(strings.get(0), strings.get(1) + "%");
    //      }
    //      return responseMap;
    //     catch (URISyntaxException | IOException | InterruptedException e) {
    //      responseMap.put("result", "Exception");
    //      responseMap.put("reason", e.getMessage());
    //    }
    return responseMap;
  }

  //  private String sendRequest(String state, String county, Map<String, String> responseMap)
  //      throws URISyntaxException, IOException, InterruptedException {

  //    HttpRequest buildCensusRequest =
  //        HttpRequest.newBuilder()
  //            .uri(
  //                new URI(
  //                    "https://api.census.gov/data/2021/acs/acs1/subject/"
  //                        + "variables?get="
  //                        + "NAME,S2802_C03_022E&for=county:"
  //                        + county
  //                        + "&in=state:"
  //                        + state))
  //            .GET()
  //            .build();

  // Send that API request then store the response in this variable. Note the generic type.
  //    HttpResponse<String> sentCensusResponse =
  //        HttpClient.newBuilder()
  //            .build()
  //            .send(buildCensusRequest, HttpResponse.BodyHandlers.ofString());

  //    sentCensusResponse.statusCode();
  //    responseMap.put("Status:", String.valueOf(sentCensusResponse.statusCode()));
  //
  //    System.out.println(sentCensusResponse);
  //    System.out.println(sentCensusResponse.body());
  //
  //    return sentCensusResponse.body();
  //  }
}
