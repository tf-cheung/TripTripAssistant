package com.example.tripassistant;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripassistant.models.StopPoint;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicInteger;

public class AdapterPosts extends RecyclerView.Adapter<AdapterPosts.MyHolder> {

    Context context;
    String myuid;
    List<StopPoint> modelStopPoints;

    private DatabaseReference tripsRef,likeRef, spRef,userRef;
    boolean mProcessLike = false;
    int likedInt;

    public AdapterPosts(Context context, List<StopPoint> modelTrips) {
        this.context = context;
        this.modelStopPoints = modelTrips;
        myuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        tripsRef = FirebaseDatabase.getInstance().getReference("trips"); // Initialize likeRef here
        userRef = FirebaseDatabase.getInstance().getReference("users"); // Initialize likeRef here

    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.explore_card, parent, false);
        return new MyHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, @SuppressLint("RecyclerView") final int position) {
        final String spId = modelStopPoints.get(position).getSpId();
        final String tripId = modelStopPoints.get(position).getTripId();
        final String title = modelStopPoints.get(position).getName();
        final String startDate = modelStopPoints.get(position).getDate();
        final String address = modelStopPoints.get(position).getAddress();
        final String liked = modelStopPoints.get(position).getLiked();

        holder.title.setText(title);
        holder.startDate.setText(startDate);
        holder.address.setText(address);

        holder.like.setText(liked + " Likes");
        updateLikeButtonState(holder,tripId,spId);
        updateLikedByState(holder, tripId, spId) ;

        holder.likebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProcessLike = true;
                spRef = FirebaseDatabase.getInstance().getReference("trips").child(tripId).child("stopPoints").child(spId);
                likeRef = FirebaseDatabase.getInstance().getReference("trips").child(tripId).child("stopPoints").child(spId).child("likedBy");
                Log.d("AdapterPosts", "Trip ID: " + tripId);
                Log.d("AdapterPosts", "Stop Point ID: " + spId);
                likeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (mProcessLike) {
                            if (dataSnapshot.hasChild(myuid)) {
                                updateLikeCount(holder,tripId,spId,false);
                                likeRef.child(myuid).removeValue();
                                mProcessLike = false;
                            } else {
//                                spRef.child("liked").setValue("" + (likedValue+1));
                                updateLikeCount(holder,tripId,spId,true);
                                likeRef.child(myuid).setValue(true);
                                mProcessLike = false;
                            }
                            updateLikedByState(holder,tripId,spId);
                            setLikes(holder);

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

    }

    private void setLikes(final MyHolder holder) {
        likeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(myuid)) {
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
        TextView name, startDate, address, time, title, description, like, comments,likedBy,likedByText;
        Button likebtn, comment;
        LinearLayout profile;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            startDate = itemView.findViewById(R.id.start_date);
            title = itemView.findViewById(R.id.trip_name_text_view);
            address = itemView.findViewById(R.id.address);
            like = itemView.findViewById(R.id.like_count_text_view);
            likebtn = itemView.findViewById(R.id.like_button);
            likedBy = itemView.findViewById(R.id.likedByUsername_text_view);
            likedByText = itemView.findViewById(R.id.likedBy_text_view);


            likebtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like, 0, 0, 0); // Set the initial image for the like button

            // Remove or uncomment the following lines if you have implemented these views in your layout.
        /*
        picture = itemView.findViewById(R.id.picturetv);
        image = itemView.findViewById(R.id.pimagetv);
        name = itemView.findViewById(R.id.unametv);
        time = itemView.findViewById(R.id.utimetv);
        more = itemView.findViewById(R.id.morebtn);
        description = itemView.findViewById(R.id.descript);
        comments = itemView.findViewById(R.id.pcommentco);
        comment = itemView.findViewById(R.id.comment);
        profile = itemView.findViewById(R.id.profilelayout);
        */
        }
    }

    private void updateLikeButtonState(final MyHolder holder, String tripId, String spId) {
        tripsRef.child(tripId).child("stopPoints").child(spId).child("likedBy").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(myuid)) {
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


    private void updateLikeCount(final MyHolder holder, String tripId, String spId, boolean like) {
        tripsRef.child(tripId).child("stopPoints").child(spId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (like) {
                    int likedValue = Integer.parseInt(dataSnapshot.child("liked").getValue(String.class)) + 1;
                    tripsRef.child(tripId).child("stopPoints").child(spId).child("liked").setValue(String.valueOf(likedValue));
                    Log.d("likedValue", likedValue + "");
                    holder.like.setText(likedValue+" Likes");

                } else {
                    int likedValue = Integer.parseInt(dataSnapshot.child("liked").getValue(String.class)) - 1;
                    tripsRef.child(tripId).child("stopPoints").child(spId).child("liked").setValue(String.valueOf(likedValue));
                    Log.d("likedValue", likedValue + "");
                    holder.like.setText(likedValue+" Likes");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateLikedByState(final MyHolder holder, String tripId, String spId) {
        tripsRef.child(tripId).child("stopPoints").child(spId).child("likedBy").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                StringBuilder likedByUsernames = new StringBuilder();
                int likedByCount = (int) dataSnapshot.getChildrenCount();
                AtomicInteger currentCount = new AtomicInteger(0);

                if (likedByCount > 0) {
                    holder.likedByText.setText("Liked By ");
                } else {
                    holder.likedByText.setText("");
                    holder.likedBy.setText("");
                }

                for (DataSnapshot likedBySnapshot : dataSnapshot.getChildren()) {
                    String likedById = likedBySnapshot.getKey();
                    userRef.child(likedById).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String username = dataSnapshot.child("username").getValue(String.class);
                            likedByUsernames.append(username);
                            currentCount.incrementAndGet();
                            if (currentCount.get() < likedByCount) {
                                likedByUsernames.append(", ");
                            } else {
                                holder.likedBy.setText(likedByUsernames.toString());
                            }
                            Log.d("likedBy", username);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


}
