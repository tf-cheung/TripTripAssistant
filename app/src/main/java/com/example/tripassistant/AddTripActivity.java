package com.example.tripassistant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tripassistant.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class AddTripActivity extends AppCompatActivity {
    private AutoCompleteTextView memberInput;
    private ChipGroup memberChipGroup;
    private EditText tripNameInput;

    private String currentUserId;
    private TextView startDateText;
    private List<User> users= new ArrayList<>();

    String email, uid, dp;
    private FirebaseAuth mAuth;
    private HashSet<String> members;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // If the user is not logged in, redirect them to LoginActivity
            Intent intent = new Intent(AddTripActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        members = new HashSet<>();
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        Query query = usersRef.orderByChild("email").equalTo(currentUser.getEmail());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        currentUserId = userSnapshot.getKey();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });




        memberInput = findViewById(R.id.member_input);
        memberChipGroup = findViewById(R.id.member_chip_group);
        tripNameInput = findViewById(R.id.trip_name_input);
        startDateText = findViewById(R.id.start_date_text);
//        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
//        bottomNavigationView.setOnItemSelectedListener(item -> {
//            int itemId = item.getItemId();
//            if (itemId == R.id.navigation_activity1 && !this.getClass().equals(DisplayTripActivity.class)) {
//                Intent intent = new Intent(this, DisplayTripActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//                startActivity(intent);
//            } else if (itemId == R.id.navigation_activity2 && !this.getClass().equals(ExpenseActivity.class)) {
//                Intent intent = new Intent(this, ExpenseActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//                startActivity(intent);
//            } else if (itemId == R.id.navigation_activity3 && !this.getClass().equals(LoginActivity.class)) {
//                Intent intent = new Intent(this, LoginActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//                startActivity(intent);
//            }
//            return true;
//        });




        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> usernames = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String userId = snapshot.getKey();
                    Log.d("userId",userId);

                    String username = snapshot.child("username").getValue(String.class);
                    Log.d("userId",username);

                    String email = snapshot.child("email").getValue(String.class);
                    Log.d("userId",email);

                    if(!userId.equals(currentUserId)){
                        User user = new User(userId,username,email);
                        usernames.add(username);
                        users.add(user);
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(AddTripActivity.this, android.R.layout.simple_dropdown_item_1line, usernames);
                memberInput.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };



        usersRef.addValueEventListener(valueEventListener);

        memberInput.setOnItemClickListener((parent, view, position, id) -> {
            String memberName = parent.getItemAtPosition(position).toString();
            if(members.contains(memberName)){
                memberInput.setText("");
            }else {
                members.add(memberName);
                addMemberChip(memberName);
                memberInput.setText("");
            }

        });


        memberInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String inputText = memberInput.getText().toString().trim();

                // Check if the entered text is part of the dropdown suggestions
                boolean found = false;
                for (int i = 0; i < memberInput.getAdapter().getCount(); i++) {
                    if (memberInput.getAdapter().getItem(i).toString().equals(inputText) ) {
                        found = true;
                        break;
                    }
                }

                // If the entered text is not part of the suggestions, clear the input field
                if (!found) {
                    memberInput.setText("");
                    Toast.makeText(AddTripActivity.this, "User not found. Please select a user from the suggestions.", Toast.LENGTH_SHORT).show();

                }
            }
        });

        Button createTripButton = findViewById(R.id.create_trip_button);
        createTripButton.setOnClickListener(v -> {
            String tripName = tripNameInput.getText().toString().trim();

            if (tripName.isEmpty()) {
                Toast.makeText(AddTripActivity.this, "Please enter a trip name.", Toast.LENGTH_SHORT).show();
            } else {
                tripName = tripNameInput.getText().toString();
                List<String> members = new ArrayList<>();
                for (int i = 0; i < memberChipGroup.getChildCount(); i++) {
                    Chip chip = (Chip) memberChipGroup.getChildAt(i);
                    members.add(getUserIdByUsername(users,chip.getText().toString()));
                }
                members.add(currentUserId);
                String startDate = startDateText.getText().toString();
                if (startDate.equals("Select date")) {
                    Toast.makeText(AddTripActivity.this, "Please select a start date.", Toast.LENGTH_SHORT).show();
                } else {
                    saveTripToFirebase(tripName, members, startDate);
                    onBackPressed();

                }
            }
        });

        Button cancelButton = findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(v -> onBackPressed());


        startDateText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(AddTripActivity.this, (view, selectedYear, selectedMonth, selectedDay) -> {
                selectedMonth++; // Months are indexed from 0-11, so we need to add 1 to get the correct month
                String date = String.format("%02d/%02d/%d", selectedDay, selectedMonth, selectedYear);
                startDateText.setText(date);
                startDateText.setVisibility(View.GONE);
                onDateSelected(date);

            }, year, month, day);

            datePickerDialog.show();
        });


        ConstraintLayout mainLayout = findViewById(R.id.add_trip_layout);
        mainLayout.setOnClickListener(v -> {
            // Clear focus from the currently focused view
            View currentFocus = getCurrentFocus();
            if (currentFocus != null) {
                currentFocus.clearFocus();
            }
        });

    }

    public String getUserIdByUsername(List<User> userList, String username) {
        for (User user : userList) {
            if (user.getUsername().equals(username)) {
                return user.getUserId();
            }
        }
        return null; // 如果没有找到匹配的用户，则返回 null
    }


    private void onDateSelected(String selectedDate) {
        ChipGroup dateChipGroup = findViewById(R.id.date_chip_group);

        // 创建一个新的 Chip
        Chip chip = new Chip(this);
        chip.setText(selectedDate);
        chip.setCloseIconVisible(true);
        chip.setCheckable(false);
        chip.setClickable(false);

        // 为 Chip 设置关闭图标的点击监听器，以便在用户点击关闭图标时删除该 Chip
        chip.setOnCloseIconClickListener(v -> {
            dateChipGroup.removeView(chip);
            startDateText.setText("Select date");
            startDateText.setVisibility(View.VISIBLE);
        });

        // 将新创建的 Chip 添加到 ChipGroup 中
        dateChipGroup.addView(chip);
    }

    private void addMemberChip(String memberName) {
        Chip chip = new Chip(this);
        chip.setText(memberName);
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(v -> {
            members.remove(memberName);
            memberChipGroup.removeView(chip);
        }
        );
        memberChipGroup.addView(chip);
    }

    private void saveTripToFirebase(String tripName, List<String> members,String startDate) {
        final String timestamp = String.valueOf(System.currentTimeMillis());
        DatabaseReference tripsRef = FirebaseDatabase.getInstance().getReference("trips");
        String tripId = tripsRef.push().getKey();

        if (tripId == null) {
            Toast.makeText(this, "Error creating trip. Please try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> tripData = new HashMap<>();
        tripData.put("tripName", tripName);
        tripData.put("members", members);
        tripData.put("startDate", startDate);

        tripData.put("tripId", tripId);
//        tripData.put("description", description);
        tripData.put("ptime", timestamp);
        tripData.put("plike", 0);
        tripData.put("pcomments", 0);


        tripsRef.child(tripId).setValue(tripData)
                .addOnSuccessListener(aVoid -> Toast.makeText(AddTripActivity.this, "Trip created successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(AddTripActivity.this, "Error creating trip. Please try again.", Toast.LENGTH_SHORT).show());
    }

}