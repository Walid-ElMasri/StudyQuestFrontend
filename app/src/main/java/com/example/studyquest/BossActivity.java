package com.example.studyquest;

import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studyquest.api.ApiService;
import com.example.studyquest.api.RetrofitClient;
import com.example.studyquest.models.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BossActivity extends AppCompatActivity {

    EditText username;
    TextView result;
    ProgressBar progress;
    ApiService api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boss);

        api = RetrofitClient.getClient().create(ApiService.class);

        username = findViewById(R.id.editBossUsername);
        result = findViewById(R.id.textBossResult);
        progress = findViewById(R.id.progressBarBoss);
        Button btn = findViewById(R.id.btnStartBoss);

        btn.setOnClickListener(v -> startBattle());
    }

    void startBattle() {
        String u = username.getText().toString().trim();
        if (u.isEmpty()) {
            username.setError("Required");
            return;
        }

        progress.setVisibility(ProgressBar.VISIBLE);
        result.setText("");

        api.startBoss(new BossStartRequest(u)).enqueue(new Callback<BossStartResponse>() {
            @Override
            public void onResponse(Call<BossStartResponse> call, Response<BossStartResponse> response) {
                progress.setVisibility(ProgressBar.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    result.setText(response.body().toString());
                } else {
                    result.setText("Error starting battle");
                }
            }

            @Override
            public void onFailure(Call<BossStartResponse> call, Throwable t) {
                progress.setVisibility(ProgressBar.GONE);
                result.setText("Error: " + t.getMessage());
            }
        });
    }
}

