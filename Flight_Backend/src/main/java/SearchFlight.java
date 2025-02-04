import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SearchFlight extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        setCORSHeaders(response);

        String source = request.getParameter("source");
        String destination = request.getParameter("destination");
        int priceInc=200;

        if (source == null || destination == null) {
            return; 
         }

        String insertQuery = "INSERT INTO search (source, destination, count) " +
                             "VALUES (?, ?, 1) ON DUPLICATE KEY UPDATE count = count + 1";
        String countQuery = "SELECT count FROM search WHERE source=? AND destination=?";

        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(insertQuery);
             PreparedStatement cq = con.prepareStatement(countQuery)) {

            ps.setString(1, source);
            ps.setString(2, destination);
            ps.executeUpdate();

            cq.setString(1, source);
            cq.setString(2, destination);
            try (ResultSet result = cq.executeQuery()) {
                if (result.next()) {
                    int count = result.getInt("count");
                    if(count%10==0) {
                    	String priceQuery="UPDATE schedule SET price = price + ? WHERE dep_loc = ? AND arr_loc = ?";
                    	PreparedStatement pq=con.prepareStatement(priceQuery);
                    	pq.setInt(1, priceInc);
                    	pq.setString(2, source);
                    	pq.setString(3, destination);
                    	pq.executeUpdate();
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setCORSHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setHeader("Access-Control-Max-Age", "3600");
    }
}
