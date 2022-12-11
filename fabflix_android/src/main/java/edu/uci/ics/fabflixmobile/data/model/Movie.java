package edu.uci.ics.fabflixmobile.data.model;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Movie class that captures movie information for movies retrieved from MovieListActivity
 */
public class Movie {

    private final String movieId;
    private final String name;
    private final short year;
    private final String director;
    private final ArrayList<String> star_names;
    private final ArrayList<String> genre_names;

    public Movie(String movieId, String name, short year, String director, ArrayList<String> star_names, ArrayList<String> genre_names) {
        this.movieId = movieId;
        this.name = name;
        this.year = year;
        this.director = director;
        this.star_names = star_names;
        this.genre_names = genre_names;
    }
    public String getMovieId() { return movieId; }

    public String getName() { return name; }

    public short getYear() {
        return year;
    }

    public String getDirector() { return director; }

    public ArrayList<String> getStarNames() { return star_names; }

    public ArrayList<String> getGenreNames() { return genre_names; }
}