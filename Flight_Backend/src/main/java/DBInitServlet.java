import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/init-db")
public class DBInitServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        System.out.println("🔄 Manually initializing the database...");
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {

            // User Table
            String userTable = "CREATE TABLE IF NOT EXISTS user (" +
                    "user_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(50) NOT NULL, " +
                    "mail VARCHAR(50) UNIQUE NOT NULL, " +
                    "password VARCHAR(255) NOT NULL)";
            
            //Admin Table
            String adminTable = """
            	    CREATE TABLE IF NOT EXISTS admin (
            	        admin_id INT AUTO_INCREMENT PRIMARY KEY,
            	        user_name VARCHAR(255) NOT NULL UNIQUE,
            	        password VARCHAR(255) NOT NULL
            	    )
            	""";


            // Flight Table
            String flightTable = "CREATE TABLE IF NOT EXISTS flight (" +
                    "flight_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "flight_name VARCHAR(50) NOT NULL)";

            // Schedule Table
            String scheduleTable = "CREATE TABLE IF NOT EXISTS schedule (" +
                    "sche_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "flight_id INT NOT NULL, " +
                    "dep_loc VARCHAR(50) NOT NULL, " +
                    "arr_loc VARCHAR(50) NOT NULL, " +
                    "dep_time TIME NOT NULL, " +
                    "arr_time TIME NOT NULL, " +
                    "price DECIMAL(10,2) NOT NULL, " +
                    "stoppages INT DEFAULT 0, " +
                    "FOREIGN KEY (flight_id) REFERENCES flight(flight_id) ON DELETE CASCADE)";


            // Flight Leg Table
            String flightLegTable = "CREATE TABLE IF NOT EXISTS flight_leg (" +
                    "leg_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "sche_id INT NOT NULL, " +
                    "available_seats INT CHECK (available_seats >= 0), " +
                    "FOREIGN KEY (sche_id) REFERENCES schedule(sche_id) ON DELETE CASCADE)";

            // Booking Table (Added flight_id)
            String bookingTable = "CREATE TABLE IF NOT EXISTS booking (" +
                    "book_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "user_id INT NOT NULL, " +
                    "sche_id INT NOT NULL, " +
                    "flight_id INT NOT NULL, " +
                    "FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (sche_id) REFERENCES schedule(sche_id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (flight_id) REFERENCES flight(flight_id) ON DELETE CASCADE)";

            // Ticket Table
            String ticketTable = "CREATE TABLE IF NOT EXISTS ticket (" +
                    "ticket_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "book_id INT NOT NULL, " +
                    "flight_id INT NOT NULL, " +
                    "leg_id INT NOT NULL, " +
                    "user_id INT NOT NULL, " +  
                    "seat_no INT NOT NULL CHECK (seat_no > 0), " +
                    "is_cancelled BOOLEAN DEFAULT FALSE, " +
                    "FOREIGN KEY (book_id) REFERENCES booking(book_id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (leg_id) REFERENCES flight_leg(leg_id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (flight_id) REFERENCES flight(flight_id) ON DELETE CASCADE)";

            // Search Table
            String searchTable = "CREATE TABLE search ("
                    + "search_id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "source VARCHAR(20), "
                    + "destination VARCHAR(20), "
                    + "count INT, "
                    + "UNIQUE (source, destination))";

            
            // Execute all table creation queries
            stmt.execute(userTable);
            stmt.execute(flightTable);
            stmt.execute(scheduleTable);
            stmt.execute(flightLegTable);
            stmt.execute(bookingTable);
            stmt.execute(ticketTable);
            stmt.execute(adminTable);
            stmt.execute(searchTable);
            
            System.out.println("All database tables are ready!");

            response.getWriter().println("Database initialized successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Database initialization failed: " + e.getMessage());
        }
    }
}
