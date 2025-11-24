package com.example.studyquest;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studyquest.api.ApiService;
import com.example.studyquest.api.RetrofitClient;
import com.example.studyquest.models.AvailableQuestsResponse;
import com.example.studyquest.models.Quest;
import com.example.studyquest.models.QuestCompleteResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuestsActivity extends AppCompatActivity {

    ListView listQuests;
    ApiService api;
    String username;

    List<Quest> quests = new ArrayList<>();
    QuestsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quests);

        listQuests = findViewById(R.id.listQuests);

        Button btnOpenCalendar = findViewById(R.id.btnOpenStudyCalendar);
        btnOpenCalendar.setOnClickListener(v -> {
            Intent i = new Intent(QuestsActivity.this, CalendarQuestActivity.class);
            i.putExtra("username", username);   // 👈 important
            startActivity(i);
        });


        api = RetrofitClient.getClient().create(ApiService.class);

        username = getIntent().getStringExtra("username");
        if (username == null || username.trim().isEmpty()) {
            Toast.makeText(this, "No username provided.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // ✅ Pass username into adapter (for per-user progress)
        adapter = new QuestsAdapter(this, quests, username);
        listQuests.setAdapter(adapter);

        listQuests.setOnItemClickListener((parent, view, position, id) -> {
            Quest q = quests.get(position);
            openQuestAction(q);
        });

        loadAvailableQuests();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh list so progress bars update when you return from quests
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    void openQuestAction(Quest q) {
        if (q.quest_type == null) {
            // fallback if backend didn't set quest_type
            completeQuest(q.id);
            return;
        }

        switch (q.quest_type) {
            case "timer":
                startActivity(new Intent(QuestsActivity.this, TimerQuestActivity.class)
                        .putExtra("questId", q.id)
                        .putExtra("username", username));
                break;

            case "flashcards": {
                Intent i = new Intent(QuestsActivity.this, FlashcardQuestActivity.class);
                i.putExtra("questId", q.id);
                i.putExtra("username", username);
                i.putExtra("flashcardText", q.description);
                startActivity(i);
                break;
            }

            case "calendar":
                startActivity(new Intent(QuestsActivity.this, CalendarQuestActivity.class)
                        .putExtra("questId", q.id)
                        .putExtra("username", username));
                break;

            default:
                // fallback = normal completion
                completeQuest(q.id);
        }
    }

    void showLoading(boolean show) {
        // no loading view for now
    }

    void loadAvailableQuests() {
        showLoading(true);
        api.getAvailableQuests(username).enqueue(new Callback<AvailableQuestsResponse>() {
            @Override
            public void onResponse(Call<AvailableQuestsResponse> call, Response<AvailableQuestsResponse> response) {
                showLoading(false);

                quests.clear();

                if (response.isSuccessful() && response.body() != null) {
                    AvailableQuestsResponse body = response.body();
                    if (body.getAvailable_quests() != null) {
                        quests.addAll(body.getAvailable_quests());
                    } else if (body.getMessage() != null) {
                        Toast.makeText(QuestsActivity.this, body.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(QuestsActivity.this, "Failed to load quests.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AvailableQuestsResponse> call, Throwable t) {
                showLoading(false);
                Toast.makeText(QuestsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    void completeQuest(int questId) {
        api.completeQuest(questId, username).enqueue(new Callback<QuestCompleteResponse>() {
            @Override
            public void onResponse(Call<QuestCompleteResponse> call, Response<QuestCompleteResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(QuestsActivity.this,
                            response.body().message, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(QuestsActivity.this,
                            "Could not complete quest.", Toast.LENGTH_SHORT).show();
                }
                loadAvailableQuests();
            }

            @Override
            public void onFailure(Call<QuestCompleteResponse> call, Throwable t) {
                Toast.makeText(QuestsActivity.this,
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
