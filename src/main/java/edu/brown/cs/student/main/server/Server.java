package edu.brown.cs.student.main.server;

import static spark.Spark.after;

import spark.Spark;

public class Server {

  private final Datasource state;


  public Server(Datasource state) {
    this.state = state;
    int port = 3232;
    Spark.port(port);


    after(
        (request, response) -> {
          response.header("Access-Control-Allow-Origin", "*");
          response.header("Access-Control-Allow-Methods", "*");
        });
  }

  public static void main(String[] args) {}
}
