package edu.uci.ics.fabflixmobile.ui.singlemovie;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.databinding.ActivitySingleMovieBinding;
import edu.uci.ics.fabflixmobile.ui.search.SearchActivity;

public class SingleMovieActivity extends AppCompatActivity {
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
        ActivitySingleMovieBinding binding = ActivitySingleMovieBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_single_movie);
        String movieId = getIntent().getExtras().getString("movieId");
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        final StringRequest singleMovieRequest = new StringRequest(
                Request.Method.GET,
                baseURL + "/api/single-movie?id=" + movieId,
                response -> {
                    try {
                        JSONArray obj = new JSONArray(response);
                        String title = obj.getJSONObject(0).get("movie_title").toString();
                        String year = "Released: " + obj.getJSONObject(0).get("movie_year");
                        String director = "Directed by: " + obj.getJSONObject(0).get("movie_director");
                        JSONArray starResultData = (JSONArray) obj.getJSONObject(0).get("movie_stars");
                        String stars = "Starring: ";
                        for (int j=0; j<starResultData.length()-1; j++) {
                            stars += starResultData.getJSONObject(j).get("name") + ", ";
                        }
                        stars += starResultData.getJSONObject(starResultData.length()-1).get("name");
                        JSONArray genreResultData = (JSONArray) obj.getJSONObject(0).get("movie_genres");
                        String genres = "Genres: ";
                        for (int j=0; j<genreResultData.length()-1; j++) {
                            genres += genreResultData.getJSONObject(j).get("name") + ", ";
                        }
                        genres += genreResultData.getJSONObject(genreResultData.length()-1).get("name");
                        TextView single_movie_title = (TextView)findViewById(R.id.single_movie_title);
                        TextView single_movie_subtitle = (TextView)findViewById(R.id.single_movie_subtitle);
                        TextView single_movie_director = (TextView)findViewById(R.id.single_movie_director);
                        TextView single_movie_stars = (TextView)findViewById(R.id.single_movie_stars);
                        TextView single_movie_genres = (TextView)findViewById(R.id.single_movie_genres);
                        single_movie_title.setText(title);
                        single_movie_subtitle.setText(year);
                        single_movie_director.setText(director);
                        single_movie_stars.setText(stars);
                        single_movie_genres.setText(genres);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Log.d("login.error", error.toString());
                }) { };
        final Button singleMovieBackButton = (Button) findViewById(R.id.back_button);
        singleMovieBackButton.setOnClickListener(view -> backToSearchPage());
        queue.add(singleMovieRequest);
    }

    @SuppressLint("SetTextI18n")
    public void backToSearchPage() {
        finish();
        Intent SearchPage = new Intent(SingleMovieActivity.this, SearchActivity.class);
        startActivity(SearchPage);
    }
}