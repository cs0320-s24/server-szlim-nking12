package edu.brown.cs.student.main.server;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class ACSCaching implements ACSDataSource {
  private final LoadingCache<String, List<List<String>>> cache;
  private final CensusAPISource source;

  public ACSCaching(CensusAPISource source) {
    this.source = source;
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
                    String[] k = key.split(",");
                    System.out.println("called load for: " + k[0] + k[1]);
                    // If this isn't yet present in the cache, load it:
                    return source.getData(k[0], k[1]);
                  }
                });
  }

  @Override
  public List<List<String>> getData(String statenum, String countynum) {
    String key = statenum + "," + countynum;
    System.out.println("searching for: " + statenum + countynum);
    List<List<String>> result = null;
    try {
      result = cache.get(key);
    } catch (ExecutionException e) {
      System.err.println(e.getMessage());
    }
    System.out.println(cache.stats());
    return result;
  }
}
