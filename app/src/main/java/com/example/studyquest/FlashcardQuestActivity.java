package com.example.studyquest;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studyquest.api.ApiService;
import com.example.studyquest.api.RetrofitClient;
import com.example.studyquest.models.FlashcardItem;
import com.example.studyquest.models.FlashcardRequest;
import com.example.studyquest.models.FlashcardResponse;
import com.example.studyquest.models.QuestCompleteResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FlashcardQuestActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "flashcard_progress";

    private EditText editFlashcardText;
    private Button btnGenerateQuestions;

    private TextView textFlashcardHint;
    private TextView textFlashcardCounter;
    private TextView textQuestion;
    private EditText editUserAnswer;
    private TextView textCorrectAnswer;
    private TextView textUserAnswer;
    private TextView textSummary;

    private Button btnCheckAnswer;
    private Button btnMarkCorrect;
    private Button btnMarkWrong;
    private Button btnContinueLater;
    private Button btnFinishFlashcards;

    private ApiService apiService;
    private int questId;
    private String username;

    private List<FlashcardItem> flashcards = new ArrayList<>();
    private List<String> userAnswers = new ArrayList<>();
    private List<Boolean> userCorrect = new ArrayList<>();

    private int currentIndex = 0;
    private int answeredCount = 0;
    private int correctCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard_quest);

        // find views
        editFlashcardText = findViewById(R.id.editFlashcardText);
        btnGenerateQuestions = findViewById(R.id.btnGenerateQuestions);

        textFlashcardHint = findViewById(R.id.textFlashcardHint);
        textFlashcardCounter = findViewById(R.id.textFlashcardCounter);
        textQuestion = findViewById(R.id.textQuestion);
        editUserAnswer = findViewById(R.id.editUserAnswer);
        textCorrectAnswer = findViewById(R.id.textCorrectAnswer);
        textUserAnswer = findViewById(R.id.textUserAnswer);
        textSummary = findViewById(R.id.textSummary);

        btnCheckAnswer = findViewById(R.id.btnCheckAnswer);
        btnMarkCorrect = findViewById(R.id.btnMarkCorrect);
        btnMarkWrong = findViewById(R.id.btnMarkWrong);
        btnContinueLater = findViewById(R.id.btnContinueLater);
        btnFinishFlashcards = findViewById(R.id.btnFinishFlashcards);

        apiService = RetrofitClient.getClient().create(ApiService.class);

        questId = getIntent().getIntExtra("questId", -1);
        username = getIntent().getStringExtra("username");

        if (questId == -1 || username == null || username.trim().isEmpty()) {
            Toast.makeText(this, "Invalid quest or user.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initial state: hide card + summary UI until we have questions
        setCardAreaVisible(false);
        textSummary.setVisibility(View.GONE);
        btnFinishFlashcards.setVisibility(View.GONE);

        // Try to resume saved progress
        boolean resumed = loadProgress();
        if (resumed && !flashcards.isEmpty()) {
            // we already have generated flashcards
            editFlashcardText.setVisibility(View.GONE);
            btnGenerateQuestions.setVisibility(View.GONE);

            if (answeredCount >= flashcards.size()) {
                showSummary();
            } else {
                setCardAreaVisible(true);
                updateHeader();
                showCurrentCard();
            }
        }

        btnGenerateQuestions.setOnClickListener(v -> {
            String text = editFlashcardText.getText().toString().trim();
            if (text.isEmpty()) {
                Toast.makeText(this, "Please paste your study text first.", Toast.LENGTH_SHORT).show();
                return;
            }
            generateWithAI(text);
        });

        btnCheckAnswer.setOnClickListener(v -> {
            if (flashcards.isEmpty()) return;
            String user = editUserAnswer.getText().toString().trim();
            if (user.isEmpty()) {
                Toast.makeText(this, "Type your answer first.", Toast.LENGTH_SHORT).show();
                return;
            }
            showAnswer(user);
        });

        btnMarkCorrect.setOnClickListener(v -> {
            if (flashcards.isEmpty()) return;
            String user = editUserAnswer.getText().toString().trim();
            if (user.isEmpty()) {
                Toast.makeText(this, "Check answer first.", Toast.LENGTH_SHORT).show();
                return;
            }
            saveUserResult(user, true);
        });

        btnMarkWrong.setOnClickListener(v -> {
            if (flashcards.isEmpty()) return;
            String user = editUserAnswer.getText().toString().trim();
            if (user.isEmpty()) {
                Toast.makeText(this, "Check answer first.", Toast.LENGTH_SHORT).show();
                return;
            }
            saveUserResult(user, false);
        });

        btnContinueLater.setOnClickListener(v -> {
            saveProgress();
            Toast.makeText(this, "Progress saved.", Toast.LENGTH_SHORT).show();
            finish();
        });

        btnFinishFlashcards.setOnClickListener(v -> {
            if (flashcards.isEmpty() || answeredCount < flashcards.size()) {
                Toast.makeText(this, "Answer all flashcards first.", Toast.LENGTH_SHORT).show();
                return;
            }
            completeQuestOnServer();
        });
    }

    private void setCardAreaVisible(boolean visible) {
        int vis = visible ? View.VISIBLE : View.GONE;
        textFlashcardHint.setVisibility(vis);
        textFlashcardCounter.setVisibility(vis);
        textQuestion.setVisibility(vis);
        editUserAnswer.setVisibility(vis);
        btnCheckAnswer.setVisibility(vis);
        textCorrectAnswer.setVisibility(vis);
        textUserAnswer.setVisibility(vis);
        btnMarkCorrect.setVisibility(vis);
        btnMarkWrong.setVisibility(vis);
        btnContinueLater.setVisibility(vis);
    }

    private void generateWithAI(String userText) {
        FlashcardRequest request = new FlashcardRequest(userText, 10);

        Call<FlashcardResponse> call = apiService.generateFlashcards(request);
        btnGenerateQuestions.setEnabled(false);

        call.enqueue(new Callback<FlashcardResponse>() {
            @Override
            public void onResponse(Call<FlashcardResponse> call, Response<FlashcardResponse> response) {
                btnGenerateQuestions.setEnabled(true);
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(FlashcardQuestActivity.this,
                            "AI error, please try again.", Toast.LENGTH_SHORT).show();
                    return;
                }

                FlashcardResponse body = response.body();
                if (body.getQuestions() == null || body.getQuestions().isEmpty()) {
                    Toast.makeText(FlashcardQuestActivity.this,
                            "AI did not return any questions.", Toast.LENGTH_SHORT).show();
                    return;
                }

                flashcards.clear();
                userAnswers.clear();
                userCorrect.clear();

                flashcards.addAll(body.getQuestions());
                for (int i = 0; i < flashcards.size(); i++) {
                    userAnswers.add("");
                    userCorrect.add(false);
                }

                currentIndex = 0;
                answeredCount = 0;
                correctCount = 0;

                // hide text input for this quest forever
                editFlashcardText.setVisibility(View.GONE);
                btnGenerateQuestions.setVisibility(View.GONE);

                setCardAreaVisible(true);
                updateHeader();
                showCurrentCard();
                saveProgress();
            }

            @Override
            public void onFailure(Call<FlashcardResponse> call, Throwable t) {
                btnGenerateQuestions.setEnabled(true);
                Toast.makeText(FlashcardQuestActivity.this,
                        "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateHeader() {
        if (flashcards.isEmpty()) {
            textFlashcardHint.setText("");
            textFlashcardCounter.setText("0 / 10");
            return;
        }
        int total = flashcards.size();
        textFlashcardHint.setText("Question " + (currentIndex + 1) + " of " + total);
        textFlashcardCounter.setText(correctCount + " / " + total + " correct");
    }

    private void showCurrentCard() {
        if (flashcards.isEmpty() || currentIndex < 0 || currentIndex >= flashcards.size()) {
            return;
        }

        FlashcardItem item = flashcards.get(currentIndex);
        textQuestion.setText(item.question);
        editUserAnswer.setText("");
        textCorrectAnswer.setText("");
        textUserAnswer.setText("");

        btnMarkCorrect.setEnabled(false);
        btnMarkWrong.setEnabled(false);

        updateHeader();
    }

    private void showAnswer(String user) {
        if (flashcards.isEmpty() || currentIndex < 0 || currentIndex >= flashcards.size()) {
            return;
        }
        FlashcardItem item = flashcards.get(currentIndex);

        textCorrectAnswer.setText("Correct answer: " + item.answer);
        textUserAnswer.setText("Your answer: " + user);

        btnMarkCorrect.setEnabled(true);
        btnMarkWrong.setEnabled(true);
    }

    private void saveUserResult(String user, boolean isCorrect) {
        if (currentIndex < 0 || currentIndex >= flashcards.size()) return;

        // First time answering this card?
        if (TextUtils.isEmpty(userAnswers.get(currentIndex))) {
            answeredCount++;
            if (isCorrect) {
                correctCount++;
            }
        } else {
            // already had an answer -> adjust counts if changed
            boolean oldCorrect = userCorrect.get(currentIndex);
            if (oldCorrect && !isCorrect) {
                correctCount--;
            } else if (!oldCorrect && isCorrect) {
                correctCount++;
            }
        }

        userAnswers.set(currentIndex, user);
        userCorrect.set(currentIndex, isCorrect);

        // Move to next or summary
        if (currentIndex < flashcards.size() - 1) {
            currentIndex++;
            saveProgress();
            showCurrentCard();
        } else {
            saveProgress();
            showSummary();
        }
    }

    private void showSummary() {
        setCardAreaVisible(false);

        StringBuilder sb = new StringBuilder();
        sb.append("You answered ")
                .append(correctCount)
                .append(" out of ")
                .append(flashcards.size())
                .append(" correctly.\n\n");

        for (int i = 0; i < flashcards.size(); i++) {
            FlashcardItem item = flashcards.get(i);
            sb.append("Q").append(i + 1).append(": ").append(item.question).append("\n");
            sb.append("Your answer: ").append(userAnswers.get(i)).append("\n");
            sb.append("Correct answer: ").append(item.answer).append("\n\n");
        }

        textSummary.setText(sb.toString());
        textSummary.setVisibility(View.VISIBLE);
        btnFinishFlashcards.setVisibility(View.VISIBLE);
    }

    private void completeQuestOnServer() {
        apiService.completeQuest(questId, username).enqueue(new Callback<QuestCompleteResponse>() {
            @Override
            public void onResponse(Call<QuestCompleteResponse> call, Response<QuestCompleteResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(FlashcardQuestActivity.this,
                            response.body().message, Toast.LENGTH_SHORT).show();
                    clearSavedProgress();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(FlashcardQuestActivity.this,
                            "Could not complete quest.", Toast.LENGTH_SHORT).show();
                    clearSavedProgress();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<QuestCompleteResponse> call, Throwable t) {
                Toast.makeText(FlashcardQuestActivity.this,
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                clearSavedProgress();
                finish();
            }
        });
    }

    private void saveProgress() {
        if (questId == -1 || flashcards.isEmpty()) return;

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();

        String prefix = "user_" + username + "_quest_" + questId + "_";

        ed.putInt(prefix + "currentIndex", currentIndex);
        ed.putInt(prefix + "answeredCount", answeredCount);
        ed.putInt(prefix + "correctCount", correctCount);

        Gson gson = new Gson();
        ed.putString(prefix + "flashcards", gson.toJson(flashcards));
        ed.putString(prefix + "userAnswers", gson.toJson(userAnswers));
        ed.putString(prefix + "userCorrect", gson.toJson(userCorrect));

        ed.apply();
    }

    private boolean loadProgress() {
        if (questId == -1) return false;

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String prefix = "user_" + username + "_quest_" + questId + "_";

        if (!prefs.contains(prefix + "flashcards")) {
            return false;
        }

        currentIndex = prefs.getInt(prefix + "currentIndex", 0);
        answeredCount = prefs.getInt(prefix + "answeredCount", 0);
        correctCount = prefs.getInt(prefix + "correctCount", 0);

        Gson gson = new Gson();
        Type listFlashcardType = new TypeToken<List<FlashcardItem>>() {}.getType();
        Type listStringType = new TypeToken<List<String>>() {}.getType();
        Type listBoolType = new TypeToken<List<Boolean>>() {}.getType();

        String flashcardsJson = prefs.getString(prefix + "flashcards", null);
        String userAnswersJson = prefs.getString(prefix + "userAnswers", null);
        String userCorrectJson = prefs.getString(prefix + "userCorrect", null);

        if (flashcardsJson == null || userAnswersJson == null || userCorrectJson == null) {
            return false;
        }

        flashcards = gson.fromJson(flashcardsJson, listFlashcardType);
        userAnswers = gson.fromJson(userAnswersJson, listStringType);
        userCorrect = gson.fromJson(userCorrectJson, listBoolType);

        // Safety checks
        if (flashcards == null || flashcards.isEmpty()) return false;
        if (userAnswers == null || userAnswers.size() != flashcards.size()) {
            userAnswers = new ArrayList<>();
            for (int i = 0; i < flashcards.size(); i++) userAnswers.add("");
        }
        if (userCorrect == null || userCorrect.size() != flashcards.size()) {
            userCorrect = new ArrayList<>();
            for (int i = 0; i < flashcards.size(); i++) userCorrect.add(false);
        }

        return true;
    }

    private void clearSavedProgress() {
        if (questId == -1) return;
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();
        String prefix = "user_" + username + "_quest_" + questId + "_";
        ed.remove(prefix + "currentIndex");
        ed.remove(prefix + "answeredCount");
        ed.remove(prefix + "correctCount");
        ed.remove(prefix + "flashcards");
        ed.remove(prefix + "userAnswers");
        ed.remove(prefix + "userCorrect");
        ed.apply();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveProgress();
    }
}
