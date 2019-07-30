package com.techelevator.campground;

import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;

import com.techelevator.campground.model.Campground;
import com.techelevator.campground.model.CampgroundDAO;
import com.techelevator.campground.model.Campsite;
import com.techelevator.campground.model.CampsiteDAO;
import com.techelevator.campground.model.Park;
import com.techelevator.campground.model.ParkDAO;
import com.techelevator.campground.model.ReservationDAO;
import com.techelevator.campground.model.jdbc.JDBCCampgroundDAO;
import com.techelevator.campground.model.jdbc.JDBCCampsiteDAO;
import com.techelevator.campground.model.jdbc.JDBCParkDAO;
import com.techelevator.campground.model.jdbc.JDBCReservationDAO;
import com.techelevator.campground.view.Menu;

public class CampgroundCLI {

	private static final String MAIN_MENU_OPTION_PARKS = "View Parks";
	private static final String MAIN_MENU_NEXT_30_DAY_RESERVATIONS = "See Reservations for Next 30 Days";
	private static final String MAIN_MENU_OPTION_EXIT = "Exit";
	private static final String[] MAIN_MENU_OPTIONS = new String[] { MAIN_MENU_OPTION_PARKS,
			MAIN_MENU_NEXT_30_DAY_RESERVATIONS, MAIN_MENU_OPTION_EXIT };

	private static final String PARK_MENU_OPTION_VIEW_CAMPGROUNDS = "View Campgrounds";
	private static final String PARK_MENU_OPTION_SEARCH_FOR_RESERVATION = "Search for Reservation within the Park";
	private static final String PARK_MENU_OPTION_PREVIOUS_SCREEN = "Return to Previous Screen";
	private static final String[] SELECTING_CAMPGROUND = new String[] { PARK_MENU_OPTION_VIEW_CAMPGROUNDS,
			PARK_MENU_OPTION_SEARCH_FOR_RESERVATION, PARK_MENU_OPTION_PREVIOUS_SCREEN };

	private static final String SEARCH_FOR_RESERVATION_IN_CAMPGROUND = "Search for Reservation within a Campground";
	private static final String RETURN_TO_CAMPGROUND_SCREEN = "Return to Previous Screen";
	private static final String[] AVAILABLE_CAMPGROUNDS = new String[] { SEARCH_FOR_RESERVATION_IN_CAMPGROUND,
			RETURN_TO_CAMPGROUND_SCREEN };

	private Menu menu;
	private ParkDAO parkDAO;
	private CampgroundDAO campgroundDAO;
	private CampsiteDAO campsiteDAO;
	private ReservationDAO reservationDAO;

	private Park selectedPark = null;

	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

	boolean mainMenu = false;
	boolean campgroundMenu = false;
	boolean availableReservations = false;
	boolean makeReservationBool = false;

	LocalDate startLocalDate;;
	LocalDate endLocalDate;

	public static void main(String[] args) {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setUrl("jdbc:postgresql://localhost:5432/campground");
		dataSource.setUsername("postgres");
		dataSource.setPassword("postgres1");

		CampgroundCLI application = new CampgroundCLI(dataSource);
		application.run();
	}

	public CampgroundCLI(DataSource datasource) {
		this.menu = new Menu(System.in, System.out);

		parkDAO = new JDBCParkDAO(datasource);
		campgroundDAO = new JDBCCampgroundDAO(datasource);
		campsiteDAO = new JDBCCampsiteDAO(datasource);
		reservationDAO = new JDBCReservationDAO(datasource);
	}

	// controls which menu we are in.
	public void run() {
		displayApplicationBanner();
		mainMenu = true;
		while (mainMenu) {
			if (campgroundMenu == false) {
				mainMenuSelection();
			}
			while (campgroundMenu) {
//				if (makeReservationBool) {
//					reservationOnSite();
//				} else 
				if (availableReservations == false) {
					campgroundSelection();
				}
				while (availableReservations) {
					if (makeReservationBool == false) {
						campgroundAvailability();
					}
					while (makeReservationBool) {
						reservationOnSite();
					}
				}
			}
		}
		displayDepartureBanner();
	}

