package com.techelevator.npgeek.model.survey;

import java.util.List;

import com.techelevator.npgeek.model.Park;

public interface SurveyDao {
	public void saveSurvey(Survey survey);
	public List<SurveyResults> getAllSurveys();


}
