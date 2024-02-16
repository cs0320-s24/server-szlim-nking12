package edu.brown.cs.student.tests;

import edu.brown.cs.student.main.server.ACSDataSource;
import edu.brown.cs.student.main.server.DatasourceException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class MockedCensusSource implements ACSDataSource {

  private final List<List<String>> constantData;

  public MockedCensusSource(List<List<String>> constantData){
    this.constantData = constantData;
  }

  @Override
  public List<List<String>> getData(String statenum, String countynum) {
    return constantData;
  }
}
