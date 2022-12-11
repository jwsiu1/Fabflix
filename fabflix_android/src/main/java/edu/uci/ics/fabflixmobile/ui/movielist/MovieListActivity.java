package edu.uci.ics.fabflixmobile.ui.movielist;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.data.model.Movie;
import edu.uci.ics.fabflixmobile.databinding.ActivityMovielistBinding;
import edu.uci.ics.fabflixmobile.ui.login.LoginActivity;
import edu.uci.ics.fabflixmobile.ui.search.SearchActivity;
import edu.uci.ics.fabflixmobile.ui.singlemovie.SingleMovieActivity;
import edu.uci.ics.fabflixmobile.databinding.ActivitySingleMovieBinding;
//import edu.uci.ics.fabflixmobile.ui.singleMovie.singleMovieActivity;
import edu.uci.ics.fabflixmobile.ui.movielist.MovieListActivity;
import edu.uci.ics.fabflixmobile.ui.movielist.MovieListViewAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MovieListActivity extends AppCompatActivity {
//    private final String host = "10.0.2.2";
//    private final String port = "8080";
//    private final String domain = "project4";
//    private final String baseURL = "http://" + host + ":" + port + "/" + domain;
    private final String host = "34.208.193.110";
    private final String port = "8443";
    private final String domain = "project4";
    private final String baseURL = "https://" + host + ":" + port + "/" + domain;
    private String total = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movielist);
        String searchTitle = getIntent().getExtras().getString("searchTitle");
        String pageNumber = getIntent().getExtras().getString("pageNumber");
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        final ArrayList<Movie> movies = new ArrayList<>();
        final StringRequest movieRequest = new StringRequest(
                Request.Method.GET,
                baseURL + "/api/movies?&genre_id=&title_name=&movie_title=" + searchTitle +
                        "&star_name=&director_name=&movie_year=&page=" + pageNumber + "&display=20",
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        total = obj.get("total").toString();
                        JSONArray resultData = obj.getJSONArray("movies");
                        for(int i=0; i<resultData.length(); i++) {
                            String movieId = resultData.getJSONObject(i).get("movie_id").toString();
                            String title = resultData.getJSONObject(i).get("movie_title").toString();
                            short year = Short.valueOf(resultData.getJSONObject(i).get("movie_year").toString());
                            String director = resultData.getJSONObject(i).get("movie_director").toString();
                            JSONArray starResultData = (JSONArray) resultData.getJSONObject(i).get("movie_stars");
                            ArrayList<String> star_names = new ArrayList<>();
                            for (int j=0; j<starResultData.length(); j++) {
                                String name = starResultData.getJSONObject(j).get("name").toString();
                                star_names.add(name);
                            }
                            JSONArray genreResultData = (JSONArray) resultData.getJSONObject(i).get("movie_genres");
                            ArrayList<String> genre_names = new ArrayList<>();
                            for (int j=0; j<genreResultData.length(); j++) {
                                String name = genreResultData.getJSONObject(j).get("name").toString();
                                genre_names.add(name);
                            }
                            movies.add(new Movie(movieId, title, year, director, star_names, genre_names));
                        }
                        MovieListViewAdapter adapter = new MovieListViewAdapter(this, movies);
                        ListView listView = findViewById(R.id.list);
                        listView.setAdapter(adapter);
                        listView.setOnItemClickListener((parent, view, position, id) -> {
                            Movie movie = movies.get(position);
                            @SuppressLint("DefaultLocale") String message = String.format("Clicked on position: %d, name: %s, year: %d", position, movie.getName(), movie.getYear());
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            finish();
                            Intent SingleMoviePage = new Intent(MovieListActivity.this, SingleMovieActivity.class);
                            SingleMoviePage.putExtra("movieId", movie.getMovieId());
                            startActivity(SingleMoviePage);
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Log.d("login.error", error.toString());
                }) { };
        final Button backButton = (Button) findViewById(R.id.backButton);
        backButton.setOnClickListener(view -> previousPage());
        final Button nextButton = (Button) findViewById(R.id.nextButton);
        nextButton.setOnClickListener(view -> nextPage());
        queue.add(movieRequest);
    }

    @SuppressLint("SetTextI18n")
    public void previousPage() {
        String searchTitle = getIntent().getExtras().getString("searchTitle");
        String pageNumber = getIntent().getExtras().getString("pageNumber");
        int page = Integer.parseInt(pageNumber);
        if(page > 1) {
            finish();
            Intent MovieListPage = new Intent(MovieListActivity.this, MovieListActivity.class);
            page--;
            pageNumber = String.valueOf(page);
            MovieListPage.putExtra("searchTitle", searchTitle);
            MovieListPage.putExtra("pageNumber", pageNumber);
            startActivity(MovieListPage);
        }
    }

    @SuppressLint("SetTextI18n")
    public void nextPage() {
        String searchTitle = getIntent().getExtras().getString("searchTitle");
        String pageNumber = getIntent().getExtras().getString("pageNumber");
        int page = Integer.parseInt(pageNumber);
        int totalMovies = Integer.parseInt(total);
        if(totalMovies-(20*page) > 0) {
            finish();
            Intent MovieListPage = new Intent(MovieListActivity.this, MovieListActivity.class);
            page++;
            pageNumber = String.valueOf(page);
            MovieListPage.putExtra("searchTitle", searchTitle);
            MovieListPage.putExtra("pageNumber", pageNumber);
            startActivity(MovieListPage);
        }
    }
}