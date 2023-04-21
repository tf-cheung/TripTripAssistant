package com.example.tripassistant;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ExpenseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_activity1 && !this.getClass().equals(DisplayTripActivity.class)) {
                Intent intent = new Intent(this, DisplayTripActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            } else if (itemId == R.id.navigation_activity2 && !this.getClass().equals(ExpenseActivity.class)) {
                Intent intent = new Intent(this, ExpenseActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            } else if (itemId == R.id.navigation_activity3 && !this.getClass().equals(LoginActivity.class)) {
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
            return true;
        });

    }


}