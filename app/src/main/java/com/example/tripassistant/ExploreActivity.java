package com.example.tripassistant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.example.tripassistant.models.Trip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ExploreActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    String myuid;
    RecyclerView recyclerView;
    List<Trip> posts;
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
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                posts.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Trip trip = dataSnapshot1.getValue(Trip.class);
                    if(trip != null){
                        posts.add(trip);
                    }
                    adapterPosts = new AdapterPosts(getApplicationContext(), posts);
                    recyclerView.setAdapter(adapterPosts);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}

