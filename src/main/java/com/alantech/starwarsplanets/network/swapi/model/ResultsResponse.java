package com.alantech.starwarsplanets.network.swapi.model;

import lombok.Data;

import java.util.List;

@Data
public class ResultsResponse<T> {
	Integer count;
	String next;
	String previous;
	List<T> results;
}
