import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Map;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;



@WebServlet(name = "AddToSalesServlet", urlPatterns = "/api/addToSales")
public class AddToSalesServlet extends HttpServlet {
    private static final long serialVersionUID = 9L;
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
        HttpSession session = request.getSession();
        String cart = session.getAttribute("cart").toString();
        String uid = session.getAttribute("uid").toString();
        LocalDate dateObj = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String date = dateObj.format(formatter);
        request.getServletContext().log("add to sales servlet " + uid + " " + cart);

        JsonParser parser = new JsonParser();
        JsonObject cartObj = parser.parse(cart).getAsJsonObject();

        try (Connection conn = dataSource.getConnection()) {
            for(Map.Entry<String, JsonElement> entry : cartObj.entrySet()) {
                for (int i=0; i<entry.getValue().getAsInt(); i++) {
                    String query = "INSERT INTO sales (customerId, movieId, saleDate) VALUES (?,?,?)";
                    PreparedStatement statement = conn.prepareStatement(query);
                    statement.setInt(1, Integer.valueOf(uid));
                    statement.setString(2, entry.getKey());
                    statement.setDate(3, java.sql.Date.valueOf(date));
                    statement.executeUpdate();
                    statement.close();
                }
            }
            session.setAttribute("orderConfirmation", cartObj);
            session.setAttribute("cart", "{}");
            JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("status", "success");
            responseJsonObject.addProperty("message", "Purchase confirmed");
            response.getWriter().write(responseJsonObject.toString());
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