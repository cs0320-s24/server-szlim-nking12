package edu.brown.cs.student.main.CSVParser.utility;

import edu.brown.cs.student.main.CSVParser.exceptions.FactoryFailureException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The Search class is responsible for searching and filtering data from a CSV file using specified
 * criteria.
 */
public class Search {

  //  private CSVParser csvParser;
  private List<List<String>> parsed;

  private List<List<String>> finalRows;
  private List<String> headerRow;

  /**
   * Constructs a Search object with the provided CSVParser.
   *
   * @throws IOException If an I/O error occurs during CSV parsing.
   * @throws FactoryFailureException If object creation from CSV rows fail.
   */
  public Search(List<List<String>> parsed, List<String> headerRow)
      throws IOException, FactoryFailureException {
    //    this.csvParser = csvParser;
    this.parsed = parsed;
    this.finalRows = new ArrayList<>();
    this.headerRow = headerRow;
  }
  /**
   * Searches for rows in the CSV data based on the specified criteria.
   *
   * @param searchFor The value to search for in the CSV data.
   * @param col The column name or index to restrict the search, or null for searching in all
   *     columns.
   * @return The list of rows that match the search criteria.
   * @throws NullPointerException If the searchFor parameter is null.
   */
  public List<List<String>> search(String searchFor, String col) {
    if (searchFor == null) {
      throw new NullPointerException();
    }

    int prefRowLength = this.preferredRowLength();

    for (int rowIndex = 0; rowIndex < this.parsed.size(); rowIndex++) {
      List<String> row = this.parsed.get(rowIndex);
      if (row.size()
          != prefRowLength) { // checks for malformed row, prints error message, and skips it.
        // Continues parse every other row
        System.err.println(
            "Error: Malformed row with inconsistent number of data points. Row "
                + (rowIndex + 1)
                + " has been skipped");
      } else {
        if (col == null) { // no given column
          if (this.matchesNoCol(row, searchFor)) {
            this.finalRows.add(row); // if match found, add row
          }
        } else { // given column
          if (this.matchesCol(row, searchFor, col)) {
            this.finalRows.add(row); // if match found, add row
          }
        }
      }
    }
    this.searchOutput(); // print all rows
    return this.finalRows;
  }

  /**
   * Checks if the specified search value is contained in the specific column provided
   *
   * @param row row to check for a match.
   * @param searchFor value to search for.
   * @param col column name or index to check for a match.
   * @return true if the value is found in the specified column of the row, false otherwise.
   * @throws ArrayIndexOutOfBoundsException If the column index is out of bounds (or if col name
   *     could not be found).
   */
  private boolean matchesCol(List<String> row, String searchFor, String col) {
    int colIndex;
    if (this.colIsNum(col)) {
      // It's a number so use as a column index
      colIndex = Integer.parseInt(col);
      if (colIndex >= 0 && colIndex < row.size()) {

        // compare lowercase value to lowercase of the search term
        return row.get(colIndex).toLowerCase().contains(searchFor.toLowerCase());
      } else {
        // outside range
        throw new ArrayIndexOutOfBoundsException();
      }
    } else {
      // It's a string, so use as a column name
      //      List<String> firstRow = this.parsed.get(0);
      colIndex = headerRow.indexOf(col.toLowerCase());

      // column name not found
      if (colIndex == -1) {
        throw new ArrayIndexOutOfBoundsException();
      }
      if (colIndex >= 0 && colIndex < row.size()) {
        // compare lowercase value to lowercase of the search term
        return row.get(colIndex).toLowerCase().contains(searchFor.toLowerCase());
      }
    }
    return false;
  }

  /**
   * Determines the preferred length of rows based on the headers. If headers are present, the
   * length is equal to the number of headers. If headers are indicated, the length is the maximum
   * row size encountered during parsing.
   *
   * @return The preferred length of rows.
   */
  private int preferredRowLength() {
    // get length of headers
    int prefLength = this.headerRow.size();

    if (prefLength == 0) { // if no headers, pref length is max number of rows
      for (List<String> rowData : this.parsed) {
        prefLength = Math.max(prefLength, rowData.size());
      }
    }
    return prefLength;
  }

  /**
   * Checks if the provided column input is a number
   *
   * @param colInput The column input to check.
   * @return true if the input can be parsed as an integer, false otherwise.
   */
  private boolean colIsNum(String colInput) {
    try {
      Integer.parseInt(colInput);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  /**
   * Checks if the specific search value is contained in any column of the given row.
   *
   * @param row The row to check for a match.
   * @param searchFor The value to search for.
   * @return true if the specified value is found in any column of the given row, false otherwise.
   */
  private boolean matchesNoCol(List<String> row, String searchFor) {
    for (String val : row) {
      if (val.toLowerCase().contains(searchFor.toLowerCase())) {
        return true;
      }
    }
    return false;
  }

  /** Prints the search results to the console. */
  private void searchOutput() {
    if (this.finalRows.isEmpty()) {
      System.out.println("Sorry, no matches found.");
    } else {
      for (List<String> row : this.finalRows) {
        System.out.println(row);
      }
    }
  }
}
