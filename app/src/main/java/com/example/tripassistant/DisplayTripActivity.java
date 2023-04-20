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
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DisplayTripActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TripAdapter tripAdapter;
    private List<Trip> tripsList;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;


    private String currentUserId = "KNALPmRX2VNl7lnBYdhq2gAHXBr1";

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
                    String tripsId = tripSnapshot.getKey();
                    String tripsName = tripSnapshot.child("tripName").getValue(String.class);
                    String startDate = tripSnapshot.child("startDate").getValue(String.class);
                    GenericTypeIndicator<List<String>> genericTypeIndicator = new GenericTypeIndicator<List<String>>() {};
                    List<String> members = tripSnapshot.child("members").getValue(genericTypeIndicator);
//                    Trip trip = tripSnapshot.getValue(Trip.class);
                    Trip trip = new Trip(tripsId,tripsName,members,startDate);

                    if (trip != null && trip.getMembers().contains(currentUserId)) {
                        tripsList.add(trip);
                        Log.d("tripid",trip.getTripId());
                    }
                }

                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");


                tripsList = tripsList.stream()
                        .sorted(Comparator.comparing(trip -> LocalDate.parse(trip.getStartDate(), dateFormatter)))
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
