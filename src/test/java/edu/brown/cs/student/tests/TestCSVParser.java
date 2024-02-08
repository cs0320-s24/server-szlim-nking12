package edu.brown.cs.student.tests;

import edu.brown.cs.student.main.CSVParser.creators.Creator;
import edu.brown.cs.student.main.CSVParser.creators.CreatorFromRow;
import edu.brown.cs.student.main.CSVParser.creators.IntCreator;
import edu.brown.cs.student.main.CSVParser.creators.PersonCreator;
import edu.brown.cs.student.main.CSVParser.exceptions.FactoryFailureException;
import edu.brown.cs.student.main.CSVParser.objects.Person;
import edu.brown.cs.student.main.CSVParser.utility.CSVParser;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.testng.Assert;

/** The TestCSVParser class tests the functionality of my CSVParser */
public class TestCSVParser {
  /**
   * Tests the CSV parsing functionality with headers.
   *
   * @throws IOException if an I/O error occurs during the test.
   * @throws FactoryFailureException if there is failure in the object creation process.
   */
  @Test
  public void testParseWithHeaders() throws IOException, FactoryFailureException {
    CreatorFromRow<List<String>> creator = new Creator();

    // using string reader
    String csvStringInput = "Name,Age,City\nJoe, 20, New York\nJane, 12, San Francisco";
    CSVParser<List<String>> parser1 =
        new CSVParser<>(new StringReader(csvStringInput), true, creator);
    List<List<String>> result1 = parser1.parse();
    Assert.assertEquals(2, result1.size()); // how many rows
    Assert.assertEquals(3, parser1.getHeaders().size()); // how many headers
    Assert.assertTrue(result1.contains(List.of("Joe", " 20", " New York")));
    Assert.assertFalse(result1.contains(List.of("Name", "Age", "City"))); // check it skips headers

    // using file reader
    String csvFileInput =
        "/Users/sophialim/Desktop/cs32/csv-sophlim/data/census/dol_ri_earnings_disparity.csv";
    CSVParser<List<String>> parser2 = new CSVParser<>(new FileReader(csvFileInput), true, creator);
    List<List<String>> result2 = parser2.parse();
    Assert.assertEquals(6, result2.size()); // how many rows
    Assert.assertEquals(6, parser2.getHeaders().size()); // how many headers
    Assert.assertTrue(
        parser2
            .getHeaders()
            .equals(
                List.of(
                    "state",
                    "data type",
                    "average weekly earnings",
                    "number of workers",
                    "earnings disparity",
                    "employed percent")));

    // using file reader, larger data set
    String csvFile2Input =
        "/Users/sophialim/Desktop/cs32/csv-sophlim/data/census/income_by_race.csv";
    CSVParser<List<String>> parser3 = new CSVParser<>(new FileReader(csvFile2Input), true, creator);
    List<List<String>> result3 = parser3.parse();
    Assert.assertEquals(323, result3.size()); // how many rows
    Assert.assertEquals(9, parser3.getHeaders().size()); // how many headers
    Assert.assertTrue(
        parser3
            .getHeaders()
            .equals(
                List.of(
                    "id race",
                    "race",
                    "id year",
                    "year",
                    "household income by race",
                    "household income by race moe",
                    "geography",
                    "id geography",
                    "slug geography")));

    // headers only, no values
    String headersOnly = "Name,Age,ID,Country";
    CSVParser<List<String>> parser4 = new CSVParser<>(new StringReader(headersOnly), true, creator);
    List<List<String>> result4 = parser4.parse();
    Assert.assertEquals(0, result4.size()); // how many rows
    Assert.assertEquals(4, parser4.getHeaders().size()); // how many headers
    Assert.assertTrue(parser4.getHeaders().equals(List.of("name", "age", "id", "country")));
  }

  /**
   * Tests the CSV parsing functionality without headers.
   *
   * @throws IOException if an I/O error occurs during the test.
   * @throws FactoryFailureException if there is a failure in the object creation process.
   */
  @Test
  public void testParseWithoutHeaders() throws IOException, FactoryFailureException {
    CreatorFromRow<List<String>> creator = new Creator();

    // using string reader
    String csvStringInput = "Joe, 20, New York\nJane, 12, San Francisco";
    CSVParser<List<String>> parser1 =
        new CSVParser<>(new StringReader(csvStringInput), false, creator);
    List<List<String>> result1 = parser1.parse();
    Assert.assertEquals(2, result1.size()); // how many rows
    Assert.assertEquals(0, parser1.getHeaders().size()); // how many headers
    Assert.assertTrue(result1.contains(List.of("Joe", " 20", " New York")));

    // using file reader
    String csvFileInput =
        "/Users/sophialim/Desktop/cs32/csv-sophlim/data/dummy/noHeaderData"; // file with no
    // headers. Data taken
    // from:
    // https://generatedata.com/generator
    CSVParser<List<String>> parser2 = new CSVParser<>(new FileReader(csvFileInput), false, creator);
    List<List<String>> result2 = parser2.parse();
    Assert.assertEquals(5, result2.size()); // how many rows
    Assert.assertEquals(0, parser2.getHeaders().size()); // how many headers
    Assert.assertTrue(
        result2.contains(
            List.of("Sacha Downs", "1-814-957-5789", "mauris.rhoncus@outlook.com", "Netherlands")));
  }

