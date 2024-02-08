package edu.brown.cs.student.main.CSVParser.utility;

import edu.brown.cs.student.main.CSVParser.creators.CreatorFromRow;
import edu.brown.cs.student.main.CSVParser.exceptions.FactoryFailureException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * CSVParser is a generic class designed to parse CSV files and convert each row into objects of a
 * specified type.
 *
 * @param <T> The type of objects to be created from each CSV row
 */
public class CSVParser<T> {

  private boolean hasHeaders;
  static final Pattern regexSplitCSVRow =
      Pattern.compile(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*(?![^\\\"]*\\\"))"); // Credit: from handout
  private CreatorFromRow<T> generic;

  private BufferedReader bufferedReader;
  private List<String> headers;

  /**
   * Constructs a CSVParser with the given reader, headers flag, and object creator.
   *
   * @param reader The Reader providing access to the CSV data
   * @param hasHeaders A boolean indicating if the CSV file has headers.
   * @param generic The object creator implementing the CreatorFromRow interface
   */
  public CSVParser(Reader reader, boolean hasHeaders, CreatorFromRow<T> generic) {
    this.bufferedReader = new BufferedReader(reader);
    this.hasHeaders = hasHeaders;
    this.generic = generic;
    this.headers = new ArrayList<>();
  }

  /**
   * Parses the CSV data and converts each row into objects of type T
   *
   * @return A List of objects created from the CSV rows.
   * @throws IOException If an I/O error occurs while reading the lines from the CSV data.
   * @throws FactoryFailureException If object creation from CSV row fails
   */
  public List<T> parse() throws IOException, FactoryFailureException {
    List<T> list = new ArrayList<>();
    String line;
    // If the CSV file was indicated to have headers, read and process the first line
    if (this.hasHeaders) {
      line = this.bufferedReader.readLine();
      if (line == null) { // if they indicated they have headers but file is empty
        throw new NullPointerException();
      }
      this.headers = List.of(regexSplitCSVRow.split(line));
    }

    // loop through each line of the CSV
    while ((line = bufferedReader.readLine()) != null) {

      // split line into a list of strings
      List<String> parsed = List.of(regexSplitCSVRow.split(line));

      // use the generic creator to convert the list of strings into an object of type T
      T converted = this.generic.create(parsed);

      // add the created object to the list
      list.add(converted);
    }
    // return the final list
    return list;
  }

  /**
   * gets the headers from the CSV file in lowercase form
   *
   * @return A List of headers in lowercase
   */
  public List<String> getHeaders() {
    List<String> lowercaseHeaders = new ArrayList<>();
    for (String header : this.headers) {
      lowercaseHeaders.add(header.toLowerCase());
    }
    return lowercaseHeaders;
  }
}
