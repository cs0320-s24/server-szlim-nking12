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

public class LocationCodes {

  public LocationCodes() {}

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
      // In a real system, we wouldn't println like this, but it's useful for demonstration:
      return null;
    }
  }

  public Map<String, String> getCountyCodes(String stateCode)
      throws URISyntaxException, IOException, InterruptedException {
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
    System.out.println(sentCountyCodeResponse.body());

    Map<String, String> countycodes = new HashMap<>();

    try {
      Moshi moshi = new Moshi.Builder().build();

      Type mapType = Types.newParameterizedType(List.class, List.class);
      JsonAdapter<List<List<String>>> adapter = moshi.adapter(mapType);

      List<List<String>> adaptedResponse = adapter.fromJson(sentCountyCodeResponse.body());

      for (List<String> pair : adaptedResponse) {
        countycodes.put(pair.get(0), pair.get(2));
      }
      return countycodes;

    }
    // From the Moshi Docs (https://github.com/square/moshi):
    //   "Moshi always throws a standard java.io.IOException if there is an error reading the JSON
    // document, or if it is malformed. It throws a JsonDataException if the JSON document is
    // well-formed, but doesn't match the expected format."
    catch (IOException | JsonDataException e) {
      // In a real system, we wouldn't println like this, but it's useful for demonstration:
      return null;
    }
  }
}
