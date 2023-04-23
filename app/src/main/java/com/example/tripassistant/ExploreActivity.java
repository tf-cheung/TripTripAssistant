package com.example.tripassistant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.example.tripassistant.models.StopPoint;
import com.example.tripassistant.models.Trip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExploreActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    String myuid;
    RecyclerView recyclerView;
    List<StopPoint> posts;
    AdapterPosts adapterPosts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        firebaseAuth = FirebaseAuth.getInstance();
        recyclerView = findViewById(R.id.postrecyclerview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        posts = new ArrayList<>();
        loadPosts();
    }




    private void loadPosts() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("trips");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            posts.clear();

            for (DataSnapshot tripSnapshot : dataSnapshot.getChildren()) {
                DataSnapshot stopPointsSnapshot = tripSnapshot.child("stopPoints");
                for (DataSnapshot dataSnapshot1 : stopPointsSnapshot.getChildren()) {
                    String spId = dataSnapshot1.getKey();
                    String spName = dataSnapshot1.child("name").getValue(String.class);
                    String spAddress = dataSnapshot1.child("address").getValue(String.class);
                    String spDate = dataSnapshot1.child("date").getValue(String.class);
                    String sptime = dataSnapshot1.child("timeRange").getValue(String.class);
                    double splatitude = dataSnapshot1.child("latitude").getValue(double.class);
                    double splongitude = dataSnapshot1.child("longitude").getValue(double.class);

                    GenericTypeIndicator<List<String>> genericTypeIndicator = new GenericTypeIndicator<List<String>>() {
                    };
                    List<String> members = dataSnapshot1.child("members").getValue(genericTypeIndicator);
//                    Trip trip = tripSnapshot.getValue(Trip.class);

                    StopPoint stopPoint = new StopPoint(spId, spName, spAddress, spDate, sptime, splatitude, splongitude);
                    if (stopPoint != null) {
                        posts.add(stopPoint);
                    }
                    adapterPosts = new AdapterPosts(getApplicationContext(), posts);
                    recyclerView.setAdapter(adapterPosts);
                }
            }
        }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}