	// Shows Main Menu (View Parks and Exit)
	private void mainMenuSelection() {
		printHeading("Main Menu");
		String choice = (String) menu.getChoiceFromOptions(MAIN_MENU_OPTIONS);
		if (choice.equals(MAIN_MENU_OPTION_PARKS)) {
			handleParkSearch();
		} else if (choice.equals(MAIN_MENU_NEXT_30_DAY_RESERVATIONS)) {
			handleReservationQuery();
		} else if (choice.equals(MAIN_MENU_OPTION_EXIT)) {
			mainMenu = false;
		} else {
			System.out.println();
			System.out.println("Please select a valid menu option...");
			System.out.println();
		}
	}

	// Shows Park List to choose a park. Used in mainMenuSelection()
	private void handleParkSearch() {
		printHeading("Select a park for further details");

		List<Park> allParks = parkDAO.getAllParks();

		if (allParks.size() > 0) {
			int option = 1;
			for (Park park : allParks) {
				System.out.println(option + ". " + park.getPark());
				option++;
			}
			String parkSelection = getUserInput("\nPlease select a park");

			option = Integer.parseInt(parkSelection);

			if (option >= 1 && option <= allParks.size()) {
				selectedPark = allParks.get(option - 1);
				System.out.println("\nPark Information Screen");
				//redundant call
				System.out.println(parkDAO.parkInformation(selectedPark));
				campgroundMenu = true;
			} else {
				System.out.println("\nPlease select a valid park option...\n");
			}
		} else {
			System.out.println("\n*** No results ***");
		}
	}

	// Shows 30 days worth of Reservations. Used in mainMenuSelection()
	private void handleReservationQuery() {
		System.out.println("\nReservations for the Next 30 Days Sorted by Park");
		System.out.println(reservationDAO.reservationsNext30Days());
	}

	// Can choose to show campgrounds or search for a campsite within the selected
	// park.
	private void campgroundSelection() {
		printHeading("Select a Campground Command");
		String choice = (String) menu.getChoiceFromOptions(SELECTING_CAMPGROUND);
		if (choice.equals(PARK_MENU_OPTION_VIEW_CAMPGROUNDS)) {
			availableReservations = true;
		} else if (choice.equals(PARK_MENU_OPTION_SEARCH_FOR_RESERVATION)) {
			while (selectedPark.getParkId() > 0) {
				System.out.println("\nSearch for a reservation within the park");
				String startDate = getUserInput("What is the arrival date? (mm/dd/yyyy) ");
				try {
					startLocalDate = LocalDate.parse(startDate, formatter);
					if (startLocalDate.isAfter(LocalDate.now())) {
						String endDate = getUserInput("What is the departure date? (mm/dd/yyyy) ");

						endLocalDate = LocalDate.parse(endDate, formatter);
						if (startLocalDate.isAfter(LocalDate.now())) {
							if (endLocalDate.isAfter(startLocalDate)) {
								List<Campsite> availableSites = campsiteDAO.availableCampsitesInPark(selectedPark,
										startLocalDate, endLocalDate);
								if (availableSites.size() > 0) {
									System.out.println("\nResults Matching Your Search Criteria");
									System.out.println(campsiteDAO.printAvailableSitesInPark(selectedPark,
											startLocalDate, endLocalDate));
									makeReservationBool = true;
								} else {
									System.out.println(
											"\nSorry, no sites are available for your entered dates in your choosen park.");
									System.out.println("Please enter new dates.");
									System.out.println();
								}
							} else {
								System.out.println("\nYour departure date is before your arrival date.");
								System.out.println("Please enter new dates.");
							}
						}

					} else {
						System.out.println("\nReservations cannot be made for past dates.");
						System.out.println("Please enter new dates.");
					}
				} catch (Exception e) {
					System.out.println("\nPlease enter a valid date!");
				}
			}
		} else if (choice.equals(PARK_MENU_OPTION_PREVIOUS_SCREEN)) {
			campgroundMenu = false;
		}
	}

