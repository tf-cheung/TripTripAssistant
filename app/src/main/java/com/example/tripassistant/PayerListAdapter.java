package com.example.tripassistant;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripassistant.models.ChecklistOption;

import java.util.List;

public class PayerListAdapter extends RecyclerView.Adapter<PayerListAdapter.ViewHolder> {
    private final AppCompatActivity activity;

    private final List<ChecklistOption> optionList;
    private int selectedItem;

    public PayerListAdapter(AppCompatActivity activity, List<ChecklistOption> optionList, int selectedItem) {
        this.activity = activity;
        this.optionList = optionList;
        this.selectedItem = selectedItem;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.checklist_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final int pos = position;
        holder.labelTV.setText(optionList.get(pos).getLabel());
        holder.checkBox.setChecked(selectedItem == pos);
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedItem = pos;
                notifyDataSetChanged();
            } else if (selectedItem == pos) {
                // uncheck the selected item if the user clicks it again
                selectedItem = -1;
                notifyDataSetChanged();
            }
            activity.onBackPressed();
        });
    }

    @Override
    public int getItemCount() {
        return optionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView labelTV;
        public CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            labelTV = itemView.findViewById(R.id.checklist_label);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }

    public int getSelectedItem() {
        return selectedItem;
    }
}

