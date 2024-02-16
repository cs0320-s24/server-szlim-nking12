package edu.brown.cs.student.main.server;

import edu.brown.cs.student.main.CSVParser.creators.Creator;
import edu.brown.cs.student.main.CSVParser.exceptions.FactoryFailureException;
import edu.brown.cs.student.main.CSVParser.utility.CSVParser;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

/**
 * The CSVSource class provides functionality to read and clean data from a CSV file. It utilizes
 * the CSVParser class to parse the CSV file and retrieve the data in the form of a list of lists of
 * strings.
 */
public class CSVSource {

  private List<List<String>> data;
  private List<String> headerRow;
  /** Constructs an instance of CSVSource */
  public CSVSource() {}
  /**
   * Creates a CSVParser for parsing a CSV file with the given filepath and header identifiers.
   *
   * @param filepath The path to the CSV file to be parsed.
   * @param headers Indicates whether the CSV file contains headers.
   * @return A CSVParser instance for the specified CSV file.
   */
  private CSVParser<List<String>> makeParser(String filepath, boolean headers)
      throws FileNotFoundException {
    Reader reader = new FileReader(filepath);
    return new CSVParser<>(reader, headers, new Creator());
  }
  /**
   * Parses and cleans data from a CSV file specified by the filepath and turns it into a list of
   * list of strings.
   *
   * @param filepath The path to the CSV file to be parsed.
   * @param headers Indicates whether the CSV file contains headers.
   * @return The cleaned data as a list of lists of strings.
   * @throws IOException Thrown if there is an I/O error during CSV parsing.
   * @throws FactoryFailureException Thrown if a list of strings could not be created during
   *     parsing.
   */
  public List<List<String>> cleanData(String filepath, boolean headers)
      throws IOException, FactoryFailureException {
    CSVParser<List<String>> parser = this.makeParser(filepath, headers);
    data = parser.parse();
    this.headerRow = parser.getHeaders();
    return data;
  }
  /**
   * Retrieves the cleaned data from the CSV parsing.
   *
   * @return The cleaned data as a list of lists of strings.
   */
  public List<List<String>> getData() {
    return this.data;
  }
  /**
   * Retrieves the header row from the CSV parsing.
   *
   * @return The header row as a list of strings.
   */
  public List<String> getHeaderRow() {
    return headerRow;
  }
}
