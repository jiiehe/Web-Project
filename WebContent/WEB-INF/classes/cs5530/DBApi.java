package cs5530;

import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;

import javax.swing.plaf.nimbus.State;
import javax.xml.transform.Result;
import java.net.Inet4Address;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

/**
 * This class serves as the api to access the database
 * */
public class DBApi {

    /**
     * Constructor
     * */
	public DBApi(){
		
	}

    /**
     *this is a database operation method to help to enroll the new user., just add a new user to our user table, if he's a regular user, he would be added into
	 * UU table, otherwise he would be added into UD table.
     * */
	public boolean enrollUser(Statement state,String login,String name, String address, String city,String state1,String phone, String password,String type) throws SQLException{
		boolean result=false;
		String sql = "insert into Users (login, name, address, city, state, phone, password) " + "Values ('" + login
				+ "', '" + name + "',  '" + address + "', '" + city + "', '" + state1
				+ "', '"+phone+"', '"+password+"');";
		try {
			state.executeUpdate(sql);
			//System.out.println(sql);
			if (type.equals("driver")) {
				String sql2 = "insert into UD (login) " + "Values ('" + login + "');";
				state.executeUpdate(sql2);
				result=true;
			} else {
				String sql2 = "insert into UU (login) " + "Values ('" + login + "');";
				state.executeUpdate(sql2);
				result=true;
			}
		}
		catch(SQLException e){
			System.err.println(e.getMessage());
			throw e;
		}
		return result;
	}

