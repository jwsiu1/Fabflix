package edu.uci.ics.fabflixmobile.ui.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.databinding.ActivityLoginBinding;
import edu.uci.ics.fabflixmobile.ui.search.SearchActivity;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private TextView message;

//    private final String host = "10.0.2.2";
//    private final String port = "8080";
//    private final String domain = "project4";
//    private final String baseURL = "http://" + host + ":" + port + "/" + domain;
    private final String host = "34.208.193.110";
    private final String port = "8443";
    private final String domain = "project4";
    private final String baseURL = "https://" + host + ":" + port + "/" + domain;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        username = binding.username;
        password = binding.password;
        message = binding.message;
        final Button loginButton = binding.login;
        loginButton.setOnClickListener(view -> login());
    }

    @SuppressLint("SetTextI18n")
    public void login() {
        message.setText("Logging in...");
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        final StringRequest loginRequest = new StringRequest(
                Request.Method.POST,
                baseURL + "/api/login",
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        String status = obj.getString("status");
                        if (status.equals("success")) {
                            message.setText("Successfully logged in");
                            finish();
                            Intent SearchPage = new Intent(LoginActivity.this, SearchActivity.class);
                            startActivity(SearchPage);
                        }
                        else {
                            message.setText("Login failed, please try again");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Log.d("login.error", error.toString());
                }) {
            @Override
            protected Map<String, String> getParams() {
                final Map<String, String> params = new HashMap<>();
                params.put("email", username.getText().toString());
                params.put("password", password.getText().toString());
                return params;
            }
        };
        queue.add(loginRequest);
    }
}