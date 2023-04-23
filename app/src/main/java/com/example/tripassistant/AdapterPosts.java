package com.example.tripassistant;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tripassistant.models.StopPoint;
import com.example.tripassistant.models.Trip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterPosts extends RecyclerView.Adapter<AdapterPosts.MyHolder> {

    Context context;
    String myuid;
    List<StopPoint> modelStopPoints;

    private DatabaseReference liekeref, tripref;
    boolean mprocesslike = false;

    public AdapterPosts(Context context, List<StopPoint> modelTrips) {
        this.context = context;
        this.modelStopPoints = modelTrips;
        myuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        liekeref = FirebaseDatabase.getInstance().getReference().child("Likes");
        tripref = FirebaseDatabase.getInstance().getReference().child("trips");
    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.explore_card, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, @SuppressLint("RecyclerView") final int position) {
        final String uid = modelStopPoints.get(position).getId();
        final String title = modelStopPoints.get(position).getName();
        final String startDate = modelStopPoints.get(position).getDate();
        final String address = modelStopPoints.get(position).getAddress();
        final String plike = modelStopPoints.get(position).getPlike();

        holder.title.setText(title);
        holder.startDate.setText(startDate);
        holder.address.setText(address);


        holder.like.setText(plike + " Likes");
        setLikes(holder);

//        holder.like.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(holder.itemView.getContext(), PostLikedByActivity.class);
//                intent.putExtra("pid", pid);
//                holder.itemView.getContext().startActivity(intent);
//            }
//        });
        holder.likebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int plike = Integer.parseInt(modelStopPoints.get(position).getPlike());
                mprocesslike = true;
                final String spId = modelStopPoints.get(position).getId();
                liekeref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (mprocesslike) {
                            if (dataSnapshot.child(spId).hasChild(myuid)) {
                                tripref.child(spId).child("plike").setValue("" + (plike - 1));
                                liekeref.child(spId).child(myuid).removeValue();
                                mprocesslike = false;
                            } else {
                                tripref.child(spId).child("plike").setValue("" + (plike + 1));
                                liekeref.child(spId).child(myuid).setValue("Liked");
                                mprocesslike = false;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TripDetailsActivity.class);
            }
        });

    }


    private void setLikes(final MyHolder holder) {
        final String spId = modelStopPoints.get(holder.getAdapterPosition()).getId();
        liekeref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(spId).hasChild(myuid)) {
                    holder.likebtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_liked, 0, 0, 0);
                    holder.likebtn.setText("Liked");
                } else {
                    holder.likebtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like, 0, 0, 0);
                    holder.likebtn.setText("Like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public int getItemCount() {
        return modelStopPoints.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        ImageView picture, image;
        TextView name, startDate,address, time, title, description, like, comments;
        ImageButton more;
        Button likebtn, comment;
        LinearLayout profile;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            startDate = itemView.findViewById(R.id.start_date);
            title = itemView.findViewById(R.id.trip_name_text_view);
            address = itemView.findViewById(R.id.address);
            like = itemView.findViewById(R.id.like_count_text_view);

//            picture = itemView.findViewById(R.id.picturetv);
//            image = itemView.findViewById(R.id.pimagetv);
//            name = itemView.findViewById(R.id.unametv);
//            time = itemView.findViewById(R.id.utimetv);
//            more = itemView.findViewById(R.id.morebtn);
//            title = itemView.findViewById(R.id.ptitletv);
//            description = itemView.findViewById(R.id.descript);
//            like = itemView.findViewById(R.id.plikeb);
//            comments = itemView.findViewById(R.id.pcommentco);
//            likebtn = itemView.findViewById(R.id.like);
//            comment = itemView.findViewById(R.id.comment);
//            profile = itemView.findViewById(R.id.profilelayout);
        }
    }
}
