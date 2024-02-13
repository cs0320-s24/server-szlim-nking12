package edu.brown.cs.student.main.server;

import edu.brown.cs.student.main.CSVParser.exceptions.FactoryFailureException;
import java.io.IOException;
import java.util.List;

public interface Datasource {

  public List<List<String>> getData();

  public List cleanData(String path, boolean headers) throws IOException, FactoryFailureException;

  public List<String> getHeaderRow();
}
