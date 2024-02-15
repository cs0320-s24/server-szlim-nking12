package edu.brown.cs.student.main.server;

import static spark.Spark.after;

import spark.Spark;

public class Server {

  private CSVSource state;

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
    Spark.get("broadband", new BroadbandHandler(new ACSCaching(new CensusAPISource())));

    Spark.init();
    Spark.awaitInitialization();
  }

  public static void main(String[] args) {
    new Server();
    System.out.println("Server started; exiting main...");
  }
}
