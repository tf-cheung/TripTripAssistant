package com.example.tripassistant;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripassistant.models.Trip;

import org.checkerframework.checker.units.qual.C;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {

    private List<Trip> trips;
    private String username;

    public TripAdapter(List<Trip> trips, String username) {
        this.trips = trips;
        this.username = username;
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.trip_item, parent, false);
        return new TripViewHolder(itemView, trips);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        Trip trip = trips.get(position);
        holder.tripNameTextView.setText(trip.getTripName());
        holder.starDateTextView.setText(trip.getStartDate());

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate tripDate = LocalDate.parse(trip.getStartDate(), dateFormatter);
        LocalDate today = LocalDate.now();

        if (tripDate.isBefore(today)) {
            holder.tripNameTextView.setTextColor(Color.parseColor("#808080"));
            holder.starDateTextView.setTextColor(Color.parseColor("#808080"));
            holder.itemView.setBackgroundColor(Color.parseColor("#F0F0F0"));
        } else {
            holder.tripNameTextView.setTextColor(Color.parseColor("#5381a5"));
            holder.starDateTextView.setTextColor(Color.parseColor("#5381a5"));
        }
    }


    @Override
    public int getItemCount() {
        return trips.size();
    }

    public class TripViewHolder extends RecyclerView.ViewHolder {
        private TextView tripNameTextView,starDateTextView;
        private List<Trip> trips;

        public TripViewHolder(@NonNull View itemView, List<Trip> trips) {
            super(itemView);
            this.trips = trips;
            tripNameTextView = itemView.findViewById(R.id.trip_name_text_view);
            starDateTextView = itemView.findViewById(R.id.start_date);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Trip trip = trips.get(position);
                    Intent intent = new Intent(itemView.getContext(), TripDetailsActivity.class);
                    intent.putExtra("tripId", trip.getTripId());
                    intent.putExtra("tripName", trip.getTripName());
                    intent.putExtra("startDate", trip.getStartDate());
                    intent.putExtra("username", username);
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }
    public void setTripsList(List<Trip> newTripsList) {
        this.trips = newTripsList;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
