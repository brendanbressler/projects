package com.techelevator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import com.techelevator.view.Menu;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

public class VendingMachineCLI {

	private Scanner userInput = new Scanner(System.in);
	public VendingMachine machine = new VendingMachine();
	private boolean purchaseDone = false;
	private Double total = 0.00;
	private static List<String> salesFile = new ArrayList<String>(); 

	private static final String MAIN_MENU_OPTION_DISPLAY_ITEMS = "Display Vending Machine Items";
	private static final String MAIN_MENU_OPTION_PURCHASE = "Purchase";
	private static final String MAIN_MENU_OPTION_INSERT_MONEY = "Insert Monies";
	private static final String MAIN_MENU_OPTION_ENTER_SLOT = "Choose a Slot";
	private static final String MAIN_MENU_OPTION_EXIT_PURCHASE_MENU = "Finish Transaction";
	private static final String MAIN_MENU_OPTION_SALES_REPORT = "    ";
	static String secret = MAIN_MENU_OPTION_SALES_REPORT;
	private static final String[] MAIN_MENU_OPTIONS = { MAIN_MENU_OPTION_DISPLAY_ITEMS, MAIN_MENU_OPTION_PURCHASE,secret
			/*MAIN_MENU_OPTION_EXIT*/ }; 
	private static final String[] PURCHASE_MENU_OPTIONS = { MAIN_MENU_OPTION_INSERT_MONEY, MAIN_MENU_OPTION_ENTER_SLOT,
															MAIN_MENU_OPTION_EXIT_PURCHASE_MENU, };

	private Menu menu;

	public VendingMachineCLI(Menu menu) {
		this.menu = menu;
	}

