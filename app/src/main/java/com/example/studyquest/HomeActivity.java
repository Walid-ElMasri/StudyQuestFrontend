package com.example.studyquest;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studyquest.api.ApiService;
import com.example.studyquest.api.RetrofitClient;
import com.example.studyquest.models.User;

import java.io.IOException;

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
        Button social = findViewById(R.id.btnSocialHub);
        Button quests = findViewById(R.id.btnQuestsLevels);
        Button boss = findViewById(R.id.btnBossBattle);
        Button tracker = findViewById(R.id.btnProgressTracker);
        Button cosmetics = findViewById(R.id.btnCosmetics);
        Button mentor = findViewById(R.id.btnVoiceMentor);

        btn.setOnClickListener(v -> loadUser());

        // Open real SocialActivity screen
        social.setOnClickListener(v -> startActivity(new Intent(this, SocialActivity.class)));

        // Open QuestsActivity and pass username
        quests.setOnClickListener(v -> {
            String u = username.getText().toString().trim();
            if (TextUtils.isEmpty(u)) {
                username.setError("Username is required.");
                return;
            }
            Intent i = new Intent(this, QuestsActivity.class);
            i.putExtra("username", u);
            startActivity(i);
        });

        cosmetics.setOnClickListener(v -> openPlayground("Customize avatars and badges."));
        boss.setOnClickListener(v -> startActivity(new Intent(this, BossActivity.class)));
        tracker.setOnClickListener(v -> {
            if (u.isEmpty()) u = "nour"; // fallback
            Intent intent = new Intent(this, BossActivity.class);
            intent.putExtra("username", u);
            startActivity(intent);});
        mentor.setOnClickListener(v -> startActivity(new Intent(this, TextAiActivity.class)));
    }

    void loadUser() {
        String u = username.getText().toString().trim();
        if (TextUtils.isEmpty(u)) {
            username.setError("Username is required.");
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
                    // Handle API errors gracefully
                    String errorBody = "Unknown error";
                    if (response.errorBody() != null) {
                        try {
                            errorBody = response.errorBody().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    result.setText("Error: " + response.code() + " - " + errorBody);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                progress.setVisibility(ProgressBar.GONE);
                result.setText("Network Error: " + t.getMessage());
            }
        });
    }

    private void openPlayground(String toast) {
        startActivity(new Intent(this, ApiPlaygroundActivity.class));
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
    }
}