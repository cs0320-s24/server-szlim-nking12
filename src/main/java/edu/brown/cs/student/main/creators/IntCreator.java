package edu.brown.cs.student.main.creators;

import edu.brown.cs.student.main.exceptions.FactoryFailureException;
import java.util.List;

/**
 * This class implements the CreatorFromRow interface to create Integer objects from a row of data
 * represented as a list of strings. It checks the input rows contains only one element, and
 * attempts to parse it into an Integer. If successful, the parsed Integer is returned; otherwise, a
 * FactoryFailureException is thrown.
 */
public class IntCreator implements CreatorFromRow<Integer> {

  /**
   * Creates an Integer object from the input row, which is expected to contain exactly one element.
   *
   * @param row The input row represented as a list of strings.
   * @return The Integer object created from the input row.
   * @throws FactoryFailureException If the row does not contain exactly one element, or if parsing
   *     the element into an Integer fails.
   */
  @Override
  public Integer create(List<String> row) throws FactoryFailureException {
    if (row.size() != 1) {
      throw new FactoryFailureException("invalid number of elements in the row ", row);
    }
    try {
      return Integer.parseInt(row.get(0));
    } catch (NumberFormatException e) {
      throw new FactoryFailureException("unable to parse integer ", row);
    }
  }
}
