package com.example.studyquest.api;

import com.example.studyquest.models.*;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.PATCH;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // ---------- USERS ----------
    // POST /users/ - register new user
    @POST("users/")
    Call<User> createUser(@Body UserCreateRequest request);

    // GET /users/ - list all users
    @GET("users/")
    Call<List<User>> listUsers();

    // GET /users/{username}
    @GET("users/{username}")
    Call<User> getUser(@Path("username") String username);


    // ---------- HOME / DASHBOARD ----------
    // GET /home/dashboard?user=
    @GET("home/dashboard")
    Call<DashboardStats> getDashboardStats(@Query("user") String username);


    // ---------- PROGRESS ----------
    // POST /progress/ - log a study session
    @POST("progress/")
    Call<ProgressLogResponse> logProgress(@Body ProgressLogRequest request);

    // GET /progress/?user=
    @GET("progress/")
    Call<List<ProgressItem>> listProgress(@Query("user") String username);

    // GET /progress/stats?user=
    @GET("progress/stats")
    Call<ProgressStats> getProgressStats(@Query("user") String username);


    // ---------- QUESTS ----------
    // POST /quests/
    @POST("quests/")
    Call<Quest> createQuest(@Body QuestCreateRequest request);

    // PUT /quests/{quest_id}/complete
    @PUT("quests/{quest_id}/complete")
    Call<QuestCompleteResponse> completeQuest(@Path("quest_id") int questId);

    // GET /quests/level/{username}
    @GET("quests/level/{username}")
    Call<QuestLevelData> getUserLevel(@Path("username") String username);


    // ---------- COSMETICS ----------
    // POST /cosmetics/avatar
    @POST("cosmetics/avatar")
    Call<Avatar> upsertAvatar(@Body AvatarUpsertRequest request);

    // GET /cosmetics/avatar/{username}
    @GET("cosmetics/avatar/{username}")
    Call<Avatar> getAvatar(@Path("username") String username);

    // POST /cosmetics/badge
    @POST("cosmetics/badge")
    Call<Badge> createBadge(@Body BadgeCreateRequest request);

    // GET /cosmetics/badges
    @GET("cosmetics/badges")
    Call<List<Badge>> listBadges();


    // ---------- TEXT AI ----------
    // POST /text-ai/
    @POST("text-ai/")
    Call<TextAiResponse> askTextAi(@Body TextAiRequest request);

    // GET /text-ai/?user=
    @GET("text-ai/")
    Call<List<TextAiReflection>> listTextAiReflections(@Query("user") String username);


    // ---------- BOSS BATTLE ----------
    // POST /boss/start
    @POST("boss/start")
    Call<BossStartResponse> startBoss(@Body BossStartRequest request);

    @GET("boss/question")
    Call<BossQuestionResponse> getBossQuestion(@Query("user") String user);

    // POST /boss/answer
    @POST("boss/answer")
    Call<BossAnswerResponse> submitBossAnswer(@Body BossAnswerRequest request);

    // GET /boss/status?user=
    @GET("boss/status")
    Call<BossStatusResponse> getBossStatus(@Query("user") String username);

    @POST("boss/forfeit")
    Call<BossEndResponse> forfeit(@Query("user") String user);


    // ---------- SOCIAL ----------
    // POST /social/friends/add
    @POST("social/friends/add")
    Call<GenericMessageResponse> addFriend(@Body FriendAddRequest request);

    // PATCH /social/friends/respond
    @PATCH("social/friends/respond")
    Call<GenericMessageResponse> respondFriend(
            @Query("user") String user,
            @Query("friend_username") String friendUsername,
            @Query("action") String action
    );

    // GET /social/leaderboard
    @GET("social/leaderboard")
    Call<List<LeaderboardEntry>> getLeaderboard();

    @GET("social/friends/list")
    Call<List<Friend>> listFriends(@Query("user") String username);

    @DELETE("social/friends/remove")
    Call<GenericMessageResponse> removeFriend(@Query("user") String user, @Query("friend_username") String friendUsername);
}
