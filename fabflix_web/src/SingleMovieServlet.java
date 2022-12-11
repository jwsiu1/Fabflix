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
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet(name = "SingleMovieServlet", urlPatterns = "/api/single-movie")
public class SingleMovieServlet extends HttpServlet {
    private static final long serialVersionUID = 4L;
    private DataSource dataSource;
    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String id = request.getParameter("id");
        request.getServletContext().log("getting id: " + id);
        PrintWriter out = response.getWriter();
        try (Connection conn = dataSource.getConnection()) {
            String query =
                    "SELECT * " +
                    "FROM movies as m, ratings as r " +
                    "WHERE m.id=? and r.movieId=m.id";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, id);
            ResultSet rs = statement.executeQuery();
            JsonArray jsonArray = new JsonArray();

            while (rs.next()) {
                String movieId = rs.getString("id");
                String movieTitle = rs.getString("title");
                String movieYear = rs.getString("year");
                String movieDirector = rs.getString("director");
                String movieRating = rs.getString("rating");
                String movieNumVotes = rs.getString("numVotes");

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", movieId);
                jsonObject.addProperty("movie_title", movieTitle);
                jsonObject.addProperty("movie_year", movieYear);
                jsonObject.addProperty("movie_director", movieDirector);
                jsonObject.addProperty("movie_rating", movieRating);
                jsonObject.addProperty("movie_numVotes", movieNumVotes);

                //STARS
                String starsQuery =
                        "SELECT * " +
                        "FROM (SELECT * " +
                              "FROM stars_in_movies, stars " +
                              "WHERE stars_in_movies.starId = stars.id) as combined " +
                        "WHERE combined.movieId='" + movieId + "'";
                PreparedStatement starsStatement = conn.prepareStatement(starsQuery);
                ResultSet starsRs = starsStatement.executeQuery();
                JsonArray starsArray = new JsonArray();
                while(starsRs.next()) {
                    JsonObject starsObj = new JsonObject();
                    starsObj.addProperty("id", starsRs.getString("id"));
                    starsObj.addProperty("name", starsRs.getString("name"));
                    starsArray.add(starsObj);
                }
                jsonObject.add("movie_stars", starsArray);
                starsRs.close();
                starsStatement.close();

                //GENRES
                String genresQuery = "SELECT * " +
                        "FROM (SELECT * " +
                              "FROM genres_in_movies, genres " +
                              "WHERE genres_in_movies.genreId = genres.id) as combined " +
                        "WHERE combined.movieId='" + movieId + "'";
                PreparedStatement genresStatement = conn.prepareStatement(genresQuery);
                ResultSet genresRs = genresStatement.executeQuery();

                JsonArray genresArray = new JsonArray();
                while(genresRs.next()) {
                    JsonObject genresObj = new JsonObject();
                    genresObj.addProperty("id", genresRs.getString("id"));
                    genresObj.addProperty("name", genresRs.getString("name"));
                    genresArray.add(genresObj);
                }
                jsonObject.add("movie_genres", genresArray);
                genresRs.close();
                genresStatement.close();
                jsonArray.add(jsonObject);
            }
            rs.close();
            statement.close();
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
