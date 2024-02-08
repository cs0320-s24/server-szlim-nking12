package edu.brown.cs.student.main.CSVParser.objects;

/**
 * The Person class represents an object to store information about an individual, including first
 * name, last name, and nickname.
 */
public class Person {
  public String firstName;
  public String lastName;
  public String nickname;

  /**
   * Constructs a Person with the specified first name, last name, and nickname.
   *
   * @param first The first name of the person.
   * @param last The last name of the person.
   * @param nickname The nickname of the person.
   */
  public Person(String first, String last, String nickname) {
    this.firstName = first;
    this.lastName = last;
    this.nickname = nickname;
  }
}
