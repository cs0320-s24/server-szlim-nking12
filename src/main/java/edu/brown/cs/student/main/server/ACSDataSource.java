package edu.brown.cs.student.main.server;

import edu.brown.cs.student.main.CSVParser.exceptions.FactoryFailureException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ACSDataSource {

  Map<String, String> getData();

}
