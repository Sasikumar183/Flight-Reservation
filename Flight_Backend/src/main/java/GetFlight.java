import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet("/GetFlight")
public class GetFlight extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        setCORSHeaders(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JSONArray flightArray = new JSONArray();

        String query = """
            SELECT s.sche_id, s.flight_id, f.flight_name, fl.leg_id, fl.available_seats, 
                   s.dep_loc, s.arr_loc, s.dep_time, s.arr_time, s.price,s.stoppages
            FROM schedule AS s
            JOIN flight AS f ON s.flight_id = f.flight_id
            JOIN flight_leg AS fl ON s.sche_id = fl.sche_id
        """;

        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                JSONObject flight = new JSONObject();
                flight.put("sche_id", rs.getInt("sche_id"));
                flight.put("flight_id", rs.getInt("flight_id"));
                flight.put("flight_name", rs.getString("flight_name"));
                flight.put("leg_id", rs.getInt("leg_id"));
                flight.put("available_seats", rs.getInt("available_seats"));
                flight.put("dep_loc", rs.getString("dep_loc"));
                flight.put("arr_loc", rs.getString("arr_loc"));
                flight.put("dep_time", rs.getString("dep_time"));
                flight.put("arr_time", rs.getString("arr_time"));
                flight.put("price", rs.getBigDecimal("price"));
                flight.put("stoppages",rs.getInt("stoppages"));
                flightArray.put(flight);
            }

        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("error", "Database error: " + e.getMessage());
            flightArray.put(errorResponse);
        }

        // Write JSON response
        try (PrintWriter out = response.getWriter()) {
            out.print(flightArray);
            out.flush();
        }
    }

    private void setCORSHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }
}
