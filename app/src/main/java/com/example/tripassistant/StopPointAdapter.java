package com.example.tripassistant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
public class StopPointAdapter extends RecyclerView.Adapter<StopPointAdapter.ViewHolder> {

    private List<HashMap<String, Object>> stopPoints;
    private Context context;
    private String tripId;

    public StopPointAdapter(Context context, List<HashMap<String, Object>> stopPoints, String tripId) {
        this.context = context;
        this.stopPoints = stopPoints;
        this.tripId = tripId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stop_point_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HashMap<String, Object> stopPoint = stopPoints.get(position);
        holder.stopPointName.setText((String) stopPoint.get("name"));
        holder.stopPointDate.setText((String) stopPoint.get("date"));
        holder.stopPointTimeRange.setText((String) stopPoint.get("timeRange"));

        double latitude = (double) stopPoint.get("latitude");
        double longitude = (double) stopPoint.get("longitude");
        LatLng latLng = new LatLng(latitude, longitude);

        holder.deleteStopPointButton.setOnClickListener(v -> {
            // 在此处添加删除逻辑
            stopPoints.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, stopPoints.size());

            // 从数据库中删除相应的停靠点
            DatabaseReference stopPointRef = FirebaseDatabase.getInstance().getReference("trips")
                    .child(tripId).child("stopPoints").child(stopPoint.get("stopPointId").toString());
            stopPointRef.removeValue();
        });

        // Handle the MapView lifecycle events
        holder.mapView.onCreate(null);
        holder.mapView.getMapAsync(googleMap -> {
            googleMap.addMarker(new MarkerOptions().position(latLng));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        });
        holder.mapView.onResume();
    }

    @Override
    public int getItemCount() {
        return stopPoints.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView stopPointName;
        TextView stopPointDate;
        TextView stopPointTimeRange;
        MapView mapView;
        ImageButton deleteStopPointButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            stopPointName = itemView.findViewById(R.id.stop_point_name);
            stopPointDate = itemView.findViewById(R.id.stop_point_date);
            stopPointTimeRange = itemView.findViewById(R.id.stop_point_time_range);
            mapView = itemView.findViewById(R.id.map_view);
            deleteStopPointButton = itemView.findViewById(R.id.delete_stop_point_button);
        }
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
        if(holder.mapView != null) {
            holder.mapView.onPause();
        }
    }
}


