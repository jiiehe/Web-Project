package cs5530;

import java.sql.SQLException;

/**
 * Created by jaredearl on 3/24/18.
 */
public class TestDriver {
    public static void main(String[] args) throws SQLException {
        CommandLineInterface commandLineInterface = new CommandLineInterface();
        // TODO Auto-generated method stub
        System.out.println("we are loading the Uber System");
        Connector con=null;
        try{
            con=new Connector();

            System.out.println("connected successful");
        }catch(Exception e){
            e.printStackTrace();
            System.err.println("Failed to connect! please recheck the network and try again");
        }
        commandLineInterface.displayMenu(con);

        try {
            con.closeConnection();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