	// Find and prints out list of campsites within selected campground.
	private void campgroundAvailability() {
		printHeading("Select a Command");
		System.out.println(campgroundDAO.campgroundInformation(selectedPark));
		String choice = (String) menu.getChoiceFromOptions(AVAILABLE_CAMPGROUNDS);
		long campgroundNumber = 0;

		List<Long> results = campgroundDAO.campgroundIdList(selectedPark);

		int maxOcc = 0;
		String accessible = "";
		String rvResponse = "";
		int rvLength = 0;
		String utilities = "";

		boolean campgroundSelectionArrivalDate = false;
		boolean campgroundSelectionDepartDate = false;
		boolean maxOccTest = false;
		boolean rvAccessible = false;
		boolean rvYes = false;
		boolean rvUtil = false;
		boolean wheelChair = false;

		if (choice.equals(SEARCH_FOR_RESERVATION_IN_CAMPGROUND)) {
			System.out.println("\nSearch for a reservation within the campground");
			boolean campgroundSelectionSite = true;
			while (campgroundSelectionSite) {
				try {
					campgroundNumber = Integer.parseInt(getUserInput("Which campground (enter 0 to cancel)? "));
					boolean isThisAvailable = results.contains(campgroundNumber);
					if (campgroundNumber == 0) {
						campgroundMenu = true;
						break;
					} else if (!isThisAvailable) {
						System.out.println("\nPlease select a valid campground!");
					} else {

						while (!campgroundSelectionArrivalDate) {
							String startDate = getUserInput("What is the arrival date? (mm/dd/yyyy) ");
							try {
								startLocalDate = LocalDate.parse(startDate, formatter);
								if (!campgroundDAO.isCampgroundOpen(campgroundNumber, startLocalDate)) {
									System.out.println(
											"\nThe campground you selected is not open for your choosen date.");
									System.out.println("Please enter a new date.\n");
								} else if (startLocalDate.isBefore(LocalDate.now())) {
									System.out.println("\nThe date you entered is invalid.");
									System.out.println("Please enter a valid date.\n");
								} else {
									while (!campgroundSelectionDepartDate) {
										String endDate = getUserInput("What is the departure date? (mm/dd/yyyy) ");
										try {
											endLocalDate = LocalDate.parse(endDate, formatter);
											if (endLocalDate.isBefore(startLocalDate)) {
												System.out.println(
														"\nYou entered a departure date that is before your arrival date.");
												System.out.println("Please enter new dates.\n");
											} else if (!campgroundDAO.isCampgroundOpen(campgroundNumber,
													endLocalDate)) {
												System.out.println(
														"\nThe campground you selected is not open for your choosen date.");
												System.out.println("Please enter a new date.\n");
											} else {
												while (!maxOccTest) {
													try {
														maxOcc = Integer.parseInt(getUserInput(
																"How many people will be staying? (max to 55) "));
														if (maxOcc < 1 || maxOcc > 55) {
															System.out.println(
																	"\nThat is not a valid number of peoples (1 through 55).");
														} else {
															while (!wheelChair) {

																accessible = getUserInput(
																		"Do you need a wheelchair accessible campsite? (Y/N) ");
																accessible = accessible.toUpperCase();

																if (accessible.contentEquals("Y")
																		|| accessible.contentEquals("N")) {

																	while (!rvAccessible) {
																		rvResponse = getUserInput(
																				"Will you have an RV? (Y/N) ");
																		rvResponse = rvResponse.toUpperCase();
																		if (rvResponse.contentEquals("Y")
																				|| rvResponse.contentEquals("N")) {

																			if (rvResponse.contentEquals("Y")) {

																				while (!rvYes) {
																					try {
																						rvLength = Integer
																								.parseInt(getUserInput(
																										"RV length required? (max 35) (Y/N)"));
																						if ((rvLength < 0)
																								|| (rvLength > 35)) {
																							System.out.println(
																									"Please enter a valid length.");
																						} else {
																							rvYes = true;
																						}
																					} catch (NumberFormatException e) {
																						System.out.println(
																								"\nThat is not a valid number, try again.");
																					}
																				}
																			}
																			while (!rvUtil) {
																				utilities = getUserInput(
																						"Do you need utilities at your campsite? (Y/N) ");
																				utilities = accessible.toUpperCase();

																				if (utilities.contentEquals("Y")
																						|| utilities
																								.contentEquals("N")) {
																					Campground selectedCampground = campgroundDAO
																							.getCampgroundByCampgroundId(
																									campgroundNumber);
																					List<Campsite> availableSites = campsiteDAO
																							.advancedSearchResults(
																									selectedCampground,
																									startLocalDate,
																									endLocalDate,
																									maxOcc, accessible,
																									rvLength,
																									utilities);
																					int stayLength = reservationDAO
																							.stayLength(startLocalDate,
																									endLocalDate);
																					campgroundSelectionSite = true;
																					campgroundSelectionArrivalDate = true;
																					campgroundSelectionDepartDate = true;
																					maxOccTest = true;
																					rvAccessible = true;
																					rvYes = true;
																					rvUtil = true;
																					wheelChair = true;
																					campgroundMenu = true;
																					availableReservations = false;
																					
																					if (availableSites.size() > 0) {
																						System.out.println(
																								"\nResults Matching Your Search Criteria");
																						System.out.println(campsiteDAO
																								.printAdvancedResults(
																										availableSites,
																										stayLength));
																						campgroundNumber = 0;
																						makeReservationBool = true;
																						campgroundMenu = true;
																						availableReservations = false;
																						campgroundSelectionSite = true;
																						campgroundSelectionArrivalDate = true;
																						campgroundSelectionDepartDate = true;
																						maxOccTest = true;
																						rvAccessible = true;
																						rvYes = true;
																						rvUtil = true;
																						wheelChair = true;
																					} else {
																						System.out.println(
																								"\nSorry, no sites are available for your entered dates at your choosen campground.");
																						System.out.println(
																								"Please enter new dates.\n");
																					}
																				} else {
																					System.out.println(
																							"That is not a valid answer.");
																				}
																			}
																		} else {
																			System.out.println(
																					"\nWhat is not a valid answer, try again.");
																		}
																	}
																} else {
																	System.out.println("That is not a valid answer.");
																}
															}
														}
													} catch (NumberFormatException e) {
														System.out.println("\nThat is not a valid number, try again.");
													}
												}
											}
										} catch (NullPointerException e) {
											System.out.println("\nPlease enter a valid date!");
										}
									}
								}
							} catch (NullPointerException e) {
								System.out.println("\nPlease enter a valid date!");
							}
						}
					}

				} catch (Exception e) {
					System.out.println();
					System.out.println("\nPlease enter a valid campground");
				}
				
				
			}

//			// WILL NEED TO CREATE AN IF STATEMENT HERE TO CHECK FOR INVALID OPTION
//			boolean campgroundSelectionArrivalDate = false;
//			while (!campgroundSelectionArrivalDate) {
//				String startDate = getUserInput("What is the arrival date? (mm/dd/yyyy) ");
//				try {
//					startLocalDate = LocalDate.parse(startDate, formatter);
//					
//					if (!campgroundDAO.isCampgroundOpen(campgroundNumber, startLocalDate)) {
//						System.out.println("\nThe campground you selected is not open for your choosen date.");
//						System.out.println("Please enter a new date.\n");
//					} else if (startLocalDate.isBefore(LocalDate.now())) {
//						System.out.println("\nThe date you entered is invalid.");
//						System.out.println("Please enter a valid date.\n");
//					} else {
//						campgroundSelectionArrivalDate = true;
//					}
//				} catch (NullPointerException e) {
//					System.out.println("\nPlease enter a valid date!");
//				}
//				
//			}
//
//			boolean campgroundSelectionDepartDate = false;
//			while (!campgroundSelectionDepartDate) {
//				String endDate = getUserInput("What is the departure date? (mm/dd/yyyy) ");
//				try {
//					endLocalDate = LocalDate.parse(endDate, formatter);
//					if (endLocalDate.isBefore(startLocalDate)) {
//						System.out.println("\nYou entered a departure date that is before your arrival date.");
//						System.out.println("Please enter new dates.\n");
//					}
//				} catch (NullPointerException e) {
//					System.out.println("\nPlease enter a valid date!");
//				}
//				if (!campgroundDAO.isCampgroundOpen(campgroundNumber, endLocalDate)) {
//					System.out.println("\nThe campground you selected is not open for your choosen date.");
//					System.out.println("Please enter a new date.\n");
//				}
//			}
//
//			int maxOcc = 0;
//			boolean maxOccTest = false;
//			while (!maxOccTest) {
//				try {
//					maxOcc = Integer.parseInt(getUserInput("How many people will be staying? (max to 55) "));
//					if (maxOcc < 1 || maxOcc > 55) {
//						System.out.println("\nThat is not a valid number of peoples (1 through 55).");
//					} else {
//						maxOccTest = true;
//					}
//				} catch (NumberFormatException e) {
//					System.out.println("\nThat is not a valid number, try again.");
//				}
//			}
//			
//			String accessible = "";
//			boolean rvAccessible = false;
//			while (!rvAccessible) {
//
//				accessible = getUserInput("Will you have an RV? (Y/N) ");
//				accessible = accessible.toUpperCase();
//
//				if (accessible.contentEquals("Y") || accessible.contentEquals("N")) { 
//					rvAccessible = true;
//				} else {
//					System.out.println("That is not a valid answer.");
//				}
//			}
//			
//			String rvResponse = "";
//			int rvLength = 0;
//
//			boolean rvOkay = false;
//			while (!rvOkay) {
//				
//				rvResponse = getUserInput("Will you have an RV? (Y/N) ");
//				rvResponse = rvResponse.toUpperCase();
//				
//				if (rvResponse.contentEquals("Y")) { 
//					
//					boolean rvYes = false;
//					while (!rvYes) { 
//						try {
//							rvLength = Integer.parseInt(getUserInput("RV length required? (max 35) (Y/N)"));
//							if ((rvLength > 0) && (rvLength <= 35)) {
//								System.out.println("Please enter a valid length.");
//							} else {
//								rvYes = true;
//							}
//						} catch (NumberFormatException e) { 
//							System.out.println("\nThat is not a valid number, try again.");
//						}
//					}
//					rvOkay = true;
//				} else if (rvResponse.contentEquals("N")) { 
//					rvOkay = true;
//				} else {
//					System.out.println("\nWhat is not a valid answer, try again.");
//				}
//			}
//
//			String utilities = "";
//			boolean rvUtil = false;
//			while (!rvUtil) {
//
//				utilities = getUserInput("Do you need utilities at your campsite? (Y/N) ");
//				utilities = accessible.toUpperCase();
//
//				if (utilities.contentEquals("Y") || utilities.contentEquals("N")) { 
//					rvUtil = true;
//				} else {
//					System.out.println("That is not a valid answer.");
//				}
//			}
//
//			Campground selectedCampground = campgroundDAO.getCampgroundByCampgroundId(campgroundNumber);
//			List<Campsite> availableSites = campsiteDAO.advancedSearchResults(selectedCampground, startLocalDate,
//					endLocalDate, maxOcc, accessible, rvLength, utilities);
//			int stayLength = reservationDAO.stayLength(startLocalDate, endLocalDate);
//
//			if (availableSites.size() > 0) {
//				System.out.println("\nResults Matching Your Search Criteria");
//				System.out.println(campsiteDAO.printAdvancedResults(availableSites, stayLength));
//				campgroundNumber = 0;
//				makeReservationBool = true;
//			} else {
//				System.out
//						.println("\nSorry, no sites are available for your entered dates at your choosen campground.");
//				System.out.println("Please enter new dates.\n");
//			}
		} else if (choice.equals(RETURN_TO_CAMPGROUND_SCREEN)) {
			availableReservations = false;
		}
	}

