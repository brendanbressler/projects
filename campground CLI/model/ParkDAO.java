package com.techelevator.campground.model;

import java.util.List;


public interface ParkDAO {
	
	public List<Park> getAllParks();
	
	public Park getParkByName(String parkName);

	public String parkInformation(Park selectedPark);


}
