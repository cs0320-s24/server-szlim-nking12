package edu.brown.cs.student.main.server;

import edu.brown.cs.student.main.server.CensusAPISource.CensusResponse;

public interface ACSDataSource {

  CensusResponse getData(String statenum, String countynum) throws DatasourceException;
}
