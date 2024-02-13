package edu.brown.cs.student.main.server;

import edu.brown.cs.student.main.CSVParser.exceptions.FactoryFailureException;
import java.io.IOException;
import java.util.List;

public class CensusAPISource implements Datasource {

  @Override
  public List<List<String>> getData() {
    return null;
  }

  @Override
  public List cleanData(String path, boolean headers) throws IOException, FactoryFailureException {
    return null;
  }

  @Override
  public List<String> getHeaderRow() {
    return null;
  }
}
