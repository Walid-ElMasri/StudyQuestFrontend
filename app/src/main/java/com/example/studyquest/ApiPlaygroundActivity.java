package com.example.studyquest;

import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studyquest.api.ApiService;
import com.example.studyquest.api.RetrofitClient;
import com.example.studyquest.models.*;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiPlaygroundActivity extends AppCompatActivity {

    private EditText editUsername, editEmail, editExtra1, editExtra2, editExtra3;
    private TextView textOutput;
    private ApiService api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api_playground);

        api = RetrofitClient.getClient().create(ApiService.class);

        editUsername = findViewById(R.id.editUsernameApi);
        editEmail = findViewById(R.id.editEmailApi);
        editExtra1 = findViewById(R.id.editExtra1);
        editExtra2 = findViewById(R.id.editExtra2);
        editExtra3 = findViewById(R.id.editExtra3);
        textOutput = findViewById(R.id.textApiOutput);

        Button btnCreateUser = findViewById(R.id.btnCreateUser);
        Button btnListUsers = findViewById(R.id.btnListUsers);
        Button btnDashboard = findViewById(R.id.btnDashboard);
        Button btnLogProgress = findViewById(R.id.btnLogProgress);
        Button btnProgressStats = findViewById(R.id.btnProgressStats);
        Button btnListProgress = findViewById(R.id.btnListProgress);
        Button btnCreateQuest = findViewById(R.id.btnCreateQuest);
        Button btnCompleteQuest = findViewById(R.id.btnCompleteQuest);
        Button btnUserLevel = findViewById(R.id.btnUserLevel);
        Button btnAvatarUpsert = findViewById(R.id.btnAvatarUpsert);
        Button btnAvatarGet = findViewById(R.id.btnAvatarGet);
        Button btnCreateBadge = findViewById(R.id.btnCreateBadge);
        Button btnListBadges = findViewById(R.id.btnListBadges);
        Button btnAiHistory = findViewById(R.id.btnAiHistory);
        Button btnBossAnswer = findViewById(R.id.btnBossAnswer);
        Button btnBossStatus = findViewById(R.id.btnBossStatus);
        Button btnAddFriend = findViewById(R.id.btnAddFriend);
        Button btnRespondFriend = findViewById(R.id.btnRespondFriend);
        Button btnLeaderboard = findViewById(R.id.btnLeaderboard);

        // USERS
        btnCreateUser.setOnClickListener(v -> callCreateUser());
        btnListUsers.setOnClickListener(v -> callListUsers());
        btnDashboard.setOnClickListener(v -> callDashboard());

        // PROGRESS
        btnLogProgress.setOnClickListener(v -> callLogProgress());
        btnProgressStats.setOnClickListener(v -> callProgressStats());
        btnListProgress.setOnClickListener(v -> callListProgress());

        // QUESTS
        btnCreateQuest.setOnClickListener(v -> callCreateQuest());
        btnCompleteQuest.setOnClickListener(v -> callCompleteQuest());
        btnUserLevel.setOnClickListener(v -> callUserLevel());

        // COSMETICS
        btnAvatarUpsert.setOnClickListener(v -> callAvatarUpsert());
        btnAvatarGet.setOnClickListener(v -> callAvatarGet());
        btnCreateBadge.setOnClickListener(v -> callCreateBadge());
        btnListBadges.setOnClickListener(v -> callListBadges());

        // TEXT AI history
        btnAiHistory.setOnClickListener(v -> callAiHistory());

        // BOSS
        btnBossAnswer.setOnClickListener(v -> callBossAnswer());
        btnBossStatus.setOnClickListener(v -> callBossStatus());

        // SOCIAL
        btnAddFriend.setOnClickListener(v -> callAddFriend());
        btnRespondFriend.setOnClickListener(v -> callRespondFriend());
        btnLeaderboard.setOnClickListener(v -> callLeaderboard());
    }

    private void setOutput(String text) {
        textOutput.setText(text);
    }

    // ---------- USERS ----------
    private void callCreateUser() {
        String u = editUsername.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        if (u.isEmpty()) { editUsername.setError("username"); return; }

        UserCreateRequest req = new UserCreateRequest(u, email);
        api.createUser(req).enqueue(new Callback<User>() {
            @Override public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    setOutput("Created user: " + response.body().username);
                } else setOutput("Error creating user: " + response.code());
            }
            @Override public void onFailure(Call<User> call, Throwable t) {
                setOutput("Error: " + t.getMessage());
            }
        });
    }

    private void callListUsers() {
        api.listUsers().enqueue(new Callback<List<User>>() {
            @Override public void onResponse(Call<List<User>> call, Response<List<User>> resp) {
                if (resp.isSuccessful() && resp.body() != null) {
                    StringBuilder sb = new StringBuilder("Users:\n");
                    for (User u : resp.body()) {
                        sb.append(u.username).append(" (").append(u.total_xp).append(" XP)\n");
                    }
                    setOutput(sb.toString());
                } else setOutput("Error listing users: " + resp.code());
            }
            @Override public void onFailure(Call<List<User>> call, Throwable t) {
                setOutput("Error: " + t.getMessage());
            }
        });
    }

    private void callDashboard() {
        String u = editUsername.getText().toString().trim();
        if (u.isEmpty()) { editUsername.setError("username"); return; }

        api.getDashboardStats(u).enqueue(new Callback<DashboardStats>() {
            @Override public void onResponse(Call<DashboardStats> call, Response<DashboardStats> resp) {
                if (resp.isSuccessful() && resp.body() != null) setOutput(resp.body().toString());
                else setOutput("Error: " + resp.code());
            }
            @Override public void onFailure(Call<DashboardStats> call, Throwable t) {
                setOutput("Error: " + t.getMessage());
            }
        });
    }

    // ---------- PROGRESS ----------
    private void callLogProgress() {
        String u = editUsername.getText().toString().trim();
        String minutesStr = editExtra1.getText().toString().trim(); // use extra1 as minutes
        String subject = editExtra2.getText().toString().trim();    // extra2 as subject

        if (u.isEmpty()) { editUsername.setError("username"); return; }
        if (minutesStr.isEmpty()) { editExtra1.setError("minutes"); return; }

        int minutes = Integer.parseInt(minutesStr);
        ProgressLogRequest req = new ProgressLogRequest(u, minutes, subject);
        api.logProgress(req).enqueue(new Callback<ProgressLogResponse>() {
            @Override public void onResponse(Call<ProgressLogResponse> call, Response<ProgressLogResponse> resp) {
                if (resp.isSuccessful() && resp.body() != null) setOutput(resp.body().toString());
                else setOutput("Error log progress: " + resp.code());
            }
            @Override public void onFailure(Call<ProgressLogResponse> call, Throwable t) {
                setOutput("Error: " + t.getMessage());
            }
        });
    }

    private void callProgressStats() {
        String u = editUsername.getText().toString().trim();
        if (u.isEmpty()) { editUsername.setError("username"); return; }

        api.getProgressStats(u).enqueue(new Callback<ProgressStats>() {
            @Override public void onResponse(Call<ProgressStats> call, Response<ProgressStats> resp) {
                if (resp.isSuccessful() && resp.body() != null) setOutput(resp.body().toString());
                else setOutput("Error progress stats: " + resp.code());
            }
            @Override public void onFailure(Call<ProgressStats> call, Throwable t) {
                setOutput("Error: " + t.getMessage());
            }
        });
    }

    private void callListProgress() {
        String u = editUsername.getText().toString().trim();
        if (u.isEmpty()) { editUsername.setError("username"); return; }

        api.listProgress(u).enqueue(new Callback<List<ProgressItem>>() {
            @Override public void onResponse(Call<List<ProgressItem>> call, Response<List<ProgressItem>> resp) {
                if (resp.isSuccessful() && resp.body() != null) {
                    StringBuilder sb = new StringBuilder("Sessions:\n");
                    for (ProgressItem p : resp.body()) sb.append(p.toString()).append("\n");
                    setOutput(sb.toString());
                } else setOutput("Error list progress: " + resp.code());
            }
            @Override public void onFailure(Call<List<ProgressItem>> call, Throwable t) {
                setOutput("Error: " + t.getMessage());
            }
        });
    }

    // ---------- QUESTS ----------
    private void callCreateQuest() {
        String title = editExtra1.getText().toString().trim();
        String desc = editExtra2.getText().toString().trim();
        String rewardStr = editExtra3.getText().toString().trim();
        if (title.isEmpty()) { editExtra1.setError("title"); return; }
        if (rewardStr.isEmpty()) { editExtra3.setError("reward_xp"); return; }

        int reward = Integer.parseInt(rewardStr);
        QuestCreateRequest req = new QuestCreateRequest(title, desc, reward);
        api.createQuest(req).enqueue(new Callback<Quest>() {
            @Override public void onResponse(Call<Quest> call, Response<Quest> resp) {
                if (resp.isSuccessful() && resp.body() != null) setOutput("Created quest:\n" + resp.body());
                else setOutput("Error create quest: " + resp.code());
            }
            @Override public void onFailure(Call<Quest> call, Throwable t) {
                setOutput("Error: " + t.getMessage());
            }
        });
    }

    private void callCompleteQuest() {
        String idStr = editExtra1.getText().toString().trim();
        if (idStr.isEmpty()) { editExtra1.setError("quest_id"); return; }

        String u = editUsername.getText().toString().trim();
        if (u.isEmpty()) { editUsername.setError("username"); return; }

        int id = Integer.parseInt(idStr);

        api.completeQuest(id, u).enqueue(new Callback<QuestCompleteResponse>() {
            @Override
            public void onResponse(Call<QuestCompleteResponse> call, Response<QuestCompleteResponse> resp) {
                if (resp.isSuccessful() && resp.body() != null) {
                    setOutput(resp.body().toString());
                } else {
                    setOutput("Error complete quest: " + resp.code());
                }
            }

            @Override
            public void onFailure(Call<QuestCompleteResponse> call, Throwable t) {
                setOutput("Error: " + t.getMessage());
            }
        });
    }


    private void callUserLevel() {
        String u = editUsername.getText().toString().trim();
        if (u.isEmpty()) { editUsername.setError("username"); return; }

        api.getUserLevel(u).enqueue(new Callback<QuestLevelData>() {
            @Override public void onResponse(Call<QuestLevelData> call, Response<QuestLevelData> resp) {
                if (resp.isSuccessful() && resp.body() != null) setOutput(resp.body().toString());
                else setOutput("Error user level: " + resp.code());
            }
            @Override public void onFailure(Call<QuestLevelData> call, Throwable t) {
                setOutput("Error: " + t.getMessage());
            }
        });
    }

    // ---------- COSMETICS ----------
    private void callAvatarUpsert() {
        String u = editUsername.getText().toString().trim();
        String style = editExtra1.getText().toString().trim();
        if (u.isEmpty()) { editUsername.setError("username"); return; }
        if (style.isEmpty()) { editExtra1.setError("avatar_style"); return; }

        AvatarUpsertRequest req = new AvatarUpsertRequest(u, style);
        api.upsertAvatar(req).enqueue(new Callback<Avatar>() {
            @Override public void onResponse(Call<Avatar> call, Response<Avatar> resp) {
                if (resp.isSuccessful() && resp.body() != null) setOutput("Avatar updated:\n" + resp.body());
                else setOutput("Error avatar upsert: " + resp.code());
            }
            @Override public void onFailure(Call<Avatar> call, Throwable t) {
                setOutput("Error: " + t.getMessage());
            }
        });
    }

    private void callAvatarGet() {
        String u = editUsername.getText().toString().trim();
        if (u.isEmpty()) { editUsername.setError("username"); return; }

        api.getAvatar(u).enqueue(new Callback<Avatar>() {
            @Override public void onResponse(Call<Avatar> call, Response<Avatar> resp) {
                if (resp.isSuccessful() && resp.body() != null) setOutput(resp.body().toString());
                else setOutput("Error get avatar: " + resp.code());
            }
            @Override public void onFailure(Call<Avatar> call, Throwable t) {
                setOutput("Error: " + t.getMessage());
            }
        });
    }

    private void callCreateBadge() {
        String name = editExtra1.getText().toString().trim();
        String desc = editExtra2.getText().toString().trim();
        String minXpStr = editExtra3.getText().toString().trim();
        if (name.isEmpty()) { editExtra1.setError("name"); return; }
        if (minXpStr.isEmpty()) { editExtra3.setError("min_xp"); return; }
        int minXp = Integer.parseInt(minXpStr);

        BadgeCreateRequest req = new BadgeCreateRequest(name, desc, minXp);
        api.createBadge(req).enqueue(new Callback<Badge>() {
            @Override public void onResponse(Call<Badge> call, Response<Badge> resp) {
                if (resp.isSuccessful() && resp.body() != null) setOutput("Created badge:\n" + resp.body());
                else setOutput("Error create badge: " + resp.code());
            }
            @Override public void onFailure(Call<Badge> call, Throwable t) {
                setOutput("Error: " + t.getMessage());
            }
        });
    }

    private void callListBadges() {
        api.listBadges().enqueue(new Callback<List<Badge>>() {
            @Override public void onResponse(Call<List<Badge>> call, Response<List<Badge>> resp) {
                if (resp.isSuccessful() && resp.body() != null) {
                    StringBuilder sb = new StringBuilder("Badges:\n");
                    for (Badge b : resp.body()) sb.append(b.toString()).append("\n");
                    setOutput(sb.toString());
                } else setOutput("Error list badges: " + resp.code());
            }
            @Override public void onFailure(Call<List<Badge>> call, Throwable t) {
                setOutput("Error: " + t.getMessage());
            }
        });
    }

    // ---------- TEXT AI HISTORY ----------
    private void callAiHistory() {
        String u = editUsername.getText().toString().trim();
        if (u.isEmpty()) { editUsername.setError("username"); return; }

        api.listTextAiReflections(u).enqueue(new Callback<List<TextAiReflection>>() {
            @Override public void onResponse(Call<List<TextAiReflection>> call, Response<List<TextAiReflection>> resp) {
                if (resp.isSuccessful() && resp.body() != null) {
                    StringBuilder sb = new StringBuilder("AI reflections:\n");
                    for (TextAiReflection r : resp.body()) sb.append(r.toString()).append("\n\n");
                    setOutput(sb.toString());
                } else setOutput("Error AI history: " + resp.code());
            }
            @Override public void onFailure(Call<List<TextAiReflection>> call, Throwable t) {
                setOutput("Error: " + t.getMessage());
            }
        });
    }

    // ---------- BOSS ----------
    private void callBossAnswer() {
        String u = editUsername.getText().toString().trim();
        String ans = editExtra1.getText().toString().trim();
        if (u.isEmpty()) { editUsername.setError("username"); return; }
        if (ans.isEmpty()) { editExtra1.setError("answer"); return; }

        BossAnswerRequest req = new BossAnswerRequest(u, Integer.parseInt(ans));
        api.submitBossAnswer(req).enqueue(new Callback<BossAnswerResponse>() {
            @Override public void onResponse(Call<BossAnswerResponse> call, Response<BossAnswerResponse> resp) {
                if (resp.isSuccessful() && resp.body() != null) setOutput(resp.body().toString());
                else setOutput("Error boss answer: " + resp.code());
            }
            @Override public void onFailure(Call<BossAnswerResponse> call, Throwable t) {
                setOutput("Error: " + t.getMessage());
            }
        });
    }

    private void callBossStatus() {
        String u = editUsername.getText().toString().trim();
        if (u.isEmpty()) { editUsername.setError("username"); return; }

        api.getBossStatus(u).enqueue(new Callback<BossStatusResponse>() {
            @Override public void onResponse(Call<BossStatusResponse> call, Response<BossStatusResponse> resp) {
                if (resp.isSuccessful() && resp.body() != null) setOutput(resp.body().toString());
                else setOutput("Error boss status: " + resp.code());
            }
            @Override public void onFailure(Call<BossStatusResponse> call, Throwable t) {
                setOutput("Error: " + t.getMessage());
            }
        });
    }

    // ---------- SOCIAL ----------
    private void callAddFriend() {
        String from = editUsername.getText().toString().trim();
        String to = editExtra1.getText().toString().trim();
        if (from.isEmpty()) { editUsername.setError("from_user"); return; }
        if (to.isEmpty()) { editExtra1.setError("to_user"); return; }

        FriendAddRequest req = new FriendAddRequest(from, to);
        api.addFriend(req).enqueue(new Callback<GenericMessageResponse>() {
            @Override public void onResponse(Call<GenericMessageResponse> call, Response<GenericMessageResponse> resp) {
                if (resp.isSuccessful() && resp.body() != null) setOutput(resp.body().toString());
                else setOutput("Error add friend: " + resp.code());
            }
            @Override public void onFailure(Call<GenericMessageResponse> call, Throwable t) {
                setOutput("Error: " + t.getMessage());
            }
        });
    }

    private void callRespondFriend() {
        String from = editUsername.getText().toString().trim();
        String to = editExtra1.getText().toString().trim();
        String action = editExtra2.getText().toString().trim(); // accept / decline / block

        if (from.isEmpty()) { editUsername.setError("from_user"); return; }
        if (to.isEmpty()) { editExtra1.setError("to_user"); return; }
        if (action.isEmpty()) { editExtra2.setError("action"); return; }

        api.respondFriend(from, to, action).enqueue(new Callback<GenericMessageResponse>() {
            @Override
            public void onResponse(Call<GenericMessageResponse> call, Response<GenericMessageResponse> resp) {
                if (resp.isSuccessful() && resp.body() != null) setOutput(resp.body().toString());
                else setOutput("Error respond friend: " + resp.code());
            }
            @Override
            public void onFailure(Call<GenericMessageResponse> call, Throwable t) {
                setOutput("Error: " + t.getMessage());
            }
        });
    }

    private void callLeaderboard() {
        api.getLeaderboard().enqueue(new Callback<List<LeaderboardEntry>>() {
            @Override public void onResponse(Call<List<LeaderboardEntry>> call, Response<List<LeaderboardEntry>> resp) {
                if (resp.isSuccessful() && resp.body() != null) {
                    StringBuilder sb = new StringBuilder("Leaderboard:\n");
                    for (LeaderboardEntry e : resp.body()) sb.append(e.toString()).append("\n");
                    setOutput(sb.toString());
                } else setOutput("Error leaderboard: " + resp.code());
            }
            @Override public void onFailure(Call<List<LeaderboardEntry>> call, Throwable t) {
                setOutput("Error: " + t.getMessage());
            }
        });
    }
}