  /**
   * Tests with empty CSV inputs and checks if the parser handles empty files correctly.
   *
   * @throws IOException if an I/O error occurs during the test.
   * @throws FactoryFailureException if there is a failure in the object creation process.
   */
  @Test
  public void testEmptyCSV() throws IOException, FactoryFailureException {
    CreatorFromRow<List<String>> creator = new Creator();

    // using string reader
    String csvStringInput = "";
    CSVParser<List<String>> parser1 =
        new CSVParser<>(new StringReader(csvStringInput), false, creator);
    List<List<String>> result1 = parser1.parse();
    Assert.assertEquals(0, result1.size()); // how many rows
    Assert.assertTrue(parser1.getHeaders().isEmpty()); // how many headers
    Assert.assertTrue(result1.isEmpty());

    // using file reader
    String csvFileInput =
        "/Users/sophialim/Desktop/cs32/csv-sophlim/data/dummy/noData"; // file with no data
    CSVParser<List<String>> parser2 = new CSVParser<>(new FileReader(csvFileInput), false, creator);
    List<List<String>> result2 = parser2.parse();
    Assert.assertEquals(0, result2.size()); // how many rows
    Assert.assertTrue(parser2.getHeaders().isEmpty()); // how many headers
    Assert.assertTrue(result2.isEmpty());
  }

  /**
   * Tests the CSV parsing functionality with inconsistent column counts.
   *
   * @throws FactoryFailureException if there is a failure in the object creation process.
   * @throws IOException if an I/O error occurs during the test.
   */
  @Test
  public void testInconsistentColumnCount() throws FactoryFailureException, IOException {
    CreatorFromRow<List<String>> creator = new Creator();
    // test with string
    String csvStringInput = "Name,Age,City\nJoe, 20, New York\nJane, 12"; // missing data
    CSVParser<List<String>> parser1 =
        new CSVParser<>(new StringReader(csvStringInput), true, creator);
    List<List<String>> result1 = parser1.parse();
    Assert.assertEquals(2, result1.size()); // how many rows
    Assert.assertEquals(3, parser1.getHeaders().size()); // how many headers
    Assert.assertTrue(result1.contains(List.of("Joe", " 20", " New York")));
    Assert.assertTrue(result1.contains(List.of("Jane", " 12"))); // still parses
  }

  /**
   * Tests the CSV parsing functionality for creating Person objects.a.
   *
   * @throws IOException if an I/O error occurs during the test.
   * @throws FactoryFailureException if there is a failure in the object creation process.
   */
  @Test
  public void testPersonFormat() throws IOException, FactoryFailureException {
    String csvStringInput = "Abigail,Lee,Abby\nBenjamin,Basket,Ben\nCharlotte,Chen,Charly";
    CreatorFromRow<Person> personCreator = new PersonCreator();
    CSVParser<Person> personParser =
        new CSVParser<>(new StringReader(csvStringInput), false, personCreator);
    List<Person> people = personParser.parse();

    Assert.assertEquals(3, people.size()); // how many rows

    Assert.assertEquals("Abigail", people.get(0).firstName);
    Assert.assertEquals("Lee", people.get(0).lastName);
    Assert.assertEquals("Abby", people.get(0).nickname);

    Assert.assertEquals("Benjamin", people.get(1).firstName);
    Assert.assertNotEquals("Ben", people.get(1).lastName);
    Assert.assertEquals("Ben", people.get(1).nickname);

    Assert.assertNotEquals("Abigail", people.get(2).firstName);
    Assert.assertNotEquals("Basket", people.get(2).lastName);
    Assert.assertEquals("Charly", people.get(2).nickname);

    String csvStringInput2 =
        "Abigail,Lee,Abby,dbwwnl\nBenjamin,Basket,Ben\nCharlotte,Chen,Charly"; // first row
    // incorrect number of inputs

    CreatorFromRow<Person> personCreator2 = new PersonCreator();
    Assert.assertThrows(
        FactoryFailureException.class,
        () -> {
          CSVParser<Person> personParser2 =
              new CSVParser<>(new StringReader(csvStringInput2), false, personCreator2);
          List<Person> people2 = personParser2.parse();
        });
  }

  /**
   * Tests the CSV parsing functionality for creating Integer objects.
   *
   * @throws IOException if an I/O error occurs during the test.
   * @throws FactoryFailureException if there is a failure in the object creation process.
   */
  @Test
  public void testIntFormat() throws IOException, FactoryFailureException {
    String csvStringInput = "2\n4\n6\n3928";
    CreatorFromRow<Integer> intCreator = new IntCreator();
    CSVParser<Integer> intParser =
        new CSVParser<>(new StringReader(csvStringInput), false, intCreator);
    List<Integer> ints = intParser.parse();
    System.out.println(ints);
    Assert.assertEquals(4, ints.size()); // how many rows
    Assert.assertEquals(2, ints.get(0));
    Assert.assertEquals(4, ints.get(1));
    Assert.assertEquals(6, ints.get(2));
    Assert.assertEquals(3928, ints.get(3));

    String csvStringInput2 = "2,12,34,56\n4\n6\n3928"; // incorrect number of inputs

    Assert.assertThrows(
        FactoryFailureException.class,
        () -> {
          CSVParser<Integer> intParser2 =
              new CSVParser<>(new StringReader(csvStringInput2), false, intCreator);
          intParser2.parse();
        });

    String csvStringInput3 = "two\nfour\nsix\n3928"; // uses words instead of ints
    Assert.assertThrows(
        FactoryFailureException.class,
        () -> {
          CSVParser<Integer> intParser3 =
              new CSVParser<>(new StringReader(csvStringInput3), false, intCreator);
          intParser3.parse();
        });
  }
}
