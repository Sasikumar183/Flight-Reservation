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
import org.json.JSONObject;

@WebServlet("/AdminLogin")
public class AdminLogin extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        setCORSHeaders(response); 

        PrintWriter out = response.getWriter();
        String username = request.getParameter("user_name");
        String password = request.getParameter("password");

        try (Connection connection = DatabaseConfig.getConnection()) {
            String query = "SELECT admin_id FROM admin WHERE user_name = ? AND password = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int userId = resultSet.getInt("admin_id");

                        JSONObject jsonResponse = new JSONObject();
                        jsonResponse.put("id", userId);
                        jsonResponse.put("message", "Login successful");

                        response.setContentType("application/json"); // ✅ Ensure correct response type
                        response.setCharacterEncoding("UTF-8");
                        out.print(jsonResponse.toString());
                    } else {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        out.print("{\"message\":\"Invalid credentials\"}");
                    }
                }
            }
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"message\":\"Database error: " + e.getMessage() + "\"}");
        } finally {
            out.flush();
            out.close();
        }
    }

    // ✅ Ensure this method accepts HttpServletResponse (not HttpServletRequest)
    private void setCORSHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }
}
