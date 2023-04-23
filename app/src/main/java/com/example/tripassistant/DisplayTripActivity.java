package com.example.tripassistant;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;

import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripassistant.models.Trip;
import com.example.tripassistant.models.User;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
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
    private DatabaseReference usersReference;
    private final int PERMISSION_CODE = 1;
    private Dialog progressDialog;
    private String username, userEmail;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_trip);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // If the user is not logged in, redirect them to LoginActivity
            Intent intent = new Intent(DisplayTripActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        userEmail = currentUser.getEmail();

        progressDialog = new Dialog(this);
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setCancelable(false);
        progressDialog.show(); // 显示Dialog


        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        ImageButton menuButton = findViewById(R.id.menu_button);
        FloatingActionButton epButton = findViewById(R.id.explore_button);

        menuButton.setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));
        epButton.setOnClickListener(view -> {
            Intent intent = new Intent(DisplayTripActivity.this, ExploreActivity.class);
            startActivity(intent);
        });


        ImageView signOutButton = findViewById(R.id.signout_button);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        recyclerView = findViewById(R.id.trip_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        tripsList = new ArrayList<>();
        tripAdapter = new TripAdapter(tripsList, username);
        recyclerView.setAdapter(tripAdapter);


        mDatabase = FirebaseDatabase.getInstance().getReference();

        loadUserTrips();

        Button createButton = findViewById(R.id.create_button);
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
        usersReference = FirebaseDatabase.getInstance().getReference("users");
        Query query = usersReference.orderByChild("email").equalTo(userEmail);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        currentUserId = userSnapshot.getKey();
                        break;
                    }
                    // 用户信息处理
                    usersReference.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                username = dataSnapshot.child("username").getValue(String.class);
                                String email = dataSnapshot.child("email").getValue(String.class);
                                TextView navUsername = findViewById(R.id.nav_username);
                                TextView navEmail = findViewById(R.id.nav_email);
                                navUsername.setText(getResources().getString(R.string.hello, username));
                                navUsername.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
                                tripAdapter.setUsername(username);
                                navEmail.setText(email);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });

                    // 获取用户相关的trips
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
                                Trip trip = new Trip(tripsId,tripsName,members,startDate);

                                if (trip != null && trip.getMembers() != null && trip.getMembers().contains(currentUserId)) {
                                    tripsList.add(trip);
                                    Log.d("tripid",trip.getTripId());
                                }
                            }

                            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                            LocalDate today = LocalDate.now();
                            tripsList = tripsList.stream()
                                    .sorted(Comparator.comparing((Trip trip) -> LocalDate.parse(trip.getStartDate(), dateFormatter).isBefore(today))
                                            .thenComparing(trip -> LocalDate.parse(trip.getStartDate(), dateFormatter)))
                                    .collect(Collectors.toList());
                            tripAdapter.setTripsList(tripsList);
                            tripAdapter.notifyDataSetChanged();

                            progressDialog.dismiss(); // 数据加载完成后关闭Dialog
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e("DisplayTripActivity", "Error loading trips: " + databaseError.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void signOut() {
        mAuth.signOut();
        // Clear the SharedPreferences data
        SharedPreferences.Editor editor = getSharedPreferences("login", MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(DisplayTripActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // Do nothing, effectively disabling the back button.
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }


}
