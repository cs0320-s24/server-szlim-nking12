package edu.brown.cs.student.main.server;

import edu.brown.cs.student.main.server.CensusAPISource.CensusResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ACSProxy implements ACSDataSource {
  private final Map<String, String> resultMap;
  private Map<String, String> public_resultMap = null;
  //@Override
  public Map<String, String> getData() {
    if (public_resultMap == null){
      public_resultMap = Collections.unmodifiableMap(resultMap);
    }
    return public_resultMap;
  }

  String statusMessage;

  ACSProxy() {
    this.resultMap = new HashMap<>();
  }

  @Override
  public CensusResponse getData(String statenum, String countynum) throws DatasourceException {
    return null;
  }
}
