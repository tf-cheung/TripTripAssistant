package com.example.tripassistant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddTripActivity extends AppCompatActivity {
    private AutoCompleteTextView memberInput;
    private ChipGroup memberChipGroup;
    private EditText tripNameInput;

    private String currentUserId = "herman1881";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);

        memberInput = findViewById(R.id.member_input);
        memberChipGroup = findViewById(R.id.member_chip_group);
        tripNameInput = findViewById(R.id.trip_name_input);
        TextView startDateText = findViewById(R.id.start_date_text);


        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> usernames = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String username = snapshot.child("username").getValue(String.class);
                    if(!username.equals(currentUserId)){
                        usernames.add(username);

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
            addMemberChip(memberName);
            memberInput.setText("");
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
                    members.add(chip.getText().toString());
                }
                members.add(currentUserId);
                String startDate = startDateText.getText().toString();
                if (startDate.equals("Select date")) {
                    Toast.makeText(AddTripActivity.this, "Please select a start date.", Toast.LENGTH_SHORT).show();
                } else {
                    saveTripToFirebase(tripName, members, startDate);
                    Intent intent = new Intent(AddTripActivity.this, DisplayTripActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        TextView cancelButton = findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(v -> {
            Intent intent = new Intent(AddTripActivity.this, DisplayTripActivity.class);
            startActivity(intent);
            finish();
        });


        startDateText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(AddTripActivity.this, (view, selectedYear, selectedMonth, selectedDay) -> {
                selectedMonth++; // Months are indexed from 0-11, so we need to add 1 to get the correct month
                String date = selectedDay + "/" + selectedMonth + "/" + selectedYear;
                startDateText.setText(date);
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

    private void addMemberChip(String memberName) {
        Chip chip = new Chip(this);
        chip.setText(memberName);
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(v -> memberChipGroup.removeView(chip));
        memberChipGroup.addView(chip);
    }

    private void saveTripToFirebase(String tripName, List<String> members,String startDate) {
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

        tripsRef.child(tripId).setValue(tripData)
                .addOnSuccessListener(aVoid -> Toast.makeText(AddTripActivity.this, "Trip created successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(AddTripActivity.this, "Error creating trip. Please try again.", Toast.LENGTH_SHORT).show());
    }

}