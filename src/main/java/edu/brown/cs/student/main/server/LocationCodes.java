package edu.brown.cs.student.main.server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The LocationCodes class provides methods for retrieving state and county codes from the Census
 * API.
 */
public class LocationCodes {

  /** Constructs a LocationCodes instance. */
  public LocationCodes() {}

  /**
   * Retrieves a map of state codes from the Census API.
   *
   * @return A map of state codes where the key is the state name and the value is the state code.
   */
  public Map<String, String> getStateCodes() {
    try {
      HttpRequest buildStateCodeRequest =
          HttpRequest.newBuilder()
              .uri(new URI("https://api.census.gov/data/2010/dec/sf1?get=NAME&for=state:*"))
              .GET()
              .build();

      HttpResponse<String> sentStateCodeResponse =
          HttpClient.newBuilder()
              .build()
              .send(buildStateCodeRequest, HttpResponse.BodyHandlers.ofString());

      Map<String, String> statecodes = new HashMap<>();

      Moshi moshi = new Moshi.Builder().build();

      Type mapType = Types.newParameterizedType(List.class, List.class);
      JsonAdapter<List<List<String>>> adapter = moshi.adapter(mapType);

      List<List<String>> adaptedResponse = adapter.fromJson(sentStateCodeResponse.body());

      for (List<String> pair : adaptedResponse) {
        statecodes.put(pair.get(0), pair.get(1));
      }
      return statecodes;
    } catch (IOException | URISyntaxException | JsonDataException | InterruptedException e) {
      return null;
    }
  }

  /**
   * Retrieves a map of county codes from the Census API.
   *
   * @return A map of county codes where the key is the county name and the value is the county
   *     code.
   */
  public Map<String, String> getCountyCodes(String stateCode)
      throws URISyntaxException, IOException, InterruptedException {
    try {
      HttpRequest buildCountyCodeRequest =
          HttpRequest.newBuilder()
              .uri(
                  new URI(
                      "https://api.census.gov/data/2010/dec/sf1?get=NAME&for=county:*&in=state:"
                          + stateCode))
              .GET()
              .build();

      HttpResponse<String> sentCountyCodeResponse =
          HttpClient.newBuilder()
              .build()
              .send(buildCountyCodeRequest, HttpResponse.BodyHandlers.ofString());

      Map<String, String> countycodes = new HashMap<>();

      Moshi moshi = new Moshi.Builder().build();

      Type mapType = Types.newParameterizedType(List.class, List.class);
      JsonAdapter<List<List<String>>> adapter = moshi.adapter(mapType);

      List<List<String>> adaptedResponse = adapter.fromJson(sentCountyCodeResponse.body());

      for (List<String> pair : adaptedResponse) {
        countycodes.put(pair.get(0), pair.get(2));
      }
      return countycodes;
    } catch (IOException | URISyntaxException | JsonDataException | InterruptedException e) {
      return null;
    }
  }
}
