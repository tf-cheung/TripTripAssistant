package com.example.tripassistant;

import android.content.Context;
import android.content.Intent;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripassistant.models.Expense;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {
    private final DecimalFormat decimalFormat = new DecimalFormat("#.##");
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd");
    private final Context context;

    private final List<Expense> expenses;
    private final String username;

    public ExpenseAdapter(Context context, List<Expense> expenses, String username) {
        this.context = context;
        this.expenses = expenses;
        this.username = username;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.expense_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Expense expense = expenses.get(position);
        holder.dateTV.setText(dateFormat.format(expense.getDate()));
        if (expense.getDescription().equals("Settle up")) {
            holder.descriptionTV.setText(context.getResources().getString(R.string.settle_up_info, expense.getPayer(), expense.getPayees().keySet().iterator().next(), decimalFormat.format(expense.getAmount())));
        } else {
            holder.descriptionTV.setText(trimDescription(expense.getDescription()));
        }
        float amount = 0;
        if (expense.getPayer().equals(username)) amount += expense.getAmount();
        for (Map.Entry<String, Float> entry : expense.getPayees().entrySet()) {
            if (entry.getKey().equals(username)) {
                amount -= entry.getValue();
            }
        }
        String formattedValue = decimalFormat.format(Math.abs(amount));
        if (expense.getDescription().equals("Settle up")) {
            holder.amountTV.setText("");
            holder.descriptionTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        } else if (amount > 0.001) {
            holder.amountTV.setText(context.getResources().getString(R.string.you_lent, formattedValue));
            holder.amountTV.setTextColor(context.getColor(R.color.green));
        } else if (amount < -0.001) {
            holder.amountTV.setText(context.getResources().getString(R.string.you_borrowed, formattedValue));
            holder.amountTV.setTextColor(context.getColor(R.color.tangerine));
        }
        holder.expenseLayout.setOnClickListener(view -> {
            Intent intent = new Intent(context, ViewExpenseActivity.class);
            intent.putExtra("expense", expense);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout expenseLayout;
        public TextView dateTV, descriptionTV, amountTV;

        public ViewHolder(View itemView) {
            super(itemView);
            expenseLayout = itemView.findViewById(R.id.expense_layout);
            dateTV = itemView.findViewById(R.id.date_text_view);
            descriptionTV = itemView.findViewById(R.id.description_text_view);
            amountTV = itemView.findViewById(R.id.amount_text_view);
        }
    }

    private String trimDescription(String s) {
        s = s.trim();
        return s.length() <= 20 ? s : s.substring(0, 20) + "..";
    }
}

