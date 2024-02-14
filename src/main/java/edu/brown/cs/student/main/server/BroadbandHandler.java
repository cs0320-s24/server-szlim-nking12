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
import spark.Request;
import spark.Response;
import spark.Route;

public class BroadbandHandler implements Route {

  Map<String, String> statecodes;

  public BroadbandHandler(Datasource state) {}

  @Override
  public Object handle(Request request, Response response) throws Exception {
    this.getStateCodes();
    String state = request.queryParams("state");
    String county = request.queryParams("county");
    return null;
  }

  private String sendRequest(String state, String county)
      throws URISyntaxException, IOException, InterruptedException {

    HttpRequest buildApiRequest =
        HttpRequest.newBuilder()
            .uri(
                new URI(
                    "https://api.census.gov/data/2021/acs/acs1/subject/"
                        + "variables?get="
                        + "NAME,S2802_C03_022E&for=county:*&in=state:06"))
            .GET()
            .build();

    // Send that API request then store the response in this variable. Note the generic type.
    HttpResponse<String> sentBoredApiResponse =
        HttpClient.newBuilder().build().send(buildApiRequest, HttpResponse.BodyHandlers.ofString());

    // What's the difference between these two lines? Why do we return the body? What is useful from
    // the raw response (hint: how can we use the status of response)?
    System.out.println(sentBoredApiResponse);
    System.out.println(sentBoredApiResponse.body());

    return sentBoredApiResponse.body();
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
}
