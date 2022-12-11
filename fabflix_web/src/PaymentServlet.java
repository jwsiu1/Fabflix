import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet(name = "PaymentServlet", urlPatterns = "/api/payment")
public class PaymentServlet extends HttpServlet {
    private static final long serialVersionUID = 8L;
    private DataSource dataSource;
    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String creditCardNumber = request.getParameter("creditCardNumber");
        String expirationDate = request.getParameter("expirationDate");
        request.getServletContext().log("payment servlet " + firstName + " " + lastName + " " + creditCardNumber
                + " " + expirationDate);
        try (Connection conn = dataSource.getConnection()) {
            String query =
                    "SELECT * " +
                    "FROM creditcards " +
                    "WHERE creditcards.id='" + creditCardNumber + "'";
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet rs = statement.executeQuery();

            JsonObject responseJsonObject = new JsonObject();

            if (rs.next()) {
                String targetFirstName = rs.getString("firstName");
                String targetLastName = rs.getString("lastName");
                Date targetExpiration = Date.valueOf(rs.getString(("expiration")));
                request.getServletContext().log(targetFirstName + " " + targetLastName);
                if (!firstName.equals(targetFirstName)) {
                    // Payment fail
                    responseJsonObject.addProperty("status", "fail");
                    // Log to localhost log
                    request.getServletContext().log("Payment failed: invalid first name");
                    responseJsonObject.addProperty("message", "Invalid first name");
                } else if (!lastName.equals(targetLastName)) {
                    // Payment fail
                    responseJsonObject.addProperty("status", "fail");
                    // Log to localhost log
                    request.getServletContext().log("Payment failed: invalid last name");
                    responseJsonObject.addProperty("message", "Invalid last name");
                } else if (!expirationDate.equals(targetExpiration.toString())) {
                    // Payment fail
                    responseJsonObject.addProperty("status", "fail");
                    // Log to localhost log
                    request.getServletContext().log("Payment failed: invalid expiration date");
                    responseJsonObject.addProperty("message", "Invalid expiration date");
                } else {
                    // Payment success:
                    responseJsonObject.addProperty("status", "success");
                    responseJsonObject.addProperty("message", "Payment success");
                }
            } else {
                // Payment fail
                responseJsonObject.addProperty("status", "fail");
                // Log to localhost log
                request.getServletContext().log("Payment failed: invalid credit card " + creditCardNumber);
                responseJsonObject.addProperty("message", "Sorry, we can't find the corresponding " +
                        "credit card number");
            }
            response.getWriter().write(responseJsonObject.toString());
            rs.close();
            statement.close();
        } catch (Exception e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            response.getWriter().write(jsonObject.toString());
            request.getServletContext().log(e.toString());
            response.setStatus(HttpURLConnection.HTTP_INTERNAL_ERROR);
        } finally {
            response.getWriter().close();
        }
    }
}