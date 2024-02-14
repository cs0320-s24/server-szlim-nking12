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

public class CodeHandler {

  private Map<String, String> statecodes;
  private Map<String, String> countycodes;
  public CodeHandler(){
    this.statecodes = new HashMap<>();
    this.countycodes = new HashMap<>();
  }

  public void getStateCodes() throws URISyntaxException, IOException, InterruptedException {
    HttpRequest buildStateCodeRequest =
        HttpRequest.newBuilder()
            .uri(new URI("https://api.census.gov/data/2010/dec/sf1?get=NAME&for=state:*"))
            .GET()
            .build();

    HttpResponse<String> sentStateCodeResponse =
        HttpClient.newBuilder()
            .build()
            .send(buildStateCodeRequest, HttpResponse.BodyHandlers.ofString());
    System.out.println(sentStateCodeResponse.body());

    this.statecodes = new HashMap<>();

    try {
      Moshi moshi = new Moshi.Builder().build();

      Type mapType = Types.newParameterizedType(List.class, List.class);
      JsonAdapter<List<List<String>>> adapter = moshi.adapter(mapType);

      List<List<String>> adaptedResponse = adapter.fromJson(sentStateCodeResponse.body());

      for (List<String> pair : adaptedResponse) {
        statecodes.put(pair.get(0), pair.get(1));
      }

    }
    // From the Moshi Docs (https://github.com/square/moshi):
    //   "Moshi always throws a standard java.io.IOException if there is an error reading the JSON
    // document, or if it is malformed. It throws a JsonDataException if the JSON document is
    // well-formed, but doesn't match the expected format."
    catch (IOException e) {
      // In a real system, we wouldn't println like this, but it's useful for demonstration:
      System.err.println("OrderHandler: string wasn't valid JSON.");
      throw e;
    } catch (JsonDataException e) {
      // In a real system, we wouldn't println like this, but it's useful for demonstration:
      System.err.println("OrderHandler: JSON wasn't in the right format.");
      throw e;
    }
  }

  public void getCountyCodes(int stateCode) throws URISyntaxException, IOException, InterruptedException {
    HttpRequest buildCountyCodeRequest =
        HttpRequest.newBuilder()
            .uri(new URI("https://api.census.gov/data/2010/dec/sf1?get=NAME&for=county:*&in=state:" + stateCode))
            .GET()
            .build();

    HttpResponse<String> sentCountyCodeResponse =
        HttpClient.newBuilder()
            .build()
            .send(buildCountyCodeRequest, HttpResponse.BodyHandlers.ofString());
    System.out.println(sentCountyCodeResponse.body());

    this.countycodes = new HashMap<>();

    try {
      Moshi moshi = new Moshi.Builder().build();

      Type mapType = Types.newParameterizedType(List.class, List.class);
      JsonAdapter<List<List<String>>> adapter = moshi.adapter(mapType);

      List<List<String>> adaptedResponse = adapter.fromJson(sentCountyCodeResponse.body());

      for (List<String> pair : adaptedResponse) {
        statecodes.put(pair.get(0), pair.get(2));
      }

    }
    // From the Moshi Docs (https://github.com/square/moshi):
    //   "Moshi always throws a standard java.io.IOException if there is an error reading the JSON
    // document, or if it is malformed. It throws a JsonDataException if the JSON document is
    // well-formed, but doesn't match the expected format."
    catch (IOException e) {
      // In a real system, we wouldn't println like this, but it's useful for demonstration:
      System.err.println("OrderHandler: string wasn't valid JSON.");
      throw e;
    } catch (JsonDataException e) {
      // In a real system, we wouldn't println like this, but it's useful for demonstration:
      System.err.println("OrderHandler: JSON wasn't in the right format.");
      throw e;
    }
  }

}
