package com.example.studyquest;

import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studyquest.api.ApiService;
import com.example.studyquest.api.RetrofitClient;
import com.example.studyquest.models.ProgressResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProgressActivity extends AppCompatActivity {

    EditText username;
    TextView result;
    ProgressBar progress;
    ApiService api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        api = RetrofitClient.getClient().create(ApiService.class);

        username = findViewById(R.id.editUsernameProgress);
        result = findViewById(R.id.textProgressResult);
        progress = findViewById(R.id.progressBarProgress);
        Button btn = findViewById(R.id.btnLoadProgress);

        btn.setOnClickListener(v -> loadProgress());
    }

    void loadProgress() {
        String u = username.getText().toString().trim();
        if (u.isEmpty()) {
            username.setError("Required");
            return;
        }

        progress.setVisibility(ProgressBar.VISIBLE);
        result.setText("");

        api.getProgress(u).enqueue(new Callback<ProgressResponse>() {
            @Override
            public void onResponse(Call<ProgressResponse> call, Response<ProgressResponse> response) {
                progress.setVisibility(ProgressBar.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    result.setText(response.body().toString());
                } else {
                    result.setText("Error loading progress");
                }
            }

            @Override
            public void onFailure(Call<ProgressResponse> call, Throwable t) {
                progress.setVisibility(ProgressBar.GONE);
                result.setText("Error: " + t.getMessage());
            }
        });
    }
}
