package com.techelevator.campground.model.jdbc;

import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.campground.model.Campground;
import com.techelevator.campground.model.CampgroundDAO;
import com.techelevator.campground.model.Park;

public class JDBCCampgroundDAO implements CampgroundDAO{

	private JdbcTemplate jdbcTemplate;

	public JDBCCampgroundDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public List<Campground> getAllCampgrounds(Park park) {
		List<Campground> allCampgrounds = new ArrayList<Campground>();
		
		String sql = "SELECT campground_id, park_id, name, open_from_mm, open_to_mm, daily_fee " + 
				"FROM campground WHERE park_id = ? ";
		
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, park.getParkId());
		
		Campground campground = null;
		
		while(results.next()) {
			campground = mapRowToCampground(results);
			allCampgrounds.add(campground);
		}
		
		return allCampgrounds;
	}
	
	public Campground getCampgroundByCampgroundId(Long campgroundId) {
		Campground foundCampground = new Campground();
		
		String sql = "SELECT campground_id, park_id, name, open_from_mm, open_to_mm, daily_fee " + 
				"FROM campground WHERE campground_id = ? ";
		
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, campgroundId);
				
		while(results.next()) {
			foundCampground = mapRowToCampground(results);
		}
		
		return foundCampground;
	}

	public boolean isCampgroundOpen(Long campground, LocalDate date) {
		boolean isOpen = false;
		
		int dateMonth = date.getMonthValue();
		int open = 0;
		int closed = 0;
		
		
		String sql = "SELECT open_from_mm, open_to_mm " + 
				"FROM campground " + 
				"WHERE campground_id = ?";
		
		SqlRowSet months = jdbcTemplate.queryForRowSet(sql, campground);
		
		while(months.next()) {
			open = months.getInt("open_from_mm");
			closed = months.getInt("open_to_mm");
		}
		
		if(dateMonth >= open && dateMonth < closed) {
			isOpen = true;
		}
		
		return isOpen;
	}
	
	public String campgroundInformation(Park park){
		
		List<Campground> parkCampgrounds = new ArrayList<Campground>();
				
		String campgroundInfo = park.getPark() + "\n" + "\n" +
								  String.format("%-5s", " ") +
								  String.format("%-33s", "Name") +
								  String.format("%-10s", "Open") +
								  String.format("%-10s", "Closed") +
								  String.format("%-10s", "Daily Fee") + "\n";
		
		parkCampgrounds = getAllCampgrounds(park);
		
		for(Campground campground : parkCampgrounds) {
			String openFrom = getMonth(campground.getOpenFrom());
			String openTo = getMonth(campground.getOpenTo());
			
			campgroundInfo += String.format("#%-4s", campground.getCampgroundId()) +
							  String.format("%-33s", campground.getCampground()) +
							  String.format("%-10s", openFrom) +
							  String.format("%-10s", openTo) +
							  String.format("$%-10.2f", campground.getDailyFee()) + "\n";
		}
		
		return campgroundInfo;		
	}
	
	private String getMonth(int month) {
		return new DateFormatSymbols().getMonths()[month-1];
	}
	
	private Campground mapRowToCampground(SqlRowSet rows) {
		Campground campground = new Campground();
		campground.setCampgroundId(rows.getLong("campground_id"));
		campground.setParkId(rows.getLong("park_id"));
		campground.setCampground(rows.getString("name"));
		campground.setOpenFrom(rows.getInt("open_from_mm"));
		campground.setOpenTo(rows.getInt("open_to_mm"));
		campground.setDailyFee(rows.getDouble("daily_fee"));
		return campground;
	}
	
public List<Long> campgroundIdList(Park park){
		
		List<Campground> campgroundIds = new ArrayList<Campground>();
		List<Long> results = new ArrayList<Long>();
		
		campgroundIds = getAllCampgrounds(park);
		
		for(Campground campground : campgroundIds) {
			results.add(campground.getCampgroundId());
		}
		
		return results;		
	}
	
}
