package com.techelevator;

public class Change {
	
	private static int numberOfQuarters;
	private static int numberOfDimes;
	private static int numberOfNickels;
	private static double calculateChange;
	
	public static double getCalculateChange(Double monies) {
		return calculateChange;
	}
	
	public void giveChange(double calculateChange1) {
		calculateChange = (calculateChange * 100);
		numberOfQuarters = (int) (calculateChange / 25);
		calculateChange = calculateChange - (numberOfQuarters * 25);
		numberOfDimes = (int) (calculateChange / 10);
		calculateChange = calculateChange - (numberOfDimes * 10);
		numberOfNickels = (int) (calculateChange / 5);
		
		calculateChange = 0;
		System.out.println("Your change is " + numberOfQuarters + " Quarters " + numberOfDimes + " Dimes " + numberOfNickels + " Nickels ");
	
	}
//	
//	public Change(double totalValueOfMoneyGivenInCents) {
//		this.calculateChange = calculateChange;
//		
//		while(calculateChange > 0.25) {
//			calculateChange -= 0.25;
//			numberOfQuarters += 1;
//		}
//		while(calculateChange > 0.10) {
//			calculateChange -= 0.10;
//			numberOfDimes += 1;
//		}
//		while(calculateChange > 0) {
//			calculateChange -= 0.05;
//			numberOfNickels += 1;
//			
//			System.out.println("Your change is " + numberOfQuarters + " Quarters " + numberOfDimes + " Dimes " + numberOfNickels + " Nickels ");
//		}
//		
//	}
//		
//	public String toString() {
//		return (numberOfQuarters + " Quarters " + numberOfDimes + " Dimes " + numberOfNickels + " Nickels ");
//	}
//
//	public int getNumberOfQuarters() {
//		return numberOfQuarters;
//	}
//
//	public int getNumberOfDimes() {
//		return numberOfDimes;
//	}
//
//	public int getNumberOfNickels() {
//		return numberOfNickels;
//	}

}
