package edu.brown.cs.student.main.server;

import edu.brown.cs.student.main.CSVParser.exceptions.FactoryFailureException;
import java.io.IOException;
import java.util.List;

public class CensusAPISource implements Datasource {

  private String county;
  private String state;
  private String percentage;

  public CensusAPISource(String county, String state, String percentage) {
    this.county = county;
    this.state = state;
    this.percentage = percentage;
  }

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

  @Override
  public String toString() {
    return this.percentage
        + "of households in"
        + this.county
        + " ,"
        + this.state
        + "have broadband access.";
  }
}
