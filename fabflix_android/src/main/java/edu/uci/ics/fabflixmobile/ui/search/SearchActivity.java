package edu.uci.ics.fabflixmobile.ui.search;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.widget.Button;
import android.widget.EditText;
import edu.uci.ics.fabflixmobile.databinding.ActivitySearchBinding;
import edu.uci.ics.fabflixmobile.ui.movielist.MovieListActivity;

public class SearchActivity extends AppCompatActivity {

    private EditText search_title;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySearchBinding binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        search_title = binding.searchTitle;
        final Button searchButton = binding.submitSearch;
        searchButton.setOnClickListener(view -> redirectToMovieListPage());
    }

    @SuppressLint("SetTextI18n")
    public void redirectToMovieListPage() {
        finish();
        Intent MovieListPage = new Intent(SearchActivity.this, MovieListActivity.class);
        title = search_title.getText().toString();
        MovieListPage.putExtra("searchTitle", title);
        MovieListPage.putExtra("pageNumber", "1");
        startActivity(MovieListPage);
    }
}