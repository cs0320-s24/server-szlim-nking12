package edu.brown.cs.student.main.server;

import static spark.Spark.after;

import java.util.List;
import spark.Spark;

public class Server {

  private static List<List<String>> state;

  public Server() {
    int port = 3232;
    Spark.port(port);

    after(
        (request, response) -> {
          response.header("Access-Control-Allow-Origin", "*");
          response.header("Access-Control-Allow-Methods", "*");
        });

    state = null;
    Spark.get("loadcsv", new LoadCSVHandler(state));
    Spark.get("viewcsv", new ViewCSVHandler(state));

    Spark.init();
    Spark.awaitInitialization();
  }

  public static void main(String[] args) {
    new Server();
    System.out.println("Server started; exiting main...");
  }
}
