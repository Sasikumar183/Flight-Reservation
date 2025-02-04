import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.json.JSONObject;
import org.json.JSONArray;

@WebServlet("/admin-flight-add")
public class AdminFlightAdd extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        setCORSHeaders(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        StringBuilder json = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }
        }

        JSONObject flight = new JSONObject(json.toString());
        String flight_name = flight.getString("flight_name");
        JSONArray schedules = flight.getJSONArray("schedule");

        String insertFlightQuery = "INSERT INTO flight(flight_name) VALUES(?)";

        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement psFlight = con.prepareStatement(insertFlightQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {

            psFlight.setString(1, flight_name);
            int affectedRows = psFlight.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = psFlight.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int flightId = generatedKeys.getInt(1);
                        JSONObject jsonResponse = new JSONObject();
                        jsonResponse.put("id", flightId);

                        for (int i = 0; i < schedules.length(); i++) {
                            JSONObject obj = schedules.getJSONObject(i);
                            String departure = obj.getString("departure");
                            String arrival = obj.getString("arrival");
                            String departureTime = obj.getString("departureTime");
                            String arrivalTime = obj.getString("arrivalTime");
                            int seats = obj.getInt("seats");
                            int price = obj.getInt("price");
                            int stoppages = obj.getInt("stoppages");

                            String insertScheduleQuery = "INSERT INTO schedule(flight_id, dep_loc, arr_loc, dep_time, arr_time, price,stoppages) VALUES(?,?,?,?,?,?,?)";
                            try (PreparedStatement psSche = con.prepareStatement(insertScheduleQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
                                psSche.setInt(1, flightId);
                                psSche.setString(2, departure);
                                psSche.setString(3, arrival);
                                psSche.setString(4, departureTime);
                                psSche.setString(5, arrivalTime);
                                psSche.setInt(6, price);
                                psSche.setInt(7, stoppages);
                                int rS = psSche.executeUpdate();
                                if (rS > 0) {
                                    try (ResultSet sche = psSche.getGeneratedKeys()) {
                                        if (sche.next()) {
                                            int schedule_id = sche.getInt(1);

                                            String seatQuery = "INSERT INTO flight_leg(leg_id,sche_id, available_seats) VALUES(?,?,?)";
                                            try (PreparedStatement flightLeg = con.prepareStatement(seatQuery)) {
                                                flightLeg.setInt(1, schedule_id);
                                                flightLeg.setInt(2, schedule_id);
                                                flightLeg.setInt(3, seats);
                                                flightLeg.executeUpdate();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        response.getWriter().print(jsonResponse.toString());
                    }
                }
            } else {
                response.getWriter().write("{\"error\": \"Flight insertion failed\"}");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Database error occurred\"}");
        }
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        setCORSHeaders(response);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    private void setCORSHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setHeader("Access-Control-Max-Age", "3600");
    }
}
