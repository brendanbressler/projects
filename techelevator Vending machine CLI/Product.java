package com.techelevator;

public class Product {
	
	private String name;
	private Double price;
	private String type;
	private int quantity = 5;
	
	public Product(String name, Double price, String type) {
		this.name = name;
		this.price = price;
		this.type = type;
	}
	
	public String getName() {
		return name;
	}

	public Double getPrice() {
		return price;
	}

	public String getType() {
		return type;
	}

	@Override
	public String toString() {
		return name + " " + price;
	}

	public String getMessage() {
		String message = "";
		
		if (type.contentEquals("Chip")) {
			message = "Crunch Crunch, Yum!!";
		}
		else if (type.contentEquals("Candy")) {
			message = "Munch Munch, Yum!!";
		}
		else if (type.contentEquals("Drink")) {
			message = "Glug Glug, Yum!!";
		}
		else if (type.contentEquals("Gum")) {
			message = "Chew Chew, Yum!!";
		}
		
		return message;
	}
	
	public int removeItem() {
		quantity -= 1;
		return quantity;
	}
	
	public String displayQuantity() {
		String display = "";
		
		if (quantity > 0) {
			display = (quantity + " remaining");
		}
		else {
			display = "SOLD OUT FOOL!!";
		}
		
		return display;
	}
	

}
