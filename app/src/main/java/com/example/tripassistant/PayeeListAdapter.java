package com.example.tripassistant;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripassistant.models.ChecklistOption;

import java.util.List;

public class PayeeListAdapter extends RecyclerView.Adapter<PayeeListAdapter.ViewHolder> {

    private final List<ChecklistOption> optionList;

    public PayeeListAdapter(List<ChecklistOption> optionList) {
        this.optionList = optionList;
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
        ChecklistOption option = optionList.get(position);
        holder.labelTV.setText(option.getLabel());
        holder.checkBox.setChecked(option.isChecked());
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> option.setChecked(isChecked));
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
}