	public void run() throws IOException {
		Iterator<String> itr = machine.getProductMap().keySet().iterator();
		String k = itr.next();
		Product p = machine.getProductMap().get(k);
		String type = p.getType();

		System.out.println("Welcome to the Vendor-Bot 5000\u2122! I sure hope your're hungry (and, or thirsty)!");

		
		System.out.println("|############################################|\n" + 
				"|#|                           |##############|\n" + 
				"|#|  =====  ..--''`  |~~``|   |##|````````|##|\n" + 
				"|#|  |   |  \\     |  :    |   |##| Vendor |##|\n" + 
				"|#|  |___|   /___ |  | ___|   |##|Bot 5000|##|\n" + 
				"|#|  /=__\\  ./.__\\   |/,__\\   |##| $$$$   |##|\n" + 
				"|#|  \\__//   \\__//    \\__//   |##|________|##|\n" + 
				"|#|===========================|##############|\n" + 
				"|#|```````````````````````````|##############|\n" + 
				"|#| =.._      +++     //////  |##############|\n" + 
				"|#| \\/  \\     | |     \\    \\  |#|`````````|##|\n" + 
				"|#|  \\___\\    |_|     /___ /  |#| _______ |##|\n" + 
				"|#|  / __\\\\  /|_|\\   // __\\   |#| |1|2|3| |##|\n" + 
				"|#|  \\__//-  \\|_//   -\\__//   |#| |4|5|6| |##|\n" + 
				"|#|===========================|#| |7|8|9| |##|\n" + 
				"|#|```````````````````````````|#| ``````` |##|\n" + 
				"|#| ..--    ______   .--._.   |#|[=======]|##|\n" + 
				"|#| \\   \\   |    |   |    |   |#|  _   _  |##|\n" + 
				"|#|  \\___\\  : ___:   | ___|   |#| ||| ( ) |##|\n" + 
				"|#|  / __\\  |/ __\\   // __\\   |#| |||  `  |##|\n" + 
				"|#|  \\__//   \\__//  /_\\__//   |#|  ~      |##|\n" + 
				"|#|===========================|#|_________|##|\n" + 
				"|#|```````````````````````````|##############|\n" + 
				"|############################################|\n" + 
				"|#|||||||||||||||||||||||||||||####```````###|\n" + 
				"|#||||||||||||PUSH|||||||||||||####\\|||||/###|\n" + 
				"|############################################|\n");




		    // open the sound file as a Java input stream
		    String gongFile = "/Users/student/Music/sound.wav";
		    InputStream in = new FileInputStream(gongFile);

		    // create an audiostream from the inputstream
		    AudioStream audioStream = new AudioStream(in);

		    // play the audio clip with the audioplayer class
		    AudioPlayer.player.start(audioStream);
		  

		
		
		
		
		
		
		
		
		while (true) {
			String choice = (String) menu.getChoiceFromOptions(MAIN_MENU_OPTIONS);

			if (choice.equals(MAIN_MENU_OPTION_DISPLAY_ITEMS)) {

				System.out.println();

				while (itr.hasNext()) {
					k = itr.next();
					p = machine.getProductMap().get(k);

					System.out.println(k + " " + p);
				}

			} else if (choice.equals(MAIN_MENU_OPTION_SALES_REPORT)) {
			    VendingMachine.generateSales(total);
			    getSalesReport(total);
			    System.out.println("Amount sold and total sales saved to Sales Report.");
			   }  
			else if (choice.equals(MAIN_MENU_OPTION_PURCHASE)) {
				Double monies = 0.00;

				while (purchaseDone == false) {
					String purchaseChoice = (String) menu.getChoiceFromOptions(PURCHASE_MENU_OPTIONS);

					if (purchaseChoice.equals(MAIN_MENU_OPTION_INSERT_MONEY)) {

						System.out.print("Insert currency (bills face up): ");
						String moneyInput = userInput.nextLine();
						monies = Double.parseDouble(moneyInput);
						System.out.format("%-22s %-4.2f", "Current monies available:", monies);
						System.out.println();
						machine.updateLogDeposit(monies, machine.getVendingMachineTotal());

					} else if (purchaseChoice.equals(MAIN_MENU_OPTION_ENTER_SLOT)) {

						System.out.print("Please enter the slot of the desired item: ");
						String slotChoice = userInput.nextLine();

						if (monies <= 0) {
							System.out.println("Items aren't free! Please insert currency!");
						} else {

							Product item = machine.getProductMap().get(slotChoice);
							
							item.removeItem();
							monies -= item.getPrice();
							total += item.getPrice();
						
							
							System.out.println(item.getMessage());
							System.out.format("%-22s %-4.2f", "Current monies available:", monies);
							System.out.println();
							System.out.println(item.displayQuantity());
							
							machine.updateLogPurchase(item.getName(), slotChoice, item.getPrice() + machine.getVendingMachineTotal(), machine.getVendingMachineTotal());
				
							
							
							
							
						}

					}
					else if (purchaseChoice.equals(MAIN_MENU_OPTION_EXIT_PURCHASE_MENU)) {
						Double change = machine.giveChange(monies);
						machine.updateLogMakeChange(machine.getVendingMachineTotal());
						purchaseDone = true;
						System.out.println(change);

					}
				}
			} 
		}
	}
	public void getSalesReport(Double total) {
		 String fileName = LocalDate.now() + " SalesReport.txt";
		 File destFile = new File(fileName);
		 try (PrintWriter writer = new PrintWriter(destFile)) {
		  if (destFile.createNewFile() || destFile.exists()) {
		   for (int i = 0; i < salesFile.size(); i++) {
		    writer.println(salesFile.get(i));
		   }
		   writer.println();
		   writer.print("***TOTAL SALES*** $");
		   writer.format("%.2f", total);

		   
		  } else {
		   System.out.println("Sales file to print to not found!");
		  }
		 } catch (FileNotFoundException e) {
		  System.out.println("File not found!");
		 } catch (IOException e) {
		  System.out.println("Can't create new file.");
		 }
		}
	
	
	
	
	public static void main(String[] args) throws IOException {
		Menu menu = new Menu(System.in, System.out);
		VendingMachineCLI cli = new VendingMachineCLI(menu);
		cli.run();
	}
}
