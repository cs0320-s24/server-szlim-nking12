package edu.brown.cs.student.main.server;

import java.util.List;
import spark.Request;
import spark.Response;
import spark.Route;

public class ViewCSVHandler implements Route {
  private final List<List<String>> state;


  public ViewCSVHandler(List<List<String>> state){
    this.state = state;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    return null;
  }
}
