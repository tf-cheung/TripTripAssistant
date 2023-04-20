package com.example.tripassistant;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

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

    private List<User> friends = new ArrayList<>();
    private FloatingActionButton addExpenseFAB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);
        addExpenseFAB = findViewById(R.id.addExpenseFAB);
        addExpenseFAB.setOnClickListener(view -> {
            Intent intent = new Intent(ExpenseActivity.this, AddExpenseActivity.class);
            startActivity(intent);
        });

        Intent intent = getIntent();
        String tripId = intent.getStringExtra("tripId");
        if (tripId == null) tripId = "-NTQCyHKQynPCGBeySev";
        DatabaseReference tripsRef = FirebaseDatabase.getInstance().getReference("trips");
        DatabaseReference tripRef = tripsRef.child(tripId);
        tripRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Handle the trip data here
//                Trip trip = dataSnapshot.getValue(Trip.class);
                // Do something with the trip data
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });
    }
}