import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

@WebServlet("/CancelTicket")
public class CancelTicket extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        setCORSHeaders(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String ticketIdParam = request.getParameter("ticket_id");
        String scheduleIdParam = request.getParameter("sche_id");

        if (ticketIdParam == null || scheduleIdParam == null) {
            response.getWriter().write("{\"status\":\"error\", \"message\":\"Missing parameters\"}");
            return;
        }

        int ticket_id;
        int schedule_id;
        try {
            ticket_id = Integer.parseInt(ticketIdParam);
            schedule_id = Integer.parseInt(scheduleIdParam);
        } catch (NumberFormatException e) {
            response.getWriter().write("{\"status\":\"error\", \"message\":\"Invalid parameters\"}");
            return;
        }

        String cancelQuery = "UPDATE ticket SET is_cancelled=1 WHERE ticket_id=?";
        String seatUpdate = "UPDATE flight_leg SET available_seats = available_seats + 1 WHERE leg_id=?";

        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(cancelQuery);
             PreparedStatement cs = con.prepareStatement(seatUpdate)) {

            ps.setInt(1, ticket_id);
            int updatedRows = ps.executeUpdate();

            if (updatedRows > 0) {
                cs.setInt(1, schedule_id);
                cs.executeUpdate();
                response.getWriter().write("{\"status\":\"success\", \"message\":\"Ticket cancelled\"}");
            } else {
                response.getWriter().write("{\"status\":\"error\", \"message\":\"Ticket not found\"}");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().write("{\"status\":\"error\", \"message\":\"Database error\"}");
        }
    }

    private void setCORSHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }
}
