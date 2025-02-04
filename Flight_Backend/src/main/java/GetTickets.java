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

/**
 * Servlet implementation class GetTickets
 */
@WebServlet("/GetTickets")
public class GetTickets extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        setCORSHeaders(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String userIdParam = request.getParameter("id");
        JSONObject jsonResponse = new JSONObject();

        if (userIdParam == null || userIdParam.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonResponse.put("message", "User ID is required");
            response.getWriter().print(jsonResponse);
            return;
        }

        int userId;
        try {
            userId = Integer.parseInt(userIdParam);
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonResponse.put("message", "Invalid User ID format");
            response.getWriter().print(jsonResponse);
            return;
        }

        JSONArray tickets = new JSONArray();
        String query = """
            SELECT t.ticket_id, t.user_id, t.flight_id, t.seat_no, f.flight_name, 
            s.sche_id, s.dep_loc, s.arr_loc, s.dep_time, s.arr_time, s.price
            FROM ticket AS t
            JOIN schedule AS s ON t.leg_id = s.sche_id
            JOIN flight AS f ON f.flight_id = s.flight_id
            WHERE t.user_id = ? AND is_cancelled = 0
            ORDER BY t.ticket_id ASC
        """;

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                JSONObject obj = new JSONObject();
                obj.put("ticket_id", rs.getInt("ticket_id"));
                obj.put("user_id", rs.getInt("user_id"));
                obj.put("flight_id", rs.getInt("flight_id"));
                obj.put("flight_name", rs.getString("flight_name"));
                obj.put("seat_no", rs.getInt("seat_no"));
                obj.put("sche_id", rs.getInt("sche_id"));
                obj.put("dep_loc", rs.getString("dep_loc"));
                obj.put("arr_loc", rs.getString("arr_loc"));
                obj.put("dep_time", rs.getString("dep_time"));
                obj.put("arr_time", rs.getString("arr_time"));
                obj.put("price", rs.getBigDecimal("price"));
                tickets.put(obj);
            }

            response.setStatus(HttpServletResponse.SC_OK);
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.put("message", "Database error: " + e.getMessage());
            tickets.put(jsonResponse);
        }

        try (PrintWriter out = response.getWriter()) {
            out.print(tickets.toString());
            out.flush();
        }
    }

    private void setCORSHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }
}
