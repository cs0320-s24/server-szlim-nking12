package edu.brown.cs.student.main.server;

import static spark.Spark.after;

import edu.brown.cs.student.main.server.handler.BroadbandHandler;
import edu.brown.cs.student.main.server.handler.LoadCSVHandler;
import edu.brown.cs.student.main.server.handler.SearchCSVHandler;
import edu.brown.cs.student.main.server.handler.ViewCSVHandler;
import edu.brown.cs.student.main.server.sources.ACSCaching;
import edu.brown.cs.student.main.server.sources.CSVSource;
import edu.brown.cs.student.main.server.sources.CensusAPISource;
import spark.Spark;

/**
 * Top-level class for this demo. Contains the main() method which starts Spark and runs the various
 * handlers.
 */
public class Server {

  private CSVSource state;
  /** Constructs a Server instance, initializes Spark routes, and starts the HTTP server. */
  public Server() {
    this.state = new CSVSource();
    int port = 3232;
    Spark.port(port);

    after(
        (request, response) -> {
          response.header("Access-Control-Allow-Origin", "*");
          response.header("Access-Control-Allow-Methods", "*");
        });

    Spark.get("loadcsv", new LoadCSVHandler(state));
    Spark.get("viewcsv", new ViewCSVHandler(state));
    Spark.get("searchcsv", new SearchCSVHandler(state));
    Spark.get("broadband", new BroadbandHandler(new ACSCaching(new CensusAPISource(), 10, 200)));

    Spark.init();
    Spark.awaitInitialization();
  }
  /** Main method to start the Server and print a message indicating that the server has started. */
  public static void main(String[] args) {
    new Server();
    System.out.println("Server started; exiting main...");
  }
}
