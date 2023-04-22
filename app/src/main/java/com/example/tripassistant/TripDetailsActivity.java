package com.example.tripassistant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tripassistant.models.User;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class TripDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {
    String tripId,tripName,startDate;
    private GoogleMap mMap;
    private MapView mapView;
    private PlacesClient placesClient;
    private AutocompleteSupportFragment autocompleteSupportFragment;
    private DatabaseReference mDatabaseReference;
    private String selectedAddress;
    private RecyclerView stopPointsRecyclerView;
    private TextView tripNameTextView;
    private TextView startDateTextView;
    private ImageButton membersBtn,expenseBtn;

    private StopPointAdapter stopPointAdapter;
    private List<HashMap<String, Object>> stopPointsList;

    private RecyclerView membersRecyclerView;
    private MemberAdapter memberAdapter;
    private List<User> userList;
    private DatabaseReference usersReference;

    private Dialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);


        progressDialog = new Dialog(this);
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setCancelable(false);
        progressDialog.show(); // 显示Dialog

        tripId = getIntent().getStringExtra("tripId");
        tripName = getIntent().getStringExtra("tripName");
        startDate = getIntent().getStringExtra("startDate");

        tripNameTextView = findViewById(R.id.trip_name_text_view);
        startDateTextView = findViewById(R.id.start_date_text_view);
        stopPointsRecyclerView = findViewById(R.id.stop_points_recycler_view);
        ImageButton addStopPointButton = findViewById(R.id.add_stop_point_button);
        membersBtn = findViewById(R.id.members_button);
        expenseBtn = findViewById(R.id.expense_button);
        final DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);

        membersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.END);
            }
        });

        expenseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TripDetailsActivity.this, ExpenseActivity.class);
                intent.putExtra("tripId",tripId);
                startActivity(intent);
            }
        });


        tripNameTextView.setText(tripName);
        startDateTextView.setText(startDate);

        addStopPointButton.setOnClickListener(v -> {
            showAddStopPointDialog();
        });
        String apiKey = getString(R.string.google_maps_key);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }
        placesClient = Places.createClient(this);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        usersReference = database.getReference("users");
        membersRecyclerView = findViewById(R.id.members_recycler_view);
        membersRecyclerView.setHasFixedSize(true);
        membersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        userList = new ArrayList<>();
        memberAdapter = new MemberAdapter(this, userList);
        membersRecyclerView.setAdapter(memberAdapter);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("trips");

        stopPointsList = new ArrayList<>();
        stopPointAdapter = new StopPointAdapter(this, stopPointsList,tripId);
        stopPointsRecyclerView.setAdapter(stopPointAdapter);
        stopPointsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadStopPoints();
        loadMembers();


    }
        @SuppressLint("ClickableViewAccessibility")
    private void showAddStopPointDialog() {
        selectedAddress = "";


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.add_stop_point_dialog, null);

        EditText stopPointNameInput = view.findViewById(R.id.stop_point_name_input);
        EditText stopPointDateInput = view.findViewById(R.id.stop_point_date_input);
        EditText stopPointTimeRangeInput = view.findViewById(R.id.stop_point_time_range_input);
        mapView = view.findViewById(R.id.map_view);
        Button addStopPointButton = view.findViewById(R.id.add_stop_point_button);
        stopPointDateInput.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                showDatePickerDialog(stopPointDateInput);
                return true;
            }
            return false;
        });
        stopPointTimeRangeInput.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                showTimePickerDialog(stopPointTimeRangeInput);
                return true;
            }
            return false;
        });


        // Handle the MapView lifecycle events
        mapView.onCreate(null);
        mapView.onResume();
        mapView.getMapAsync(this);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();

        AutoCompleteTextView placeAutocomplete = view.findViewById(R.id.search_address);
        initAutocomplete(placeAutocomplete);

        addStopPointButton.setOnClickListener(v -> {
            String stopPointName = stopPointNameInput.getText().toString().trim();
            String stopPointDate = stopPointDateInput.getText().toString().trim();
            String stopPointTimeRange = stopPointTimeRangeInput.getText().toString().trim();
            LatLng stopPointLatLng = mMap.getCameraPosition().target; // 获取当前地图中心的经纬度

            // 创建一个 HashMap 来存储停靠点信息
            HashMap<String, Object> stopPoint = new HashMap<>();
            stopPoint.put("name", stopPointName);
            stopPoint.put("date", stopPointDate);
            stopPoint.put("timeRange", stopPointTimeRange);
            stopPoint.put("address",selectedAddress);
            stopPoint.put("latitude", stopPointLatLng.latitude);
            stopPoint.put("longitude", stopPointLatLng.longitude);

            // 将停靠点信息添加到 Firebase 数据库
            mDatabaseReference.child(tripId).child("stopPoints").push().setValue(stopPoint)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(TripDetailsActivity.this, "Stop point added successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(TripDetailsActivity.this, "Failed to add stop point", Toast.LENGTH_SHORT).show();
                        }
                    });

            dialog.dismiss(); // 关闭对话框
        });

    }

    private void showDatePickerDialog(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            selectedMonth = selectedMonth + 1; // Months are indexed from 0
            String dateString = String.format("%02d/%02d/%d", selectedDay, selectedMonth, selectedYear);
            editText.setText(dateString);
        }, year, month, day);

        datePickerDialog.show();
    }
    private void showTimePickerDialog(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, selectedHour, selectedMinute) -> {
            String timeString = String.format("%02d:%02d", selectedHour, selectedMinute);
            editText.setText(timeString);
        }, hour, minute, true);

        timePickerDialog.show();
    }

    private void loadMembers() {
        mDatabaseReference.child(tripId).child("members").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot memberIdSnapshot : dataSnapshot.getChildren()) {
                    String memberId = memberIdSnapshot.getValue(String.class);
                    Log.d("members loading",memberId);
                    usersReference.child(memberId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            if (user != null) {
                                userList.add(user);
                                memberAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadStopPoints() {mDatabaseReference.child(tripId).child("stopPoints").addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            stopPointsList.clear();
            for (DataSnapshot stopPointSnapshot : dataSnapshot.getChildren()) {
                String stopPointId = stopPointSnapshot.getKey();
                HashMap<String, Object> stopPoint = (HashMap<String, Object>) stopPointSnapshot.getValue();
                stopPoint.put("stopPointId",stopPointId);
                stopPointsList.add(stopPoint);
            }
            stopPointAdapter.notifyDataSetChanged();
            progressDialog.dismiss(); // 数据加载完成后关闭Dialog

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Toast.makeText(TripDetailsActivity.this, "Failed to load stop points", Toast.LENGTH_SHORT).show();
        }
    });
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker and move the camera
        LatLng nowhere = new LatLng(0.1, 0.1);
        mMap.addMarker(new MarkerOptions().position(nowhere).title(""));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(nowhere, 10));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.onDestroy();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) {
            mapView.onLowMemory();
        }
    }

    private void initAutocomplete(AutoCompleteTextView placeAutocomplete) {
        PlaceAutocompleteAdapter placeAutocompleteAdapter = new PlaceAutocompleteAdapter(this, placesClient);
        placeAutocomplete.setAdapter(placeAutocompleteAdapter);
        placeAutocomplete.setOnItemClickListener((parent, view, position, id) -> {
            AutocompletePrediction item = placeAutocompleteAdapter.getItem(position);
            if (item != null) {
                selectedAddress = item.getPrimaryText(null) + ", " + item.getSecondaryText(null);
                placeAutocomplete.setText(selectedAddress);
                String placeId = item.getPlaceId();
                if (placeId != null) {
                    List<Place.Field> placeFields = Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.ID, com.google.android.libraries.places.api.model.Place.Field.NAME, com.google.android.libraries.places.api.model.Place.Field.LAT_LNG);
                    FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

                    placesClient.fetchPlace(request).addOnSuccessListener(response -> {
                        com.google.android.libraries.places.api.model.Place place = response.getPlace();
                        LatLng latLng = place.getLatLng();
                        if (latLng != null) {
                            mMap.clear();
                            mMap.addMarker(new MarkerOptions().position(latLng).title(place.getName()));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                        }
                    }).addOnFailureListener(exception -> {
                        if (exception instanceof ApiException) {
                            ApiException apiException = (ApiException) exception;
                            Log.e("TripDetailsActivity", "Place not found: " + apiException.getStatusCode());
                        }
                    });
                }
            }
        });

    }



}
