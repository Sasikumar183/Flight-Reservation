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

@WebServlet("/AdminFlightEdit")
public class AdminFlightEdit extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        setCORSHeaders(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        JSONArray jsonRes = new JSONArray();

        int flight_id = Integer.parseInt(request.getParameter("id"));
        String flight_name;

        String queryFlightName = "SELECT flight_name FROM flight WHERE flight_id = ?";

        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(queryFlightName)) {

            ps.setInt(1, flight_id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                flight_name = rs.getString("flight_name");

                JSONObject flightObj = new JSONObject();
                JSONObject sche = new JSONObject();

                flightObj.put("flight_name", flight_name);
                jsonRes.put(flightObj);

                String querySchedule = "SELECT s.sche_id, s.arr_loc, s.dep_loc, s.dep_time, s.arr_time, s.price, s.stoppages, l.available_seats " +
                                       "FROM schedule AS s " +
                                       "JOIN flight_leg AS l ON s.sche_id = l.sche_id " +
                                       "WHERE s.flight_id = ?";

                try (PreparedStatement psSchedule = con.prepareStatement(querySchedule)) {
                    psSchedule.setInt(1, flight_id);
                    ResultSet rsSchedule = psSchedule.executeQuery();
                    JSONArray schedule= new JSONArray();
                    while (rsSchedule.next()) {
                        JSONObject scheduleObj = new JSONObject();
                        scheduleObj.put("sche_id", rsSchedule.getInt("sche_id"));
                        scheduleObj.put("arr_loc", rsSchedule.getString("arr_loc"));
                        scheduleObj.put("dep_loc", rsSchedule.getString("dep_loc"));
                        scheduleObj.put("dep_time", rsSchedule.getString("dep_time"));
                        scheduleObj.put("arr_time", rsSchedule.getString("arr_time"));
                        scheduleObj.put("price", rsSchedule.getBigDecimal("price"));
                        scheduleObj.put("stoppages", rsSchedule.getInt("stoppages"));
                        scheduleObj.put("available_seats", rsSchedule.getInt("available_seats"));
                        
                        schedule.put(scheduleObj);
                    }
                    sche.put("schedule", schedule);
                    jsonRes.put(sche);
                }
            } else {
                // If no flight found, send an error response
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                JSONObject errorObj = new JSONObject();
                errorObj.put("error", "Flight not found");
                jsonRes.put(errorObj);
            }

        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JSONObject errorObj = new JSONObject();
            errorObj.put("error", "Database error: " + e.getMessage());
            jsonRes.put(errorObj);
        }

        out.print(jsonRes);
        out.flush();
    }

    private void setCORSHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setHeader("Access-Control-Max-Age", "3600");
    }
}
