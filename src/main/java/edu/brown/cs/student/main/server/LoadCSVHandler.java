package edu.brown.cs.student.main.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import spark.Request;
import spark.Response;
import spark.Route;

public class LoadCSVHandler implements Route {


  public LoadCSVHandler(String filepath) {
    try {
      Reader reader = new FileReader(filepath);
      } catch(FileNotFoundException e) {
      System.err.println("File not found");
      }
    }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    return null;
  }
}

