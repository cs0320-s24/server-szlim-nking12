package edu.brown.cs.student.main.server;

import edu.brown.cs.student.main.CSVParser.exceptions.FactoryFailureException;
import java.io.IOException;
import java.util.List;

public class CensusAPISource {

  private String county;
  private String state;
  private String percentage;

  public CensusAPISource(String county, String state, String percentage) {
    this.county = county;
    this.state = state;
    this.percentage = percentage;
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
