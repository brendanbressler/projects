package com.techelevator.campground.model;

import java.time.LocalDate;
import java.util.List;

public interface ReservationDAO {

	public boolean makeReservation(Campsite campsite, String name, LocalDate start, LocalDate end);
	
	public Long getConfirmId(Campsite campsite, String name, LocalDate start, LocalDate end);
	
	public String reservationsNext30Days();
	
	public int stayLength(LocalDate start, LocalDate end);

}
