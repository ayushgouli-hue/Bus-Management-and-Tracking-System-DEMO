package yatraa.util; // put this in the util package same as Database.java

import java.sql.Connection; // Connection is the object returned by Database.getConnection()

// DBTest class - a simple test class to check if our database connection works
// Run this class first (right-click → Run As → Java Application) to test the connection
// BEFORE running the main app, make sure this test prints "✅ Connection successful!"
public class DBTest {

    // main() - Java starts running from here when you run this class
    public static void main(String[] args) {

        // call Database.getConnection() to try connecting to MySQL
        // it returns a Connection object if it worked, or null if it failed
        Connection conn = Database.getConnection();

        if (conn != null) {
            // conn is not null means the connection was successful
            System.out.println("✅ Connection successful!"); // print success message

        } else {
            // conn is null means something went wrong
            System.out.println("❌ Connection failed!"); // print failure message
            System.out.println("Check: Is MySQL running? Is the password correct in Database.java?");
        }
    }
}
