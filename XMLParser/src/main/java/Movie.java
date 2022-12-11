import org.w3c.dom.html.HTMLImageElement;

import java.util.ArrayList;
import java.util.Objects;

public class Movie {
    String id;
    String director;
    String title;
    int year;
    ArrayList<String> genres;

    public Movie(String id, String director, String title, int year) {
        this.id = id;
        this.director = director;
        this.title = title;
        this.year = year;
        this.genres = new ArrayList<String>();
    }

    public String getId() {
        return id;
    }

    public String getDirector() {
        return director;
    }

    public String getTitle() {
        return title;
    }

    public int getYear() {
        return year;
    }

    public ArrayList<String> getGenres() {
        return genres;
    }

    public void addGenre(String genre) {
        this.genres.add(genre);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return director.equals(movie.director) && title.equals(movie.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, director, title, year, genres);
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id='" + id + '\'' +
                ", director='" + director + '\'' +
                ", title='" + title + '\'' +
                ", genre='" + genres.toString() + '\'' +
                ", year=" + year +
                '}';
    }
}
