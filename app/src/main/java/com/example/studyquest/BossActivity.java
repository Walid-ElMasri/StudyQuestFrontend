package com.example.studyquest;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studyquest.api.ApiService;
import com.example.studyquest.api.RetrofitClient;
import com.example.studyquest.models.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BossActivity extends AppCompatActivity {

    TextView textTimer, textLives, textScore, textQuestion, textQuestionNumber, textFeedback;
    RadioGroup radioAnswers;
    Button btnSubmit, btnForfeit;
    ProgressBar progress;

    ApiService api;
    String currentUser;
    CountDownTimer countDownTimer;
    long timeRemainingMs = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boss);

        api = RetrofitClient.getClient().create(ApiService.class);

        textTimer = findViewById(R.id.textTimer);
        textLives = findViewById(R.id.textLives);
        textScore = findViewById(R.id.textScore);
        textQuestion = findViewById(R.id.textQuestion);
        textQuestionNumber = findViewById(R.id.textQuestionNumber);
        textFeedback = findViewById(R.id.textFeedback);
        radioAnswers = findViewById(R.id.radioAnswers);
        btnSubmit = findViewById(R.id.btnSubmitAnswer);
        btnForfeit = findViewById(R.id.btnForfeit);
        progress = findViewById(R.id.progressBoss);

        currentUser = getIntent().getStringExtra("username");
        if (currentUser == null) currentUser = "nour"; // fallback

        startBossBattle(currentUser, "medium", 5, 180);


        btnSubmit.setOnClickListener(v -> {submitSelectedAnswer();});

        btnForfeit.setOnClickListener(v -> {confirmForfeit();});
    }

    private void startBossBattle(String user, String difficulty, int totalQuestions, int timeLimitSeconds) {
        progress.setVisibility(View.VISIBLE);
        BossStartRequest req = new BossStartRequest(user, difficulty, totalQuestions, timeLimitSeconds);
        api.startBoss(req).enqueue(new Callback<BossStartResponse>() {
            @Override
            public void onResponse(Call<BossStartResponse> call, Response<BossStartResponse> response) {
                progress.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    BossStartResponse body = response.body();
                    textLives.setText(String.valueOf(body.lives != null ? body.lives : 3));
                    textScore.setText("0");
                    int timerSec = (body.timer_seconds != null) ? body.timer_seconds : timeLimitSeconds;
                    startLocalCountdown(timerSec);

                    if (body.current_question != null) {
                        showQuestion(body.current_question.question, body.current_question.choices,
                                body.current_question.number, body.current_question.total);
                    }
                } else {
                    showError("Start failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<BossStartResponse> call, Throwable t) {
                progress.setVisibility(View.GONE);
                showError("Network error: " + t.getMessage());
            }
        });
    }

    private void showQuestion(String question, List<String> choices, int number, int total) {
        textQuestionNumber.setText("Question " + number + "/" + total);
        textQuestion.setText(question);
        textFeedback.setText("");
        radioAnswers.removeAllViews();

        if (choices != null) {
            for (int i = 0; i < choices.size(); i++) {
                RadioButton rb = new RadioButton(this);
                rb.setId(View.generateViewId());
                rb.setText(choices.get(i));
                rb.setTag(i);
                radioAnswers.addView(rb);
            }
        }
    }

    private void submitSelectedAnswer() {
        int checkedId = radioAnswers.getCheckedRadioButtonId();
        if (checkedId == -1) {
            Toast.makeText(this, "Please select an answer.", Toast.LENGTH_SHORT).show();
            return;
        }

        View rbView = findViewById(checkedId);
        Object tag = rbView.getTag();
        int chosenIndex = 0;
        if (tag instanceof Integer) {
            chosenIndex = (Integer) tag;
        } else {
            chosenIndex = radioAnswers.indexOfChild(rbView);
        }

        progress.setVisibility(View.VISIBLE);
        BossAnswerRequest req = new BossAnswerRequest(currentUser, chosenIndex);
        api.submitBossAnswer(req).enqueue(new Callback<BossAnswerResponse>() {
            @Override
            public void onResponse(Call<BossAnswerResponse> call, Response<BossAnswerResponse> response) {
                progress.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    BossAnswerResponse body = response.body();

                    if (Boolean.TRUE.equals(body.ended) || (body.status != null && body.status.length() > 0)) {
                        String status = (body.status != null) ? body.status : "ended";
                        String message = "Battle ended: " + status;
                        if (body.xp_reward != null) {
                            message += "\nXP reward: " + body.xp_reward;
                        }
                        showEndDialog(message);
                        stopCountdown();
                        return;
                    }

                    if (body.feedback != null) {
                        textFeedback.setText(body.feedback);
                    } else if (body.correct != null) {
                        textFeedback.setText(body.correct ? "Correct!" : "Wrong!");
                    }

                    if (body.lives != null) textLives.setText(String.valueOf(body.lives));
                    if (body.score != null) textScore.setText(String.valueOf(body.score));
                    if (body.timer_remaining != null) {
                        restartLocalCountdown(body.timer_remaining);
                    }

                    if (body.next_question != null && body.next_question.question != null) {
                        showQuestion(
                                body.next_question.question,
                                body.next_question.choices,
                                body.next_question.number != null ? body.next_question.number : 1,
                                body.next_question.total != null ? body.next_question.total : 1
                        );
                    } else {
                        fetchCurrentQuestion();
                    }
                } else {
                    showError("Answer failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<BossAnswerResponse> call, Throwable t) {
                progress.setVisibility(View.GONE);
                showError("Network error: " + t.getMessage());
            }
        });
    }

    private void fetchCurrentQuestion() {
        progress.setVisibility(View.VISIBLE);
        api.getBossQuestion(currentUser).enqueue(new Callback<BossQuestionResponse>() {
            @Override
            public void onResponse(Call<BossQuestionResponse> call, Response<BossQuestionResponse> response) {
                progress.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    BossQuestionResponse q = response.body();
                    textLives.setText(String.valueOf(q.lives));
                    textScore.setText(String.valueOf(q.score));
                    restartLocalCountdown(q.timer_remaining);
                    showQuestion(q.question, q.choices, q.number, q.total);
                } else {
                    showError("Could not load question: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<BossQuestionResponse> call, Throwable t) {
                progress.setVisibility(View.GONE);
                showError("Network error: " + t.getMessage());
            }
        });
    }

    private void confirmForfeit() {
        new AlertDialog.Builder(this)
                .setTitle("Forfeit battle")
                .setMessage("Are you sure you want to forfeit the boss battle? This will end the session.")
                .setPositiveButton("Forfeit", (dialog, which) -> {
                    progress.setVisibility(View.VISIBLE);
                    api.forfeit(currentUser).enqueue(new Callback<BossEndResponse>() {
                        @Override
                        public void onResponse(Call<BossEndResponse> call, Response<BossEndResponse> response) {
                            progress.setVisibility(View.GONE);
                            if (response.isSuccessful() && response.body() != null) {
                                BossEndResponse body = response.body();
                                String message = "Forfeited. Status: " + body.status;
                                if (body.xp_reward != null) message += "\nXP: " + body.xp_reward;
                                showEndDialog(message);
                                stopCountdown();
                            } else {
                                showError("Forfeit failed: " + response.code());
                            }
                        }

                        @Override
                        public void onFailure(Call<BossEndResponse> call, Throwable t) {
                            progress.setVisibility(View.GONE);
                            showError("Network error: " + t.getMessage());
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showEndDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Battle Result")
                .setMessage(message)
                .setPositiveButton("OK", (d, w) -> finish())
                .show();
    }

    private void showError(String message) {
        Toast.makeText(BossActivity.this, message, Toast.LENGTH_LONG).show();
    }

    private void startLocalCountdown(int seconds) {
        stopCountdown();
        timeRemainingMs = seconds * 1000L;
        countDownTimer = new CountDownTimer(timeRemainingMs, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeRemainingMs = millisUntilFinished;
                textTimer.setText(formatSeconds((int) (millisUntilFinished / 1000)));
            }

            @Override
            public void onFinish() {
                textTimer.setText("0s");
                api.getBossStatus(currentUser).enqueue(new Callback<BossStatusResponse>() {
                    @Override
                    public void onResponse(Call<BossStatusResponse> call, Response<BossStatusResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            BossStatusResponse s = response.body();
                            if (Boolean.TRUE.equals(s.completed) || (s.timer_remaining != null && s.timer_remaining == 0)) {
                                showEndDialog("Time's up!");
                            } else {
                                if (s.timer_remaining != null) restartLocalCountdown(s.timer_remaining);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<BossStatusResponse> call, Throwable t) {
                    }
                });
            }
        }.start();
    }

    private void restartLocalCountdown(Integer seconds) {
        if (seconds == null) return;
        startLocalCountdown(seconds);
    }

    private void stopCountdown() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    private String formatSeconds(int sec) {
        if (sec < 0) sec = 0;
        long m = TimeUnit.SECONDS.toMinutes(sec);
        long s = sec - (int) TimeUnit.MINUTES.toSeconds(m);
        if (m > 0) return String.format("%dm %ds", m, s);
        return String.format("%ds", s);
    }

    @Override
    protected void onDestroy() {
        stopCountdown();
        super.onDestroy();
    }
}