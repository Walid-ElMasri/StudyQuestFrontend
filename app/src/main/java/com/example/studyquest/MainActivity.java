package com.example.studyquest;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button apiPlaygroundBtn = findViewById(R.id.btnApiPlayground);
        Button btnHome = findViewById(R.id.btnHome);
        Button btnProgress = findViewById(R.id.btnProgress);
        Button btnBoss = findViewById(R.id.btnBoss);
        Button btnAi = findViewById(R.id.btnTextAi);

        apiPlaygroundBtn.setOnClickListener(v ->
                startActivity(new Intent(this, ApiPlaygroundActivity.class)));

        btnHome.setOnClickListener(v -> startActivity(new Intent(this, HomeActivity.class)));
        btnProgress.setOnClickListener(v -> startActivity(new Intent(this, ProgressActivity.class)));
        btnBoss.setOnClickListener(v -> startActivity(new Intent(this, BossActivity.class)));
        btnAi.setOnClickListener(v -> startActivity(new Intent(this, TextAiActivity.class)));
    }
}
