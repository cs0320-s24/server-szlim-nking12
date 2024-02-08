package edu.brown.cs.student.main.utility;

import edu.brown.cs.student.main.creators.Creator;
import edu.brown.cs.student.main.creators.CreatorFromRow;
import edu.brown.cs.student.main.exceptions.FactoryFailureException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * The Utility class represents a simple command-line utility for searching and analyzing data
 * stored in CSV files. Users can input the file path, search term, and optional search parameters
 * to perform searches on CSV data.
 */
public class Utility {
  private String file;
  private String searchTerm;
  private boolean hasHeaders;
  private String column;

  private FileReader fileReader;

  private BufferedReader reader;

  public Utility() throws IOException, FactoryFailureException {
    this.start();
  }

  /** The main execution method. */
  public void start() throws IOException, FactoryFailureException {
    System.out.println("Hi! Welcome to search.");
    this.reader = new BufferedReader(new InputStreamReader(System.in));
    this.filePrompt();
    this.searchTermPrompt();
    this.headerPrompt();
    this.columnIdentifierPrompt();

    CreatorFromRow<List<String>> creator = new Creator();

    CSVParser csvParser = new CSVParser(this.fileReader, this.hasHeaders, creator);
    Search searcher = new Search(csvParser);
    searcher.search(this.searchTerm, this.column);
    System.exit(0);
  }

  /**
   * Prompts the user to enter the absolute path for the file and checks if it's in the "data/"
   * (protected) directory.
   *
   * @throws IOException if an I/O error occurs
   */
  private void filePrompt() throws IOException {
    System.out.println("Please enter absolute path for your file:");
    try {
      String line = reader.readLine();
      if (!line.isEmpty()) {
        this.file = line;
      }
      this.fileReader = new FileReader(this.file);
    } catch (FileNotFoundException e) {
      System.err.println("Error: File not found. Please check the file path.");
      System.exit(1);
    }
    if (!this.file.contains("data/")) {
      throw new IllegalArgumentException();
    }
  }

  /**
   * Prompts the user to enter their search term directory.
   *
   * @throws IOException if an I/O error occurs
   */
  private void searchTermPrompt() throws IOException {
    System.out.println("Please enter the value you are searching for: ");
    String line2 = reader.readLine();
    if (!line2.isEmpty()) {
      this.searchTerm = line2;
    }
  }

  /**
   * Prompts the user to enter their search term directory.
   *
   * @throws IOException if an I/O error occurs
   */
  private void headerPrompt() throws IOException {
    System.out.println("Does your csv have headers? yes/no: ");
    String line3 = reader.readLine().toLowerCase();
    if (!line3.isEmpty()) { // empty assumes no headers
      if (line3.equals("yes")) {
        this.hasHeaders = true;
      } else {
        this.hasHeaders = false;
      }
    }
  }

  /**
   * Prompts the user to enter their column identifier which is optional. They may press enter if
   * they don't want to identify.
   *
   * @throws IOException if an I/O error occurs
   */
  private void columnIdentifierPrompt() throws IOException {
    System.out.println(
        "Are you looking for this value in a specific column? If so, please enter the column number or name. Else, please hit enter.");
    String line4 = reader.readLine();
    if (line4.isEmpty()) {
      this.column = null;
    } else this.column = line4;
  }
}
