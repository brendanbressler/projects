package com.techelevator.npgeek.dao;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import com.techelevator.npgeek.model.Park;


@Component
public class JDBCPARKDAO implements ParkDao{

	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public JDBCPARKDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	
	
	private static final String PARK_THINGS = "parkcode, parkname, state, acreage, elevationinfeet, "
			+ "milesoftrail, numberofcampsites, climate, yearfounded, "
			+ "annualvisitorcount, inspirationalquote, inspirationalquotesource, "
			+ "parkdescription, entryfee, numberofanimalspecies";
	
	
	public List<Park> getAllParks() {
		List<Park> parkList = new ArrayList<Park>();
		String sqlGetAllParks = "SELECT " + PARK_THINGS + " FROM park ORDER BY parkname";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlGetAllParks);
		while(results.next()) {
			Park park = mapRowToPark(results);
			parkList.add(park);
		}
		return parkList;
	}
	

	@Override
	public Park getParkByParkCode(String parkCode) {
		Park park = null;
		String sqlGetParkByParkCode = "SELECT " + PARK_THINGS + " From park WHERE parkcode = ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlGetParkByParkCode, parkCode);
		while(results.next()){
			park = mapRowToPark(results);
		}
		return park;
	}


	
	private Park mapRowToPark(SqlRowSet results) {
		Park park = new Park();
		park.setParkCode(results.getString("parkcode"));
		park.setParkName(results.getString("parkname"));
		park.setState(results.getString("state"));
		park.setAcreage(results.getInt("acreage"));
		park.setElevationInFeet(results.getInt("elevationinfeet"));
		park.setMilesOfTrail(results.getDouble("milesoftrail"));
		park.setNumberOfCampSites(results.getInt("numberofcampsites"));
		park.setClimate(results.getString("climate"));
		park.setYearFounded(results.getInt("yearfounded"));
		park.setAnnualVisitors(results.getInt("annualvisitorcount"));
		park.setInspirationalQuote(results.getString("inspirationalquote"));
		park.setInspirationalQuoteSource(results.getString("inspirationalquotesource"));
		park.setDescription(results.getString("parkdescription"));
		park.setEntryFee(results.getInt("entryfee"));
		park.setNumberOfAnimalSpecies(results.getInt("numberofanimalspecies"));
		
		return park;
	}


}
