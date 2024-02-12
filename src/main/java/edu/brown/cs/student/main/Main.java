// package edu.brown.cs.student.main;
//
// import edu.brown.cs.student.main.CSVParser.exceptions.FactoryFailureException;
//// import edu.brown.cs.student.main.CSVParser.utility.Utility;
// import java.io.FileNotFoundException;
// import java.io.IOException;
//
/// ** The Main class of our project. This is where execution begins. */
// public final class Main {
//  /**
//   * The initial method called when execution begins.
//   *
//   * @param args An array of command line arguments
//   */
//  public static void main(String[] args) {
//    new Main(args).run();
//  }
//
//  private Main(String[] args) {}
//
//  /** The main execution method. */
//  private void run() {
//    try {
//      Utility utility = new Utility();
//      utility.start();
//    } catch (FileNotFoundException e) {
//      System.err.println("Error: File not found. Please check the file path.");
//      System.exit(1);
//    } catch (IOException | FactoryFailureException e) {
//      System.err.println(e.getMessage());
//      System.exit(1);
//    } catch (ArrayIndexOutOfBoundsException e) {
//      System.err.println("Error: invalid column input.");
//      System.exit(1);
//    } catch (IllegalArgumentException e) {
//      System.err.println("Error: File outside of allowed directory.");
//      System.exit(1);
//    } catch (NullPointerException e) {
//      System.err.println(
//          "Error: Please provide all required inputs and/or ensure your file is not empty.");
//      System.exit(1);
//    }
//  }
// }
