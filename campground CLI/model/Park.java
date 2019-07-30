package com.techelevator.campground.model;

import java.time.LocalDate;

public class Park {
	
	private Long parkId;
	private String park;
	private String location;
	private LocalDate estDate;
	private int area;
	private int visitors;
	private String description;
	
	public Long getParkId() {
		return parkId;
	}
	public void setParkId(Long parkId) {
		this.parkId = parkId;
	}
	public String getPark() {
		return park;
	}
	public void setPark(String park) {
		this.park = park;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public LocalDate getEstDate() {
		return estDate;
	}
	public void setEstDate(LocalDate estDate) {
		this.estDate = estDate;
	}
	public int getArea() {
		return area;
	}
	public void setArea(int area) {
		this.area = area;
	}
	public int getVisitors() {
		return visitors;
	}
	public void setVisitors(int visitors) {
		this.visitors = visitors;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	

}
