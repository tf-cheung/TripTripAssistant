package com.example.tripassistant;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.tripassistant.models.ChecklistOption;
import com.example.tripassistant.models.SharelistOption;

import java.util.List;

public class ShareListAdapter extends RecyclerView.Adapter<ShareListAdapter.ViewHolder> {

    private final List<SharelistOption> optionList;

    public ShareListAdapter(List<SharelistOption> optionList) {
        this.optionList = optionList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sharelist_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.labelTV.setText(optionList.get(position).getLabel());
        holder.amountEDT.setText(String.valueOf(optionList.get(position).getAmount()));
        holder.amountEDT.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                optionList.get(position).setAmount(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return optionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView labelTV;
        public EditText amountEDT;

        public ViewHolder(View itemView) {
            super(itemView);
            labelTV = itemView.findViewById(R.id.checklist_label);
            amountEDT = itemView.findViewById(R.id.edit_text);
        }
    }
}

