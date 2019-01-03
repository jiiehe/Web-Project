package cs5530;


import jdk.nashorn.internal.runtime.ECMAException;

import java.lang.*;
import java.sql.*;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * class that provides the cmd interface for the DBApi
 * */
public class CommandLineInterface {

	private static String userLogin;
	private static String adminUser = "Admin";
	private static List<String> categories = Arrays.asList("SUV", "Sedan", "Compact", "Truck");

	/**
	 * This method ensures that a user gives a valid category
	 * */
	private String getValidCategoryFromUser(){
		String category = "";
		Scanner scanner = new Scanner(System.in);
		category = scanner.nextLine();

		while(!categories.contains(category)){
			System.out.print("Valid categories include: ");
			for (String cat : categories) {
				System.out.print(" " + cat);
			}
			System.out.println();
			category = scanner.nextLine();
		}

		return category;
	}

	/**
	 * this method ensures that a user gives a valid vin number
	 * */
	private Integer getValidVinFromUser(Connector conn){
		DBApi api = new DBApi();
		Integer loopCount = 0;
		Integer vin = getNumberFromUser();

		while(!api.uberCarExisits(conn.stmt, vin)){
			loopCount += 1;
			System.err.println("That car does not exist. Try again");
			if(loopCount > 3){
				System.err.println("Figure out a proper vin then try again.");
				return -1;
			}
			vin = getNumberFromUser();
		}
		return vin;
	}

	/**
	 * this method properly gets a max list number for multiple methods
	 * */
	private Integer getMaxNumber() {
		Scanner scanner = new Scanner(System.in);
		Integer number = 0;
		String numberString = "";

		while(number == 0) {
			numberString = scanner.nextLine();
			try {
				number = Integer.valueOf(numberString);
				if(number == 0) {
					System.err.println("You must enter a non-zero number");
				}

			}catch(NumberFormatException e){
				System.err.println("You must enter a number");
			}
		}
		return number;
	}

	/**
	 * This method gets any valid number from the user
	 * */
	private Integer getNumberFromUser() {
		Scanner scanner = new Scanner(System.in);
		Integer number = 0;
		
		while(true) {
			try {
				number = scanner.nextInt();
				break;
			}
			catch(Exception e){
				System.err.println("You must enter a number");
				scanner.next();
			}
		}
		return number;
	}

