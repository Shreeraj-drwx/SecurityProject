package ia.spm;

import java.sql.Connection;
import java.sql.DriverManager;
public class MysqlConnection {
    public static Connection openConnection() throws ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");

        String jdbcUrl = "jdbc:mysql://localhost:3306/demo";
        String username = "suppalapati";
        String password = "epicrider";
        Connection connection = null;
        try {
            // Open a connection
            System.out.println("Connecting to the database...");
            connection = DriverManager.getConnection(jdbcUrl, username, password);

            // Print a message if connected successfully
            System.out.println("Successfully connected again!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }
}
