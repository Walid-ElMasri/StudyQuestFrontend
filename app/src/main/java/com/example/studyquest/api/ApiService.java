package com.example.studyquest.api;

import com.example.studyquest.models.*;

import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    @GET("users/{username}")
    Call<User> getUser(@Path("username") String username);

    @GET("progress/")
    Call<ProgressResponse> getProgress(@Query("user") String user);

    @POST("boss/start")
    Call<BossStartResponse> startBoss(@Body BossStartRequest request);

    @POST("text-ai")
    Call<TextAiResponse> askTextAi(@Body TextAiRequest request);
}