    /**
     * Checks if the given login is a UU
     * */
	public boolean isUberUser(Statement statement, String login){
        String sql = "select * from UU u where u.login = '" + login + "';";

        ResultSet resultSet = null;

        try {
            resultSet = statement.executeQuery(sql);
            if(!resultSet.next()){
                return false;
            }

        }catch (SQLException e){
            System.err.println(e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * Checks if the given login is a UD
     * */
	public boolean isDriverUser(Statement statement, String login){
	    String sql = "select * from UD d where d.login = '" + login + "';";
	    ResultSet resultSet = null;

	    try {
	        resultSet = statement.executeQuery(sql);
	        if(!resultSet.next()){
	            return false;
            }

        }catch (SQLException e){
	        System.err.println(e.getMessage());
	        return false;
        }

        return true;
    }

    /**
     * Working method to add a reservation
     * */
	public boolean addReservationWorks(Statement statement, String login, Integer cost, java.sql.Date date, Integer vin, Integer from, Integer to){
	    //add the period
        String sql = "INSERT INTO Period (pid, fromHour, toHour) VALUES (NULL, " + from + ", " + to + ");";
        try {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }

        //add the reservation
        sql = "INSERT INTO Reserve (login, cost, date, vin, pid) VALUES ('" + login + "', " + cost + ", '" + date + "', " + vin + ", LAST_INSERT_ID());";
        try {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * this is a helper method to check whether the user information correct, (if a user try to login in our system)
	 * it include matches password and username.
     * */
	public boolean checkUser(String login, String password,Statement state){
		String sql = "select login, password from Users where login = '" + login + "';";
		ResultSet result=null;
		try {
			result= state.executeQuery(sql);
			while(result.next()){

				if(result.getString("password").equals(password)){
					return true;
				}else{
					result.close();
					return false;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}

    /**
     * Given a vin checks if the car exists
     * */
	public boolean uberCarExisits(Statement statement, Integer vin){
        String sql="select * from UC c where c.vin = " + vin + ";";
        boolean carDoesExist = false;
        ResultSet resultSet = null;

        try{
            resultSet = statement.executeQuery(sql);

            if(resultSet.next()){
                carDoesExist = true;
            }

        }catch(SQLException e){

        }
        return carDoesExist;
    }

    /**
     * given a date and timeframe, checks if a UC is available
     * */
    public boolean carIsAvailable(Integer from, Integer to, java.sql.Date date, Integer vin, Statement statement){
        boolean isAvailable = false;
        ResultSet resultSet = null;

        //check if the car we are looking for is available
        String sql = "select * from Period p, Available a, UC c where p.pid=a.pid AND c.login = a.login and p.fromHour <= " + from + " AND p.toHour>= " + to + " and c.vin = " + vin + ";";
        try {

            //System.out.println(sql);
            resultSet = statement.executeQuery(sql);
            if (!resultSet.next()) {
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        //check if the reservation conflicts with any other reservations
        sql = "select * from Reserve r, Period p where r.pid = p.pid and r.date = " + date + " and p.fromHour <= " + from + " and p.toHour >= " + to + ";";
        try {
            //System.out.println(sql);
            resultSet = statement.executeQuery(sql);
            if (!resultSet.next()) {
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

	/**
	 * checks if the user was the one who gave the feedback
	 */
	public boolean userGaveTheFeedback(Statement statement, String login, Integer fid) {
		String sql="select * from Feedback where login="+'"'+login+'"'+"AND fid="+'"'+fid+'"';
		ResultSet resultSet = null;

		try{
			resultSet = statement.executeQuery(sql);
			if(resultSet.next()){
				return true;
			}
		}catch(SQLException e){
			e.printStackTrace();
			return false;
		}
		return false;
	}


	/**
     * checks if a user has already given feedback to a certain car
     * */
    public boolean hasGivenFeedbackToCar(Statement statement, String login, Integer vin) {
        String sql="select * from Feedback where login="+'"'+login+'"'+"AND vin="+'"'+vin+'"';
        ResultSet resultSet = null;

        try{
            resultSet = statement.executeQuery(sql);
            if(resultSet.next()){
                return true;
            }
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
        return false;
    }

    /**
     *  adds a new feedback to a car for a user
     * */
    public boolean addFeedback(String text, int vin, String login, int score, Statement stmt){
        Date date=new Date();
        java.sql.Date sqlnow=new java.sql.Date(date.getTime());
        String sql = "insert into Feedback (text, fbdate, vin, login, score) " + "Values ('" + text
                + "', '" + sqlnow + "',  '" + vin + "', '" + login + "', '" + score
                + "');";
        Integer resultSet = 0;
        try {
            resultSet = stmt.executeUpdate(sql);
            if(resultSet == 0){
                return false;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     *this is a helper method for add a new Uber car, just insert a new car information to our UC table.
     * */
    public boolean addNewCar(int vin, String login, String category, String make, String model,Statement stmt){
			boolean result=false;
			String sql = "insert into UC (vin, login, category, make, model) " + "Values ('" + vin
					+ "', '" + login + "',  '" + category + "', '" + make + "', '" + model
					+ "');";
			//System.out.println(sql);
		try {
			stmt.executeUpdate(sql);
			result=true;
		} catch (SQLException e) {
			System.err.println(e.getMessage());

		}
		return result;
	}

    /**
     * checks if a user has marked a certain car as favorite
     * */
	public boolean isFavorite(Statement statement, String login, Integer vin){
        String sql="select * from Favorites f where f.login= '" + login + "' and f.vin = " + vin + ";";
        ResultSet resultSet = null;

        try{
            resultSet = statement.executeQuery(sql);
            if(resultSet.next()){
                return true;
            }
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
        return false;
    }

    /**
     *this is a helper method for add user's favorite car, just insert a favorite information to our favorite table.
     * */
	public boolean addFavorite(int vin, String login, Statement stmt){
		String sql="insert into Favorites (vin, login) " + "Values ('" + vin
                + "', '" + login + "');";
		Integer resultSet = 0;
		try {
                resultSet = stmt.executeUpdate(sql);
                if(resultSet == 1){
                    return true;
                }

		} catch (SQLException e) {
		    return false;
		}
		return false;
	}

    /**
     * this is a heelpermethod for rate the feedback function, after the user type the vin number, we will use this method to query all
	 * feedbacks of that car, and then show them to users.
     * */
	public ArrayList<feedback> accessFeedback(int vin, String login, Statement stmt){
	    String sql="select fid, text, score from Feedback where vin="+'"'+vin+'"'+" AND login!="+'"'+login+'"';
	    ResultSet rs=null;
	    ArrayList<feedback> result=new ArrayList<feedback>();
        try {
            rs=stmt.executeQuery(sql);
            while(rs.next()){
                int fid=rs.getInt("fid");
                String text=rs.getString("text");
                int score=rs.getInt("score");
                feedback temp=new feedback(fid,text,score);
                result.add(temp);

            }
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     *this is a helper method to rate a feedback, just added the rate information into our rate table.
     * */
    public boolean addRate(String login, int fid, int rate,Statement stmt){
        String sql = "insert into Rates (login, fid, rating) " + "Values ('" + login + "', '" + fid + "', '" + rate
                + "');";
        try {
            stmt.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            //e.printStackTrace();
			System.err.println("you rated this feedback before");
        }
        return false;
    }

    /**
     *this is a helper method to check whether the inforamtion that the user type for recored the ride is available time for a Uber driver.
	 * if it is , just return true. on the other hand, the information that user provided is correct, and he can just put the information into the ride table.
     * */
    public boolean checkAvailability(Connector conn, Integer vin, Integer fromHour, Integer toHour){
		String sql="select * from Period r, Available a" +" where r.pid=a.pid "+"AND r.fromHour<="+'"'+ fromHour+'"'+" AND r.toHour>="+'"'+toHour+ '"';
		boolean correctRide=false;
		//Statement stmt=statement;

		ResultSet rs=null;
		try {
			Statement stmt=conn.con.createStatement();
			  rs=stmt.executeQuery(sql);

			while(rs.next()) {
				String driver = rs.getString("login");
				String sql1 = "select * from UC where login=" + '"' + driver + '"';
				Statement stmt1=conn.con.createStatement();
				ResultSet rs1 = stmt1.executeQuery(sql1);
				while (rs1.next()) {
					int getVin = rs1.getInt("vin");
					if (vin == getVin) {
						correctRide = true;
					}
				}


			}
			if(rs.next()==false){
				return correctRide;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return correctRide;
	}

    /**
     *the helper method to just added the ride information into our ride table.
     * */
    public boolean addRide(int cost, java.sql.Date date, String login, int vin, int fromHour, int toHour, Statement stmt){
		boolean result=false;
		try {

			String sql1 = "insert into Ride (cost, date, login, vin, fromHour, toHour) " + "Values ('" + cost
					+ "', '" + date + "',  '" + login + "', '" + vin + "', '" + fromHour+ "', '" + toHour
					+ "');";
			stmt.executeUpdate(sql1);
			result=true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

    /**
     * returns a ranked list of the most trusted users up to listSize in length
     * */
	public ResultSet mostTrustedUsers(Statement statement, Integer listSize) {
		ResultSet resultSet = null;
		String sql = "select t.login2, sum(t.isTrusted) as trustScore from Trust t\n" +
				"group by t.login2\n" +
				"order by trustScore DESC\n" +
				"LIMIT " + listSize + ";";

		try {

			//System.out.println(sql);
			resultSet = statement.executeQuery(sql);

			/*while(resultSet.next()) {
				System.out.println(resultSet.getString("login2")
						+ " " + resultSet.getString("trustScore"));
			}*/
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return resultSet;
	}

    /**
     * Returns a ranked list of the most useful up to listSize
     * */
	public ResultSet mostUsefulUsers(Statement statement, Integer listSize){
		ResultSet resultSet = null;
		String sql = "select f.login, AVG(r.rating) as avgUsefulNess from Feedback f, Rates r\n" +
				"where f.fid = r.fid\n" +
				"group by f.login\n" +
				"order by avgUsefulNess DESC\n" +
				"LIMIT " + listSize + ";";

		try {

			//System.out.println(sql);
			resultSet = statement.executeQuery(sql);

			/*while(resultSet.next()) {
				System.out.println(resultSet.getString("login")
						+ " " + resultSet.getString("avgUsefulness"));
			}*/
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return resultSet;
	}

    /**
     * Returns various statistics on cars and drivers based on the parameters
     * */
	public ResultSet statistics(Statement statement, String type, Integer listSizePerCategory) {
		ResultSet resultSet = null;
		String sql = "";

		if(type.equals("pUC")){
			sql = "select *\n" +
					"from UC c\n" +
					"inner JOIN\n" +
					"(\n" +
					"    select r.vin as innerVin, count(*) as rideCount from Reserve r\n" +
					"    group by r.vin\n" +
					") as t1\n" +
					"on c.vin = t1.innerVin\n" +
					"order by category, rideCount DESC;";
		}
		else if (type.equals("eUC")) {
			sql = "select *\n" +
					"from UC c\n" +
					"inner JOIN\n" +
					"(\n" +
					"    select r.vin as innerVin, AVG(r.cost) as avgCost from Reserve r\n" +
					"    group by r.vin\n" +
					") as t1\n" +
					"on c.vin = t1.innerVin\n" +
					"order by category, avgCost DESC;";
		}
		else if (type.equals("rUD")) {
			sql = "select c.login, c.category, AVG(f.score) as averageFeedback\n" +
					"from Feedback f, UD d, UC c\n" +
					"where d.login = c.login and f.vin = c.vin\n" +
					"group by c.login, c.category\n" +
					"ORDER BY category, averageFeedback DESC;";
		}
		else {
			System.out.println("Type of statistics is given by either \"pUC\" for most popular UC's, \"eUC\" for most expensive UC's, or \"rUD\" for most highly rated UD's");
			return null;
		}

		try {

			//System.out.println(sql);
			resultSet = statement.executeQuery(sql);

			/*while(resultSet.next()) {
				System.out.println(resultSet.getString("category"));
						*//*+ " " + resultSet.getString("make")
						+ " " + resultSet.getString("model")
						+ " " + resultSet.getString("rideCount")
						+ " " + resultSet.getString("categoryRank"));*//*
			}*/
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return resultSet;
	}

    /**
     * Calculates the degrees of separation between two users
     * */
	public Integer degreesOfSeparation(Statement statement, String user1, String user2) {
		Integer result = 0;
		ResultSet resultSet = null;
		String sql = "select * from Favorites f, Favorites f2\n" +
				"where f.login = '" + user1 + "' and f2.login = '" + user2 + "' and f.vin = f2.vin;";

		try {

			resultSet = statement.executeQuery(sql);

			if(resultSet.next()) {
				result = 1;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		if (result != 1){
			sql = "select * from " +
					"(select f2.login from Favorites f, Favorites f2 " +
					"where f.login = '" + user1 + "' and f.vin = f2.vin) as t1 " +
					"INNER JOIN " +
					" ( " +
					" select f2.login as t2Login from Favorites f, Favorites f2 " +
					" where f.login = '" + user2 + "' and f.vin = f2.vin " +
					"    ) as t2\n" +
					"on t1.login = t2.t2Login;";

			try {

				resultSet = statement.executeQuery(sql);

				if(resultSet.next()) {
					result = 2;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}


		return result;
	}

    /**
     * Finds other cars based on users that used the same car as the userMakingTheReservation and ranks them based on popularity
     * */
	public ResultSet ucSuggestions(Statement statement, String userMakingTheReservation, Integer vin) {
		ResultSet result = null;
		String sql = "select r.vin, c.category, c.make, c.model, Count(*) as popular from UC c, Reserve r " +
				"INNER JOIN " +
				"(select distinct r2.login from Reserve r, Reserve r2 " +
				"where r.login = '" + userMakingTheReservation + "' and r2.login != '" + userMakingTheReservation + "' and r.vin = " + vin +
				" and r2.vin = " + vin +
				" ) as t1 " +
				"on r.vin != " + vin + " and t1.login = r.login " +
				"where c.vin = r.vin " +
				"GROUP BY r.vin " +
				"order by popular DESC";


		try {

			result = statement.executeQuery(sql);

			/*while(result.next()) {
				System.out.println(result.getString("vin")
						+ " " + result.getString("category")
						+ " " + result.getString("make")
						+ " " + result.getString("model")
						+ " " + result.getString("popular"));
			}*/
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

    /**
     * Checks if a user has already marked another user as trusted or not
     * */
	public boolean isTrusted(Statement statement, String user1, String user2){
	    String sql = "select * from Trust t where t.login1 = '" + user1 + "' and t.login2 = '" + user2 + "';";
	    ResultSet resultSet = null;

	    try{
            resultSet = statement.executeQuery(sql);
            if(resultSet.next()){
                return true;
            }

        }catch(SQLException e){
	        e.printStackTrace();
	        return false;
        }
        return false;
    }

    /**
     * marks user2 as trusted or not by the decidingUser
     * */
	public boolean addTrustedUser(String decidingUser, String user2, boolean isTrusted, Statement statement) {
		Integer trusted = 0;
		boolean success = false;
		if(isTrusted) {
			trusted = 1;
		}
		else {
			trusted = -1;
		}
		String sql = "insert into Trust (login1, login2, isTrusted) values ('" + decidingUser + "', '" + user2 + "', " + trusted + ");";
		int result = 0;
		try {
			result = statement.executeUpdate(sql);

			//System.out.println(result);
		} catch	(SQLException e) {
			//e.printStackTrace();
		}
		if (result == 1) {
			success = true;
		}
		return success;
	}

    /**
     * finds the most useful feedbacks about driverUsername with a maximum list length of maxListLength
     * */
	public ResultSet usefulFeedbacks(Statement statement, String driverUserName, Integer maxListLength) {
		ResultSet result = null;
		String sql = "select u.name, u.login, f.fid, f.score, f.text, f.fbdate, t1.avg as usefullnessRating from Users u, UD d, UC c, Feedback f " +
				"left join " +
				" ( " +
				" select r.fid, AVG(r.rating) as avg from Rates r " +
				" group by r.fid " +
				" ) as t1 " +
				"    on t1.fid = f.fid " +
				"where u.login = c.login and c.login = d.login and f.vin = c.vin and d.login = '" +
				driverUserName + "' " +
				"order by avg DESC " +
				"LIMIT " +
				maxListLength.toString();


		try {

			result = statement.executeQuery(sql);

			/*while(result.next()) {
				System.out.println(result.getString("name")
						+ " " + result.getString("login")
						+ " " + result.getString("score")
						+ " " + result.getString("text")
						+ " " + result.getString("fbdate")
						+ " " + result.getString("usefullnessRating"));
			}*/
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

    /**
     * finds UC's in the database based on the criteria
     * */
	public ResultSet browseUCs(Statement statement, String userWhoIsAsking, String category, String state, String city, String model, boolean isSortedByTrustedFeedback) {
		String sql = "select * from Users u, UC c ";

		sql += "left join (select AVG(f.score) as avg, f.vin from Feedback f ";
		if(isSortedByTrustedFeedback){
			sql += ", Trust t " +
					"where t.login1 = '" + userWhoIsAsking + "' and f.login = t.login2 " +
					"group by f.vin " +
					") as t1 " +
					"on t1.vin = c.vin " +
					"where u.login = c.login ";
		}
		else {
			sql += "group by f.vin " +
					") as t1 " +
					"on t1.vin = c.vin " +
					"where u.login = c.login ";
		}
		sql += "and c.category = '" + category + "' ";
		if(!city.equals("")) {
			sql += "and u.city = '" + city + "' ";
		}
		if(!state.equals("")) {
			sql += "and u.state = '" + state + "' ";
		}
		if(!model.equals("")) {
			sql += "and c.model = '" + model + "' ";
		}
		sql += "order by -avg desc, avg desc;";


		ResultSet result = null;
		try {

			result = statement.executeQuery(sql);

			/*while(result.next()) {
				System.out.println(result.getString("name")
						+ " " + result.getString("city")
						+ " " + result.getString("state")
						+ " " + result.getString("category")
						+ " " + result.getString("make")
						+ " " + result.getString("model")
						+ " " + result.getString("avg"));
			}*/
		} catch (SQLException e) {
		    //System.err.println(sql);
			//e.printStackTrace();
		}

		return result;
	}

}

/**
 * this is a helper data structure for rate feedback function, we query the feedback for a car, and record its fid, text, and score into this data structure, and
 * store it into a list, and then it would be showed to our users.
 * */
class feedback{
    int fid;
    String text;
    int score;
    public feedback(int fid, String text, int score){
        this.fid=fid;
        this.text=text;
        this.score=score;
    }
}
