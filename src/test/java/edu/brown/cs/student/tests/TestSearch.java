package edu.brown.cs.student.tests;

import edu.brown.cs.student.main.creators.Creator;
import edu.brown.cs.student.main.exceptions.FactoryFailureException;
import edu.brown.cs.student.main.utility.CSVParser;
import edu.brown.cs.student.main.utility.Search;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.testng.Assert;

/** Test class for the Search functionality for CSV data */
public class TestSearch {

  /**
   * Test case for basic string search with a specified column.
   *
   * @throws IOException if an I/O error occurs
   * @throws FactoryFailureException if there is a failure in the factory during testing
   */
  @Test
  public void testBasicStringWCol() throws IOException, FactoryFailureException {
    // with headers
    String csvStringInput = "Name,Age,City\nJoe, 20, New York\nJane, 12, San Francisco";
    CSVParser<List<String>> parser =
        new CSVParser<>(new StringReader(csvStringInput), true, new Creator());

    Search searcher = new Search(parser);

    List<List<String>> result = searcher.search("Joe", "Name");

    Assert.assertEquals(1, result.size()); // how many rows
    Assert.assertTrue(result.contains(List.of("Joe", " 20", " New York")));
    Assert.assertFalse(result.contains(List.of("Jane", " 12", " San Francisco")));

    // lowercase header
    String csvStringInput2 = "Name,Age,City\nJoe, 20, New York\nJane, 12, San Francisco";
    CSVParser<List<String>> parser2 =
        new CSVParser<>(new StringReader(csvStringInput2), true, new Creator());

    Search searcher2 = new Search(parser2);

    List<List<String>> result2 = searcher2.search("joe", "name");

    Assert.assertEquals(1, result2.size()); // how many rows
    Assert.assertTrue(result2.contains(List.of("Joe", " 20", " New York")));
    Assert.assertFalse(result2.contains(List.of("Jane", " 12", " San Francisco")));

    // no headers, number column identifier
    String csvStringInput3 = "Joe, 23, New Jersey\nJoe, 20, New York\nJane, 12, San Francisco";
    CSVParser<List<String>> parser3 =
        new CSVParser<>(new StringReader(csvStringInput3), false, new Creator());

    Search searcher3 = new Search(parser3);

    List<List<String>> result3 = searcher3.search("Joe", "0");

    Assert.assertEquals(2, result3.size()); // how many rows
    Assert.assertTrue(result3.contains(List.of("Joe", " 20", " New York")));
    Assert.assertTrue(result3.contains(List.of("Joe", " 23", " New Jersey")));
    Assert.assertFalse(result3.contains(List.of("Jane", " 12", " San Francisco")));

    // no headers, no column identifier
    CSVParser<List<String>> parser4 =
        new CSVParser<>(new StringReader(csvStringInput3), false, new Creator());
    Search searcher4 = new Search(parser4);

    List<List<String>> result4 = searcher4.search("Joe", null);

    Assert.assertEquals(2, result4.size()); // how many rows
    Assert.assertTrue(result4.contains(List.of("Joe", " 20", " New York")));
    Assert.assertTrue(result4.contains(List.of("Joe", " 23", " New Jersey")));
    Assert.assertFalse(result4.contains(List.of("Jane", " 12", " San Francisco")));

    // randomly formed
    String csvStringInput5 =
        "Joe.Jilly, 23, New-Jersey\nJoe, 20, New York\nJane, 12, \"SanFrancisco\"";
    CSVParser<List<String>> parser5 =
        new CSVParser<>(new StringReader(csvStringInput5), false, new Creator());

    Search searcher5 = new Search(parser5);

    List<List<String>> result5 = searcher5.search("Joe", "0");

    Assert.assertEquals(2, result5.size()); // how many rows
    Assert.assertTrue(result5.contains(List.of("Joe", " 20", " New York")));
    Assert.assertTrue(result5.contains(List.of("Joe.Jilly", " 23", " New-Jersey")));

    Search searcher6 = new Search(parser5);
    List<List<String>> result6 = searcher6.search("SanFrancisco", null);
    Assert.assertTrue(result6.isEmpty());

    Search searcher7 = new Search(parser5);
    List<List<String>> result7 = searcher7.search("San Francisco", null);
    Assert.assertTrue(result7.isEmpty()); // how many rows

    // from handout
    String csvStringInput8 = "Caesar, Julius, \"veni, vidi, vici\"";
    CSVParser<List<String>> parser8 =
        new CSVParser<>(new StringReader(csvStringInput8), false, new Creator());

    Search searcher8 = new Search(parser8);

    List<List<String>> result8 = searcher8.search("\"veni, vidi, vici\"", null);

    Assert.assertEquals(1, result8.size()); // how many rows
    Assert.assertTrue(result8.contains(List.of("Caesar", " Julius", " \"veni, vidi, vici\"")));
  }

