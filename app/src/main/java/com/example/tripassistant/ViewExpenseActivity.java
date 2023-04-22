package com.example.tripassistant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.tripassistant.models.Expense;
import com.example.tripassistant.models.SharelistOption;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ViewExpenseActivity extends AppCompatActivity {
    private final DecimalFormat decimalFormat = new DecimalFormat("#.##");
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_expense);

        Expense expense = getIntent().getParcelableExtra("expense");
        TextView descriptionTV = findViewById(R.id.description_text_view);
        descriptionTV.setText(expense.getDescription());

        TextView amountTV = findViewById(R.id.amount_text_view);
        amountTV.setText(getString(R.string.us_money, decimalFormat.format(expense.getAmount())));

        TextView dateTV = findViewById(R.id.date_text_view);
        dateTV.setText(getString(R.string.added_on, dateFormat.format(expense.getDate())));

        TextView payerTV = findViewById(R.id.payer_text_view);
        payerTV.setText(getString(R.string.someone_paid, expense.getPayer(), decimalFormat.format(expense.getAmount())));

        RecyclerView payeeRV = findViewById(R.id.payee_recycler_view);
        payeeRV.setLayoutManager(new LinearLayoutManager(this));
        List<SharelistOption> oweList = new ArrayList<>();
        for (Map.Entry<String, Float> entry : expense.getPayees().entrySet()) {
            oweList.add(new SharelistOption(entry.getKey(), decimalFormat.format(entry.getValue())));
        }
        OweListAdapter adapter = new OweListAdapter(this, oweList, expense.getDescription().equals("Settle up") ? "received" : "owes");
        payeeRV.setAdapter(adapter);

        ImageButton backBtn = findViewById(R.id.back_button);
        backBtn.setOnClickListener(view -> onBackPressed());
    }
}