	// Makes reservation for choosen campsite either in park or campground.
	private void reservationOnSite() {
		while (makeReservationBool) {
			int reserveSite = Integer.parseInt(getUserInput("Which site should be reserved (enter 0 to cancel)? "));

			if (reserveSite == 0) {
				makeReservationBool = false;
			} else {
				String nameForReservation = getUserInput("What name should the reservation be made under? ");
				Campsite campsite = campsiteDAO.getCampsiteById(reserveSite);

				if (reservationDAO.makeReservation(campsite, nameForReservation, startLocalDate, endLocalDate)) {
					Long confirmId = reservationDAO.getConfirmId(campsite, nameForReservation, startLocalDate,
							endLocalDate);
					System.out.println("\nThe reservation has been made and the confirmation ID is " + confirmId + ".");
					makeReservationBool = false;
				} else {
					System.out.println("Sorry, an error has occurred and your reservation was not made.");
				}

			}
		}
	}

	/*
	 * SIDE METHODS
	 */
	private void printHeading(String headingText) {
		System.out.println("\n" + headingText);
		for (int i = 0; i < headingText.length(); i++) {
			System.out.print("-");
		}
		System.out.println();
	}

	@SuppressWarnings("resource")
	private String getUserInput(String prompt) {
		System.out.print(prompt + " >>> ");
		return new Scanner(System.in).nextLine();
	}