  /**
   * Test case for basic file search with a specified column.
   *
   * @throws IOException if an I/O error occurs
   * @throws FactoryFailureException if there is a failure in the factory during testing
   */
  @Test
  public void testFileWColIndicator() throws IOException, FactoryFailureException {
    // with headers
    String csvFileInput =
        "/Users/sophialim/Desktop/cs32/csv-sophlim/data/census/income_by_race.csv";
    CSVParser<List<String>> parser =
        new CSVParser<>(new FileReader(csvFileInput), true, new Creator());

    Search searcher = new Search(parser);

    List<List<String>> result = searcher.search("Total", "Race");

    Assert.assertEquals(40, result.size()); // how many rows
    Assert.assertTrue(
        result.contains(
            List.of(
                "0",
                "Total",
                "2020",
                "2020",
                "75857",
                "2022",
                "\"Kent County, RI\"",
                "05000US44003",
                "kent-county-ri")));

    CSVParser<List<String>> parser4 =
        new CSVParser<>(new FileReader(csvFileInput), true, new Creator());

    Search searcher4 = new Search(parser4);

    List<List<String>> result4 =
        searcher4.search("\"Bristol County, RI\"", null); // check double quotations

    Assert.assertEquals(48, result4.size()); // how many rows
    Assert.assertTrue(
        result4.contains(
            List.of(
                "9",
                "Hispanic",
                "2013",
                "2013",
                "53011",
                "26340",
                "\"Bristol County, RI\"",
                "05000US44001",
                "bristol-county-ri")));

    // check it still matches, even though not an exact match
    CSVParser<List<String>> parser2 =
        new CSVParser<>(new FileReader(csvFileInput), true, new Creator());
    Search searcher2 = new Search(parser2);
    List<List<String>> result2 = searcher2.search("TOTAL", "RACE");
    Assert.assertEquals(40, result2.size()); // how many rows
    Assert.assertTrue(
        result2.contains(
            List.of(
                "0",
                "Total",
                "2020",
                "2020",
                "75857",
                "2022",
                "\"Kent County, RI\"",
                "05000US44003",
                "kent-county-ri")));

    // number column identifier
    CSVParser<List<String>> parser3 =
        new CSVParser<>(new FileReader(csvFileInput), true, new Creator());
    Search searcher3 = new Search(parser3);
    List<List<String>> result3 = searcher3.search("total", "1");
    Assert.assertEquals(40, result3.size()); // how many rows
    Assert.assertTrue(
        result3.contains(
            List.of(
                "0",
                "Total",
                "2020",
                "2020",
                "75857",
                "2022",
                "\"Kent County, RI\"",
                "05000US44003",
                "kent-county-ri")));
  }
  /**
   * Test to ensure the proper handling of scenarios where the search term is not found in the CSV
   * data.
   *
   * @throws IOException if an I/O error occurs
   * @throws FactoryFailureException if there is a failure in the factory during testing
   */
  @Test
  public void testSearchTermNotFound() throws IOException, FactoryFailureException {
    // test with string, invalid word
    String csvStringInput = "Name, Age, City\nJoe, 20, New York\nJane, 12, San Francisco";
    CSVParser<List<String>> parser =
        new CSVParser<>(new StringReader(csvStringInput), true, new Creator());
    Search searcher = new Search(parser);

    Assert.assertThrows(
        NullPointerException.class,
        () -> {
          searcher.search(null, "0");
        });
  }
  /**
   * Verifies that the search finds all instances of a value, even if it's part of a larger string.
   *
   * @throws IOException if an I/O error occurs
   * @throws FactoryFailureException if there is a failure in the factory during testing
   */
  @Test
  public void testFindsAll() throws IOException, FactoryFailureException {
    String csvFileInput = "/Users/sophialim/Desktop/cs32/csv-sophlim/data/stars/ten-star.csv";
    CSVParser<List<String>> parser =
        new CSVParser<>(new FileReader(csvFileInput), true, new Creator());

    Search searcher = new Search(parser);

    List<List<String>> result = searcher.search("15", null);

    Assert.assertEquals(3, result.size()); // how many rows
    Assert.assertTrue(
        result.contains(
            List.of(
                "70667",
                "Proxima Centauri",
                "-0.47175",
                "-0.36132",
                "-1.15037"))); // just like command-F, it wont check if the value is the search term
    // only, but it the search term may be part of a value

    CSVParser<List<String>> parser2 =
        new CSVParser<>(new FileReader(csvFileInput), true, new Creator());

    Search searcher2 = new Search(parser2);

    List<List<String>> result2 = searcher2.search("15.", null);

    Assert.assertEquals(1, result2.size()); // how many rows
  }

