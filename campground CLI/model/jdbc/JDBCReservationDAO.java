package com.techelevator.campground.model.jdbc;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.campground.model.Campsite;
import com.techelevator.campground.model.Reservation;
import com.techelevator.campground.model.ReservationDAO;

public class JDBCReservationDAO implements ReservationDAO {

	private JdbcTemplate jdbcTemplate;

	public JDBCReservationDAO(DataSource datasource) {
		this.jdbcTemplate = new JdbcTemplate(datasource);
	}
	
	public String reservationsNext30Days() {
		List<Reservation> allReservations = new ArrayList<Reservation>();
		String fullLine = "------------------------------------------------------------------------------------------------------------\n";

		String reservations =
//				  String.format(format, "ResID", "SiteID", "Name", "Start Date", "End Date", "Date Created");  
				  fullLine +
				  String.format("| %-10s", "ResId") +
				  String.format("| %-10s", "SiteId") +
				  String.format("| %-30s", "Name") + 
				  String.format("| %-15s", "Start Date") +
				  String.format("| %-15s", "End Date") +
				  String.format("| %-15s|", "Date Created") + "\n" +
				  fullLine;
		
		String sql = "SELECT reservation_id, site_id, name, from_date, to_date, create_date\n" + 
				"FROM reservation\n" + 
				"WHERE from_date >= CURRENT_DATE AND from_date <= CURRENT_DATE + interval '30 days'\n" + 
				"ORDER BY site_id, from_date";
		
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
		
		Reservation r = null;
		
		while(results.next()) {
			r = mapRowToReservation(results);
			allReservations.add(r);
		}

		for(Reservation rsv : allReservations) {
			reservations +=	  String.format("| %-10s", rsv.getReservationId()) +
							  String.format("| %-10s", rsv.getSiteId()) +
							  String.format("| %-30s", rsv.getName()) + 
							  String.format("| %-15s", rsv.getStartDate()) +
							  String.format("| %-15s", rsv.getEndDate()) +
							  String.format("| %-15s|", rsv.getReserveDate()) + "\n";
		}
		
		reservations += fullLine;

		return reservations;
	}

	public int stayLength(LocalDate start, LocalDate end) {
		Period stayLength = Period.between(start, end);
		int numDays = stayLength.getDays();
		return numDays;
	}

	public boolean makeReservation(Campsite campsite, String name, LocalDate start, LocalDate end) {
		boolean reservationMade = false;
		LocalDate today = LocalDate.now();
		
		// returning reservation ID instead of boolean being returned
		String sql = "INSERT INTO reservation (site_id, name, from_date, to_date, create_date) " +
					 "VALUES (?, ?, '" + start + "', '" + end + "', '" + today + "')";
				
		if(jdbcTemplate.update(sql, campsite.getSiteId(), name) == 1) {
			reservationMade = true;
		};

		return reservationMade;
	}
	
	public Long getConfirmId(Campsite campsite, String name, LocalDate start, LocalDate end) {
		Long confirmId = (long) 0;
		
		String sql = "SELECT reservation_id FROM reservation " +
						"WHERE site_id = ? AND name = ? AND from_date = ? AND to_date = ?";
		
		SqlRowSet result = jdbcTemplate.queryForRowSet(sql, campsite.getSiteId(), name, start, end);

		if(result.next()) {
			confirmId = result.getLong("reservation_id");
		}
		
		return confirmId;
	}
	
	private Reservation mapRowToReservation(SqlRowSet rows) {
		Reservation r = new Reservation();
		r.setReservationId(rows.getLong("reservation_id"));
		r.setSiteId(rows.getLong("site_id"));
		r.setName(rows.getString("name"));
		r.setStartDate(rows.getDate("from_date").toLocalDate());
		r.setEndDate(rows.getDate("to_date").toLocalDate());
		r.setReserveDate(rows.getDate("create_date").toLocalDate());
		return r;
	}
	
}
