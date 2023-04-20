package com.example.tripassistant;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;



public class TripDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {
    String tripId;
    private GoogleMap mMap;
    private MapView mapView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);

        tripId = getIntent().getStringExtra("tripId");
        ImageButton addStopPointButton = findViewById(R.id.add_stop_point_button);
        addStopPointButton.setOnClickListener(v -> {
            showAddStopPointDialog();
        });


    }

    private void showAddStopPointDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.add_stop_point_dialog, null);

        EditText stopPointNameInput = view.findViewById(R.id.stop_point_name_input);
        EditText stopPointDateInput = view.findViewById(R.id.stop_point_date_input);
        EditText stopPointTimeRangeInput = view.findViewById(R.id.stop_point_time_range_input);
        mapView = view.findViewById(R.id.map_view);
        Button addStopPointButton = view.findViewById(R.id.add_stop_point_button);

        // Handle the MapView lifecycle events
        mapView.onCreate(null);
        mapView.onResume();
        mapView.getMapAsync(this);

        addStopPointButton.setOnClickListener(v -> {
            // Handle adding the stop point logic here
        });

        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 10));
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
}
