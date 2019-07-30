package com.techelevator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class VendingMachine {

	private static File inventoryFile;

	private static int numberOfQuarters;
	private static int numberOfDimes;
	private static int numberOfNickels;
	private static double calculateChange;

	private static Map<String, Product> productMap = new LinkedHashMap<String, Product>();
	private static Map<String, Integer> slotStockMap = new LinkedHashMap<String, Integer>();
	private static Product item;
	private static Double vendingMachineTotal = 0.00;
	private String slot;
	private int stock = 5;
	PrintWriter auditWriter = null;
	private static List<String> salesFile = new ArrayList<String>(); 
	
	

	public VendingMachine() {
		inventoryFile = new File("/Users/student/workspace"
				+ "/week-4-pair-exercises-java-team-1/19_Capstone/Example Files/VendingMachine.txt");

		try (Scanner fileScanner = new Scanner(inventoryFile)) {

			while (fileScanner.hasNextLine()) {

				String[] stuff = fileScanner.nextLine().split("\\|");

				Double stuffPrice = Double.parseDouble(stuff[2]);

				for (int i = 0; i < 16; i += 16) {
					slot = stuff[0];
					item = new Product(stuff[1], stuffPrice, stuff[3]);

					productMap.put(slot, item);

				}

			}

		} catch (FileNotFoundException e) {
			System.out.println("File not found dummy!");
		}

	}

	public Map<String, Product> getProductMap() {
		return productMap;
	}

	public Double getVendingMachineTotal() {
		return vendingMachineTotal;
	}

	public String getSlot() {

		return slot;
	}

	public Double giveChange(double calculateChange1) {
		calculateChange = (calculateChange1 * 100);
		numberOfQuarters = (int) (calculateChange / 25);
		calculateChange = calculateChange - (numberOfQuarters * 25);
		numberOfDimes = (int) (calculateChange / 10);
		calculateChange = calculateChange - (numberOfDimes * 10);
		numberOfNickels = (int) (calculateChange / 5);

		calculateChange1 = 0;
		System.out.println("Your change is " + numberOfQuarters + " Quarters " + numberOfDimes + " Dimes "
				+ numberOfNickels + " Nickels ");
		return calculateChange1;

	}

	public void updateLogDeposit(double depositAmount, double currentBalance) throws FileNotFoundException {

		LocalDate today = LocalDate.now();
		LocalTime now = LocalTime.now();
		String printToday = today.toString();
		String printTime = now.toString();

		auditWriter = new PrintWriter(new FileOutputStream("audit.txt", true));
		auditWriter.println(printTime + " " + printToday + " FEED AMOUNT: " + depositAmount + " Ending Balance: " + currentBalance);
		auditWriter.close();
	}

	public void updateLogPurchase(String productName, String key, double startingBalance, double currentBalance)
			throws FileNotFoundException {
		LocalDate today = LocalDate.now();
		LocalTime now = LocalTime.now();
		String printToday = today.toString();
		String printTime = now.toString();

		auditWriter = new PrintWriter(new FileOutputStream("audit.txt", true));
		auditWriter.println(printTime + " " + printToday + " " + productName + " " + key + " Starting Balance: " + startingBalance + " Ending Balance: " + currentBalance);
		auditWriter.close();

	}

	public void updateLogMakeChange(double currentBalance) throws FileNotFoundException {
		LocalDate today = LocalDate.now();
		LocalTime now = LocalTime.now();
		String printToday = today.toString();
		String printTime = now.toString();
		
		auditWriter = new PrintWriter(new FileOutputStream("audit.txt", true));
		auditWriter.println(printTime + " " + printToday + " MAKE CHANGE: " + currentBalance + " Ending Balance: 0");
		auditWriter.close();
	}

	public static void generateSales(Double monies) {
		 for (String key : slotStockMap.keySet()) {
		  Product currentProduct = item;
		  String productName = currentProduct.getName();
		  int inventorySold = slotStockMap.get(key);
		  String salesReportLine = String.format("%-22s", productName) + String.format("|%-3s|", inventorySold);
		  salesFile.add(salesReportLine);
		 }
		}

	
	
	
	
	
	
}
