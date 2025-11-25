package com.example.studyquest;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studyquest.api.ApiService;
import com.example.studyquest.api.RetrofitClient;
import com.example.studyquest.models.QuestCompleteResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TimerQuestActivity extends AppCompatActivity {

    TextView textTimer;
    Button btnStart, btnGiveUp;

    ApiService api;
    int questId;
    String username;

    CountDownTimer timer;
    boolean running = false;

    // 25 minutes in ms -> you can change to 60000 for 1 min while testing
    private static final long DURATION_MS = 25L * 60L * 1000L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_quest);

        textTimer = findViewById(R.id.textTimer);
        btnStart = findViewById(R.id.btnStartTimer);
        btnGiveUp = findViewById(R.id.btnGiveUp);

        api = RetrofitClient.getClient().create(ApiService.class);

        questId = getIntent().getIntExtra("questId", -1);
        username = getIntent().getStringExtra("username");

        if (questId == -1 || username == null || username.trim().isEmpty()) {
            Toast.makeText(this, "Invalid quest or user.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        updateTimerText(DURATION_MS);

        btnStart.setOnClickListener(v -> {
            if (!running) {
                startTimer();
            }
        });

        btnGiveUp.setOnClickListener(v -> {
            if (running && timer != null) {
                timer.cancel();
            }
            running = false;
            Toast.makeText(this, "Timer stopped.", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    void startTimer() {
        running = true;

        timer = new CountDownTimer(DURATION_MS, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                updateTimerText(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                running = false;
                updateTimerText(0);
                Toast.makeText(TimerQuestActivity.this,
                        "Well done! Timer finished.", Toast.LENGTH_SHORT).show();
                completeQuest();
            }
        }.start();
    }

    void updateTimerText(long ms) {
        long seconds = ms / 1000;
        long minutes = seconds / 60;
        long remainingSec = seconds % 60;
        String text = String.format("%02d:%02d", minutes, remainingSec);
        textTimer.setText(text);
    }

    void completeQuest() {
        api.completeQuest(questId, username).enqueue(new Callback<QuestCompleteResponse>() {
            @Override
            public void onResponse(Call<QuestCompleteResponse> call,
                                   Response<QuestCompleteResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(TimerQuestActivity.this,
                            response.body().message, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(TimerQuestActivity.this,
                            "Could not complete quest.", Toast.LENGTH_SHORT).show();
                }
                finish();
            }

            @Override
            public void onFailure(Call<QuestCompleteResponse> call, Throwable t) {
                Toast.makeText(TimerQuestActivity.this,
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
    }
}
