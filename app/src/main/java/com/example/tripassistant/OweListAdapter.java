package com.example.tripassistant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripassistant.models.SharelistOption;

import java.util.List;

public class OweListAdapter extends RecyclerView.Adapter<OweListAdapter.ViewHolder> {

    private final Context context;
    private final List<SharelistOption> list;
    private final String type;

    public OweListAdapter(Context context, List<SharelistOption> list, String type) {
        this.context = context;
        this.list = list;
        this.type = type;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.owe_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemTV.setText(context.getString(R.string.someone_owes, list.get(position).getLabel(), type, list.get(position).getAmount()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView itemTV;

        public ViewHolder(View itemView) {
            super(itemView);
            itemTV = itemView.findViewById(R.id.text_view);
        }
    }
}

