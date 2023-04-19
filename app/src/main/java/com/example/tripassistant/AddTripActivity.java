package com.example.tripassistant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddTripActivity extends AppCompatActivity {
    private AutoCompleteTextView memberInput;
    private ChipGroup memberChipGroup;
    private EditText tripNameInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);

        memberInput = findViewById(R.id.member_input);
        memberChipGroup = findViewById(R.id.member_chip_group);
        tripNameInput = findViewById(R.id.trip_name_input);

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> usernames = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String username = snapshot.child("username").getValue(String.class);
                    usernames.add(username);
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
                saveTripToFirebase(tripName, members);
                Intent intent = new Intent(AddTripActivity.this, DisplayTripActivity.class);
                startActivity(intent);
            }

        });

        TextView cancelButton = findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(v -> {
            Intent intent = new Intent(AddTripActivity.this, DisplayTripActivity.class);
            startActivity(intent);
        });

    }

    private void addMemberChip(String memberName) {
        Chip chip = new Chip(this);
        chip.setText(memberName);
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(v -> memberChipGroup.removeView(chip));
        memberChipGroup.addView(chip);
    }

    private void saveTripToFirebase(String tripName, List<String> members) {
        DatabaseReference tripsRef = FirebaseDatabase.getInstance().getReference("trips");
        String tripId = tripsRef.push().getKey();

        if (tripId == null) {
            Toast.makeText(this, "Error creating trip. Please try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> tripData = new HashMap<>();
        tripData.put("tripName", tripName);
        tripData.put("members", members);

        tripsRef.child(tripId).setValue(tripData)
                .addOnSuccessListener(aVoid -> Toast.makeText(AddTripActivity.this, "Trip created successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(AddTripActivity.this, "Error creating trip. Please try again.", Toast.LENGTH_SHORT).show());
    }

}