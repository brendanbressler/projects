package com.techelevator.npgeek.model;

public class Weather {

	private String parkCode;
	private int high;
	private int low;
	private String forecast;
	private int fiveDayForecastValue;
	
	
	public String getParkCode() {
		return parkCode;
	}
	public void setParkCode(String parkCode) {
		this.parkCode = parkCode;
	}
	public int getHigh() {
		return high;
	}
	public void setHigh(int high) {
		this.high = high;
	}
	public int getLow() {
		return low;
	}
	public void setLow(int low) {
		this.low = low;
	}
	public String getForecast() {
		return forecast;
	}
	public void setForecast(String forecast) {
		this.forecast = forecast;
	}
	public int getFiveDayForecastValue() {
		return fiveDayForecastValue;
	}
	public void setFiveDayForecastValue(int fiveDayForecastValue) {
		this.fiveDayForecastValue = fiveDayForecastValue;
	}
	
	
public String getAdvisory() {
		
		String result = "";
		switch(this.getForecast()) {
		case "snow": result="Pack snowshoes";
		break;
		case "rain": result="Pack rain gear and wear rainboots! ";
		break;
		case "thunderstorms": result="Seek shelter and avoid hiking on exposed ridges! ";
		break;
		case "sun": result="Pack sunblock! ";
		break;
		}
		
		int high = this.getHigh();
		int low = this.getLow();
		if (high > 75) {
			result += "Bring an extra gallon of water! ";
		}
		if (low < 20) {
			result += "Beware of exposure to frigid temperatures! ";
		}
		if((high - low) > 20) {
			result += "Wear breathable layers!";
		}
		
		return result;
	}
	
public String changeStringToCamelCase(String string) {
	 String[] words = string.split(" ");
	 
	 for(int i = 1; i < words.length; i++) {
		words[i]= words[i].substring(0,1).toUpperCase() + words[i].substring(1).toLowerCase();
		string = String.join("",words);
		}
	 
	
	  	return string;
}
			
			
}
	

	