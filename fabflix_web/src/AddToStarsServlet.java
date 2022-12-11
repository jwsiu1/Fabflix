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
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet(name = "AddToStarsServlet", urlPatterns = "/api/addToStars")
public class AddToStarsServlet extends HttpServlet {
    private static final long serialVersionUID = 12L;
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
        String add_star_name = request.getParameter("star_name");
        request.getServletContext().log("getting add_star_name: " + add_star_name);
        String add_birth_year = request.getParameter("birth_year");
        request.getServletContext().log("getting add_birth_year: " + add_birth_year);
        try (Connection conn = dataSource.getConnection()) {
            String query = "SELECT MAX(id) as id FROM stars";
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            String id = "";
            if(rs.next()) {
                id = rs.getString("id");
            }
            int tempId = Integer.parseInt(id.substring(2, 9));
            tempId++;
            id = "nm" + tempId;
            rs.close();
            statement.close();
            if(!add_birth_year.isEmpty()) {
                String addQuery = "INSERT INTO stars (id, name, birthYear) VALUES (?,?,?)";
                PreparedStatement insertStatement = conn.prepareStatement(addQuery);
                insertStatement.setString(1, id);
                insertStatement.setString(2, add_star_name);
                insertStatement.setInt(3, Integer.valueOf(add_birth_year));
                insertStatement.executeUpdate();
                insertStatement.close();
            }
            else {
                String addQuery = "INSERT INTO stars (id, name) VALUES (?,?)";
                PreparedStatement insertStatement = conn.prepareStatement(addQuery);
                insertStatement.setString(1, id);
                insertStatement.setString(2, add_star_name);
                insertStatement.executeUpdate();
                insertStatement.close();
            }
            JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("status", "success");
            responseJsonObject.addProperty("message", "Star added");
            response.getWriter().write(responseJsonObject.toString());
            response.setStatus(HttpURLConnection.HTTP_OK);
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
