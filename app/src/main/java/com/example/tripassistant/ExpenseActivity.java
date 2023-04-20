package com.example.tripassistant;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.tripassistant.models.Trip;
import com.example.tripassistant.models.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ExpenseActivity extends AppCompatActivity {

    private ArrayList<String> tripMembers = new ArrayList<>();
    private FloatingActionButton addExpenseFAB;
    private FirebaseDatabase databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        Intent intent = getIntent();
        String tripId = intent.getStringExtra("tripId") == null ? "-NTQdSYhAYbUmzB71vRU" : intent.getStringExtra("tripId");

        databaseRef = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = databaseRef.getReference("users"), tripsRef = databaseRef.getReference("trips");
        DatabaseReference tripRef = tripsRef.child(tripId);
        tripRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Handle the trip data here
                Trip trip = dataSnapshot.getValue(Trip.class);
                if (trip == null) {
                    Toast.makeText(ExpenseActivity.this, "Trip doesn't exist!", Toast.LENGTH_SHORT).show();
                    return;
                }
                tripMembers.clear();
                for (String member : trip.getMembers()) {
                    DatabaseReference userRef = usersRef.child(member);
                    userRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            if (user != null) {
                                Log.d("DEBUG", user.getUsername());
                                tripMembers.add(user.getUsername());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e(TAG, "onCancelled", error.toException());
                        }
                    });
                }
                Log.d("DEBUG", tripMembers.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });

        addExpenseFAB = findViewById(R.id.addExpenseFAB);
        addExpenseFAB.setOnClickListener(view -> {
            Intent newIntent = new Intent(ExpenseActivity.this, AddExpenseActivity.class);
            newIntent.putStringArrayListExtra("tripMembers", tripMembers);
            startActivity(newIntent);
        });
    }
}