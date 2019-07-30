package com.techelevator.campground.model.jdbc;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.campground.model.Park;
import com.techelevator.campground.model.ParkDAO;

public class JDBCParkDAO implements ParkDAO {

	private JdbcTemplate jdbcTemplate;
	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyy");

	public JDBCParkDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<Park> getAllParks() {
		List<Park> allParks = new ArrayList<Park>();

		String sql = "SELECT park_id, name, location, establish_date, area, visitors, description\n" + "FROM park\n"
				+ "ORDER BY name";

		SqlRowSet results = jdbcTemplate.queryForRowSet(sql);

		Park park = null;

		while (results.next()) {
			park = mapRowToPark(results);
			allParks.add(park);
		}

		return allParks;
	}

	public String parkInformation(Park park) {
		LocalDate estDate = park.getEstDate();
		String estDateFormat = estDate.format(formatter);

		String parkInfo = park.getPark() + "\n" + String.format("%-18s", "Location:") + park.getLocation() + "\n"
				+ String.format("%-18s", "Established:") + estDateFormat + "\n"
				+ String.format("%-18s%,-20d", "Area:", park.getArea()) + "\n"
				+ String.format("%-18s%,-20d", "Annual Visitors: ", park.getVisitors()) + "\n" + "\n"
				+ String.format("%-40s", park.getDescription());

		return parkInfo;
	}

	private Park mapRowToPark(SqlRowSet rows) {
		Park park = new Park();
		park.setParkId(rows.getLong("park_id"));
		park.setPark(rows.getString("name"));
		park.setLocation(rows.getString("location"));
		park.setEstDate(rows.getDate("establish_date").toLocalDate());
		park.setArea(rows.getInt("area"));
		park.setVisitors(rows.getInt("visitors"));
		park.setDescription(rows.getString("description"));
		return park;
	}

	@Override
	public Park getParkByName(String parkName) {
		// TODO Auto-generated method stub
		return null;
	}

}