  /**
   * Tests error handling when searching in a non-existing column.
   *
   * @throws IOException if an I/O error occurs
   * @throws FactoryFailureException if there is a failure in the factory during testing
   */
  @Test
  public void testColNotFound() throws IOException, FactoryFailureException {
    // test with string, invalid word
    String csvStringInput = "Name, Age, City\nJoe, 20, New York\nJane, 12, San Francisco";
    CSVParser<List<String>> parser =
        new CSVParser<>(new StringReader(csvStringInput), true, new Creator());
    Search searcher = new Search(parser);

    Assert.assertThrows(
        ArrayIndexOutOfBoundsException.class,
        () -> {
          searcher.search("Joe", "abcde");
        });

    // test with string, invalid number
    CSVParser<List<String>> parser2 =
        new CSVParser<>(new StringReader(csvStringInput), true, new Creator());
    Search searcher2 = new Search(parser2);

    Assert.assertThrows(
        ArrayIndexOutOfBoundsException.class,
        () -> {
          searcher2.search("Joe", "5");
        });

    // test with file, invalid word
    String csvFileInput =
        "/Users/sophialim/Desktop/cs32/csv-sophlim/data/census/dol_ri_earnings_disparity.csv";
    CSVParser<List<String>> parser3 =
        new CSVParser<>(new FileReader(csvFileInput), true, new Creator());
    Search searcher3 = new Search(parser3);

    Assert.assertThrows(
        ArrayIndexOutOfBoundsException.class,
        () -> {
          searcher3.search("White", "abcde");
        });

    // test with file, invalid number
    CSVParser<List<String>> parser4 =
        new CSVParser<>(new FileReader(csvFileInput), true, new Creator());
    Search searcher4 = new Search(parser4);

    Assert.assertThrows(
        ArrayIndexOutOfBoundsException.class,
        () -> {
          searcher4.search("White", "12");
        });
  }
  /**
   * Checks if the search works without specifying a column identifier.
   *
   * @throws IOException if an I/O error occurs
   * @throws FactoryFailureException if there is a failure in the factory during testing
   */
  @Test
  public void testNoCol() throws IOException, FactoryFailureException {
    // test with string, has headers
    String csvStringInput =
        "Name,Age,City\nJoe, 16, LA\nJoe, 20, New York\nJane, 12, San Francisco";
    CSVParser<List<String>> parser =
        new CSVParser<>(new StringReader(csvStringInput), true, new Creator());
    Search searcher = new Search(parser);
    List<List<String>> result = searcher.search("joe", null); // no col provided
    Assert.assertEquals(2, result.size()); // how many rows
    Assert.assertTrue(result.contains(List.of("Joe", " 20", " New York")));
    Assert.assertTrue(result.contains(List.of("Joe", " 16", " LA")));

    // test with file, no headers
    String csvFileInput = "/Users/sophialim/Desktop/cs32/csv-sophlim/data/dummy/irrelevantColumn";
    CSVParser<List<String>> parser2 =
        new CSVParser<>(new FileReader(csvFileInput), false, new Creator());
    Search searcher2 = new Search(parser2);
    List<List<String>> result2 = searcher2.search("a", null);
    Assert.assertEquals(3, result2.size()); // how many rows
    Assert.assertTrue(result2.contains(List.of("a", "b", "c", "d", "e")));
    Assert.assertFalse(result2.contains(List.of("1", "2", "3", "4", "5")));
  }
  /**
   * Tests handling of rows with irregular formatting.
   *
   * @throws IOException if an I/O error occurs
   * @throws FactoryFailureException if there is a failure in the factory during testing
   */
  @Test
  public void testMalformedRows() throws IOException, FactoryFailureException {
    String csvFileInput =
        "/Users/sophialim/Desktop/cs32/csv-sophlim/data/malformed/malformed_signs.csv";
    CSVParser<List<String>> parser =
        new CSVParser<>(new FileReader(csvFileInput), true, new Creator());
    Search searcher = new Search(parser);
    List<List<String>> result = searcher.search("Alexis", null);
    Assert.assertEquals(1, result.size()); // how many rows
    Assert.assertTrue(result.contains(List.of("Cancer", "Alexis")));
    List<List<String>> result2 =
        searcher.search("Nick", null); // same file but is skipped due to being a malformed row
    Assert.assertFalse(result2.contains(List.of("Gemini", "Roberto", "Nick")));
  }
  /**
   * Checks cases where the search term is not present in the CSV.
   *
   * @throws IOException if an I/O error occurs
   * @throws FactoryFailureException if there is a failure in the factory during testing
   */
  @Test
  public void testNotInCSV() throws IOException, FactoryFailureException {
    String csvFileInput =
        "/Users/sophialim/Desktop/cs32/csv-sophlim/data/census/postsecondary_education.csv";
    CSVParser<List<String>> parser =
        new CSVParser<>(new FileReader(csvFileInput), true, new Creator());
    Search searcher = new Search(parser);
    List<List<String>> result = searcher.search("women", null); // women is in csv
    Assert.assertEquals(8, result.size()); // how many rows

    CSVParser<List<String>> parser2 =
        new CSVParser<>(new FileReader(csvFileInput), true, new Creator());
    Search searcher2 = new Search(parser2);
    List<List<String>> result2 = searcher2.search("boy", null); // boy is not in the csv file
    Assert.assertEquals(0, result2.size()); // how many rows
  }
  /**
   * Tests the scenario where the search is performed in the wrong column.
   *
   * @throws IOException if an I/O error occurs
   * @throws FactoryFailureException if there is a failure in the factory during testing
   */
  @Test
  public void testWrongCol() throws IOException, FactoryFailureException {
    String csvFileInput =
        "/Users/sophialim/Desktop/cs32/csv-sophlim/data/census/income_by_race.csv";
    CSVParser<List<String>> parser =
        new CSVParser<>(new FileReader(csvFileInput), true, new Creator());
    Search searcher = new Search(parser);
    List<List<String>> result = searcher.search("2020", null); // no col indicated
    Assert.assertEquals(40, result.size()); // how many rows

    CSVParser<List<String>> parser2 =
        new CSVParser<>(new FileReader(csvFileInput), true, new Creator());
    Search searcher2 = new Search(parser2);
    List<List<String>> result2 = searcher2.search("2020", "year"); // correct col indicated
    Assert.assertEquals(40, result2.size()); // how many rows

    CSVParser<List<String>> parser3 =
        new CSVParser<>(new FileReader(csvFileInput), true, new Creator());
    Search searcher3 = new Search(parser3);
    List<List<String>> result3 = searcher3.search("2020", "race"); // wrong col indicated (string)
    Assert.assertEquals(0, result3.size()); // how many rows

    CSVParser<List<String>> parser4 =
        new CSVParser<>(new FileReader(csvFileInput), true, new Creator());
    Search searcher4 = new Search(parser4);
    List<List<String>> result4 = searcher4.search("2020", "8"); // wrong col indicated (number)
    Assert.assertEquals(0, result4.size()); // how many rows
  }
  /**
   * Test case for searching within an empty CSV file or string
   *
   * @throws IOException if an I/O error occurs
   * @throws FactoryFailureException if there is a failure in the factory during testing
   */
  @Test
  public void emptyCSV() throws IOException, FactoryFailureException {
    String csvStringInput = "";
    CSVParser<List<String>> parser =
        new CSVParser<>(new StringReader(csvStringInput), false, new Creator());
    Search searcher = new Search(parser);
    List<List<String>> result = searcher.search("hello", null);
    Assert.assertTrue(result.isEmpty()); // how many rows

    String csvFileInput = "/Users/sophialim/Desktop/cs32/csv-sophlim/data/dummy/noData";
    CSVParser<List<String>> parser2 =
        new CSVParser<>(new FileReader(csvFileInput), false, new Creator());
    Search searcher2 = new Search(parser2);
    List<List<String>> result2 = searcher2.search("hello", null);
    Assert.assertTrue(result2.isEmpty()); // how many rows

    CSVParser<List<String>> parser3 =
        new CSVParser<>(
            new FileReader(csvFileInput),
            true,
            new Creator()); // indicates has headers on an empty file

    Assert.assertThrows(
        NullPointerException.class,
        () -> {
          Search searcher3 = new Search(parser3);
          searcher3.search("hello", null);
        });
  }
  /**
   * Test case for searching a CSV for a specific value in multiple columns but using a specified
   * column identifier.
   *
   * @throws IOException if an I/O error occurs
   * @throws FactoryFailureException if there is a failure in the factory during testing
   */
  @Test
  public void testMultipleColWColIdentifier() throws IOException, FactoryFailureException {
    String csvStringInput = "18,Joe,yucky\nJoe,20,New York\nJane,12,San Francisco";
    CSVParser<List<String>> parser =
        new CSVParser<>(new StringReader(csvStringInput), true, new Creator());

    Search searcher = new Search(parser);

    List<List<String>> result = searcher.search("Joe", "0");

    Assert.assertEquals(1, result.size()); // how many rows
    Assert.assertTrue(result.contains(List.of("Joe", "20", "New York")));
    Assert.assertFalse(result.contains(List.of("18", "Joe", "yucky")));
  }
}
