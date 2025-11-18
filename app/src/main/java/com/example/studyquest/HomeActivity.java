package com.example.studyquest;

import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studyquest.api.ApiService;
import com.example.studyquest.api.RetrofitClient;
import com.example.studyquest.models.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    EditText username;
    TextView result;
    ProgressBar progress;
    ApiService api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        api = RetrofitClient.getClient().create(ApiService.class);

        username = findViewById(R.id.editUsername);
        result = findViewById(R.id.textResult);
        progress = findViewById(R.id.progressBar);
        Button btn = findViewById(R.id.btnLoadUser);

        btn.setOnClickListener(v -> loadUser());
    }

    void loadUser() {
        String u = username.getText().toString().trim();
        if (u.isEmpty()) {
            username.setError("Required");
            return;
        }

        progress.setVisibility(ProgressBar.VISIBLE);
        result.setText("");

        api.getUser(u).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                progress.setVisibility(ProgressBar.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    result.setText(
                            "Username: " + user.username +
                                    "\nXP: " + user.total_xp +
                                    "\nJoin Date: " + user.join_date
                    );
                } else {
                    result.setText("User not found");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                progress.setVisibility(ProgressBar.GONE);
                result.setText("Error: " + t.getMessage());
            }
        });
    }
}
