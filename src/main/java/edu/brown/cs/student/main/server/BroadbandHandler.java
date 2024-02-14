package edu.brown.cs.student.main.server;

import spark.Request;
import spark.Response;
import spark.Route;

public class BroadbandHandler implements Route {

  public BroadbandHandler(Datasource state){}

  @Override
  public Object handle(Request request, Response response) throws Exception {
    String state = request.queryParams("state");
    String county = request.queryParams("county");
    return null;
  }
}
