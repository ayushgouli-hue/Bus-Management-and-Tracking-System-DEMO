package yatraa.util; // put this in the util package where our other helper classes are

import java.sql.Connection;      // Connection is the object that represents a live DB connection
import java.sql.DriverManager;   // DriverManager is used to create the connection to MySQL
import java.sql.SQLException;    // SQLException is the error thrown when database connection fails

// Database class - handles connecting to the MySQL database
// This class has one job: give us a Connection object we can use to run SQL queries
// All methods are static so we call them directly: Database.getConnection()
public class Database {

    // URL tells Java where to find the MySQL database
    // "jdbc:mysql://" is the protocol for MySQL
    // "localhost" means the database is on this same computer
    // "3306" is the default MySQL port number
    // "home_service_app" is the name of the database (schema) to connect to
	private static final String URL = "jdbc:mysql://localhost:3306/yaatri_bus_system";

    // USER is the MySQL username — "root" is the default admin user in MySQL
    private static final String USER = "root";

    // PASSWORD is your MySQL root password that you set when you installed MySQL
    // change this to whatever your MySQL password is on your computer
    private static final String PASSWORD = "POOLCOOL394anu@";

    // getConnection() - tries to connect to the MySQL database and returns the connection
    // returns a Connection object if successful, or null if it failed
    public static Connection getConnection() {
        try {
            // DriverManager.getConnection() tries to open a connection to the database
            // it uses the URL, username, and password we defined above
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);

            System.out.println("Database connected!"); // print success message to console
            return conn; // return the connection so other classes can use it

        } catch (SQLException e) {
            // if something goes wrong (wrong password, MySQL not running, etc.)
            // print the error details to the console so we can see what went wrong
            e.printStackTrace();
            return null; // return null to tell the caller that connection failed
        }
    }
}
