package edu.brown.cs.student.main.server;

import static spark.Spark.after;

import spark.Spark;

public class Server {

  private final Datasource state;

  public Server(Datasource state) {
    this.state = state;
    int port = 3233;
    Spark.port(port);

    after(
        (request, response) -> {
          response.header("Access-Control-Allow-Origin", "*");
          response.header("Access-Control-Allow-Methods", "*");
        });

    Spark.get("loadcsv", new LoadCSVHandler());
    // Spark.get("viewcsv")

    Spark.init();
    Spark.awaitInitialization();
  }

  public static void main(String[] args) {
    new Server(new CensusAPISource());
    System.out.println("Server started; exiting main...");
  }
}
