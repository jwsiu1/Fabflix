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
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.util.Map;

@WebServlet(name = "ShoppingCartServlet", urlPatterns = "/api/shoppingCart")
public class ShoppingCartServlet extends HttpServlet {
    private static final long serialVersionUID = 6L;
    private DataSource dataSource;
    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        String currCart = session.getAttribute("cart") == null ? "{}" : (String) session.getAttribute("cart");
        JsonParser parser = new JsonParser();
        JsonObject cart = parser.parse(currCart).getAsJsonObject();
        PrintWriter out = response.getWriter();
        JsonObject responseJsonObject = new JsonObject();
        try (Connection conn = dataSource.getConnection()) {
            for(Map.Entry<String, JsonElement> entry : cart.entrySet()) {
                request.getServletContext().log("Key = " + entry.getKey() + " Value = " + entry.getValue() );
                String movieId = entry.getKey();
                int quantity = entry.getValue().getAsInt();
                JsonObject movieItem = new JsonObject();
                String movieQuery =
                        "SELECT * " +
                        "FROM movies " +
                        "WHERE id='" + movieId + "'";
                PreparedStatement movieStatement = conn.prepareStatement(movieQuery);
                ResultSet movieRs = movieStatement.executeQuery();
                while (movieRs.next()) {
                    String movieTitle = movieRs.getString("title");
                    movieItem.addProperty("title", movieTitle);
                    movieItem.addProperty("quantity", quantity);
                    movieItem.addProperty("price", 10);
                    movieItem.addProperty("totalPrice", quantity*10);
                }
                movieRs.close();
                movieStatement.close();
                responseJsonObject.add(movieId, movieItem);
            }
            out.write(responseJsonObject.toString());
            out.close();
            response.setStatus(HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());
            request.getServletContext().log("Error:", e);
            response.setStatus(HttpURLConnection.HTTP_INTERNAL_ERROR);
        } finally {
            out.close();
        }
    }
}