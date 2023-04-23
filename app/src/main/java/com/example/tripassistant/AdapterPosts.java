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

import com.example.tripassistant.models.Trip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterPosts extends RecyclerView.Adapter<AdapterPosts.MyHolder> {

    Context context;
    String myuid;
    private DatabaseReference liekeref, tripref;
    boolean mprocesslike = false;

    public AdapterPosts(Context context, List<Trip> modelTrips) {
        this.context = context;
        this.modelTrips = modelTrips;
        myuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        liekeref = FirebaseDatabase.getInstance().getReference().child("Likes");
        tripref = FirebaseDatabase.getInstance().getReference().child("trips");
    }

    List<Trip> modelTrips;

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_posts, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, @SuppressLint("RecyclerView") final int position) {
        final String uid = modelTrips.get(position).getTripId();
        final String titlee = modelTrips.get(position).getTripName();
        final String startDate = modelTrips.get(position).getStartDate();
        final List<String> members = modelTrips.get(position).getMembers();
        
//        String nameh = modelPosts.get(position).getUname();
//        final String ptime = modelPosts.get(position).getPtime();
//        String plike = modelPosts.get(position).getPlike();
//        String comm = modelPosts.get(position).getPcomments();

        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
//        calendar.setTimeInMillis(Long.parseLong(ptime));
        String timedate = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();
//        holder.name.setText(nameh);
        holder.title.setText(titlee);
        holder.startDate.setText(startDate);

//        holder.description.setText(descri);
//        holder.time.setText(timedate);
//        holder.like.setText(plike + " Likes");
//        holder.comments.setText(comm + " Comments");
//        setLikes(holder, ptime);
//        try {
//            Glide.with(context).load(dp).into(holder.picture);
//        } catch (Exception e) {
//
//        }
//        holder.image.setVisibility(View.VISIBLE);
//        try {
//            Glide.with(context).load(image).into(holder.image);
//        } catch (Exception e) {
//
//        }
//        holder.like.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(holder.itemView.getContext(), PostLikedByActivity.class);
//                intent.putExtra("pid", pid);
//                holder.itemView.getContext().startActivity(intent);
//            }
//        });
//        holder.likebtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final int plike = Integer.parseInt(modelTrips.get(position).getPlike());
//                mprocesslike = true;
//                final String tripId = modelTrips.get(position).getTripId();
//                liekeref.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        if (mprocesslike) {
//                            if (dataSnapshot.child(tripId).hasChild(myuid)) {
//                                tripref.child(tripId).child("plike").setValue("" + (plike - 1));
//                                liekeref.child(tripId).child(myuid).removeValue();
//                                mprocesslike = false;
//                            } else {
//                                tripref.child(tripId).child("plike").setValue("" + (plike + 1));
//                                liekeref.child(tripId).child(myuid).setValue("Liked");
//                                mprocesslike = false;
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//            }
//        });
        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TripDetailsActivity.class);
            }
        });
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TripDetailsActivity.class);
//                intent.putExtra("pid", ptime);
                context.startActivity(intent);
            }
        });
    }


//    private void setLikes(final MyHolder holder, final String pid) {
//        liekeref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////                if (dataSnapshot.child(pid).hasChild(myuid)) {
////                    holder.likebtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_liked, 0, 0, 0);
////                    holder.likebtn.setText("Liked");
////                } else {
////                    holder.likebtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like, 0, 0, 0);
////                    holder.likebtn.setText("Like");
////                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

    @Override
    public int getItemCount() {
        return modelTrips.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        ImageView picture, image;
        TextView name, startDate, time, title, description, like, comments;
        ImageButton more;
        Button likebtn, comment;
        LinearLayout profile;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            startDate = itemView.findViewById(R.id.startdatetv);
            picture = itemView.findViewById(R.id.picturetv);
            image = itemView.findViewById(R.id.pimagetv);
            name = itemView.findViewById(R.id.unametv);
            time = itemView.findViewById(R.id.utimetv);
            more = itemView.findViewById(R.id.morebtn);
            title = itemView.findViewById(R.id.ptitletv);
//            description = itemView.findViewById(R.id.descript);
//            like = itemView.findViewById(R.id.plikeb);
//            comments = itemView.findViewById(R.id.pcommentco);
//            likebtn = itemView.findViewById(R.id.like);
//            comment = itemView.findViewById(R.id.comment);
//            profile = itemView.findViewById(R.id.profilelayout);
        }
    }
}
