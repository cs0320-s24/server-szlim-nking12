package edu.brown.cs.student.main.creators;

import java.util.List;

/**
 * The Creator class implements the CreatorFromRow interface to create objects from a row of data
 * represented by a list of strings. It is a generic implementation where the create method simply
 * returns the input row as is.
 */
public class Creator implements CreatorFromRow<List<String>> {

  /**
   * Creates a List of Strings from the given row.
   *
   * @param row The row to be transformed.
   * @return A list of strings created from the input row.
   */
  @Override
  public List<String> create(List<String> row) {
    return row;
  }
}
