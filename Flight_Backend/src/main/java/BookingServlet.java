import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONObject;

@WebServlet("/BookingServlet")
public class BookingServlet extends HttpServlet {
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

        // Parse JSON Data
        JSONObject jsonobj = new JSONObject(json.toString());
        String userId = jsonobj.getString("userId");
        int noOfTicket = jsonobj.getInt("tickets");

        JSONObject flight = jsonobj.getJSONObject("flight");
        int leg_id = flight.getInt("leg_id");
        int sche_id = flight.getInt("sche_id");
        int flight_id = flight.getInt("flight_id");

        System.out.println("User: " + userId + ", Tickets: " + noOfTicket);
        System.out.println("Leg ID: " + leg_id + ", Schedule ID: " + sche_id + ", Flight ID: " + flight_id);

        String bookQuery = "INSERT INTO booking(user_id, sche_id, flight_id) VALUES (?, ?, ?)";
        String ticketQuery = "INSERT INTO ticket(book_id, flight_id, leg_id, user_id, seat_no) VALUES (?, ?, ?, ?, ?)";
        String updateSeatsQuery = "UPDATE flight_leg SET available_seats = available_seats - ? WHERE leg_id = ?";

        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement bookStmt = con.prepareStatement(bookQuery, PreparedStatement.RETURN_GENERATED_KEYS);
             PreparedStatement ticketStmt = con.prepareStatement(ticketQuery);
             PreparedStatement updateSeatStmt = con.prepareStatement(updateSeatsQuery)) {

            bookStmt.setString(1, userId);
            bookStmt.setInt(2, sche_id);
            bookStmt.setInt(3, flight_id);
            int rowsAffected = bookStmt.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = bookStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int book_id = generatedKeys.getInt(1);

                        // Insert tickets
                        for (int i = 0; i < noOfTicket; i++) {
                            int seat_no = getSeatNumber(leg_id);

                            ticketStmt.setInt(1, book_id);
                            ticketStmt.setInt(2, flight_id);
                            ticketStmt.setInt(3, leg_id);
                            ticketStmt.setString(4, userId);
                            ticketStmt.setInt(5, seat_no);
                            ticketStmt.executeUpdate();
                        }

                        // Update available seats in flight_leg table
                        updateSeatStmt.setInt(1, noOfTicket);
                        updateSeatStmt.setInt(2, leg_id);
                        updateSeatStmt.executeUpdate();

                        // Send success response
                        JSONObject responseObj = new JSONObject();
                        responseObj.put("message", "Booking successful");
                        responseObj.put("bookingId", book_id);
                        out.print(responseObj.toString());
                    }
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\": \"Booking failed\"}");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"Database error occurred\"}");
        }
    }

    public int getSeatNumber(int legId) {
        int nextSeatNo = 1;
        String cancelledSeatQuery = "SELECT seat_no FROM ticket WHERE is_cancelled=1 AND leg_id=?";
        String nextSeatQuery = "SELECT COALESCE(MAX(seat_no), 0) + 1 AS next_seat_no FROM ticket WHERE leg_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(cancelledSeatQuery)) {

            stmt.setInt(1, legId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                nextSeatNo = rs.getInt("seat_no");
                String updateQuery="UPDATE ticket set is_cancelled=0 where seat_no=? and leg_id=?";
                PreparedStatement stmt2=conn.prepareStatement(updateQuery);
                stmt2.setInt(1, nextSeatNo);
                stmt2.setInt(2, legId);
                stmt2.executeUpdate();
            } else {
                try (PreparedStatement stmt2 = conn.prepareStatement(nextSeatQuery)) {
                    stmt2.setInt(1, legId);
                    ResultSet rs2 = stmt2.executeQuery();

                    if (rs2.next()) {
                        nextSeatNo = rs2.getInt("next_seat_no");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nextSeatNo;
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
