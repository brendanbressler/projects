package com.techelevator.campground.model;

public class Campground {

	private Long campgroundId;
	private Long parkId;
	private String campground;
	private int openFrom;
	private int openTo;
	private Double dailyFee;
	
	public Long getCampgroundId() {
		return campgroundId;
	}
	public void setCampgroundId(Long campgroundId) {
		this.campgroundId = campgroundId;
	}
	public Long getParkId() {
		return parkId;
	}
	public void setParkId(Long parkId) {
		this.parkId = parkId;
	}
	public String getCampground() {
		return campground;
	}
	public void setCampground(String campground) {
		this.campground = campground;
	}
	public int getOpenFrom() {
		return openFrom;
	}
	public void setOpenFrom(int openFrom) {
		this.openFrom = openFrom;
	}
	public int getOpenTo() {
		return openTo;
	}
	public void setOpenTo(int openTo) {
		this.openTo = openTo;
	}
	public Double getDailyFee() {
		return dailyFee;
	}
	public void setDailyFee(Double dailyFee) {
		this.dailyFee = dailyFee;
	}
	
	
	
}
