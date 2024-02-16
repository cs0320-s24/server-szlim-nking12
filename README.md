# Project Details
**Project Name:** Server 

**Team members and contributions:** szlim, nking12

**Total estimated time it took to complete project:** 30hrs

**Github repo:** https://github.com/cs0320-s24/server-szlim-nking12.git

# Design Choices
**Interface and class interaction:**

ACSDataSource Interface: An interface for classes that serve as a data source for retrieving information related to ACS

ACSCaching Class: Implements the ACSDataSource interface and uses a cache to store ACS data. It delegates data retrieval to CensusAPISource when the data is not present in the cache. 

CensusAPISource Class: Implements the ACSDataSource interface and is responsible for making HTTP requests to the Census API. It retrieves ACS data for a specific state and county.

MockedCensusSource Class: Implements the ACSDataSource interface for testing purposes. Returns constant data instead of making actual API requests. 

**Runtime/Space Optimizations:**
Caching: ACSCaching optimizes runtime by caching the data. This reduces the need to fetch data from the Census API for repeated requests within a specific cache duration (which can be altered by any developer stakeholder).

**Other Design Choices**
- We decided to use List<List<String>> to represent the ACS data structure.
- The information from the API will only load if the state request exactly matches the census data. For example, the request will fail if the user requests "california" instead of "California".
- If the county is not found, it will return data for all the counties in the specified state.

# Errors/Bugs
- This is more a potential user error that one may encounter that I wanted to mention. If a user indicated that a CSV has headers and it actually does not, and the value they are searching for is in that first row, then the first row will not be considered when searching. However, if a user inputs anything other than yes, it will automatically assume no headers and continue to search the entire file. This is only one specific case

# Tests
TestBroadband:
- testMissingCensusRequestFail: checks the API responds appropriately when called without the required parameters. It expects an error response
- testSuccessfulDataRetrieval: Tests the successful retrieval of broadband data based on state and county parameters. It checks if the response contains the expected values for success and the specified county.
- testCountyCodeRetrievalFailure: Tests that providing an invalid county code results in the server responding with data for all counties in the specified state.
- testStateCodeRetrievalFailure: Validates the server's response when an invalid state code is provided. It expects an error response and includes details about the invalid state code.
- testInvalidParameters: Tests the server's handling of requests with invalid parameters, specifically an invalid state code. 

The test suite utilizes mocked data through a MockedCensusSource and sets up a Spark server for each test case, ensuring an environment for testing the Broadband API endpoint

TestCSV:
- testLoadHandlerCorrect: tests that the "loadcsv" endpoint successfully loads a CSV file with correct inputs, expecting a response with the status "success."
- testLoadHandlerProtectedFile: Tests the "loadcsv" endpoint with an attempt to load a file from outside a protected directory. It expects an error response.
- testLoadHandlerNonexistent: Checks the "loadcsv" endpoint's response when attempting to load a nonexistent CSV file. It expects an error response.
- testViewNotLoaded: Tests the "viewcsv" endpoint without having loaded a CSV file. It expects an error response indicating that the CSV must be loaded first.
- testViewSuccess: Tests the "viewcsv" endpoint's response when attempting to view a successfully loaded CSV file, expecting a response with the status "success."
- testSearchNoResults: Tests the "searchcsv" endpoint with a search term that returns no results. It expects a response indicating that no matches were found.
- testSearchNotLoaded: Tests the "searchcsv" endpoint without having loaded a CSV file. It expects an error response indicating that the CSV must be loaded first.
- testSearchSuccessOneResult: Tests the "searchcsv" endpoint's response when a search term returns a single result. It expects a response with the status "success" and the details of the matching row.
- testSearchMultipleResults: Tests the "searchcsv" endpoint with a search term returning multiple results.It expects a response with the status "success" and the details of the matching row.

# How to
1. compile and ./run
2. open a web browser and input: http://localhost:3232/<request>
  - If you request to **load** a CSV file, please also provide the absolute CSV file path and if it has headers (true/false) in the following format:
    - `http://localhost:3232/loadcsv?csv=<csv file path>&headers=<true/false>.`
    - For example: `http://localhost:3232/loadcsv?csv=/Users/sophialim/Desktop/cs32/server-szlim-nking12/data/census/dol_ri_earnings_disparity.csv.`
    - Note that it will only successfully load a CSV if it is in the "data/" directory.
  - If you request to **view** a CSV file, please do so in the following format:
    - `http://localhost:3232/viewcsv`
  - If you request to **search** a CSV file, please provide the search target and column identifier (optional):
    - `http://localhost:3232/searchcsv?target=<target>&col=<col identifier>`
    - For example: `http://localhost:3232/searchcsv?target=White&col=1`
  - If you request to get **broadband** data from the ACS, please provide the state and county that you would like the data on in the following format:
    - `http://localhost:3232/broadband?state=<state>&county=<county>`
    - For example: `http://localhost:3232/broadband?state=California&county=Los%20Angeles%20County,%20California`

