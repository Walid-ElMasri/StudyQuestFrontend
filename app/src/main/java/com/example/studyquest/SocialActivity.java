package com.example.studyquest;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.studyquest.R;
import com.example.studyquest.api.ApiService;
import com.example.studyquest.api.RetrofitClient;
import com.example.studyquest.models.Friend;
import com.example.studyquest.models.LeaderboardEntry;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;
import com.example.studyquest.models.FriendAddRequest;
import com.example.studyquest.models.GenericMessageResponse;

public class SocialActivity extends AppCompatActivity {

    private ListView listFriends, listLeaderboard, listPendingRequests;
    private EditText editFriendUsername;
    private Button btnAddFriend;
    private ApiService api;
    private String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social);

        api = RetrofitClient.getClient().create(ApiService.class);

        listFriends = findViewById(R.id.listFriends);
        listLeaderboard = findViewById(R.id.listLeaderboard);
        listPendingRequests = findViewById(R.id.listPendingRequests);
        editFriendUsername = findViewById(R.id.editFriendUsername);
        btnAddFriend = findViewById(R.id.btnAddFriend);

        loadPendingRequests();
        loadLeaderboard();
        loadFriendsList();
        setupAddFriend();
        setupFriendRemoval();
    }

    private void loadLeaderboard() {
        api.getLeaderboard().enqueue(new Callback<List<LeaderboardEntry>>() {
            @Override
            public void onResponse(Call<List<LeaderboardEntry>> call, Response<List<LeaderboardEntry>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<LeaderboardEntry> leaderboard = response.body();

                    List<String> leaderboardDisplay = new ArrayList<>();
                    for (LeaderboardEntry entry : leaderboard) {
                        String entryText = "#" + entry.id + " " + entry.user + " - " + entry.total_xp + " XP";
                        leaderboardDisplay.add(entryText);
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                            SocialActivity.this,
                            android.R.layout.simple_list_item_1,
                            leaderboardDisplay
                    ) {
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            View view = super.getView(position, convertView, parent);
                            TextView textView = (TextView) view.findViewById(android.R.id.text1);
                            textView.setTextColor(0xFFE5E7EB);
                            return view;
                        }
                    };
                    listLeaderboard.setAdapter(adapter);

                } else {
                    Toast.makeText(SocialActivity.this, "Failed to load leaderboard", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<LeaderboardEntry>> call, Throwable t) {
                Toast.makeText(SocialActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadFriendsList() {
        String currentUser = "demo";

        api.listFriends(currentUser).enqueue(new Callback<List<Friend>>() {
            @Override
            public void onResponse(Call<List<Friend>> call, Response<List<Friend>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Friend> friends = response.body();

                    if (friends.isEmpty()) {
                        List<String> noFriends = new ArrayList<>();
                        noFriends.add("No friends yet. Add some friends!");

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                SocialActivity.this,
                                android.R.layout.simple_list_item_1,
                                noFriends
                        ) {
                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                View view = super.getView(position, convertView, parent);
                                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                                textView.setTextColor(0xFFE5E7EB);
                                return view;
                            }
                        };
                        listFriends.setAdapter(adapter);
                    } else {
                        List<String> friendsDisplay = new ArrayList<>();
                        for (Friend friend : friends) {
                            String displayText = friend.friend_username;
                            if (friend.status != null) {
                                displayText += " - " + friend.status;
                            }
                            if (friend.since != null) {
                                String shortDate = friend.since.substring(0, 10);
                                displayText += "\nSince: " + shortDate;
                            }
                            friendsDisplay.add(displayText);
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                SocialActivity.this,
                                android.R.layout.simple_list_item_1,
                                friendsDisplay
                        ) {
                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                View view = super.getView(position, convertView, parent);
                                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                                textView.setTextColor(0xFFE5E7EB);
                                return view;
                            }
                        };
                        listFriends.setAdapter(adapter);
                    }
                } else {
                    Toast.makeText(SocialActivity.this, "Failed to load friends", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Friend>> call, Throwable t) {
                Toast.makeText(SocialActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupAddFriend() {
        btnAddFriend.setOnClickListener(v -> {
            String friendUsername = editFriendUsername.getText().toString().trim();
            if (!friendUsername.isEmpty()) {
                FriendAddRequest request = new FriendAddRequest("demo", friendUsername);

                api.addFriend(request).enqueue(new Callback<GenericMessageResponse>() {
                    @Override
                    public void onResponse(Call<GenericMessageResponse> call, Response<GenericMessageResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Toast.makeText(SocialActivity.this,
                                    "Friend request sent to " + friendUsername,
                                    Toast.LENGTH_SHORT).show();
                            editFriendUsername.setText("");
                        } else {
                            Toast.makeText(SocialActivity.this,
                                    "Failed to send friend request",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<GenericMessageResponse> call, Throwable t) {
                        Toast.makeText(SocialActivity.this,
                                "Error: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "Enter a username", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPendingRequests() {
        String currentUser = "demo";

        List<String> pendingPlaceholder = new ArrayList<>();
        pendingPlaceholder.add("No pending requests");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                SocialActivity.this,
                android.R.layout.simple_list_item_1,
                pendingPlaceholder
        ) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                textView.setTextColor(0xFFE5E7EB);
                return view;
            }
        };
        listPendingRequests.setAdapter(adapter);
    }

    private void setupFriendRemoval() {
        listFriends.setOnItemLongClickListener((parent, view, position, id) -> {
            String displayedText = ((TextView) view).getText().toString();
            String friendUsername = displayedText.split(" - ")[0];

            new AlertDialog.Builder(this)
                    .setTitle("Remove Friend")
                    .setMessage("Remove " + friendUsername + "?")
                    .setPositiveButton("Remove", (dialog, which) -> {
                        removeFriend(friendUsername);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();

            return true;
        });
    }

    private void removeFriend(String friendUsername) {
        String currentUser = "demo";

        api.removeFriend(currentUser, friendUsername).enqueue(new Callback<GenericMessageResponse>() {
            @Override
            public void onResponse(Call<GenericMessageResponse> call, Response<GenericMessageResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(SocialActivity.this, "Friend removed successfully", Toast.LENGTH_SHORT).show();
                    loadFriendsList();
                } else {
                    Toast.makeText(SocialActivity.this, "Failed to remove friend", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GenericMessageResponse> call, Throwable t) {
                Toast.makeText(SocialActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}