	private void displayApplicationBanner() {
		System.out.println("                       \n"
				+ "  ______   ______     ______     __  __     ______        ______     __   __     _____        ______     ______     ______    \n"
				+ "/\\  == \\ /\\  __ \\   /\\  == \\   /\\ \\/ /    /\\  ___\\      /\\  __ \\   /\\ \"-.\\ \\   /\\  __-.     /\\  == \\   /\\  ___\\   /\\  ___\\   \n"
				+ "\\ \\  _-/ \\ \\  __ \\  \\ \\  __<   \\ \\  _\"-.  \\ \\___  \\     \\ \\  __ \\  \\ \\ \\-.  \\  \\ \\ \\/\\ \\    \\ \\  __<   \\ \\  __\\   \\ \\ \\____  \n"
				+ " \\ \\_\\    \\ \\_\\ \\_\\  \\ \\_\\ \\_\\  \\ \\_\\ \\_\\  \\/\\_____\\     \\ \\_\\ \\_\\  \\ \\_\\\\\"\\_\\  \\ \\____-     \\ \\_\\ \\_\\  \\ \\_____\\  \\ \\_____\\ \n"
				+ "  \\/_/     \\/_/\\/_/   \\/_/ /_/   \\/_/\\/_/   \\/_____/      \\/_/\\/_/   \\/_/ \\/_/   \\/____/      \\/_/ /_/   \\/_____/   \\/_____/ \n"
				+ "                                                                                                                                                                \n"
				+

				"..,.,.,,.,.,.,,.,._,,,____,,,...,.,.,,.....,....,.,..,,.  ,-=--'\\\\_\\/\n"
				+ "\"\"\"\"\"\"\"\"\"\"############z_ _`\"\"\"########################' ,---'>__,>,_`-.     |\n"
				+ "       :  |  `\"\"\"V#######,,_ `-  \"\"##################' --z--;\" /_/  `. `.   |\n"
				+ "          |          `/\"\"\"\".`|`|| } }|.\"\"\"\"\"\"\"\"|\"\"\"\"\"  --'//`/'  `    \\  '. |\n"
				+ "    :          :      |:     ||   |  |  :   :  |   :   ,_\\---_\\._   :  `.\\ |/\n"
				+ "                   :  /  :   |  |   || :  :      :    //--'> ___ ``-,_   \\  \\\n"
				+ "         `\"^            :  ` ||   || |   :  :         '=-`',' / `-, __`-. |\n"
				+ "      :    :          : :  : |  |    ||    :     ---  //7;<\\     / ,--._ ` |\n"
				+ "         .,      :        :  |   ||| || '       :     -/;\\'/` -='/|(    \\ \\\n"
				+ "        %#'            :    `| |||    |  :   :      :  // '\\   // | `    | |\n"
				+ "    :         :   `'   :  :  || # | |||    :            `    .        :  | \n"
				+ "                         :   | ||#|#| | ':    :    :             :       ` \n"
				+ "         '#\"      :   :    : | ||,|, ||   :      /        :           |:  ||\n"
				+ "  \"\"'                    :  \\\\|\\ X XX///`      :|   :    |   :      : |   |\n"
				+ "        :             :  / >\\\\> <\\/\\< </</==::_ |' ,`  ,.|_____|______|`--|\n"
				+ "   :         :   |    ,''{` /\"^\"/'/\\^\\'\"\\ --  \"\"\"==;:zz,,_;__ ,; : , ,,hjm|\n"
				+ "        |'       |`--', ',`--,,_    -_ --.  `--.     __ \"\"`\"=;;--=--=;;-==||\n"
				+ "        |    ,,--' ''  ,` ,',' ,'`--,._    -=-  /%%\\.___     ---          |\n"
				+ "  :     |---'  '  ' ,' ,`` , .'`.,,` , ';'--,._   `-=='  ~~  __ __ _,  ~~ |\n"
				+ "     :,-',' '` `` ,  .`, ,',' ', ` , ,','',`, `'\"`--,,_____..__          / |\n"
				+ ",,---',  ' ' , , '  ', , ` ' ,' `, ,  , ` '` ',`,`,` ,',' ',, ,`\"\"'--~',' /\n"
				+ "               ");
		System.out.println();
	}

