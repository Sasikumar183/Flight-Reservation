import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet("/FlightAdmin")
public class FlightAdmin extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        setCORSHeaders(response);
        response.setContentType("application/json"); // Set response type to JSON

        JSONArray flightArray = new JSONArray();

        try (Connection con = DatabaseConfig.getConnection();
             Statement ps = con.createStatement();
             ResultSet rs = ps.executeQuery("SELECT flight_id, flight_name FROM flight")) {

            while (rs.next()) {
                JSONObject flightObj = new JSONObject();
                flightObj.put("flight_id", rs.getInt("flight_id"));
                flightObj.put("flight_name", rs.getString("flight_name"));
                flightArray.put(flightObj);
            }

        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("error", "Database error: " + e.getMessage());
            flightArray.put(errorResponse);
        }

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
