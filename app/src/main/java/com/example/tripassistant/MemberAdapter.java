package com.example.tripassistant;import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripassistant.models.Member;
import com.example.tripassistant.models.User;

import java.util.List;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MemberViewHolder> {

    private List<User> memberList;
    private Context context;

    public MemberAdapter(Context context, List<User> memberList) {
        this.context = context;
        this.memberList = memberList;
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_member, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        User member = memberList.get(position);
        holder.memberUsernameTextView.setText(member.getUsername());
        holder.memberEmailTextView.setText(member.getEmail());
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }

    public static class MemberViewHolder extends RecyclerView.ViewHolder {

        TextView memberUsernameTextView;
        TextView memberEmailTextView;

        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            memberUsernameTextView = itemView.findViewById(R.id.member_username_text_view);
            memberEmailTextView = itemView.findViewById(R.id.member_email_text_view);
        }
    }
}

