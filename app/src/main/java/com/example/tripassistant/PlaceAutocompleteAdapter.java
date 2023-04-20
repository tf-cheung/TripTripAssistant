package com.example.tripassistant;
import android.content.Context;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.List;

public class PlaceAutocompleteAdapter extends ArrayAdapter<AutocompletePrediction> implements Filterable {
    private static final CharacterStyle STYLE_BOLD = new ForegroundColorSpan(-16777216);
    private final PlacesClient placesClient;
    private final AutocompleteSessionToken sessionToken;
    private List<AutocompletePrediction> resultList = new ArrayList();

    public PlaceAutocompleteAdapter(Context context, PlacesClient placesClient) {
        super(context, android.R.layout.simple_expandable_list_item_2, android.R.id.text1);
        this.placesClient = placesClient;
        this.sessionToken = AutocompleteSessionToken.newInstance();
    }

    public int getCount() {
        return this.resultList.size();
    }

    public AutocompletePrediction getItem(int position) {
        return (AutocompletePrediction) this.resultList.get(position);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_expandable_list_item_2, parent, false);
        }

        AutocompletePrediction item = getItem(position);
        if (item != null) {
            TextView textView1 = convertView.findViewById(android.R.id.text1);
            TextView textView2 = convertView.findViewById(android.R.id.text2);
            textView1.setText(item.getPrimaryText(STYLE_BOLD));
            textView2.setText(item.getSecondaryText(STYLE_BOLD));
        }

        return convertView;
    }

    @NonNull
    public Filter getFilter() {
        return new Filter() {
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint != null) {
                    findPredictions(constraint.toString());
                    results.values = resultList;
                    results.count = resultList.size();
                }
                return results;
            }

            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
    }

    private void findPredictions(String query) {
        FindAutocompletePredictionsRequest.Builder requestBuilder = FindAutocompletePredictionsRequest.builder()
                .setQuery(query)
                .setSessionToken(sessionToken);

        placesClient.findAutocompletePredictions(requestBuilder.build()).addOnSuccessListener(response -> {
            resultList.clear();
            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                resultList.add(prediction);
            }
            notifyDataSetChanged();
        }).addOnFailureListener(exception -> {
            exception.printStackTrace();
        });
    }
}
