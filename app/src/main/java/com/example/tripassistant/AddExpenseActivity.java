package com.example.tripassistant;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;

public class AddExpenseActivity extends AppCompatActivity {
    private ImageButton backBtn;
    private Button okayBtn, selectPayerBtn, selectPayeeBtn;
    private EditText descriptionEDT, amountEDT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);
        descriptionEDT = findViewById(R.id.editTextDescription);
        amountEDT = findViewById(R.id.editTextAmount);
        backBtn = findViewById(R.id.back_button);
        okayBtn = findViewById(R.id.okay_button);
        selectPayerBtn = findViewById(R.id.payer_button);
        selectPayeeBtn = findViewById(R.id.payee_button);

        ArrayList<String> tripMembers = getIntent().getStringArrayListExtra("tripMembers");
        Log.d("DEBUG", tripMembers.toString());

        backBtn.setOnClickListener(view -> {
            onBackPressed();
        });

        okayBtn.setOnClickListener(view -> {
            handleExpenseSubmit();
        });

        selectPayerBtn.setOnClickListener(view -> {
            Intent intent = new Intent(AddExpenseActivity.this, SelectPayerActivity.class);
            intent.putStringArrayListExtra("tripMembers", tripMembers);
            startActivity(intent);
        });

        selectPayeeBtn.setOnClickListener(view -> {
            Intent intent = new Intent(AddExpenseActivity.this, SelectPayeeActivity.class);
            intent.putStringArrayListExtra("tripMembers", tripMembers);
            startActivity(intent);
        });
    }

    private void handleExpenseSubmit() {
        String description = descriptionEDT.toString().trim();
        if (description.isEmpty()) {
            Toast.makeText(AddExpenseActivity.this, "Description is empty.",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        String amountStr = amountEDT.toString().trim();
        if (amountStr.isEmpty()) {
            Toast.makeText(AddExpenseActivity.this, "Amount is empty.",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        float amount;
        try {
            amount = Float.parseFloat(amountStr);
        } catch (Exception e) {
            Toast.makeText(AddExpenseActivity.this, e.getMessage(),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (amount <= 0) {
            Toast.makeText(AddExpenseActivity.this, "Amount must be greater than zero.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

    }
}