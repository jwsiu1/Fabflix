//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.sql.*;
import javax.print.DocFlavor;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.sun.source.tree.NewArrayTree;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLParser {
    ArrayList<Actor> actors = new ArrayList<Actor>();
    ArrayList<Movie> movies = new ArrayList<Movie>();

    //Maps movie id to a list of actor ids
    HashMap<String, ArrayList<String>> casts = new HashMap<>();

    HashMap<String, Actor> existingActors = new HashMap<>();
    HashMap<String, Integer> existingGenres = new HashMap<String, Integer>();
    HashMap<String, ArrayList<Movie>> existingMovies = new HashMap<String, ArrayList<Movie>>();
    HashSet<String> movieIds = new HashSet<String>();
    int startingGenreLength;
    int inconsistencies = 0;
    String duplicates = "";
    int genresInMoviesInserts = 0;
    int starsInMoviesInserts = 0;

    Document actorsDom;
    Document moviesDom;
    Document castDom;
    int currActorID;

    public XMLParser() {
    }

    public void runExample() {
        long start = System.currentTimeMillis();

        this.currActorID = this.getLatestActorIDFromDb();
        this.existingActors = this.getExistingActors();
        this.existingGenres = this.getExistingGenres();
        this.startingGenreLength = this.existingGenres.size();
        this.existingMovies = this.getExistingMovies();

//        for (String title : this.existingMovies.keySet()) {
//            System.out.println(title + "----");
//            for (Movie movie : this.existingMovies.get(title)) {
//                System.out.println("\t" + movie.toString());
//            }
//        }

        System.out.println("PARSING ACTORS");
        this.actorsDom = this.parseXmlFile("XML/actors63.xml");
        this.parseActorsDocument(this.actorsDom);
//        this.printData(this.actors, "actors");
        this.removeExistingActors();
        System.out.println("\nINSERTING ACTORS INTO DB");
        this.insertActors();
        System.out.println("\n");
//

        System.out.println("PARSING MOVIES");
        this.moviesDom = this.parseXmlFile("XML/mains243.xml");
        this.parseMoviesDocument(this.moviesDom);
//        this.printData(this.movies, "movies");
        this.removeExistingMovies();
        System.out.println("\nINSERTING MOVIES INTO DB");
        this.insertMovies();
        System.out.println("\n");
//
        this.existingMovies = this.getExistingMovies();
        System.out.println("\nINSERTING genres INTO DB");
        this.insertGenres();

        this.existingActors = this.getExistingActors();
        this.existingMovies = getExistingMovies();
        System.out.println("PARSING CASTS");
        this.castDom = this.parseXmlFile("XML/casts124.xml");
        System.out.println("\nINSERTING stars_in_movies INTO DB");
        this.parseCastDocument(this.castDom);
        this.insertCast();

        long end = System.currentTimeMillis();
        System.out.println("\nSUMMARY:");
        System.out.println("Inserted " + this.actors.size() + " actors into STARS table");
        System.out.println("Inserted " + this.movies.size() + " MOVIES into MOVIES table");
        System.out.println("Inserted " + (this.existingGenres.size() - this.startingGenreLength) + " genres into genres table");
        System.out.println("Inserted " + (this.genresInMoviesInserts) + " genres into genres_in_movies table");
        System.out.println("Inserted " + (this.starsInMoviesInserts) + " stars into stars_in_movies table");
        System.out.println("Inconsistencies found: " + this.inconsistencies);
        System.out.println(this.duplicates);
        System.out.println("total time taken = " + (end - start) + " ms");
    }

    private int getLatestActorIDFromDb() {
        String idString = "";
        int num = -1;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to the test database
            Connection conn = DriverManager.getConnection("jdbc:" + Parameters.dbtype + ":///" + Parameters.dbname + "?autoReconnect=true&useSSL=false&allowPublicKeyRetrieval=TRUE",
                    Parameters.username, Parameters.password);
            String query = "SELECT id FROM stars ORDER BY id DESC LIMIT 1";
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                idString = rs.getString("id");
                num = Integer.parseInt(idString.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")[1]);
            }

            statement.close();
            rs.close();

            if (conn != null) {
                conn.close();
            }
        } catch (Exception var8) {
            var8.printStackTrace();
        }

        return num;
    }

    private HashMap<String, Integer> getExistingGenres() {
        HashMap<String, Integer> genres = new HashMap<String, Integer>();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to the test database
            Connection conn = DriverManager.getConnection("jdbc:" + Parameters.dbtype + ":///" + Parameters.dbname + "?autoReconnect=true&useSSL=false",
                    Parameters.username, Parameters.password);

            String query = "SELECT * from genres";
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String name = rs.getString("name");
                int id = rs.getInt("id");
                genres.put(name, id);
            }

            statement.close();
            rs.close();

            if (conn != null) {
                conn.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return genres;
    }

    private HashMap<String, Actor> getExistingActors() {
        HashMap<String, Actor> actors = new HashMap<String, Actor>();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to the test database
            Connection conn = DriverManager.getConnection("jdbc:" + Parameters.dbtype + ":///" + Parameters.dbname + "?autoReconnect=true&useSSL=false",
                    Parameters.username, Parameters.password);

            String query = "SELECT * from stars";
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String name = rs.getString("name");
                Actor actor = new Actor(rs.getString("id"), name, rs.getInt("birthYear"));
                actors.put(rs.getString("name"),actor);
            }

            statement.close();
            rs.close();

            if (conn != null) {
                conn.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return actors;
    }

    private HashMap<String, ArrayList<Movie>> getExistingMovies() {
        HashMap<String, ArrayList<Movie>> movies = new HashMap<String, ArrayList<Movie>>();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to the test database
            Connection conn = DriverManager.getConnection("jdbc:" + Parameters.dbtype + ":///" + Parameters.dbname + "?autoReconnect=true&useSSL=false",
                    Parameters.username, Parameters.password);

            String query = "SELECT * from movies";
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String id = rs.getString("id");
                String title = rs.getString("title");
                String director = rs.getString("director");
                int year = rs.getInt("year");
                Movie movie = new Movie(id, director, title, year);

                ArrayList<Movie> moviesWithTitle;
                if (movies.containsKey(title)) {
                    moviesWithTitle = movies.get(title);
                } else {
                    moviesWithTitle = new ArrayList<>();
                }
                moviesWithTitle.add(movie);
                movies.put(title, moviesWithTitle);
            }

            statement.close();
            rs.close();

            if (conn != null) {
                conn.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return movies;
    }

    private Document parseXmlFile(String file) {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document dom = documentBuilder.parse(file);
            return dom;
        } catch (SAXException | IOException | ParserConfigurationException var5) {
            var5.printStackTrace();
            return null;
        }
    }

    private void parseActorsDocument(Document dom) {
        Element documentElement = dom.getDocumentElement();
        NodeList nodeList = documentElement.getElementsByTagName("actor");
        if (nodeList != null) {
            for(int i = 0; i < nodeList.getLength(); ++i) {
                Element element = (Element)nodeList.item(i);
                Actor actor = this.parseActor(element);
                actors.add(actor);
            }
        }

    }

    private void parseCastDocument(Document dom) {
        Element documentElement = dom.getDocumentElement();
        NodeList nodeList = documentElement.getElementsByTagName("dirfilms");
        if (nodeList != null) {
            for(int i = 0; i < nodeList.getLength(); ++i) {
                Element element = (Element)nodeList.item(i);
//                String director = this.getTextValue(element, "is");
                NodeList films = element.getElementsByTagName("filmc");
                if (films != null) {
                    for (int j=0; j<films.getLength(); ++j) {
                        Element film = (Element)films.item(j);
                        String title = this.getTextValue(film, "t");
                        String id = this.getTextValue(film, "f");
                        if (checkIfMovieExists(title, id) && id.length() <= 10) {
                            ArrayList<String> cast = this.parseCast(film);
                            if (cast.size() != 0) {
                                this.casts.put(id, cast);
                            }
                        }
                    }
                }
            }
        }

    }

    private boolean checkIfMovieExists(String title, String id) {
        if (this.existingMovies.containsKey(title)) {
            ArrayList<Movie> movies = this.existingMovies.get(title);
            for (Movie movie : movies) {
                if (title.equals(movie.getTitle()) && id.equals(movie.getId())) {
                    return true;
                }
            }
        }

        return false;
    }

    private ArrayList<String> parseCast(Element film) {
        ArrayList<String> cast = new ArrayList<>();

        NodeList actors = film.getElementsByTagName("m");
        if (actors != null) {
            for (int i=0; i<actors.getLength(); ++i) {
                Element actor = (Element)actors.item(i);
                String id = this.getTextValue(actor, "i");
                String movieTitle = this.getTextValue(actor, "t");
                String actorName = this.getTextValue(actor, "a");

                if (this.existingActors.containsKey(actorName)) {
                    cast.add(this.existingActors.get(actorName).getId());
                }

            }
        }
        return cast;
    }

    private Actor parseActor(Element element) {
        String name = this.getTextValue(element, "stagename");
        int birthYear = this.getIntValue(element, "dob");

        this.currActorID += 1;

        String actorID = "nm" + currActorID;
        return new Actor(actorID, name, birthYear);
    }

    private void parseMoviesDocument(Document dom) {
        Element documentElement = dom.getDocumentElement();
        NodeList nodeList = documentElement.getElementsByTagName("directorfilms");
        if (nodeList != null) {
            for(int i = 0; i < nodeList.getLength(); ++i) {
                Element element = (Element)nodeList.item(i);
                String director = getTextValue(element, "dirname");

                NodeList films = element.getElementsByTagName("film");
                for (int j=0; j<films.getLength(); ++j) {
                    Movie movie = parseMovie((Element)films.item(j), director);

                    String inconsistency = "";
                    if (movie.getTitle() == null || movie.getTitle().isEmpty()) {
                        System.out.println("INCONSISTENCY: Element name(t) is blank");
                        inconsistency += " missing title";
                    }
                    if (movie.getYear() == -1) {
                        inconsistency += " invalid year";
                    }
                    if (movie.getId() == null || movie.getId().isEmpty()) {
                        System.out.println("INCONSISTENCY: Element name(fid) is blank");
                        inconsistency += " missing id";
                    }
                    if (movie.getDirector() == null || movie.getDirector().isEmpty()) {
                        System.out.println("INCONSISTENCY: Element name(dirname) is blank");
                        inconsistency += " missing director";
                    }
                    if (movie.getGenres().size() == 0) {
                        System.out.println("INCONSISTENCY: Element name(cat) is blank");
                        inconsistency += " missing genre";
                    }
                    if (this.movieIds.contains(movie.getId())) {
                        System.out.println("INCONSISTENCY: Element name(fid) already exists");
                        inconsistency += " id already exists";
                    } else {
                        this.movieIds.add(movie.getId());
                    }

                    if (inconsistency.isEmpty()) {
                        movies.add(movie);
                    } else {
                        inconsistency = "\tDetails-> " + movie.getTitle() + " is" + inconsistency;
                        System.out.println(inconsistency);
                    }
                }
            }
        }

    }

    private Movie parseMovie(Element element, String director) {
        String id = getTextValue(element, "fid");
        String title = getTextValue(element, "t");
        int year = getIntValue(element, "year");

        Movie movie = new Movie(id, director, title, year);

        NodeList genreList = element.getElementsByTagName("cat");
        for(int i = 0; i < genreList.getLength(); ++i) {
            if (genreList.item(i).getFirstChild() != null) {
                if (genreList.item(i).getFirstChild().getNodeValue() != null) {

                    String genre = genreList.item(i).getFirstChild().getNodeValue();
                    movie.addGenre(genre);
                }
            }

        }

        return movie;
    }


    private String getTextValue(Element element, String tagName) {
        String textVal = null;
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList != null && nodeList.getLength() > 0) {
            try {
                textVal = nodeList.item(0).getFirstChild().getNodeValue().strip();
            } catch (NullPointerException e) {
                return "";
            }
        }

        return textVal;
    }

    private int getIntValue(Element element, String tagName) {
        String textVal = this.getTextValue(element, tagName);
        try {
            int val = Integer.parseInt(textVal);
            return val;
        } catch (NumberFormatException e) {
            if (textVal == null || textVal.equals("")) {
                return -1;
            }
            System.out.println("INCONSISTENCY: Element name (" + tagName + ") and Node value (" + textVal + ")");
            this.inconsistencies += 1;
            return -1;
        }
    }

    /**
     * Iterate through the list and print the
     * content to console
     */
    private void printData(ArrayList<?> arr, String dataName) {

        System.out.println("Total parsed " + arr.size() + " " + dataName);

        for (int i=0; i<arr.size(); i++) {
            System.out.println("\t" + arr.get(i).toString());
        }
    }


    private void removeExistingActors() {
        ArrayList<String> duplicates = new ArrayList<String>();
        ArrayList<Actor> newActors = new ArrayList<Actor>();

        for (Actor actor: actors) {
            if (this.existingActors.containsKey(actor.getName())) {
                duplicates.add(actor.getName());
            } else {
                newActors.add(actor);
            }
        }

        System.out.println(duplicates.size() + " DUPLICATE ACTORS FOUND");
        this.duplicates += duplicates.size() + " DUPLICATE ACTORS FOUND\n";
        this.actors = newActors;
    }

    private void removeExistingMovies() {

        ArrayList<String> duplicates = new ArrayList<String>();
        ArrayList<Movie> newMovies = new ArrayList<Movie>();

        for (Movie movie: movies) {
            if (this.existingMovies.containsKey(movie.getTitle())) {
                boolean match = false;
                for (Movie movieWithTitle : this.existingMovies.get(movie.getTitle())) {
//                    System.out.println("Comparing");
//                    System.out.println("\t" + movie.toString());
//                    System.out.println("\t" + movieWithTitle.toString());
                    if (movieWithTitle.equals(movie) || this.issubsequence(movie.getDirector().replace(".", ""), movieWithTitle.getDirector())) {
//                        System.out.println("Duplicate found " + movieWithTitle.toString() + "\n" + movie.toString());
                        duplicates.add(movie.getTitle());
                        match = true;
                        break;
                    }
                }
                if (!match) {
//                    System.out.println("Duplicate not found " + movie.toString());
                    newMovies.add(movie);
                }
            } else {
//                System.out.println("Duplicate not found " + movie.toString());
                newMovies.add(movie);
            }
        }

        System.out.println();
        System.out.println(duplicates.size() + " DUPLICATE MOVIES FOUND");
        this.duplicates += duplicates.size() + " DUPLICATE MOVIES FOUND\n";
        this.movies = newMovies;
    }


    private boolean insertActors() {

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to the test database
            Connection conn = DriverManager.getConnection("jdbc:" + Parameters.dbtype + ":///" + Parameters.dbname + "?autoReconnect=true&useSSL=false&rewriteBatchedStatements=true",
                    Parameters.username, Parameters.password);
//            conn.setAutoCommit(false);
//            conn.commit();

            String query = "INSERT INTO stars VALUES(?,?,?)";
            PreparedStatement statement = conn.prepareStatement(query);
            for (Actor actor: actors) {
                System.out.println("Inserting " + actor.toString());


                statement.setString(1, actor.getId());
                statement.setString(2, actor.getName());

                if (actor.getBirthYear() != -1) {
                    statement.setInt(3, actor.getBirthYear());
                } else {
                    statement.setNull(3, Types.BIGINT);
                }

                statement.addBatch();
            }
            statement.executeBatch();
//            conn.rollback();
//            conn.setAutoCommit(true);
            System.out.println("INSERTED " + this.actors.size() + " ACTORS INTO STARS TABLE");

            statement.close();



            if (conn != null) {
                conn.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private boolean insertMovies() {

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to the test database
            Connection conn = DriverManager.getConnection("jdbc:" + Parameters.dbtype + ":///" + Parameters.dbname + "?autoReconnect=true&useSSL=false&rewriteBatchedStatements=true",
                    Parameters.username, Parameters.password);
//            conn.setAutoCommit(false);
//            conn.commit();

            String query = "INSERT INTO movies (id, title, director, year) VALUES(?,?,?,?);";
            PreparedStatement statement = conn.prepareStatement(query);
            for (Movie movie: movies) {

                statement.setString(1, movie.getId());
                statement.setString(2, movie.getTitle());
                statement.setString(3, movie.getDirector());
                statement.setInt(4, movie.getYear());

                statement.addBatch();
            }
            int[] rows = statement.executeBatch();
//            conn.rollback();
//            conn.setAutoCommit(true);
            statement.close();
            System.out.println("INSERTED " + this.movies.size() + " MOVIES INTO MOVIES TABLE");

            if (rows.length > 0) {
                insertRatings();
            }

            if (conn != null) {
                conn.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return true;
    }

    private boolean insertRatings() {
        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to the test database
            Connection conn = DriverManager.getConnection("jdbc:" + Parameters.dbtype + ":///" + Parameters.dbname + "?autoReconnect=true&useSSL=false&rewriteBatchedStatements=true",
                    Parameters.username, Parameters.password);
//            conn.setAutoCommit(false);
//            conn.commit();

            System.out.println("INSERTING RATINGS");
            String query = "INSERT INTO ratings (movieId, rating, numVotes) VALUES(?, 0.0, 0);";
            PreparedStatement statement = conn.prepareStatement(query);
            for (Movie movie: movies) {

                statement.setString(1, movie.getId());
                statement.addBatch();
            }
            statement.executeBatch();
//            conn.rollback();
//            conn.setAutoCommit(true);
            statement.close();

            if (conn != null) {
                conn.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    private boolean insertGenres() {
        if (movies.size() == 0) {
            System.out.println("No genres to add - no new movies");
            return true;
        }
        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to the test database
            Connection conn = DriverManager.getConnection("jdbc:" + Parameters.dbtype + ":///" + Parameters.dbname + "?autoReconnect=true&useSSL=false&rewriteBatchedStatements=true",
                    Parameters.username, Parameters.password);
//            conn.setAutoCommit(false);
//            conn.commit();

            for (Movie movie: movies) {
                if (!this.existingMovies.containsKey(movie.getTitle())) {
                    System.out.println("Inserting to genres_in_movies failed because (" + movie.getTitle() + ") is not in db");
                    continue;
                }
                boolean movieIdExists = false;
                for (Movie existingMovie : this.existingMovies.get(movie.getTitle())) {
                    if (existingMovie.getId().equals(movie.getId())) {
                        movieIdExists = true;
                    }
                }

                if (!movieIdExists) {
                    System.out.println("Inserting to genres_in_movies failed because (" + movie.getTitle() + ") with id " + movie.getId() + " is not in db");
                    continue;
                }


                for (String genre : movie.getGenres()) {

                    String fullGenre = getFullGenreName(genre);

                    PreparedStatement genresInMovieStatement = conn.prepareStatement("INSERT INTO genres_in_movies (genreId, movieId) VALUES(?,?)");

                    if (this.existingGenres.containsKey(genre)) {
                        System.out.println(genre + " exists");
                        genresInMovieStatement.setInt(1, this.existingGenres.get(genre));
                    } else if (fullGenre != null) {
                        System.out.println(genre + " is abbr of " + fullGenre);
                        genresInMovieStatement.setInt(1, this.existingGenres.get(fullGenre));
                    } else {
                        System.out.println(genre + " doesn't exists");
                        PreparedStatement genreStatement = conn.prepareStatement("INSERT INTO genres (name) VALUES(?)");
                        genreStatement.setString(1, genre);
                        int rows = genreStatement.executeUpdate();
                        genreStatement.close();

                        if (rows > 0) {
                            this.existingGenres = this.getExistingGenres();
                            System.out.println(this.existingGenres.size());
                        }

                        genresInMovieStatement.setInt(1, this.existingGenres.get(genre));
                    }

                    genresInMovieStatement.setString(2, movie.getId());
                    genresInMovieStatement.executeUpdate();
                    genresInMovieStatement.close();
                    this.genresInMoviesInserts += 1;
                }
            }

            System.out.println();
            System.out.println("Inserted " + (this.existingGenres.size() - this.startingGenreLength) + " genres into genres table");
            System.out.println("Inserted " + (this.genresInMoviesInserts) + " genres into genres_in_movies table");

            if (conn != null) {
                conn.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private String getFullGenreName(String currGenre) {
        for (String genre : this.existingGenres.keySet()) {
            if (issubsequence(currGenre.toLowerCase(), genre.toLowerCase())) {
                return genre;
            }
        }
        return null;
     }

    static boolean issubsequence(String s1, String s2)
    {
        int n = s1.length(), m = s2.length();
        int i = 0, j = 0;
        while (i < n && j < m) {
            if (s1.charAt(i) == s2.charAt(j))
                i++;
            j++;
        }
        return i == n;
    }

    private void insertCast() {

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection conn = DriverManager.getConnection("jdbc:" + Parameters.dbtype + ":///" + Parameters.dbname + "?autoReconnect=true&useSSL=false&rewriteBatchedStatements=true",
                    Parameters.username, Parameters.password);

            String query = "INSERT INTO stars_in_movies (movieId, starId) VALUES(?,?);";
            PreparedStatement statement = conn.prepareStatement(query);
            for (String movieId: this.casts.keySet()) {
                for (String actorId : this.casts.get(movieId) ) {
                    this.starsInMoviesInserts += 1;

                    statement.setString(1, movieId);
                    statement.setString(2, actorId);
                    statement.addBatch();
                }
            }
            statement.executeBatch();
            statement.close();
            System.out.println("INSERTED " + this.starsInMoviesInserts + " ENTRIES INTO STARS_IN_MOVIES TABLE");


            if (conn != null) {
                conn.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        XMLParser domParserExample = new XMLParser();
        domParserExample.runExample();
    }
}
