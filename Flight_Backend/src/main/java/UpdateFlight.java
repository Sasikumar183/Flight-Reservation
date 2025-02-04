import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet("/UpdateFlight")
public class UpdateFlight extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        setCORSHeaders(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        StringBuilder json = new StringBuilder();

        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }
        }

        JSONObject jsonobj = new JSONObject(json.toString());
        int flight_id = jsonobj.getInt("flight_id");
        String flight_name = jsonobj.getString("flight_name");
        JSONArray scheduleArray = jsonobj.getJSONArray("schedule");

        try (Connection conn = DatabaseConfig.getConnection() ) {
            PreparedStatement flightStmt = conn.prepareStatement("UPDATE flight SET flight_name=? WHERE flight_id=?");
            flightStmt.setString(1, flight_name);
            flightStmt.setInt(2, flight_id);
            flightStmt.executeUpdate();

            for (int i = 0; i < scheduleArray.length(); i++) {
                JSONObject schedule = scheduleArray.getJSONObject(i);
                int sche_id = schedule.getInt("sche_id");
                int stoppages = schedule.getInt("stoppages");
                String dep_time = schedule.getString("dep_time");
                String arr_time = schedule.getString("arr_time");
                int price = schedule.getInt("price");
                int available_seats = schedule.getInt("available_seats");

                PreparedStatement scheStmt = conn.prepareStatement("UPDATE schedule SET stoppages=?, dep_time=?, arr_time=?, price=? WHERE flight_id=? AND sche_id=?");
                scheStmt.setInt(1, stoppages);
                scheStmt.setString(2, dep_time);
                scheStmt.setString(3, arr_time);
                scheStmt.setInt(4, price);
                scheStmt.setInt(5, flight_id);
                scheStmt.setInt(6, sche_id);
                scheStmt.executeUpdate();

                PreparedStatement legStmt = conn.prepareStatement("UPDATE flight_leg SET available_seats=? WHERE sche_id=?");
                legStmt.setInt(1, available_seats);
                legStmt.setInt(2, sche_id);
                legStmt.executeUpdate();
            }

            JSONObject responseObj = new JSONObject();
            responseObj.put("flight_name", flight_name);
            responseObj.put("schedule", scheduleArray);

            JSONArray responseArray = new JSONArray();
            responseArray.put(responseObj);
            out.print(responseArray.toString());
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void setCORSHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setHeader("Access-Control-Max-Age", "3600");
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        setCORSHeaders(response);
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
