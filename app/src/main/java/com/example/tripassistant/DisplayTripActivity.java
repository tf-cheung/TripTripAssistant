package com.example.tripassistant;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripassistant.models.Trip;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
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
    private final int PERMISSION_CODE = 1;


    private String currentUserId = "KNALPmRX2VNl7lnBYdhq2gAHXBr1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_trip);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

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

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

        } else {
            ActivityCompat.requestPermissions(DisplayTripActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
        }

    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted..", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please provide the permissions", Toast.LENGTH_SHORT).show();
            }
        }
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
