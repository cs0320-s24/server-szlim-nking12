package edu.brown.cs.student.main.server;

import edu.brown.cs.student.main.CSVParser.creators.Creator;
import edu.brown.cs.student.main.CSVParser.exceptions.FactoryFailureException;
import edu.brown.cs.student.main.CSVParser.utility.CSVParser;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

public class CSVSource {

  private List<List<String>> data;
  private List<String> headerRow;

  public CSVSource() {}

  private CSVParser<List<String>> makeParser(String filepath, boolean headers) {
    Reader reader = null;
    try {
      reader = new FileReader(filepath);
    } catch (FileNotFoundException e) {
      System.err.println("File not found");
    }
    return new CSVParser<>(reader, headers, new Creator());
  }


  public List<List<String>> cleanData(String filepath, boolean headers)
      throws IOException, FactoryFailureException {
    CSVParser<List<String>> parser = this.makeParser(filepath, headers);
    try {
      data = parser.parse();
      this.headerRow = parser.getHeaders();
    } catch (IOException e) {
      System.err.println(e.getMessage());
    } catch (FactoryFailureException j) {
      System.err.println("Factory Failure Exception: List of strings could not be created");
    }
    return data;
  }


  public List<List<String>> getData() {
    return this.data;
  }


  public List<String> getHeaderRow() {
    return headerRow;
  }
}
