package com.example.tripassistant;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tripassistant.models.Expense;
import com.example.tripassistant.models.Transaction;
import com.example.tripassistant.models.Trip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpenseActivity extends AppCompatActivity {

    private final ArrayList<String> tripMembers = new ArrayList<>();
    private DatabaseReference tripsRef;
    private String tripId, tripName;
    private final List<Expense> expenseList = new ArrayList<>();
    private final List<Transaction> transactions = new ArrayList<>();
    private Trip trip;
    private ExpenseAdapter expenseAdapter;
    private TransactionAdapter transactionAdapter;

    private ProgressBar progressBar;
    private Button historyBtn, balancesBtn;
    private TextView instructionTV;
    private RecyclerView expenseRV, balancesRV;
    private int selectedTab, green, white;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        instructionTV = findViewById(R.id.instruction_text_view);
        historyBtn = findViewById(R.id.history_button);
        balancesBtn = findViewById(R.id.balances_button);
        ImageButton addExpenseFAB = findViewById(R.id.addExpenseFAB);

        expenseRV = findViewById(R.id.expense_recycler_view);
        balancesRV = findViewById(R.id.balances_recycler_view);
        progressBar = findViewById(R.id.progress_bar);
        TextView tripNameTV = findViewById(R.id.trip_name_text_view);

        green = ContextCompat.getColor(this, R.color.green);
        white = ContextCompat.getColor(this, R.color.white);

        progressBar.setVisibility(View.VISIBLE);

        Intent intent = getIntent();
        tripId = intent.getStringExtra("tripId");
        String username = intent.getStringExtra("username");

        tripsRef = FirebaseDatabase.getInstance().getReference("trips");
        DatabaseReference tripRef = tripsRef.child(tripId);
        tripRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                trip = dataSnapshot.getValue(Trip.class);
                if (trip == null) {
                    Toast.makeText(ExpenseActivity.this, "Trip doesn't exist!", Toast.LENGTH_SHORT).show();
                    return;
                }
                tripName = trip.getTripName();
                transactionAdapter.setTripName(tripName);
                tripMembers.clear();
                tripMembers.addAll(trip.getMembers());
                tripNameTV.setText(trip.getTripName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });

        addExpenseFAB.setOnClickListener(view -> {
            Intent newIntent = new Intent(ExpenseActivity.this, AddExpenseActivity.class);
            newIntent.putExtra("tripId", tripId);
            newIntent.putExtra("username", username);
            newIntent.putStringArrayListExtra("tripMembers", tripMembers);
            startActivity(newIntent);
        });

        expenseRV.setLayoutManager(new LinearLayoutManager(this));
        expenseAdapter = new ExpenseAdapter(this, expenseList, username);
        expenseRV.setAdapter(expenseAdapter);

        balancesRV.setLayoutManager(new LinearLayoutManager(this));
        transactionAdapter = new TransactionAdapter(this, transactions, tripId, tripName);
        balancesRV.setAdapter(transactionAdapter);

        historyBtn.setOnClickListener(view -> {
            if (selectedTab == 1) {
                selectHistory();
                selectedTab = 0;
            }
        });

        balancesBtn.setOnClickListener(view -> {
            if (selectedTab == 0) {
                selectBalances();
                selectedTab = 1;
            }
        });

        loadExpenses();
    }

    private void loadExpenses() {
        tripsRef.child(tripId).child("expenses").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                expenseList.clear();
                expenseAdapter.notifyDataSetChanged();
                for (DataSnapshot expenseSnapshot : snapshot.getChildren()) {
                    Expense expense = expenseSnapshot.getValue(Expense.class);
                    if (expense != null) {
                        expenseList.add(expense);
                    }
                }
                Collections.reverse(expenseList);
                expenseAdapter.notifyItemRangeInserted(0, expenseList.size());
                transactions.clear();
                transactions.addAll(calculateTransactions());
                transactionAdapter.notifyItemRangeInserted(0, transactions.size());
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Expense", "Failed to read expenses", databaseError.toException());
            }
        });
    }

    private void selectHistory() {
        balancesBtn.setBackgroundColor(white);
        balancesBtn.setTextColor(green);
        historyBtn.setBackgroundColor(green);
        historyBtn.setTextColor(white);
        instructionTV.setText(getResources().getString(R.string.take_a_look_at_past_expenses_in_your_trip));
        expenseRV.setVisibility(View.VISIBLE);
        balancesRV.setVisibility(View.GONE);
    }

    private void selectBalances() {
        historyBtn.setBackgroundColor(white);
        historyBtn.setTextColor(green);
        balancesBtn.setBackgroundColor(green);
        balancesBtn.setTextColor(white);
        instructionTV.setText(getResources().getString(R.string.balance_instruction));
        expenseRV.setVisibility(View.GONE);
        balancesRV.setVisibility(View.VISIBLE);
    }

    private Map<String, Float> calculateBalances() {
        Map<String, Float> balances = new HashMap<>();
        for (Expense expense : expenseList) {
            String payer = expense.getPayer();
            balances.put(payer, balances.getOrDefault(payer, 0f) - expense.getAmount());
            for (Map.Entry<String, Float> entry : expense.getPayees().entrySet()) {
                String payee = entry.getKey();
                balances.put(payee, balances.getOrDefault(payee, 0f) + entry.getValue());
            }
        }
        return balances;
    }

    public List<Transaction> calculateTransactions() {
        Map<String, Float> balances = calculateBalances();

        List<String> positiveBalances = new ArrayList<>();
        List<String> negativeBalances = new ArrayList<>();
        for (String person : balances.keySet()) {
            float balance = balances.get(person);
            if (balance > 0) {
                positiveBalances.add(person);
            } else if (balance < 0) {
                negativeBalances.add(person);
            }
        }

        List<Transaction> transactions = new ArrayList<>();
        List<String> tempPositive = new ArrayList<>(positiveBalances);
        List<String> tempNegative = new ArrayList<>(negativeBalances);
        for (String positivePerson : tempPositive) {
            for (String negativePerson : tempNegative) {
                float amount = Math.min(balances.get(positivePerson), -balances.get(negativePerson));
                if (amount > 0.001) {
                    transactions.add(new Transaction(negativePerson, positivePerson, amount));
                    balances.put(positivePerson, balances.get(positivePerson) - amount);
                    balances.put(negativePerson, balances.get(negativePerson) + amount);
                    if (balances.get(positivePerson) == 0) {
                        positiveBalances.remove(positivePerson);
                    }
                    if (balances.get(negativePerson) == 0) {
                        negativeBalances.remove(negativePerson);
                    }
                }
            }
        }

        return transactions;
    }
}