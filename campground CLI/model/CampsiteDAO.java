package com.techelevator.campground.model;

import java.time.LocalDate;
import java.util.List;

public interface CampsiteDAO {
	
	public List<Campsite> getAllCampsites(Campground campground);
	
	public List<Campsite> availableCampsites(Campground campground, LocalDate start, LocalDate end);
	
	public String printAvailableSites(Campground campground, LocalDate start, LocalDate end);
	
	public List<Campsite> availableCampsitesInPark(Park park, LocalDate start, LocalDate end);
	
	public String printAvailableSitesInPark(Park park, LocalDate start, LocalDate end);
	
	public List<Campsite> advancedSearchResults(Campground campground, LocalDate start, LocalDate end, int maxOcc, String accessible, int rvLength, String utilities);
		
	public String printAdvancedResults(List<Campsite> availableSites, int stayLength);
	
	public Campsite getCampsiteById(int campsiteId);

}
