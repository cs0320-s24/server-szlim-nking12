package edu.brown.cs.student.main.server;

import java.util.List;

public interface Datasource {

  public List<List<String>> getData();

  public List cleanData(String path);
}
