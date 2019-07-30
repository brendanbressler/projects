package com.techelevator.campground.model.jdbc;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.campground.model.Campground;
import com.techelevator.campground.model.Campsite;
import com.techelevator.campground.model.CampsiteDAO;
import com.techelevator.campground.model.Park;

public class JDBCCampsiteDAO implements CampsiteDAO {

	private JdbcTemplate jdbcTemplate;

	public JDBCCampsiteDAO(DataSource datasource) {
		this.jdbcTemplate = new JdbcTemplate(datasource);
	}

	@Override
	public List<Campsite> getAllCampsites(Campground campground) {
		List<Campsite> allCamps = new ArrayList<Campsite>();

		String sql = "SELECT site_number,campground_id, max_occupancy, accessible, "
				+ "max_rv_length, utilities FROM site ORDER BY site_id;";

		SqlRowSet results = jdbcTemplate.queryForRowSet(sql);

		Campsite Camps = null;

		while (results.next()) {
			Camps = mapRowToCampsite(results);
			allCamps.add(Camps);
		}

		return allCamps;
	}

	@Override
	public List<Campsite> availableCampsites(Campground campground, LocalDate start, LocalDate end) {
		List<Campsite> availableSites = new ArrayList<Campsite>();

		String sql = "SELECT site_id, campground_id, site_number, max_occupancy, accessible, max_rv_length, utilities "
				+ "FROM site "
				+ "WHERE site_id IN (SELECT site_id FROM site WHERE campground_id = ?) "
				+ "AND site_id NOT IN (SELECT site_id FROM reservation "
				+ "WHERE ((from_date > '" + start + "' AND from_date < '" + end + "') OR (to_date > '" + start + "' AND to_date < '" + end + "'))) LIMIT 5";

		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, campground.getCampgroundId());

		Campsite campsite = null;

		while (results.next()) {
			campsite = mapRowToCampsite(results);
			availableSites.add(campsite);
		}

