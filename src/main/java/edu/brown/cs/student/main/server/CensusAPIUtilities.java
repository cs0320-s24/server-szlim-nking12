package edu.brown.cs.student.main.server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.server.CensusAPISource.CensusResponse;
import java.io.IOException;

public class CensusAPIUtilities {
  public CensusAPIUtilities() {}

  public static CensusResponse deserializeCensusData(String jsonList) throws IOException {
    // List<CensusAPISource> censusDataList = new ArrayList<>();
    try {
      Moshi moshi = new Moshi.Builder().build();
      JsonAdapter<CensusResponse> adapter = moshi.adapter(CensusResponse.class);

      return adapter.fromJson(jsonList);
    } catch (IOException e) {
      // In a real system, we wouldn't println like this, but it's useful for demonstration:
      System.err.println("BroadbandHandler: string wasn't valid JSON.");
      throw e;
    } catch (JsonDataException e) {
      // In a real system, we wouldn't println like this, but it's useful for demonstration:
      System.err.println("BroadbandHandler: JSON wasn't in the right format.");
      throw e;
    }
  }
}
