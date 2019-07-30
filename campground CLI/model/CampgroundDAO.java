package com.techelevator.campground.model;

import java.time.LocalDate;
import java.util.List;

public interface CampgroundDAO {
	
	public List<Campground> getAllCampgrounds(Park park);
	
	public String campgroundInformation(Park park);
	
	public boolean isCampgroundOpen(Long campground, LocalDate date);

	public Campground getCampgroundByCampgroundId(Long campgroundId);
	
	public List<Long> campgroundIdList(Park park);


}
