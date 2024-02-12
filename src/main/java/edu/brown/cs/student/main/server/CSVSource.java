package edu.brown.cs.student.main.server;

import edu.brown.cs.student.main.CSVParser.creators.Creator;
import edu.brown.cs.student.main.CSVParser.exceptions.FactoryFailureException;
import edu.brown.cs.student.main.CSVParser.utility.CSVParser;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

public class CSVSource implements Datasource {

  private List<List<String>> data;

  public CSVSource() {}

  private CSVParser makeParser(String filepath) {
    Reader reader = null;
    try {
      reader = new FileReader(filepath);
    } catch (FileNotFoundException e) {
      System.err.println("File not found");
    }
    return new CSVParser(reader, false, new Creator());
  }

  @Override
  public List<List<String>> cleanData(String filepath) {
    CSVParser parser = this.makeParser(filepath);
    try {
      data = parser.parse();
    } catch (IOException e) {
      System.err.println(e.getMessage());
    } catch (FactoryFailureException j) {
      System.err.println("Factory Failure Exception: List of strings could not be created");
    }
    return data;
  }

  @Override
  public List<List<String>> getData() {
    return this.data;
  }
}
