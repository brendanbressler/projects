package com.techelevator.npgeek.model.survey;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Range;

public class Survey {
	
	private Long surveyId;
	
	@NotBlank
	private String parkCode;
	
	@Email
	@NotBlank(message="Email address is required.")
	private String email;
	
	private String emailVerification;
	
	@NotBlank
	private String state;
	
	@NotBlank
	private String activityLevel;	
	
	@AssertTrue(message="Email addresses do not match")
	public boolean isEmailMatching() {
		return email != null && email.equals(emailVerification);
	}
	
	
	public String getEmailVerification() {
		return emailVerification;
	}


	public void setEmailVerification(String emailVerification) {
		this.emailVerification = emailVerification;
	}


	public Long getSurveyId() {
		return surveyId;
	}
	public void setSurveyId(Long surveyId) {
		this.surveyId = surveyId;
	}
	public String getParkCode() {
		return parkCode;
	}
	public void setParkCode(String parkCode) {
		this.parkCode = parkCode;
	}
	
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getActivityLevel() {
		return activityLevel;
	}
	public void setActivityLevel(String activityLevel) {
		this.activityLevel = activityLevel;
	}

	
	
	
	
	
	
	
}
