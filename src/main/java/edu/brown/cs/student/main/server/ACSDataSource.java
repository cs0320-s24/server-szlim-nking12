package edu.brown.cs.student.main.server;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public interface ACSDataSource {

  List<List<String>> getData(String statenum, String countynum)
      throws DatasourceException, IOException, InterruptedException, URISyntaxException;
}
