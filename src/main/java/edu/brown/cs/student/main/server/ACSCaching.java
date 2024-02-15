package edu.brown.cs.student.main.server;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class ACSCaching {
  // private final Map<String,String> wrappedMap;
  private final LoadingCache<String, Collection<String>> cache;

  public ACSCaching(BroadbandHandler resultsToWrap, int time) {
    // this.wrappedMap = resultsToWrap;

    this.cache =
        CacheBuilder.newBuilder()
            // How many entries maximum in the cache?
            .maximumSize(200)
            // How long should entries remain in the cache?
            .expireAfterWrite(time, TimeUnit.MINUTES)
            // Keep statistical info around for profiling purposes
            .recordStats()
            .build(
                // Strategy pattern: how should the cache behave when
                // it's asked for something it doesn't have?
                new CacheLoader<>() {
                  @Override
                  public Collection<String> load(String key) {
                    System.out.println("called load for: " + key);
                    // If this isn't yet present in the cache, load it:
                    // return wrappedSearcher.search(key);
                    return null;
                  }
                });
  }

  public Collection<String> search(String target) {
    // "get" is designed for concurrent situations; for today, use getUnchecked:
    Collection<String> result = cache.getUnchecked(target);
    // For debugging and demo (would remove in a "real" version):
    System.out.println(cache.stats());
    return result;
  }
}
