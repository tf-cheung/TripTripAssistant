package com.example.tripassistant;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripassistant.models.Trip;

import java.util.List;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {

    private List<Trip> trips;

    public TripAdapter(List<Trip> trips) {
        this.trips = trips;
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
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }
    public void setTripsList(List<Trip> newTripsList) {
        this.trips = newTripsList;
    }
}
