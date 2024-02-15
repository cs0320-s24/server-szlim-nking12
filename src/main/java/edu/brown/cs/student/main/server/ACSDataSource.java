package edu.brown.cs.student.main.server;

import edu.brown.cs.student.main.CSVParser.exceptions.FactoryFailureException;
import edu.brown.cs.student.main.server.CensusAPISource.CensusResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ACSDataSource {


   CensusResponse getData(String statenum, String countynum) throws DatasourceException;
}