	/**
	 * This method shows the most valuable users by trustworthyness and usefulness
	 * */
	public void userAwards(Connector conn) {

		if(!userLogin.equals("admin")){
			System.err.println("You must create and be logged in as a user called \"admin\" to use this command");
			return;
		}

		DBApi api = new DBApi();
		Scanner scanner = new Scanner(System.in);
		ResultSet result = null;
		Integer listSizePerCategory = 0;

		System.out.println("You will see the most trusted users as well as the most useful users. Please enter the max number of each you want to see");

		listSizePerCategory = getMaxNumber();

		result = api.mostTrustedUsers(conn.stmt, listSizePerCategory);
		System.out.println("Most trusted users:");
		try {

			while(result.next()) {
				System.out.println(result.getString("login2")
						+ " " + result.getString("trustScore"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Failed to print results");
		}

		result = api.mostUsefulUsers(conn.stmt, listSizePerCategory);
		System.out.println("Most useful users:");
		try {

			while(result.next()) {
				System.out.println(result.getString("login")
						+ " " + result.getString("avgUsefulness"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Failed to print results");
		}
		System.out.println("\n------------------------------------");
	}

	/**
	 * This method gathers various statistics for the user logged in
	 * */
	public void statistics(Connector conn) {
		DBApi api = new DBApi();
		Scanner scanner = new Scanner(System.in);
		ResultSet result = null;
		String typeString = "";
		Integer listSizePerCategory = 0;

		System.out.println("Type \"pUC\" for most popular UC's, \"eUC\" for most expensive UC's, or \"rUD\" for highest rated UD's");

		typeString = scanner.nextLine();
		while(!(typeString.equals("pUC") || typeString.equals("eUC") || typeString.equals("rUD"))){
			System.out.println("input must be pUC, eUC, or rUD");
			typeString = scanner.nextLine();
		}

		System.out.println("Please enter the maximum number of stats per category");

		listSizePerCategory = getMaxNumber();

		result = api.statistics(conn.stmt, typeString, listSizePerCategory);

		if(result != null) {
			System.out.println(typeString + " statistics:");
			if(typeString.equals("pUC")){
				String login, category, make, model, curCategory = "";
				Integer categoryCount = 0;
				//for each category, show the maximum number of stats
				try {
					while (result.next()) {
						login = result.getString("login");
						category = result.getString("category");
						make = result.getString("make");
						model = result.getString("model");

						//if we have aleady met the max category count
						if (!curCategory.equals(category)) {
							curCategory = category;
							categoryCount = 1;
						}
						if (categoryCount > listSizePerCategory){
							continue;
						}
						System.out.println(login + " " + category + " " + make + " " + model);
						categoryCount += 1;
					}
				}catch (Exception e){
					e.printStackTrace();
					System.out.println("Failed to print results");
				}
			}
			else if(typeString.equals("eUC")){
				String login, category, make, model, avgCost, curCategory = "";
				Integer categoryCount = 0;
				//for each category, show the maximum number of stats
				try {
					while (result.next()) {
						login = result.getString("login");
						category = result.getString("category");
						make = result.getString("make");
						model = result.getString("model");
						avgCost = result.getString("avgCost");

						//if we have aleady met the max category count
						if (!curCategory.equals(category)) {
							curCategory = category;
							categoryCount = 1;
						}
						if (categoryCount > listSizePerCategory){
							continue;
						}
						System.out.println(login + " " + category + " " + make + " " + model + " " + avgCost);
						categoryCount += 1;
					}
				}catch (Exception e){
					e.printStackTrace();
					System.out.println("Failed to print results");
				}
			}
			else if(typeString.equals("rUD")){
				String login, category, averageFeedback, curCategory = "";
				Integer categoryCount = 0;
				//for each category, show the maximum number of stats
				try {
					while (result.next()) {
						login = result.getString("login");
						category = result.getString("category");
						averageFeedback = result.getString("averageFeedback");

						//if we have aleady met the max category count
						if (!curCategory.equals(category)) {
							curCategory = category;
							categoryCount = 1;
						}
						if (categoryCount > listSizePerCategory){
							continue;
						}
						System.out.println(login + " " + category + " " + averageFeedback);
						categoryCount += 1;
					}
				}catch (Exception e){
					e.printStackTrace();
					System.out.println("Failed to print results");
				}
			}
		}
		else {
			System.out.println("No results matched your criteria.");
			return;
		}
		System.out.println("\n------------------------------------");
	}

	/**
	 * This method obtains the degrees of separation of two users using dbapi
	 * */
	public void degreesOfSeparation(Connector conn) {
		DBApi api = new DBApi();

		if(!api.isUberUser(conn.stmt, userLogin)){
			System.err.println("Only Uber Users can have degrees of separation");
			return;
		}

		Scanner scanner = new Scanner(System.in);
		String specifiedUser = "";
		System.out.println("Please type the user you want to check");

		while((specifiedUser = scanner.nextLine()).length() == 0){
			if(specifiedUser.length()==0){
				System.err.println("that is not a valid username");
			}
		}
		Integer result = api.degreesOfSeparation(conn.stmt, userLogin, specifiedUser);
		if (result == 0){
			System.out.println("No degrees of separation for the specified user");
		}
		else {
			System.out.println("Degrees of separation: " + result);
		}
		System.out.println("\n------------------------------------");
	}

	/**
	 * this method gives suggestions based on other uber users
	 * */
	public ResultSet ucSuggestions(Connector conn, String userMakingReservation, Integer vin) {
		DBApi api = new DBApi();
		return api.ucSuggestions(conn.stmt, userMakingReservation, vin);
	}

	/**
	 * this method obtains the most useful feedbacks for a UD
	 * */
	public void usefulFeedbacks(Connector conn) {
		DBApi api = new DBApi();
		Scanner scanner = new Scanner(System.in);
		ResultSet result = null;
		String numFeedbacksString = "";
		Integer numFeedbacks = 0;
		String driverUser = "";

		System.out.println("Please type the username of the driver you want to review");

		driverUser = scanner.nextLine();
		if(!api.isDriverUser(conn.stmt, driverUser)){
			System.err.println("Not a valid driver");
			return;
		}

		System.out.println("Please enter the maximum number of feedbacks you would like to review");

		numFeedbacks = getMaxNumber();

		result = api.usefulFeedbacks(conn.stmt, driverUser, numFeedbacks);

		if(result != null) {
			System.out.println("feedbacks about " + driverUser + " ----------------------");
			try {
				while (result.next()) {
					System.out.println(result.getString("name")
							+ " " + result.getString("login")
							+ " " + result.getString("score")
							+ " " + result.getString("text")
							+ " " + result.getString("fbdate")
							+ " " + result.getString("usefullnessRating"));
				}
			}catch (Exception e){
				e.printStackTrace();
				System.out.println("Failed to print results");
			}
		}
		else {
			System.out.println("No results matched your criteria.");
			return;
		}
		System.out.println("\n------------------------------------");
	}

	/**
	 * This method shows UC's based on input from the user
	 * */
	public void browseUCs(Connector	conn) {
		DBApi api = new DBApi();
		Scanner scanner = new Scanner(System.in);
		ResultSet result = null;
		String category = "";
		String city = "";
		String state = "";
		String model = "";
		boolean onlyTrustedFeedback = false;

		System.out.println("Please type a category");

		category = getValidCategoryFromUser();

		System.out.println("You may specify a state to narrow the search or you may leave it blank to move on");
		state = scanner.nextLine();

		System.out.println("You may specify a city to narrow the search or you may leave it blank to move on");
		city = scanner.nextLine();

		System.out.println("You may specify a model of car to narrow the search or you may leave it blank to move on");
		model = scanner.nextLine();

		System.out.println("enter 1 if you want the results sorted only by feedback that is trusted by you");
		if(scanner.nextLine().equals("1")) {
			onlyTrustedFeedback = true;
		}

		result = api.browseUCs(conn.stmt, "user1", category, state, city, model, onlyTrustedFeedback);

		if(result != null) {
			System.out.println("Uber cars matching " + category + " " + state + " " + city + " " + model + "---------------");
			try {
				while (result.next()) {
					System.out.println(result.getString("name")
							+ " " + result.getString("city")
							+ " " + result.getString("state")
							+ " " + result.getString("category")
							+ " " + result.getString("make")
							+ " " + result.getString("model")
							+ " " + result.getString("avg"));
				}
			}catch (Exception e){
				e.printStackTrace();
				System.out.println("Failed to print results");
			}
		}
		else {
			System.out.println("No results matched your criteria.");
			return;
		}

		System.out.println("\n------------------------------------");
	}

	/**
	 * This method allows a user to designate another user as trusted or not trustec
	 * */
	public void addTrustedUser(Connector conn) {
		DBApi api = new DBApi();

		if(!api.isUberUser(conn.stmt, userLogin)){
			System.err.println("Only Uber Users may declare other users as trustworthy");
			return;
		}

		Scanner scan = new Scanner(System.in);
		boolean isTrusted = false;
		String otherUser = "";
		String trusted = "";

		System.out.println("Please type another username to designate as trustworthy or not");

		otherUser = scan.nextLine();

		if(!api.isUberUser(conn.stmt, otherUser)){
			System.err.println("Not a valid user");
			return;
		}
		else if(api.isTrusted(conn.stmt, userLogin, otherUser)){
			System.err.println("You have already delclared this user as trusted or not trusted");
			return;
		}

		System.out.println("Type 1 for trusted or 0 for not trusted");

		trusted = scan.nextLine();
		while(!(trusted.equals("1") || !trusted.equals("0"))) {
			System.out.println("Not a valid input, type 1 for trusted or 0 for not trusted");
			trusted = scan.nextLine();
		}

		if(trusted.equals("1")){
			isTrusted = true;
		}

		if(api.addTrustedUser(userLogin, otherUser, isTrusted, conn.stmt)) {
			System.out.println("Success");
		}
		else {
			String output = "Failed to delcare " + otherUser;
			if(isTrusted) {
				output += " as trusted";
			}
			else {
				output += " as not trusted";
			}
			System.out.println(output);
		}
	}

	/**
	 * This allows a user to register as a user or a driver
	 * */
	public void registerStep(Connector conn) throws SQLException {
		System.out.println("\nnow you need to spend some minutes to finish the registration.");
		System.out.println("-----------------------------------------------------------------");
		System.out.println("please choose a type that you desired in Uber: driver or user");
		Scanner scan=new Scanner(System.in);
		String userType;
		while(true){
			userType=scan.nextLine();
			if(userType.length()==0){
				System.err.println("this one can not be empty, please retype and try again");
				continue;
			}else if(!userType.equalsIgnoreCase("user")&&!userType.equalsIgnoreCase("driver")){
				System.err.println("it's not a legal user type, please type driver or user");
				continue;
			}else{
				break;
			}
		}
		System.out.println("please type a username for logging in");
		String login;
		while((login=scan.nextLine()).length()==0){
			if(login.length()==0){
				System.err.println("this is not a valid username");
				continue;
			}
		}
		System.out.println("please type your password");
		String password;
		while((password=scan.nextLine()).length()==0){
			if(password.length()==0){
				System.err.println("this is not a valid password");
				continue;
			}else{
				break;
			}
		}
		//(, , address, city, state, phone, password
		System.out.println("please type a full name");
		String name;
		while((name=scan.nextLine()).length()==0){
			if(name.length()==0){
				System.err.println("this is not a valid username");
				continue;
			}else{
				break;
			}
		}
		//(,  state, phone, password)
		System.out.println("please type your address");
		String address;
		while((address=scan.nextLine()).length()==0){
			if(address.length()==0){
				System.err.println("this is not a valid address");
				continue;
			}else{
				break;
			}
		}
		System.out.println("please type your city");
		String city;
		while((city=scan.nextLine()).length()==0){
			if(city.length()==0){
				System.err.println("this is not a valid city");
				continue;
			}else{
				break;
			}
		}
		System.out.println("please type your state");
		String state;
		while((state=scan.nextLine()).length()==0){
			if(state.length()==0){
				System.err.println("this is not a valid state");
				continue;
			}else{
				break;
			}
		}
		System.out.println("please type your phone");
		String phone;
		while((phone=scan.nextLine()).length()==0){
			if(phone.length()==0){
				System.err.println("this is not a valid phone");
				continue;
			}else{
				break;
			}
		}
		DBApi api=new DBApi();
		if(api.enrollUser(conn.stmt,login,name,address,city,state,phone,password,userType)) {
			System.out.println("---------------------------------------------------");
			System.out.println("----------successful to register-------------------");
			System.out.println("---------------------------------------------------\n\n\n\n");
		}else{
			System.out.println("fail to register, please check the information");
			displayMenu(conn);
		}
	}

	/**
	 * This method allows a user to login
	 * */
	public void loginAsUser(Connector conn){
		System.out.println("--------type 'back' to return to the last menu--------");
		System.out.println("--------type your login name here------------------");
		Scanner scan=new Scanner(System.in);
		String login;

		while((login=scan.nextLine()).length()==0){

			if(login.length()==0){
				System.err.println("username can not be empty");
				continue;
			}
		}
		if(login.equalsIgnoreCase("back")){
			try {
				displayMenu(conn);
				//break;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		System.out.println("--------type 'back' to return the last menu--------");
		System.out.println("--------type your password here------------------");
		String password;
		while((password=scan.nextLine()).length()==0){
			if(password.length()==0){
				System.err.println("password can not be empty");
				continue;
			}else{
				break;
			}
		}
		if(password.equalsIgnoreCase("back")){
			try {
				displayMenu(conn);
				//break;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		DBApi api=new DBApi();
		if(api.checkUser(login,password,conn.stmt)==true){
			userLogin = login;
			System.out.println("--------userLogin successful!!------------------");
			userMenu(conn);
		}else{
			System.err.println("the input information is incorrect.");
			try {
				displayMenu(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * the helper method to make the user add a ride, this should be a past trip, whatever a trip is reserved or the trip is gotten by the uber driver off the internet.
	 * */
	public void addRide(Connector conn){
		DBApi api=new DBApi();

		if(!api.isUberUser(conn.stmt, userLogin)){
			System.err.println("Only Uber Users may add rides");
			return;
		}

		System.out.println("please type a vin number that you want to add the ride");
		Scanner scan=new Scanner(System.in);
		Integer vin;
		String date;
		int cost;
		int start;
		int end;

		if((vin = getValidVinFromUser(conn)) == -1){
			return;
		}


		System.out.println("please type a date you want to add to ride(MM-DD-YYYY)");
		DateFormat df =new SimpleDateFormat("MM-dd-yyyy");
		scan=new Scanner(System.in);
		date=scan.nextLine();
		java.util.Date realDate;
		java.sql.Date sqlDate=null;
		while(true){
			try {
				realDate = df.parse(date);
				sqlDate=new java.sql.Date(realDate.getTime());
				break;
			}catch(Exception e){
				System.err.println("this is not a valid date, type again!");
				continue;
			}
		}
		System.out.println("please type the cost that you spent in the ride");
		scan=new Scanner(System.in);
		while(true){
			try {
				cost = getNumberFromUser();
				break;
			}catch(Exception e){
				System.err.println("cost should be a number please type again");
				continue;
			}
		}
		System.out.println("please type a start time for this ride(a number from 1 to 24)");
		scan=new Scanner(System.in);
		while(true){
			start = getNumberFromUser();
			if(start>=0&&start<=24){
				break;
			}else{
				System.err.println("this is not a valid start time, try again");
				continue;
			}
		}
		System.out.println("please type a end time for your ride(a number from 1 to 24)");
		scan=new Scanner(System.in);
		while(true){
			end = getNumberFromUser();
			if(end>=0&&end<=24&&end>start){
				break;
			}else{
				if(end<start){
					System.err.println("end time have to later than start time, type again");
					continue;
				}
				System.err.println("this is not a valid end time, type again");
				continue;
			}
		}

		if(api.checkAvailability(conn,vin,start,end)){
			/*int vin;
			String date;
			int cost;
			int start;
			int end;*/
			System.out.println(vin + " " + date + " " + cost + " " + start + " " + end);
			System.out.println("are you sure to add the ride? Y/N");
			while(true){
				scan=new Scanner(System.in);
				String choose=scan.nextLine();
				if(choose.equalsIgnoreCase("Y")){
					break;
				}else if(choose.equalsIgnoreCase("N")){
					return;
				}else{
					System.err.println("it's not a valid input, please type again");
				}
			}
		}

		if(api.addRide(cost,sqlDate, userLogin,vin,start,end,conn.stmt)){
				System.out.println("-------successful to add a ride------");
				userMenu(conn);
		}else{
				System.out.println("-------failed to add the ride----");
				userMenu(conn);
		}
	}

	/**
	 * the helper method to make user rate a feedback, we will provides all feedbacks of the current car, and user can get the fid to rate.
	 * he just need to type the fid, and the score he thinks, and then finish the rate.
	 * */
	public void addRate(Connector conn){
		DBApi api=new DBApi();

		if(!api.isUberUser(conn.stmt, userLogin)){
			System.err.println("Only Uber Users may rate feedback");
			return;
		}

		System.out.println("please type a vin number to get all feedback about this car");
		Scanner scan=new Scanner(System.in);
		Integer vin = 0;

		if((vin = getValidVinFromUser(conn)) == -1){
			return;
		}

		ArrayList<feedback> list=api.accessFeedback(vin, userLogin,conn.stmt);
		System.out.println(list.size());
		for(int i=0;i<list.size();i++){
			feedback temp=list.get(i);
			System.out.println("fid: "+temp.fid+"   score: "+temp.score);
			System.out.println("comment: "+temp.text);
		}
		System.out.println("type '-1' to quit the system");
		System.out.println("type a fid that you want to rate");
		scan=new Scanner(System.in);
		int fid;
		while(true){
			try {
				fid = getNumberFromUser();
				break;
			}catch(Exception e){

				System.err.println("this is not a number, retype it");
				scan=new Scanner(System.in);
				continue;
			}
		}
		if(fid==-1){
			System.exit(1);
		}
		System.out.println("type '-1' to quit the system");
		System.out.println("type a rate for this feedback(number 0-2)");
		scan=new Scanner(System.in);
		int rate;
		while(true){
			try {
				rate = getNumberFromUser();
				if(rate!=-1&&rate<0||rate>2){
					System.err.println("this is not a valid number, retype it");
					scan=new Scanner(System.in);
					continue;
				}
				break;
			}catch(Exception e){

				System.err.println("this is not a number, retype it");
				scan=new Scanner(System.in);
				continue;
			}
		}
		if(rate==-1){
			System.exit(1);
		}
		boolean result=api.addRate(userLogin,fid,rate,conn.stmt);
		if(result==true){
			System.out.println("congratulations! successful to add a rate for this feedback. ");
			userMenu(conn);
		}else{
			System.out.println("failed to rate, please check the information you typied");
			userMenu(conn);
		}
	}

	/**
	 * the helper method to make the user provide a feedback , the user need to know the car's vin number.
	 * */
	public void addFeedback(Connector conn){
		Scanner scan=new Scanner(System.in);
		DBApi api=new DBApi();
		Integer loopCount = 0;

		if(!api.isUberUser(conn.stmt, userLogin)){
			System.err.println("Only Uber Users may add feedback");
			return;
		}

		System.out.println("please type a vin number of a car that you want feedback");

		Integer vin = 0;

		if((vin = getValidVinFromUser(conn)) == -1){
			return;
		}

		if(api.hasGivenFeedbackToCar(conn.stmt, userLogin, vin)){
			System.err.println("You have already given feedback on that car");
			return;
		}

		int score;
		System.out.println("please type the score for this car (1-10)");
		scan=new Scanner(System.in);
		while(true){
			try {
				score = getNumberFromUser();
				if(score>=1&&score<=10) {
					break;
				}else{
					System.out.println("this is not a valid score, retype it");
					scan=new Scanner(System.in);
					continue;
				}
			}catch(Exception e){
				System.out.println("this is not a valid score, retype it");
				scan=new Scanner(System.in);
				continue;
			}
		}
		System.out.println("type some extra text about this car");
		String text;
		while (true){
			scan=new Scanner(System.in);
			text=scan.nextLine();
			break;
		}
		if(api.addFeedback(text,vin, userLogin,score,conn.stmt)){
			System.out.println("successfully added a new feedback");
			userMenu(conn);
		}else{
			System.out.println("failed to add a new feedback, check information and try again");
			userMenu(conn);
		}
	}

	/**
	 * this is a helper method to help the user to add his favorite car, the user need to know the vin number of the car that he favorites.
	 * */
	public void addFavorite(Connector conn){
		DBApi api=new DBApi();
		Scanner scan=new Scanner(System.in);

		if(!api.isUberUser(conn.stmt, userLogin)){
			System.err.println("Only Uber Users can add favorite cars");
			return;
		}

		System.out.println("please type a vin number of the car that you want to favorite");

		Integer vin = 0;
		if((vin = getValidVinFromUser(conn)) == -1){
			return;
		}

		if(api.isFavorite(conn.stmt, userLogin, vin)){
			System.err.println("You have already favorited this car");
			return;
		}

		if(api.addFavorite(vin, userLogin,conn.stmt)){
			System.out.println("successfully added favorite car");
			userMenu(conn);
		}else{
			System.out.println("failed to add the car, vin does not exist");
			userMenu(conn);
		}

	}

	/**
	 *the helper method to make the user add a new reservation, this function just provided for a regular user instead of all uber users,
	 * driver can not use this function.
	 * */
	public void makeReservation(Connector conn){
		DBApi api=new DBApi();

		if(!api.isUberUser(conn.stmt, userLogin)){
			System.err.println("Only Uber Users can make reservations");
			return;
		}

		Scanner scan=new Scanner(System.in);
		System.out.println("please type a date you want to make the reservation(MM-DD-YYYY)");
		String date;
		DateFormat df = new SimpleDateFormat("MM-dd-yyyy");
		date=scan.nextLine();
		java.util.Date realDate;
		java.sql.Date sqlDate = null;
		Integer loopCount = 0;
		int start;
		int end;
		int cost;
		int vin;
		while(true){
			try {
				realDate = df.parse(date);
				sqlDate = new java.sql.Date(realDate.getTime());
				break;
			}catch(Exception e){
				System.err.println("this is not a valid date, type again!");
				scan=new Scanner(System.in);
				date=scan.nextLine();
				continue;
			}
		}
		System.out.println("please enter the vin of the car you would like");
		scan=new Scanner(System.in);
		vin = getNumberFromUser();
		while(!api.uberCarExisits(conn.stmt, vin)){
			loopCount += 1;
			System.err.println("That car does not exist. Try again");
			if(loopCount > 3){
				System.err.println("Figure out a proper vin then try again.");
				return;
			}
			vin = getNumberFromUser();
		}
		System.out.println("please type a start time for your trip(a number from 1 to 24)");
		scan=new Scanner(System.in);
		while(true){
			start=getNumberFromUser();
			if(start>=0&&start<=24){
				break;
			}else{
				System.err.println("this is not a valid start time, type again");
				scan=new Scanner(System.in);
				continue;
			}
		}
		System.out.println("please type an end time for your trip(a number from 1 to 24)");
		scan=new Scanner(System.in);
		while(true){
			end=getNumberFromUser();
			if(end>=0&&end<=24&&end>start){
				break;
			}else{
				if(end<start){
					System.err.println("end time have to later than start time, type again");
					scan=new Scanner(System.in);
					continue;
				}
				System.err.println("this is not a valid end time, type again");
				scan=new Scanner(System.in);
				continue;
			}
		}
		System.out.println("please type the cost that you will spend in this trip");
		scan=new Scanner(System.in);
		while(true){
			try {
				cost = getNumberFromUser();
				break;
			}catch(Exception e){
				System.err.println("cost should be a number please type again");
				scan=new Scanner(System.in);
				continue;
			}
		}
		//String date;
		/*int start;
		int end;
		int cost;*/
		System.out.println(date + " " + start + " " + end + " " + cost);
		if(api.carIsAvailable(start, end, sqlDate, vin, conn.stmt)){
			System.out.println("are you sure you want to make the reservaton? Y/N");
			while(true){
				scan=new Scanner(System.in);
				String choose=scan.nextLine();
				if(choose.equalsIgnoreCase("Y")){
					break;
				}else if(choose.equalsIgnoreCase("N")){
					return;
				}else{
					System.err.println("it's not a valid input, please type again");
				}
			}
			if(api.addReservationWorks(conn.stmt, userLogin, cost, sqlDate, vin, start, end)){
				System.out.println("Successfully added Reservation");

				//make suggestions
				ResultSet result = ucSuggestions(conn, userLogin, vin);
				if (result != null) {
					try {
						System.out.println("Other suggested cars you may like:");
						while(result.next()) {
							System.out.println(result.getString("vin")
									+ " " + result.getString("category")
									+ " " + result.getString("make")
									+ " " + result.getString("model")
									+ " " + result.getString("popular"));
						}
					} catch (SQLException e) {
						System.err.println("Failed to show results");
					}
				}
			}
			else {
				System.out.println("Failed to add Reservation");
			}

		}
		else {
			System.err.println("That car is not available during that time.");
			return;
		}

		System.out.println("\n------------------------------------");
	}

	/**
	 * the helper method to make the user add a new Uber car, but it's just provided for a Uber driver, a regular user can not use this function.
	 * */
	public void addNewUC(Connector conn){
		DBApi api = new DBApi();
		if(!api.isDriverUser(conn.stmt, userLogin)){
			System.err.println("You are not an Uber Driver, Access Denied.");
			return;
		}

		System.out.println("please type the vin number of your new car");
		Scanner scan=new Scanner(System.in);
		int vin;
		while(true){
			try {
				vin = getNumberFromUser();
				break;
			}catch(Exception e){
				System.out.println("this is not a number, retype it");
				scan=new Scanner(System.in);
				continue;
			}
		}
		System.out.println("please type the model of your new car");
		String model;
		while(true){
			scan=new Scanner(System.in);
			model=scan.nextLine();
			if(model.length()==0){
				System.err.println("this is not a valid input, please retype it");
				continue;
			}else{
				break;
			}
		}
		System.out.println("please type the make of your new car");
		String make;
		while(true){
			scan=new Scanner(System.in);
			make=scan.nextLine();
			if(make.length()==0){
				System.err.println("this is not a valid input, please retype it");
				continue;
			}else{
				break;
			}
		}
		System.out.println("please type the category of your new car");
		String category;
		category = getValidCategoryFromUser();

		if(api.addNewCar(vin, userLogin,category,make,model,conn.stmt)) {
			System.out.println("successfully added new car!");
			userMenu(conn);
		}else{
			System.out.println("failed to add a new car, recheck the information");
			userMenu(conn);
		}
	}

	/**
	 * if the user is a existed user in our system, then after he successful to login, he will get this menu to make him decide what kind of
	 * operation does he want to do, include make reservation, feedback provided and so on.
	 * */
	public void userMenu(Connector conn){
		while(true) {
			System.out.println("--------------------System member menu----------------------------------");
			System.out.println("------1. Enter 1 to make reservation");
			System.out.println("------2. Enter 2 to add new UC(driver user only)");
			System.out.println("------3. Enter 3 to declare your favorite car");
			System.out.println("------4. Enter 4 to provide feedback for a car");
			System.out.println("------5. Enter 5 to rate a feedback");
			System.out.println("------6. Enter 6 to record a ride");
			System.out.println("------8. Enter 8 to declare another user as trustworthy");
			System.out.println("------9. Enter 9 to browse UCs");
			System.out.println("-----10. Enter 10 to see most useful feedback on any UD");
			System.out.println("-----11. Enter 11 to check degrees of separation to the specified user");
			System.out.println("-----12. Enter 12 to get statistics");
			System.out.println("-----13. Enter 13 to get statistics on most valuable users");
			System.out.println("-----14. Enter 14 to logout.");
			System.out.println("-----0. Enter 0 to exit the System.");
			System.out.println("----please enter your choice:------");
			Scanner scan = new Scanner(System.in);
			int choice = 1000;
			try{
				choice = getNumberFromUser();
			}
			catch (InputMismatchException e){
				System.out.println("Must input a number");
			}
			if (choice == 1) {
				makeReservation(conn);
			} else if (choice == 2) {
				addNewUC(conn);
			} else if (choice == 3) {
				addFavorite(conn);
			} else if (choice == 4) {
				addFeedback(conn);
			} else if (choice == 5) {
				addRate(conn);
			} else if (choice == 6) {
				addRide(conn);
			} else if (choice == 8) {
				addTrustedUser(conn);
			} else if (choice == 9) {
				browseUCs(conn);
			} else if (choice == 10) {
				usefulFeedbacks(conn);
			} else if (choice == 11) {
				degreesOfSeparation(conn);
			} else if (choice == 12) {
				statistics(conn);
			} else if (choice == 13) {
				userAwards(conn);
			} else if (choice == 14) {
				return;
			} else if (choice == 0) {
				System.exit(0);
			}
			else {
				System.out.println("Incorrect input.");
			}
		}
	}
	/**
	 * Displays the main menu (just make the user decide to login in or register as a new user).
	 */
	public void displayMenu(Connector conn) throws SQLException
	{
		Scanner scan=new Scanner(System.in);
		while(true){
			System.out.println("        Welcome to Uber Interface    ");
			System.out.println("1. Enter 1 to login to your own account");
			System.out.println("2. Enter 2 to create a new account");
			System.out.println("3. Enter 3 to exit.");
			System.out.println("pleasse enter your choice:");
			int choice=0;
			try{
				choice=getNumberFromUser();
			}catch(Exception e){
				System.err.println("Must input a number");
				scan=new Scanner(System.in);
				continue;
			}
			if(choice>3){
				System.err.println("this is not a valid choice");
				scan=new Scanner(System.in);
				continue;
			}
			if(choice<1){
				System.err.println("this is not a valid choice");
				scan=new Scanner(System.in);
				continue;
			}
			if(choice==3){
				break;
			}
			//register part.
			if(choice ==2){
				registerStep(conn);
			}
			if(choice==1) {
				loginAsUser(conn);
			}
		}
	}
}
