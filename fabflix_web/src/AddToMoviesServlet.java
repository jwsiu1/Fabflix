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

@WebServlet(name = "AddToMoviesServlet", urlPatterns = "/api/addToMovies")
public class AddToMoviesServlet extends HttpServlet {
    private static final long serialVersionUID = 13L;
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
        String add_movie_title = request.getParameter("movie_title");
        request.getServletContext().log("getting add_movie_title: " + add_movie_title);
        String add_movie_director = request.getParameter("movie_director");
        request.getServletContext().log("getting add_movie_director: " + add_movie_director);
        String add_movie_year = request.getParameter("movie_year");
        request.getServletContext().log("getting add_movie_year: " + add_movie_year);
        String add_star_name = request.getParameter("single_star");
        request.getServletContext().log("getting add_star_name: " + add_star_name);
        String add_genre_name = request.getParameter("single_genre");
        request.getServletContext().log("getting add_genre_name: " + add_genre_name);

        try (Connection conn = dataSource.getConnection()) {
            String query = "CALL add_movie('" + add_movie_title + "', '" + add_movie_year+ "', '" + add_movie_director +
                    "', '" + add_star_name + "', '" + add_genre_name + "')";
            PreparedStatement statement = conn.prepareStatement(query);
            try {
                statement.executeQuery();
                JsonObject responseJsonObject = new JsonObject();
                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "Movie added");
                response.getWriter().write(responseJsonObject.toString());
            } catch (Exception f) {
                JsonObject responseJsonObject = new JsonObject();
                responseJsonObject.addProperty("status", "failed");
                responseJsonObject.addProperty("message", "Movie already exists");
            }
            statement.close();
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
