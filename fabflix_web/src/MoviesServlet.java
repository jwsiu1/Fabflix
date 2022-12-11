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
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet(name = "MoviesServlet", urlPatterns = "/api/movies")
public class MoviesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private DataSource dataSource;
    private long TJ_elapsed_time;
    private long elapsedTime;
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
        long startTime = System.nanoTime();
        response.setContentType("application/json");
        String genre_id = request.getParameter("genre_id");
        request.getServletContext().log("getting genre_id: " + genre_id);
        String title_name = request.getParameter("title_name");
        request.getServletContext().log("getting title_name: " + title_name);
        String movieTitle = request.getParameter("movie_title");
        String star_name = request.getParameter("star_name");
        String director_name = request.getParameter("director_name");
        String movieYear = request.getParameter("movie_year");
        request.getServletContext().log("getting title,star,director,movie: " + movieTitle + "," + star_name + ","
                + director_name + "," + movieYear);
        request.getServletContext().log(request.getParameter("page"));
        String pageNumber = request.getParameter("page");
        String displayNumber = request.getParameter("display");
        PrintWriter out = response.getWriter();

        String[] movie_search_tokens = movieTitle.split(" ");
        String fullTextMovieExpression = "";
        for (String token : movie_search_tokens) {
            fullTextMovieExpression += "+" + token + "*" + " ";
        }

        try (Connection conn = dataSource.getConnection()) {
            String query =
                    "SELECT * " +
                    "FROM movies, (SELECT * FROM ratings ORDER BY rating DESC LIMIT 20) as RATE " +
                    "WHERE movies.id = RATE.movieId";
            if(!genre_id.isEmpty() && genre_id != null) {
                query = "SELECT * " +
                        "FROM movies as m, ratings as r, genres as g, genres_in_movies as gim " +
                        "WHERE g.id=" + genre_id + " AND g.id=gim.genreId AND gim.movieId=m.id AND r.movieId=m.id " +
                        "ORDER BY m.title";
            }
            else if(!title_name.isEmpty() && title_name != null) {
                if (title_name.equals("*")) {
                    query = "SELECT * " +
                            "FROM movies,ratings " +
                            "WHERE title REGEXP '^[^a-zA-Z0-9]' AND id=movieId";
                } else {
                    query = "SELECT * " +
                            "FROM movies, ratings " +
                            "WHERE MATCH(title) AGAINST ('" + fullTextMovieExpression + "' IN BOOLEAN MODE) AND id=movieId";
                }
            }
            else if(!star_name.isEmpty() || !movieTitle.isEmpty() || !director_name.isEmpty() || !movieYear.isEmpty()){
                query = "SELECT * " +
                        "FROM movies as m, ratings as r " +
                        "WHERE r.movieId=m.id";
            }
            if(!star_name.isEmpty()) {
                query = "SELECT m.id, m.title, m.year, m.director, r.rating, r.numVotes " +
                        "FROM (SELECT * " +
                              "FROM stars_in_movies, stars " +
                              "WHERE starId=id AND name LIKE '%" + star_name + "%') as s, movies as m, ratings as r " +
                        "WHERE r.movieId=m.id AND s.movieId=m.id";
            }
            if(!movieTitle.isEmpty()) {
                query = query + " AND MATCH(title) AGAINST ('" + fullTextMovieExpression + "' IN BOOLEAN MODE)";
            }
            if(!director_name.isEmpty()) {
                query = query + " AND m.director LIKE '%" + director_name + "%'";
            }
            if(!movieYear.isEmpty()) {
                query = query + " AND m.year=" + movieYear;
            }
            PreparedStatement quantityStatement = conn.prepareStatement(query);
            long TJ_startTime = System.nanoTime();
            ResultSet quantityRs = quantityStatement.executeQuery();
            long TJ_endTime = System.nanoTime();
            TJ_elapsed_time = TJ_endTime - TJ_startTime;
            int totalRows = 0;
            JsonArray quantityArray = new JsonArray();
            while(quantityRs.next()) {
                JsonObject temp = new JsonObject();
                temp.addProperty("movieId", quantityRs.getString("id"));
                quantityArray.add(temp);
            }
            totalRows = quantityArray.size();
            request.getServletContext().log("TOTAL SIZE: " + quantityArray.size());
            if (!pageNumber.isEmpty() && !displayNumber.isEmpty()) {
                int offset = (Integer.valueOf(pageNumber) - 1) * Integer.valueOf(displayNumber);
                query += " LIMIT " + displayNumber + " OFFSET " + offset;
                request.getServletContext().log("QUERY IS: " + query);
            }
            PreparedStatement statement = conn.prepareStatement(query);
            TJ_startTime = System.nanoTime();
            ResultSet rs = statement.executeQuery(query);
            TJ_endTime = System.nanoTime();
            TJ_elapsed_time = TJ_elapsed_time + (TJ_endTime - TJ_startTime);
            JsonArray jsonArray = new JsonArray();
            while (rs.next()) {
                String movie_id = rs.getString("id");
                String movie_title = rs.getString("title");
                String movie_year = rs.getString("year");
                String movie_director = rs.getString("director");
                String movie_rating = rs.getString("rating");
                String movie_votes = rs.getString("numVotes");

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", movie_id);
                jsonObject.addProperty("movie_title", movie_title);
                jsonObject.addProperty("movie_year", movie_year);
                jsonObject.addProperty("movie_director", movie_director);
                jsonObject.addProperty("movie_rating", movie_rating);
                jsonObject.addProperty("movie_votes", movie_votes);

//                GETTING 3 STARS
                String starsQuery =
                        "SELECT * " +
                        "FROM (SELECT * " +
                              "FROM stars_in_movies, stars " +
                              "WHERE stars_in_movies.starId = stars.id) as combined " +
                        "WHERE combined.movieId='" + movie_id + "' limit 3";
                PreparedStatement starsStatement = conn.prepareStatement(starsQuery);

                TJ_startTime = System.nanoTime();
                ResultSet starsRs = starsStatement.executeQuery();
                TJ_endTime = System.nanoTime();
                TJ_elapsed_time = TJ_elapsed_time + (TJ_endTime - TJ_startTime);
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

                //GETTING 3 GENRES
                String genresQuery =
                        "SELECT * " +
                        "FROM (" +
                                "SELECT * " +
                                "FROM genres_in_movies, genres " +
                                "WHERE genres_in_movies.genreId = genres.id) as combined " +
                        "WHERE combined.movieId='" + movie_id + "' limit 3";
                PreparedStatement genresStatement = conn.prepareStatement(genresQuery);

                TJ_startTime = System.nanoTime();
                ResultSet genresRs = genresStatement.executeQuery();
                TJ_endTime = System.nanoTime();
                TJ_elapsed_time = TJ_elapsed_time + (TJ_endTime - TJ_startTime);

                JsonArray genresArray = new JsonArray();
                while(genresRs.next()) {
                    JsonObject genreObj = new JsonObject();
                    genreObj.addProperty("id", genresRs.getString("id"));
                    genreObj.addProperty("name", genresRs.getString("name"));
                    genresArray.add(genreObj);
                }
                jsonObject.add("movie_genres", genresArray);
                genresRs.close();
                genresStatement.close();
                jsonArray.add(jsonObject);
            }
            JsonObject responseObj = new JsonObject();
            responseObj.addProperty("total", totalRows);
            responseObj.add("movies", jsonArray);

            rs.close();
            statement.close();
            request.getServletContext().log("getting " + jsonArray.size() + " results");
            out.write(responseObj.toString());
            response.setStatus(HttpURLConnection.HTTP_OK);

            long endTime = System.nanoTime();
            elapsedTime = endTime - startTime;
        } catch (Exception e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());
            response.setStatus(HttpURLConnection.HTTP_INTERNAL_ERROR);
        } finally {
            String str = request.getServletContext().getRealPath("/");

            FileWriter fileWriter = new FileWriter(str + "log_jdbc.txt", true);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.printf(TJ_elapsed_time + "\n");
            printWriter.close();

            FileWriter fileWriter2 = new FileWriter(str + "log_search.txt", true);
            PrintWriter printWriter2 = new PrintWriter(fileWriter2);
            printWriter2.printf(elapsedTime + "\n");
            printWriter2.close();
            out.close();
        }
    }
}