package com.example.tripassistant;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.tripassistant.models.ChecklistOption;
import com.example.tripassistant.models.Expense;
import com.example.tripassistant.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddExpenseActivity extends AppCompatActivity {
    private static final int SELECT_PAYER_REQUEST_CODE = 100;
    private static final int SELECT_PAYEE_REQUEST_CODE = 200;
    private Button selectPayerBtn, selectPayeeBtn;
    private EditText descriptionEDT, amountEDT;
    private String username = "Jade", description, tripId;

    private final ArrayList<String> memberNames = new ArrayList<>();
    private ArrayList<Integer> isNeeded = new ArrayList<>();
    private ArrayList<String> shares = new ArrayList<>();
    private int selectedIndex, selectedTab;
    private float sum = -1f, amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);
        descriptionEDT = findViewById(R.id.editTextDescription);
        amountEDT = findViewById(R.id.editTextAmount);
        ImageButton backBtn = findViewById(R.id.back_button);
        Button okayBtn = findViewById(R.id.okay_button);
        selectPayerBtn = findViewById(R.id.payer_button);
        selectPayeeBtn = findViewById(R.id.payee_button);

        Intent currIntent = getIntent();
        tripId = currIntent.getStringExtra("tripId");
        ArrayList<String> memberIds = currIntent.getStringArrayListExtra("tripMembers");

        for (int i = 0; i < memberIds.size(); i++) {
            isNeeded.add(1);
            shares.add("");
            String memberId = memberIds.get(i);
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(memberId);
            int finalI = i;
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        if (user.getUsername().equals(username)) {
                            selectedIndex = finalI;
                        }
                        memberNames.add(user.getUsername());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "onCancelled", error.toException());
                }
            });
        }

        backBtn.setOnClickListener(view -> {
            onBackPressed();
        });

        okayBtn.setOnClickListener(view -> {
            handleExpenseSubmit();
        });

        selectPayerBtn.setOnClickListener(view -> {
            Intent intent = new Intent(AddExpenseActivity.this, SelectPayerActivity.class);
            intent.putStringArrayListExtra("tripMembers", memberNames);
            intent.putExtra("currIndex", selectedIndex);
            startActivityForResult(intent, SELECT_PAYER_REQUEST_CODE);
        });

        selectPayeeBtn.setOnClickListener(view -> {
            Intent intent = new Intent(AddExpenseActivity.this, SelectPayeeActivity.class);
            intent.putStringArrayListExtra("tripMembers", memberNames);
            intent.putIntegerArrayListExtra("isNeeded", isNeeded);
            intent.putStringArrayListExtra("shares", shares);
            intent.putExtra("selectedTab", selectedTab);
            startActivityForResult(intent, SELECT_PAYEE_REQUEST_CODE);
        });
    }

    private void handleExpenseSubmit() {
        if (!isValid()) return;
        Map<String, Float> splitMap = new HashMap<>();
        if (selectedTab == 0) {
            int count = 0;
            for (int i : isNeeded) count += i;
            float share = amount / count;
            for (int i = 0; i < memberNames.size(); i++) {
                if (isNeeded.get(i) == 1) {
                    splitMap.put(memberNames.get(i), share);
                }
            }
        } else {
            for (int i = 0; i < memberNames.size(); i++) {
                if (!shares.get(i).isEmpty()) {
                    splitMap.put(memberNames.get(i), Float.parseFloat(shares.get(i)));
                }
            }
        }
        Expense expense = new Expense(memberNames.get(selectedIndex), description, amount, splitMap, new Date());
        DatabaseReference tripRef = FirebaseDatabase.getInstance().getReference().child("trips").child(tripId);
        DatabaseReference expensesRef = tripRef.child("expenses");
        expensesRef.push().setValue(expense)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(AddExpenseActivity.this, "Expense added successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AddExpenseActivity.this, "Failed to add expense", Toast.LENGTH_SHORT).show();
                    }
                });

        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == SELECT_PAYER_REQUEST_CODE) {
                selectedIndex = data.getIntExtra("selectedIndex", -1);
                String selectedName = memberNames.get(selectedIndex);
                if (!selectedName.equals(username)) selectPayerBtn.setText(selectedName);
                else selectPayerBtn.setText("you");
            } else if (requestCode == SELECT_PAYEE_REQUEST_CODE) {
                selectedTab = data.getIntExtra("selectedTab", 0);
                selectPayeeBtn.setText(selectedTab == 0 ? "Equally" : "Unequally");
                isNeeded = data.getIntegerArrayListExtra("isNeeded");
                shares = data.getStringArrayListExtra("shares");
                if (selectedTab == 1) {
                    float shareSum = 0;
                    for (String share : shares) {
                        if (!share.isEmpty())
                            shareSum += Float.parseFloat(share);
                    }
                    sum = shareSum;
                    amountEDT.setText(String.valueOf(sum));
                } else {
                    sum = -1f;
                }
            }
        }
    }

    private boolean isValid() {
        description = descriptionEDT.getText().toString().trim();
        if (description.isEmpty()) {
            Toast.makeText(AddExpenseActivity.this, "Description is empty.",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        String amountStr = amountEDT.getText().toString().trim();
        if (amountStr.isEmpty()) {
            Toast.makeText(AddExpenseActivity.this, "Amount is empty.",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        try {
            amount = Float.parseFloat(amountStr);
        } catch (Exception e) {
            Toast.makeText(AddExpenseActivity.this, e.getMessage(),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (amount <= 0) {
            Toast.makeText(AddExpenseActivity.this, "Amount must be greater than zero.",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (sum > 0 && Math.abs(Float.compare(sum, amount)) > 0.0001) {
            Toast.makeText(AddExpenseActivity.this, "Please make sure the amount is equal to the sum of shares.",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (selectedIndex < 0) {
            Toast.makeText(AddExpenseActivity.this, "Payer is not selected.",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}