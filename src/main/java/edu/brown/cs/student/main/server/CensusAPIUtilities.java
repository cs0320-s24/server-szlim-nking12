package edu.brown.cs.student.main.server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

/**
 * This class provides utility methods for working with Census API responses, particularly for
 * deserializing JSON data into a list of lists of strings.
 */
public class CensusAPIUtilities {
  /** Constructs a CensusAPIUtilities instance. */
  public CensusAPIUtilities() {}
  /**
   * Deserializes JSON data received from the Census API into a list of lists of strings.
   *
   * @param jsonList The JSON data to be deserialized.
   * @return The deserialized data as a list of lists of strings.
   * @throws IOException Thrown if there is an I/O error during JSON deserialization.
   * @throws JsonDataException Thrown if the JSON data is not in the expected format.
   */
  public static List<List<String>> deserializeCensusData(String jsonList) throws IOException {
    // List<CensusAPISource> censusDataList = new ArrayList<>();
    try {
      Moshi moshi = new Moshi.Builder().build();
      Type listType = Types.newParameterizedType(List.class, List.class, String.class);
      JsonAdapter<List<List<String>>> adapter = moshi.adapter(listType);

      List<List<String>> response = adapter.fromJson(jsonList);

      return response;

    } catch (IOException e) {
      // In a real system, we wouldn't println like this, but it's useful for demonstration:
      System.err.println(e.getMessage());
      System.err.println("BroadbandHandler: string wasn't valid JSON.");
    } catch (JsonDataException e) {
      // In a real system, we wouldn't println like this, but it's useful for demonstration:
      System.err.println(e.getMessage());
      System.err.println("BroadbandHandler: JSON wasn't in the right format.");
    }
    return null;
  }
}
