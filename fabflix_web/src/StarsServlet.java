import com.google.gson.JsonArray;
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
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

@WebServlet(name = "MainPageServlet", urlPatterns = "/api/main")
public class StarsServlet extends HttpServlet {
    private static final long serialVersionUID = 3L;
    private DataSource dataSource;
    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        try (Connection conn = dataSource.getConnection()) {
            String query =
                    "SELECT DISTINCT name, id " +
                    "FROM genres";
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            JsonArray jsonArray = new JsonArray();
            while (rs.next()) {
                String genre_name = rs.getString("name");
                String genre_id = rs.getString("id");
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("genre_name", genre_name);
                jsonObject.addProperty("genre_id", genre_id);
                jsonArray.add(jsonObject);
            }
            rs.close();
            statement.close();

            String title_query =
                    "SELECT DISTINCT SUBSTRING(title,1,1) AS titles " +
                    "FROM movies " +
                    "ORDER BY titles";
            PreparedStatement title_statement = conn.prepareStatement(title_query);
            ResultSet title_rs = title_statement.executeQuery();
            while (title_rs.next()) {
                JsonObject titleObject = new JsonObject();
                titleObject.addProperty("title_name", title_rs.getString("titles"));
                jsonArray.add(titleObject);
            }
            title_rs.close();
            title_statement.close();
            request.getServletContext().log("getting " + jsonArray.size() + " results");
            out.write(jsonArray.toString());
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
