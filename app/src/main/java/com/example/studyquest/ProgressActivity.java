package com.example.studyquest;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studyquest.api.ApiService;
import com.example.studyquest.api.RetrofitClient;
import com.example.studyquest.models.ProgressItem;
import com.example.studyquest.models.ProgressLogRequest;
import com.example.studyquest.models.ProgressLogResponse;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProgressActivity extends AppCompatActivity {

    private EditText username;
    private EditText subjectInput;
    private EditText minutesInput;
    private EditText reflectionInput;
    private TextView result;
    private TextView summary;
    private TextView reflectionPrompt;
    private TextView selectedDateView;
    private ProgressBar progress;
    private Chronometer timer;
    private ApiService api;

    private long timerPauseOffset = 0L;
    private boolean timerRunning = false;
    private String selectedDateIso;
    private int promptIndex = 0;
    private final String[] prompts = new String[]{
            "What worked well in this session and why?",
            "Where did focus slip, and what will you change next time?",
            "What concept still feels fuzzy? Plan the next step to fix it.",
            "How did your energy feel? Adjust breaks or time of day?",
            "What tiny win are you proud of from this session?"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        api = RetrofitClient.getClient().create(ApiService.class);

        username = findViewById(R.id.editUsernameProgress);
        subjectInput = findViewById(R.id.editSubject);
        minutesInput = findViewById(R.id.editMinutes);
        reflectionInput = findViewById(R.id.editReflection);
        reflectionPrompt = findViewById(R.id.textReflectionPrompt);
        selectedDateView = findViewById(R.id.textSelectedDate);
        result = findViewById(R.id.textProgressResult);
        summary = findViewById(R.id.textProgressSummary);
        progress = findViewById(R.id.progressBarProgress);
        timer = findViewById(R.id.chronometerTimer);
        Button btnLoad = findViewById(R.id.btnLoadProgress);
        Button btnLog = findViewById(R.id.btnLogSession);
        Button btnStart = findViewById(R.id.btnStartTimer);
        Button btnStop = findViewById(R.id.btnStopTimer);
        Button btnReset = findViewById(R.id.btnResetTimer);
        Button btnPrompt = findViewById(R.id.btnNewPrompt);
        Button btnDate = findViewById(R.id.btnPickDate);

        promptIndex = 0;
        reflectionPrompt.setText(prompts[promptIndex]);
        setTodayAsSelectedDate();
        resetTimer();

        btnLoad.setOnClickListener(v -> loadProgress());
        btnLog.setOnClickListener(v -> logSession());
        btnStart.setOnClickListener(v -> startTimer());
        btnStop.setOnClickListener(v -> stopTimer());
        btnReset.setOnClickListener(v -> resetTimer());
        btnPrompt.setOnClickListener(v -> rotatePrompt());
        btnDate.setOnClickListener(v -> pickDate());
    }

    private void loadProgress() {
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
                    renderProgress(response.body());

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

    private void logSession() {
        String u = username.getText().toString().trim();
        String minutesStr = minutesInput.getText().toString().trim();
        if (u.isEmpty()) {
            username.setError("Required");
            return;
        }
        if (minutesStr.isEmpty()) {
            minutesInput.setError("Add minutes or run the timer.");
            return;
        }

        int minutes;
        try {
            minutes = Integer.parseInt(minutesStr);
        } catch (NumberFormatException e) {
            minutesInput.setError("Minutes must be a number");
            return;
        }

        String subject = subjectInput.getText().toString().trim();
        String reflection = reflectionInput.getText().toString().trim();

        progress.setVisibility(ProgressBar.VISIBLE);
        result.setText("Logging session...");

        api.logProgress(new ProgressLogRequest(u, minutes, subject))
                .enqueue(new Callback<ProgressLogResponse>() {
                    @Override
                    public void onResponse(Call<ProgressLogResponse> call, Response<ProgressLogResponse> resp) {
                        progress.setVisibility(ProgressBar.GONE);
                        if (resp.isSuccessful() && resp.body() != null) {
                            ProgressLogResponse body = resp.body();
                            StringBuilder sb = new StringBuilder();
                            sb.append("Logged ").append(minutes).append(" min");
                            if (!subject.isEmpty()) sb.append(" on ").append(subject);
                            sb.append(".\nGained XP: ").append(body.gained_xp)
                                    .append("\nNew streak: ").append(body.new_streak).append(" days");
                            if (selectedDateIso != null) sb.append("\nCalendar day: ").append(selectedDateIso);
                            if (!reflection.isEmpty()) sb.append("\nReflection saved: ").append(reflection);
                            result.setText(sb.toString());
                            summary.setText("Newest session: +" + body.gained_xp + " XP from "
                                    + minutes + " minutes");

                            // Pull fresh history so the timeline and totals update right away.
                            resetTimer();
                            loadProgress();
                        } else {
                            result.setText("Error logging session: " + resp.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<ProgressLogResponse> call, Throwable t) {
                        progress.setVisibility(ProgressBar.GONE);
                        result.setText("Network error: " + t.getMessage());
                    }
                });
    }

    private void renderProgress(List<ProgressItem> items) {
        if (items.isEmpty()) {
            result.setText("No sessions yet. Start the timer and log your first run.");
            summary.setText("");
            return;
        }

        int totalMinutes = 0;
        int totalXp = 0;
        int selectedDayMinutes = 0;
        int selectedDayXp = 0;

        StringBuilder sb = new StringBuilder("Study Sessions:\n\n");
        for (ProgressItem item : items) {
            totalMinutes += item.minutes;
            totalXp += item.xp;
            String date = extractDate(item.timestamp);
            if (selectedDateIso != null && selectedDateIso.equals(date)) {
                selectedDayMinutes += item.minutes;
                selectedDayXp += item.xp;
            }
            sb.append(date).append(" • ")
                    .append(item.subject == null ? "Session" : item.subject)
                    .append("\n  ").append(item.minutes).append(" min, +")
                    .append(item.xp).append(" XP\n\n");
        }

        result.setText(sb.toString());

        double xpPerMinute = totalMinutes > 0 ? (double) totalXp / totalMinutes : 0;
        String dayLine = selectedDateIso == null ? "Today" : selectedDateIso;

        summary.setText("Sessions: " + items.size() +
                "\nTotal: " + totalMinutes + " min • " + totalXp + " XP" +
                "\nXP/min: " + String.format(Locale.US, "%.1f", xpPerMinute) +
                "\n" + dayLine + ": " + selectedDayMinutes + " min • " + selectedDayXp + " XP");
    }

    private void startTimer() {
        if (!timerRunning) {
            timer.setBase(SystemClock.elapsedRealtime() - timerPauseOffset);
            timer.start();
            timerRunning = true;
        }
    }

    private void stopTimer() {
        if (timerRunning) {
            timer.stop();
            timerPauseOffset = SystemClock.elapsedRealtime() - timer.getBase();
            timerRunning = false;
            pushTimerToMinutes();
            Toast.makeText(this, "Timer captured " + minutesInput.getText() + " minutes", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetTimer() {
        timer.stop();
        timer.setBase(SystemClock.elapsedRealtime());
        timerPauseOffset = 0L;
        timerRunning = false;
        minutesInput.setText("");
    }

    private void pushTimerToMinutes() {
        long mins = Math.max(1, Math.round(timerPauseOffset / 60000f));
        minutesInput.setText(String.valueOf(mins));
    }

    private void rotatePrompt() {
        promptIndex = (promptIndex + 1) % prompts.length;
        reflectionPrompt.setText(prompts[promptIndex]);
    }

    private void setTodayAsSelectedDate() {
        Calendar c = Calendar.getInstance();
        selectedDateIso = formatDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        selectedDateView.setText("Today (" + selectedDateIso + ")");
    }

    private void pickDate() {
        Calendar c = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedDateIso = formatDate(year, month, dayOfMonth);
                    selectedDateView.setText(selectedDateIso);
                },
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }

    private String formatDate(int year, int month, int day) {
        return String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day);
    }

    private String extractDate(String timestamp) {
        if (timestamp == null || timestamp.isEmpty()) return "";
        int tIndex = timestamp.indexOf('T');
        if (tIndex > 0) {
            return timestamp.substring(0, tIndex);
        }
        int spaceIndex = timestamp.indexOf(' ');
        if (spaceIndex > 0) {
            return timestamp.substring(0, spaceIndex);
        }
        return timestamp;
    }
}
