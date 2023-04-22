package com.example.tripassistant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.tripassistant.models.ChecklistOption;
import com.example.tripassistant.models.SharelistOption;

import java.util.ArrayList;
import java.util.List;

public class SelectPayeeActivity extends AppCompatActivity {
    private int selectedTab, green, white;

    private ArrayList<Integer> isNeeded;
    private ArrayList<String> shares;
    private List<ChecklistOption> optionList;
    private List<SharelistOption> shareList;

    private Button equalBtn, unequalBtn;

    private RecyclerView equalRV, unequalRV;
    private TextView equalTV, unequalTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_payee);

        ImageButton backBtn = findViewById(R.id.back_button);
        equalRV = findViewById(R.id.equalRV);
        unequalRV = findViewById(R.id.unequalRV);
        equalBtn = findViewById(R.id.equal_button);
        unequalBtn = findViewById(R.id.unequal_button);
        equalTV = findViewById(R.id.equal_text_view);
        unequalTV = findViewById(R.id.unequal_text_view);
        Button okayBtn = findViewById(R.id.okay_button);

        Intent intent = getIntent();
        List<String> tripMembers = intent.getStringArrayListExtra("tripMembers");
        selectedTab = intent.getIntExtra("selectedTab", 0);
        isNeeded = intent.getIntegerArrayListExtra("isNeeded");
        shares = intent.getStringArrayListExtra("shares");

        green = ContextCompat.getColor(this, R.color.green);
        white = ContextCompat.getColor(this, R.color.white);

        backBtn.setOnClickListener(view -> onBackPressed());

        equalRV.setLayoutManager(new LinearLayoutManager(this));
        optionList = new ArrayList<>();
        for (int i = 0; i < tripMembers.size(); i++) {
            optionList.add(new ChecklistOption(tripMembers.get(i), isNeeded.get(i) == 1));
        }
        PayeeListAdapter adapter = new PayeeListAdapter(optionList);
        equalRV.setAdapter(adapter);

        unequalRV.setLayoutManager(new LinearLayoutManager(this));
        shareList = new ArrayList<>();
        for (int i = 0; i < tripMembers.size(); i++) {
            shareList.add(new SharelistOption(tripMembers.get(i), shares.get(i)));
        }
        ShareListAdapter adapter2 = new ShareListAdapter(shareList);
        unequalRV.setAdapter(adapter2);

        if (selectedTab == 1) {
            selectUnequal();
        }

        equalBtn.setOnClickListener(view -> {
            if (selectedTab == 1) {
                selectEqual();
                selectedTab = 0;
            }
        });

        unequalBtn.setOnClickListener(view -> {
            if (selectedTab == 0) {
                selectUnequal();
                selectedTab = 1;
            }
        });

        okayBtn.setOnClickListener(view -> onBackPressed());
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("selectedTab", selectedTab);
        for (int i = 0; i < optionList.size(); i++)
            isNeeded.set(i, optionList.get(i).isChecked() ? 1 : 0);
        intent.putIntegerArrayListExtra("isNeeded", isNeeded);
        for (int i = 0; i < shareList.size(); i++)
            shares.set(i, shareList.get(i).getAmount());
        intent.putStringArrayListExtra("shares", shares);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    private void selectEqual() {
        equalBtn.setBackgroundColor(green);
        equalBtn.setTextColor(white);
        unequalBtn.setBackgroundColor(white);
        unequalBtn.setTextColor(green);
        equalRV.setVisibility(View.VISIBLE);
        unequalRV.setVisibility(View.GONE);
        equalTV.setVisibility(View.VISIBLE);
        unequalTV.setVisibility(View.GONE);
    }

    private void selectUnequal() {
        unequalBtn.setBackgroundColor(green);
        unequalBtn.setTextColor(white);
        equalBtn.setBackgroundColor(white);
        equalBtn.setTextColor(green);
        unequalRV.setVisibility(View.VISIBLE);
        equalRV.setVisibility(View.GONE);
        unequalTV.setVisibility(View.VISIBLE);
        equalTV.setVisibility(View.GONE);
    }
}