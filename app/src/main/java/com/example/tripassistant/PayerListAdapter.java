package com.example.tripassistant;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.tripassistant.models.ChecklistOption;

import java.util.List;

public class PayerListAdapter extends RecyclerView.Adapter<PayerListAdapter.ViewHolder> {

    private final List<ChecklistOption> optionList;
    private int selectedItem;

    public PayerListAdapter(List<ChecklistOption> optionList, int selectedItem) {
        this.optionList = optionList;
        this.selectedItem = selectedItem;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.checklist_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.labelTV.setText(optionList.get(position).getLabel());
        holder.checkBox.setChecked(selectedItem == position);
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    selectedItem = position;
                    notifyDataSetChanged();
                } else if (selectedItem == position) {
                    // uncheck the selected item if the user clicks it again
                    selectedItem = -1;
                    notifyDataSetChanged();
                }
            }
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

