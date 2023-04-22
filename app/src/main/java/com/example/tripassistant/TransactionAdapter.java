package com.example.tripassistant;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripassistant.models.Expense;
import com.example.tripassistant.models.Transaction;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {
    private final DecimalFormat df = new DecimalFormat("#.##");
    private final Context context;
    private final List<Transaction> transactions;
    private final String tripId;
    private String tripName;
    private Transaction transaction;

    public TransactionAdapter(Context context, List<Transaction> transactions, String tripId, String tripName) {
        this.context = context;
        this.transactions = transactions;
        this.tripId = tripId;
        this.tripName = tripName;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transaction_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        transaction = transactions.get(position);
        holder.payerTV.setText(context.getResources().getString(R.string.to_someone, transaction.getPayer()));
        holder.amountTV.setText(context.getResources().getString(R.string.us_money, df.format(transaction.getAmount())));
        holder.payeeTV.setText(context.getResources().getString(R.string.payer_owes, transaction.getPayee()));

        holder.remindBtn.setOnClickListener(view -> sendReminder());

        holder.settleUpBtn.setOnClickListener(view -> settleUp(position));
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView payerTV, amountTV, payeeTV;
        public Button remindBtn, settleUpBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            payerTV = itemView.findViewById(R.id.payer_text_view);
            amountTV = itemView.findViewById(R.id.amount_text_view);
            payeeTV = itemView.findViewById(R.id.payee_text_view);
            remindBtn = itemView.findViewById(R.id.remind_button);
            settleUpBtn = itemView.findViewById(R.id.settle_up_button);
        }
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    private void sendReminder() {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        Query query = usersRef.orderByChild("username").equalTo(transaction.getPayee());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String userEmail = userSnapshot.child("email").getValue(String.class);
                        String subject = "Trip Assistant Reminder";
                        String body = String.format("Hello %s! This is a reminder that you owe %s US$%s for expenses in your Trip Assistant group \"%s\". ", transaction.getPayee(), transaction.getPayer(), df.format(transaction.getAmount()), tripName);

                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{userEmail});
                        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                        intent.putExtra(Intent.EXTRA_TEXT, body);

                        if (intent.resolveActivity(context.getPackageManager()) != null) {
                            context.startActivity(intent);
                        } else {
                            Toast.makeText(context, "No email app found.", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    // User with usernameToFind does not exist
                    Toast.makeText(context, "User doesn't exist!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });
    }

    private void settleUp(int position) {
        Map<String, Float> payees = new HashMap<>();
        payees.put(transaction.getPayer(), transaction.getAmount());
        Expense expense = new Expense(transaction.getPayee(), "Settle up", transaction.getAmount(), payees, new Date());
        DatabaseReference tripRef = FirebaseDatabase.getInstance().getReference().child("trips").child(tripId);
        DatabaseReference expensesRef = tripRef.child("expenses");
        expensesRef.push().setValue(expense)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(context, "Balance settled up successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Failed to settle up balance", Toast.LENGTH_SHORT).show();
                    }
                });

        transactions.remove(position);
        notifyItemRemoved(position);
    }
}

