package com.techelevator.npgeek.dao;

import java.util.List;

import com.techelevator.npgeek.model.Park;

public interface ParkDao {
	
	public List<Park>getAllParks();
	public Park getParkByParkCode(String parkCode);
	

}
