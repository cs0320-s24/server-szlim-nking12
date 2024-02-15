package edu.brown.cs.student.main.server;

import com.squareup.moshi.Json;
import com.squareup.moshi.JsonClass;

@JsonClass(generateAdapter = true)
public record CensusResponse(
    @Json(name = "NAME") String name, @Json(name = "S2802_C03_022E") String S2802_C03_022E) {}
