package edu.brown.cs.student.tests;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Types;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

public class TestBroadband {

  @BeforeAll
  public static void setupOnce() {
    // Pick an arbitrary free port
    Spark.port(0);
    // Eliminate logger spam in console for test suite
    Logger.getLogger("").setLevel(Level.WARNING); // empty name = root
  }

  private final Type mapStringObject = Types.newParameterizedType(Map.class, String.class, String.class);
  private JsonAdapter<Map<String, String>> adapter;
  //private JsonAdapter<> weatherDataAdapter;
}
