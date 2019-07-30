package com.techelevator.npgeek.model.survey;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import com.techelevator.npgeek.model.Park;

@Component
public class JBDCSURVEYDAO implements SurveyDao {

	private JdbcTemplate jdbcTemplate;
	
	

	
	@Autowired 
	public JBDCSURVEYDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	
	@Override
	public void saveSurvey(Survey survey) {
				String insertIntoSurvey = "INSERT INTO survey_result(surveyid,parkcode,emailaddress,state,activitylevel) VALUES (NEXTVAL('seq_surveyId'), ?, ?, ?, ?)";
		jdbcTemplate.update(insertIntoSurvey, survey.getParkCode(), survey.getEmail(), survey.getState(), survey.getActivityLevel());
	}


		
	@Override
	public List<SurveyResults> getAllSurveys() {
		List<SurveyResults> surveyResultList = new ArrayList<SurveyResults>();
		String getAllSurveyResults = "SELECT survey_result.parkcode, parkname, "
				+ " count(survey_result.parkcode) AS surveycount FROM survey_result "
				+ " JOIN park ON park.parkcode = survey_result.parkcode " 
				+ " GROUP BY survey_result.parkcode, parkname ORDER BY surveycount DESC, parkname ASC";
		SurveyResults surveyResult = new SurveyResults();
		SqlRowSet results = jdbcTemplate.queryForRowSet(getAllSurveyResults);
		while (results.next()) {
			surveyResult = mapRowToSurveyResult(results);
			surveyResultList.add(surveyResult);
			
		}
		return surveyResultList;
	}
	
	public long getNextSurveyId() {
		SqlRowSet nextIdResult = jdbcTemplate.queryForRowSet("SELECT nextval('seq_surveyid')");
		if (nextIdResult.next()) {
			return nextIdResult.getLong(1);
		}
		else {
			throw new RuntimeException("Something went wrong with survey sequence");
		}
	}

	
	private SurveyResults mapRowToSurveyResult(SqlRowSet results) {
		SurveyResults surveyResult = new SurveyResults();
		surveyResult.setParkCode(results.getString("parkcode"));
		surveyResult.setParkName(results.getString("parkname"));
		surveyResult.setsurveycount(results.getInt("surveycount"));
		return surveyResult;
	}



	

}
