package com.example.studyquest;

import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import com.example.studyquest.models.ProgressItem;
import com.example.studyquest.api.ApiService;
import com.example.studyquest.api.RetrofitClient;
import com.example.studyquest.models.ProgressResponse;
import java.util.List;
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

        api.listProgress(u).enqueue(new Callback<List<ProgressItem>>() {
            @Override
            public void onResponse(Call<List<ProgressItem>> call, Response<List<ProgressItem>> response) {
                progress.setVisibility(ProgressBar.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    // Build a nice string showing all study sessions
                    StringBuilder sb = new StringBuilder("Study Sessions:\n\n");
                    for (ProgressItem item : response.body()) {
                        sb.append(item.toString()).append("\n");
                    }
                    result.setText(sb.toString());

                } else {
                    result.setText("Error loading progress: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<ProgressItem>> call, Throwable t) {
                progress.setVisibility(ProgressBar.GONE);
                result.setText("Network error: " + t.getMessage());
            }
        });
    }


}
