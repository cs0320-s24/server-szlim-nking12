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
- n/a

# Tests

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