	private void displayDepartureBanner() {
		System.out.println();
		System.out.println("Thanks for using the Park Reservation System!");
		System.out.println();

		System.out.println(
				" ____ ____ ____ _________ ____ ____ _________ ____ ____ ____ ____ ____ ____ ____ \n" + 
				"||S |||e |||e |||       |||y |||a |||       |||a |||r |||o |||u |||n |||d |||! ||\n" + 
				"||__|||__|||__|||_______|||__|||__|||_______|||__|||__|||__|||__|||__|||__|||__||\n" + 
				"|/__\\|/__\\|/__\\|/_______\\|/__\\|/__\\|/_______\\|/__\\|/__\\|/__\\|/__\\|/__\\|/__\\|/__\\|"
				+ "(                 ,&&&.\n" + 
				"            )                .,.&&\n" + 
				"           (  (              \\=__/\n" + 
				"               )             ,'-'.\n" + 
				"         (    (  ,,      _.__|/ /|\n" + 
				"          ) /\\ -((------((_|___/ |\n" + 
				"        (  // | (`'      ((  `'--|\n" + 
				"      _ -.;_/ \\\\--._      \\\\ \\-._/.\n" + 
				"     (_;-// | \\ \\-'.\\    <_,\\_\\`--'|\n" + 
				"     ( `.__ _  ___,')      <_,-'__,'\n" + 
				"      `'(_ )_)(_)_)'"
				);


	}
}
