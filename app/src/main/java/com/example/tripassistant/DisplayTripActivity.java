package com.example.tripassistant;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripassistant.models.Trip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DisplayTripActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TripAdapter tripAdapter;
    private List<Trip> tripsList;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private String currentUserId = "herman1881";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_trip);

        recyclerView = findViewById(R.id.trip_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        tripsList = new ArrayList<>();
        tripAdapter = new TripAdapter(tripsList);
        recyclerView.setAdapter(tripAdapter);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        loadUserTrips();

        TextView createButton = findViewById(R.id.create_button);
        createButton.setOnClickListener(v -> {
            Intent intent = new Intent(DisplayTripActivity.this, AddTripActivity.class);
            startActivity(intent);
        });


    }

    private void loadUserTrips() {
        mDatabase.child("trips").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tripsList.clear();
                for (DataSnapshot tripSnapshot : dataSnapshot.getChildren()) {
                    Trip trip = tripSnapshot.getValue(Trip.class);
                    if (trip != null && trip.getMembers().contains(currentUserId)) {
                        tripsList.add(trip);
                    }
                }
                tripsList = tripsList.stream()
                        .sorted(Comparator.comparing(Trip::getStartDate))
                        .collect(Collectors.toList());
                tripAdapter.setTripsList(tripsList);
                tripAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("DisplayTripActivity", "Error loading trips: " + databaseError.getMessage());
            }
        });
    }




}
