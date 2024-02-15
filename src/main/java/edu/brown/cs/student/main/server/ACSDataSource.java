package edu.brown.cs.student.main.server;

import java.util.List;

public interface ACSDataSource {

  List<CensusResponse> getData(String statenum, String countynum) throws DatasourceException;
}
