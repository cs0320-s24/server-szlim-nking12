package edu.brown.cs.student.main.CSVParser.creators;

import edu.brown.cs.student.main.CSVParser.exceptions.FactoryFailureException;
import edu.brown.cs.student.main.CSVParser.objects.Person;
import java.util.List;

/**
 * This class implements the CreatorFromRow interface to create Person objects from a row of data
 * represented as a list of strings. It checks the input row contains exactly three elements
 * representing the first name, last name, and nickname. If successful, it creates and returns a new
 * Person object.
 */
public class PersonCreator implements CreatorFromRow<Person> {

  @Override
  public Person create(List<String> row) throws FactoryFailureException {
    if (row.size() != 3) {
      throw new FactoryFailureException("Invalid number of elements in the row", row);
    }

    String firstName = row.get(0);
    String lastName = row.get(1);
    String nickname = row.get(2);

    return new Person(firstName, lastName, nickname);
  }
}
