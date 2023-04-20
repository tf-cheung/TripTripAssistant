package com.example.tripassistant;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageButton;

public class SelectPayerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_payer);

        ImageButton backBtn = findViewById(R.id.back_button);
        backBtn.setOnClickListener(view -> {
            onBackPressed();
        });
    }
}