		return availableSites;
	}

	public String printAvailableSites(Campground campground, LocalDate start, LocalDate end) {

		List<Campsite> availableSites = availableCampsites(campground, start, end);
		Period stayLength = Period.between(start, end);
		int numDays = stayLength.getDays();

		String availableCampsites = String.format("%-10s", "Site No.") + String.format("%-12s", "Max Occup.")
									+ String.format("%-13s", "Accessible?") + String.format("%-15s", "Max RV Length")
									+ String.format("%-10s", "Utility") + String.format("%-10s", "Cost") + "\n";

		for (Campsite site : availableSites) {
			String accessible = "No";
			String utility = "No";
			Double dailyFee = 0.0;

			if (site.isAccessible()) {
				accessible = "Yes";
			}

			if (site.isUtilities()) {
				utility = "Yes";
			}

			String sql = "SELECT daily_fee " + "FROM campground WHERE campground_id = ?";

			SqlRowSet results = jdbcTemplate.queryForRowSet(sql, site.getCampgroundId());

			if (results.next()) {
				dailyFee = results.getDouble("daily_fee");
			}

			availableCampsites += String.format("%-10s", site.getSiteId())
								+ String.format("%-12s", site.getMaxOccupancy()) + String.format("%-13s", accessible)
								+ String.format("%-15s", site.getMaxLength()) + String.format("%-10s", utility)
								+ String.format("$%-10.2f", (dailyFee * numDays)) + "\n";
		}

		return availableCampsites;
	}
	
	public List<Campsite> availableCampsitesInPark(Park park, LocalDate start, LocalDate end) {
		List<Campsite> availableSites = new ArrayList<Campsite>();

		String sql = "SELECT site_id, campground_id, site_number, max_occupancy, accessible, max_rv_length, utilities "
				+ "FROM site "
				+ "WHERE campground_id IN ("
				+ "SELECT campground_id FROM campground "
				+ "WHERE park_id IN (SELECT park_id FROM park WHERE park_id = ?)) "
				+ "AND site_id NOT IN (SELECT site_id FROM reservation "
				+ "WHERE ((from_date > '" + start + "' AND from_date < '" + end + "') OR (to_date > '" + start + "' AND to_date < '" + end + "'))) LIMIT 5";

		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, park.getParkId());

		Campsite campsite = null;

		while (results.next()) {
			campsite = mapRowToCampsite(results);
			availableSites.add(campsite);
		}

		return availableSites;
	}

	public String printAvailableSitesInPark(Park park, LocalDate start, LocalDate end) {

		List<Campsite> availableSites = availableCampsitesInPark(park, start, end);
		Period stayLength = Period.between(start, end);
		int numDays = stayLength.getDays();

		String availableCampsites = String.format("%-20s", "Campground") + String.format("%-10s", "Site No.") + String.format("%-12s", "Max Occup.")
									+ String.format("%-13s", "Accessible?") + String.format("%-15s", "Max RV Length")
									+ String.format("%-10s", "Utility") + String.format("%-10s", "Cost") + "\n";

		for (Campsite site : availableSites) {
			String accessible = "No";
			String utility = "No";
			Double dailyFee = 0.0;
			String campground = null;

			if (site.isAccessible()) {
				accessible = "Yes";
			}

			if (site.isUtilities()) {
				utility = "Yes";
			}

			String sql = "SELECT daily_fee, name " + "FROM campground WHERE campground_id = ?";

			SqlRowSet results = jdbcTemplate.queryForRowSet(sql, site.getCampgroundId());

			if (results.next()) {
				dailyFee = results.getDouble("daily_fee");
				campground = results.getString("name");
			}
			
			availableCampsites += String.format("%-20s", campground) + String.format("%-10s", site.getSiteId())
								+ String.format("%-12s", site.getMaxOccupancy()) + String.format("%-13s", accessible)
								+ String.format("%-15s", site.getMaxLength()) + String.format("%-10s", utility)
								+ String.format("$%-10.2f", (dailyFee * numDays)) + "\n";
		}

		return availableCampsites;
	}

	public List<Campsite> advancedSearchResults(Campground campground, LocalDate start, LocalDate end, int maxOcc, String accessible, int rvLength, String utilities){
		
		List<Campsite> availableSites = new ArrayList<Campsite>();
		
		String isAccessible = "";
		String hasUtilities = "";
		
		if(accessible.contentEquals("Y")) {
			isAccessible = "TRUE";
		} else if (accessible.contentEquals("N")) {
			isAccessible = "TRUE OR FALSE";
		}
		
		if(utilities.contentEquals("Y")) {
			hasUtilities = "TRUE";
		} else if (utilities.contentEquals("N")) {
			hasUtilities = "TRUE OR FALSE";
		}
		
		String sql = "SELECT site_id, campground_id, site_number, max_occupancy, accessible, max_rv_length, utilities "
				+ "FROM site WHERE site_id IN (SELECT site_id FROM site WHERE campground_id = ?) "
				+ "AND site_id NOT IN (SELECT site_id FROM reservation "
				+ "WHERE ((from_date >= '" + start + "' AND from_date < '" + end + "') "
				+ "OR (to_date >= '" + start + "' AND to_date < '" + end + "'))) "
				+ "AND max_occupancy >= ? AND accessible IS " + isAccessible + " AND max_rv_length >= ? AND utilities IS " + hasUtilities
				+ " LIMIT 5";
		
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, campground.getCampgroundId(), maxOcc, rvLength);

		Campsite campsite = null;

		while (results.next()) {
			campsite = mapRowToCampsite(results);
			availableSites.add(campsite);
		}

		return availableSites;		
		
	}
	
	public String printAdvancedResults(List<Campsite> availableSites, int stayLength) {

		String availableCampsites = //String.format("%-20s", "Campground") + 
										String.format("%-10s", "Site No.") + String.format("%-12s", "Max Occup.")
									+ String.format("%-13s", "Accessible?") + String.format("%-15s", "Max RV Length")
									+ String.format("%-10s", "Utility") + String.format("%-10s", "Cost") + "\n";

		for (Campsite site : availableSites) {
			String accessibleAns = "No";
			String utility = "No";
			Double dailyFee = 0.0;

			if (site.isAccessible()) {
				accessibleAns = "Yes";
			}

			if (site.isUtilities()) {
				utility = "Yes";
			}

			String sql = "SELECT daily_fee, name " + "FROM campground WHERE campground_id = ?";

			SqlRowSet results = jdbcTemplate.queryForRowSet(sql, site.getCampgroundId());

			if (results.next()) {
				dailyFee = results.getDouble("daily_fee");
//				campground = results.getString("name");
			}
			
			availableCampsites += //String.format("%-20s", campground) +
								  String.format("%-10s", site.getSiteId())
								+ String.format("%-12s", site.getMaxOccupancy()) + String.format("%-13s", accessibleAns)
								+ String.format("%-15s", site.getMaxLength()) + String.format("%-10s", utility)
								+ String.format("$%-10.2f", (dailyFee * stayLength)) + "\n";
		}

		return availableCampsites;
	}
	
	
	public Campsite getCampsiteById(int campsiteId) {
		Campsite foundCampsite = new Campsite();
		
		String sql = "SELECT site_id, campground_id, site_number, max_occupancy, accessible, max_rv_length, utilities " + 
				"FROM site WHERE site_id = ? ";
		
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, campsiteId);
				
		while(results.next()) {
			foundCampsite = mapRowToCampsite(results);
		}
		
		return foundCampsite;
	}
	

	private Campsite mapRowToCampsite(SqlRowSet rows) {
		Campsite campsite = new Campsite();
		campsite.setSiteId(rows.getLong("site_id"));
		campsite.setCampgroundId(rows.getLong("campground_id"));
		campsite.setSiteNumber(rows.getInt("site_number"));
		campsite.setMaxOccupancy(rows.getInt("max_occupancy"));
		campsite.setAccessible(rows.getBoolean("accessible"));
		campsite.setMaxLength(rows.getInt("max_rv_length"));
		campsite.setUtilities(rows.getBoolean("utilities"));
		return campsite;
	}

	

}
