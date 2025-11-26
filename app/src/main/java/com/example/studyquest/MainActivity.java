package com.example.studyquest;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studyquest.api.ApiService;
import com.example.studyquest.api.RetrofitClient;
import com.example.studyquest.models.BossAnswerRequest;
import com.example.studyquest.models.BossAnswerResponse;
import com.example.studyquest.models.BossStartRequest;
import com.example.studyquest.models.BossStartResponse;
import com.example.studyquest.models.BossStatusResponse;
import com.example.studyquest.models.ProgressItem;
import com.example.studyquest.models.ProgressLogRequest;
import com.example.studyquest.models.ProgressLogResponse;
import com.example.studyquest.models.ProgressStats;
import com.example.studyquest.models.TextAiReflection;
import com.example.studyquest.models.TextAiRequest;
import com.example.studyquest.models.TextAiResponse;
import com.example.studyquest.models.User;
import com.example.studyquest.models.UserCreateRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ApiService api;
    private EditText usernameInput;
    private EditText emailInput;
    private EditText minutesInput;
    private EditText subjectInput;
    private EditText aiPromptInput;
    private EditText bossAnswerInput;
    private TextView outputView;
    private ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        api = RetrofitClient.getClient().create(ApiService.class);

        usernameInput = findViewById(R.id.editMainUsername);
        emailInput = findViewById(R.id.editMainEmail);
        minutesInput = findViewById(R.id.editMainMinutes);
        subjectInput = findViewById(R.id.editMainSubject);
        aiPromptInput = findViewById(R.id.editMainAiPrompt);
        bossAnswerInput = findViewById(R.id.editMainBossAnswer);
        outputView = findViewById(R.id.textMainOutput);
        loading = findViewById(R.id.progressMain);

        findViewById(R.id.btnMainCreateUser).setOnClickListener(v -> createUser());
        findViewById(R.id.btnMainFetchUser).setOnClickListener(v -> fetchUser());
        findViewById(R.id.btnMainListUsers).setOnClickListener(v -> listUsers());
        findViewById(R.id.btnMainLogProgress).setOnClickListener(v -> logProgress());
        findViewById(R.id.btnMainProgressStats).setOnClickListener(v -> loadProgressStats());
        findViewById(R.id.btnMainListProgress).setOnClickListener(v -> listProgress());
        findViewById(R.id.btnMainStartBoss).setOnClickListener(v -> startBoss());
        findViewById(R.id.btnMainBossStatus).setOnClickListener(v -> bossStatus());
        findViewById(R.id.btnMainSubmitBossAnswer).setOnClickListener(v -> submitBossAnswer());
        findViewById(R.id.btnMainAskAi).setOnClickListener(v -> askAi());
        findViewById(R.id.btnMainAiHistory).setOnClickListener(v -> aiHistory());
    }

    private String requireText(EditText field) {
        String value = field.getText().toString().trim();
        if (value.isEmpty()) {
            field.setError("Required");
            return null;
        }
        field.setError(null);
        return value;
    }

    private void setLoading(boolean show) {
        loading.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void setOutput(String text) {
        outputView.setText(text);
    }

    private void createUser() {
        String username = requireText(usernameInput);
        if (username == null) return;
        String email = emailInput.getText().toString().trim();

        setLoading(true);
        setOutput("Creating user...");

        api.createUser(new UserCreateRequest(username, email)).enqueue(new Callback<User>() {
            @Override public void onResponse(Call<User> call, Response<User> resp) {
                setLoading(false);
                if (resp.isSuccessful() && resp.body() != null) {
                    User user = resp.body();
                    setOutput("Created user: " + user.username +
                            "\nEmail: " + user.email +
                            "\nXP: " + user.total_xp +
                            "\nJoined: " + user.join_date);
                } else setOutput("Error creating user: " + resp.code());
            }
            @Override public void onFailure(Call<User> call, Throwable t) {
                setLoading(false);
                setOutput("Network error: " + t.getMessage());
            }
        });
    }

    private void fetchUser() {
        String username = requireText(usernameInput);
        if (username == null) return;

        setLoading(true);
        setOutput("Loading user...");

        api.getUser(username).enqueue(new Callback<User>() {
            @Override public void onResponse(Call<User> call, Response<User> resp) {
                setLoading(false);
                if (resp.isSuccessful() && resp.body() != null) {
                    User user = resp.body();
                    setOutput("Username: " + user.username +
                            "\nEmail: " + user.email +
                            "\nXP: " + user.total_xp +
                            "\nJoined: " + user.join_date);
                } else setOutput("User not found (" + resp.code() + ")");
            }
            @Override public void onFailure(Call<User> call, Throwable t) {
                setLoading(false);
                setOutput("Network error: " + t.getMessage());
            }
        });
    }

    private void listUsers() {
        setLoading(true);
        setOutput("Loading users...");

        api.listUsers().enqueue(new Callback<List<User>>() {
            @Override public void onResponse(Call<List<User>> call, Response<List<User>> resp) {
                setLoading(false);
                if (resp.isSuccessful() && resp.body() != null) {
                    StringBuilder sb = new StringBuilder("Users:\n");
                    for (User u : resp.body()) {
                        sb.append("- ").append(u.username);
                        if (u.total_xp > 0) sb.append(" (").append(u.total_xp).append(" XP)");
                        sb.append("\n");
                    }
                    setOutput(sb.toString());
                } else setOutput("Error listing users: " + resp.code());
            }
            @Override public void onFailure(Call<List<User>> call, Throwable t) {
                setLoading(false);
                setOutput("Network error: " + t.getMessage());
            }
        });
    }

    private void logProgress() {
        String username = requireText(usernameInput);
        String minutesStr = requireText(minutesInput);
        if (username == null || minutesStr == null) return;

        int minutes;
        try {
            minutes = Integer.parseInt(minutesStr);
        } catch (NumberFormatException e) {
            minutesInput.setError("Minutes must be a number");
            return;
        }

        String subject = subjectInput.getText().toString().trim();
        setLoading(true);
        setOutput("Logging progress...");

        api.logProgress(new ProgressLogRequest(username, minutes, subject))
                .enqueue(new Callback<ProgressLogResponse>() {
                    @Override public void onResponse(Call<ProgressLogResponse> call, Response<ProgressLogResponse> resp) {
                        setLoading(false);
                        if (resp.isSuccessful() && resp.body() != null) {
                            setOutput(resp.body().toString());
                        } else setOutput("Error logging progress: " + resp.code());
                    }
                    @Override public void onFailure(Call<ProgressLogResponse> call, Throwable t) {
                        setLoading(false);
                        setOutput("Network error: " + t.getMessage());
                    }
                });
    }

    private void loadProgressStats() {
        String username = requireText(usernameInput);
        if (username == null) return;

        setLoading(true);
        setOutput("Loading progress stats...");

        api.getProgressStats(username).enqueue(new Callback<ProgressStats>() {
            @Override public void onResponse(Call<ProgressStats> call, Response<ProgressStats> resp) {
                setLoading(false);
                if (resp.isSuccessful() && resp.body() != null) {
                    setOutput(resp.body().toString());
                } else setOutput("Error loading stats: " + resp.code());
            }
            @Override public void onFailure(Call<ProgressStats> call, Throwable t) {
                setLoading(false);
                setOutput("Network error: " + t.getMessage());
            }
        });
    }

    private void listProgress() {
        String username = requireText(usernameInput);
        if (username == null) return;

        setLoading(true);
        setOutput("Loading progress...");

        api.listProgress(username).enqueue(new Callback<List<ProgressItem>>() {
            @Override public void onResponse(Call<List<ProgressItem>> call, Response<List<ProgressItem>> resp) {
                setLoading(false);
                if (resp.isSuccessful() && resp.body() != null) {
                    StringBuilder sb = new StringBuilder("Study sessions:\n\n");
                    for (ProgressItem item : resp.body()) {
                        sb.append(item.toString()).append("\n");
                    }
                    setOutput(sb.toString());
                } else setOutput("Error loading progress: " + resp.code());
            }
            @Override public void onFailure(Call<List<ProgressItem>> call, Throwable t) {
                setLoading(false);
                setOutput("Network error: " + t.getMessage());
            }
        });
    }

    private void startBoss() {
        String username = requireText(usernameInput);
        if (username == null) return;

        setLoading(true);
        setOutput("Starting boss battle...");

        api.startBoss(new BossStartRequest(username)).enqueue(new Callback<BossStartResponse>() {
            @Override public void onResponse(Call<BossStartResponse> call, Response<BossStartResponse> resp) {
                setLoading(false);
                if (resp.isSuccessful() && resp.body() != null) {
                    setOutput(resp.body().toString());
                } else setOutput("Error starting boss: " + resp.code());
            }
            @Override public void onFailure(Call<BossStartResponse> call, Throwable t) {
                setLoading(false);
                setOutput("Network error: " + t.getMessage());
            }
        });
    }

    private void submitBossAnswer() {
        String username = requireText(usernameInput);
        String answer = requireText(bossAnswerInput);
        if (username == null || answer == null) return;

        setLoading(true);
        setOutput("Submitting answer...");

        api.submitBossAnswer(new BossAnswerRequest(username, Integer.parseInt(answer))).enqueue(new Callback<BossAnswerResponse>() {
            @Override public void onResponse(Call<BossAnswerResponse> call, Response<BossAnswerResponse> resp) {
                setLoading(false);
                if (resp.isSuccessful() && resp.body() != null) {
                    setOutput(resp.body().toString());
                } else setOutput("Error submitting answer: " + resp.code());
            }
            @Override public void onFailure(Call<BossAnswerResponse> call, Throwable t) {
                setLoading(false);
                setOutput("Network error: " + t.getMessage());
            }
        });
    }

    private void bossStatus() {
        String username = requireText(usernameInput);
        if (username == null) return;

        setLoading(true);
        setOutput("Checking boss status...");

        api.getBossStatus(username).enqueue(new Callback<BossStatusResponse>() {
            @Override public void onResponse(Call<BossStatusResponse> call, Response<BossStatusResponse> resp) {
                setLoading(false);
                if (resp.isSuccessful() && resp.body() != null) {
                    setOutput(resp.body().toString());
                } else setOutput("Error loading boss status: " + resp.code());
            }
            @Override public void onFailure(Call<BossStatusResponse> call, Throwable t) {
                setLoading(false);
                setOutput("Network error: " + t.getMessage());
            }
        });
    }

    private void askAi() {
        String username = requireText(usernameInput);
        String prompt = requireText(aiPromptInput);
        if (username == null || prompt == null) return;

        setLoading(true);
        setOutput("Asking AI...");

        api.askTextAi(new TextAiRequest(username, prompt)).enqueue(new Callback<TextAiResponse>() {
            @Override public void onResponse(Call<TextAiResponse> call, Response<TextAiResponse> resp) {
                setLoading(false);
                if (resp.isSuccessful() && resp.body() != null) {
                    setOutput(resp.body().response);
                } else setOutput("Error from AI: " + resp.code());
            }
            @Override public void onFailure(Call<TextAiResponse> call, Throwable t) {
                setLoading(false);
                setOutput("Network error: " + t.getMessage());
            }
        });
    }

    private void aiHistory() {
        String username = requireText(usernameInput);
        if (username == null) return;

        setLoading(true);
        setOutput("Loading AI history...");

        api.listTextAiReflections(username).enqueue(new Callback<List<TextAiReflection>>() {
            @Override public void onResponse(Call<List<TextAiReflection>> call, Response<List<TextAiReflection>> resp) {
                setLoading(false);
                if (resp.isSuccessful() && resp.body() != null) {
                    StringBuilder sb = new StringBuilder("AI history:\n\n");
                    for (TextAiReflection reflection : resp.body()) {
                        sb.append(reflection.toString()).append("\n\n");
                    }
                    setOutput(sb.toString());
                } else setOutput("Error loading AI history: " + resp.code());
            }
            @Override public void onFailure(Call<List<TextAiReflection>> call, Throwable t) {
                setLoading(false);
                setOutput("Network error: " + t.getMessage());
            }
        });
    }
}
