package edu.brown.cs.student.main.server;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ACSCaching implements ACSDataSource {
  private final LoadingCache<String, List<List<String>>> cache;

  public ACSCaching(CensusAPISource source, String statenum, String countynum) {

    this.cache =
        CacheBuilder.newBuilder()
            // How many entries maximum in the cache?
            .maximumSize(200)
            // How long should entries remain in the cache?
            .expireAfterWrite(10, TimeUnit.MINUTES)
            // Keep statistical info around for profiling purposes
            .recordStats()
            .build(
                // Strategy pattern: how should the cache behave when
                // it's asked for something it doesn't have?
                new CacheLoader<>() {
                  @Override
                  public List<List<String>> load(String key)
                      throws DatasourceException, IOException, URISyntaxException,
                          InterruptedException {
                    System.out.println("called load for: " + statenum + countynum);
                    // If this isn't yet present in the cache, load it:
                    return source.getData(statenum, countynum);
                  }
                });
  }

  @Override
  public List<List<String>> getData(String statenum, String countynum) {
    List<List<String>> result = cache.getUnchecked(statenum);
    // For debugging and demo (would remove in a "real" version):
    System.out.println(cache.stats());
    return result;
  }
}
