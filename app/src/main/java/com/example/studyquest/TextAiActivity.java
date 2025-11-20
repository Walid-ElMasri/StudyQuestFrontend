package com.example.studyquest;

import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studyquest.api.ApiService;
import com.example.studyquest.api.RetrofitClient;
import com.example.studyquest.models.TextAiRequest;
import com.example.studyquest.models.TextAiResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TextAiActivity extends AppCompatActivity {

    private EditText promptEdit;
    private EditText usernameEdit;
    private TextView responseText;
    private ProgressBar progressBar;
    private ApiService api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_ai);

        api = RetrofitClient.getClient().create(ApiService.class);

        usernameEdit = findViewById(R.id.editAiUser);
        promptEdit = findViewById(R.id.editPrompt);
        responseText = findViewById(R.id.textAiResponse);
        progressBar = findViewById(R.id.progressBarAi);
        Button btnSend = findViewById(R.id.btnSendPrompt);

        btnSend.setOnClickListener(v -> sendPrompt());
    }

    private void sendPrompt() {
        String username = usernameEdit.getText().toString().trim();
        String prompt = promptEdit.getText().toString().trim();
        if (username.isEmpty()) {
            usernameEdit.setError("Required");
            return;
        } else usernameEdit.setError(null);

        if (prompt.isEmpty()) {
            promptEdit.setError("Required");
            return;
        } else promptEdit.setError(null);

        progressBar.setVisibility(ProgressBar.VISIBLE);
        responseText.setText("");

        TextAiRequest req = new TextAiRequest(username, prompt);

        api.askTextAi(req).enqueue(new Callback<TextAiResponse>() {
            @Override
            public void onResponse(Call<TextAiResponse> call, Response<TextAiResponse> response) {
                progressBar.setVisibility(ProgressBar.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    responseText.setText(response.body().response);
                } else {
                    responseText.setText("Error from AI");
                }
            }

            @Override
            public void onFailure(Call<TextAiResponse> call, Throwable t) {
                progressBar.setVisibility(ProgressBar.GONE);
                responseText.setText("Error: " + t.getMessage());
            }
        });
    }
}
