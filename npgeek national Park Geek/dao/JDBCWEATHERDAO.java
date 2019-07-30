package com.techelevator.npgeek.dao;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;


import com.techelevator.npgeek.model.Weather;


@Component
public class JDBCWEATHERDAO implements WeatherDao {

	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public JDBCWEATHERDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	
	
	
	private static final String WEATHER = " parkcode, fivedayforecastvalue, low, high, forecast ";


		

		@Override
		public List<Weather> getTheWeather(String parkCode) {
			List<Weather> weatherList = new ArrayList<Weather>();
			String sqlGetTheWeather = "SELECT " + WEATHER + " FROM weather WHERE parkcode = ? "
									+ " ORDER BY fivedayforecastvalue";
					
					
			SqlRowSet results = jdbcTemplate.queryForRowSet(sqlGetTheWeather, parkCode);
			while(results.next()) {
				Weather seasonal = mapRowToWeather(results);
				weatherList.add(seasonal);
			}
			return weatherList;
		}

		
		
		
		private Weather mapRowToWeather(SqlRowSet results) {
			Weather nature = new Weather();
			nature.setParkCode(results.getString("parkcode"));
			nature.setFiveDayForecastValue(results.getInt("fivedayforecastvalue"));
			nature.setLow(results.getInt("low"));
			nature.setHigh(results.getInt("high"));
			nature.setForecast(results.getString("forecast"));
			return nature;
		}

		
		
		
}
