package com.example.tripassistant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tripassistant.models.ChecklistOption;
import com.example.tripassistant.models.SharelistOption;

import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.List;

public class SelectPayeeActivity extends AppCompatActivity {
    private int selectedTab;

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
        backBtn.setOnClickListener(view -> {
            onBackPressed();
        });

        Intent intent = getIntent();
        List<String> tripMembers = intent.getStringArrayListExtra("tripMembers");
        selectedTab = intent.getIntExtra("selectedTab", 0);
        isNeeded = intent.getIntegerArrayListExtra("isNeeded");
        shares = intent.getStringArrayListExtra("shares");

        equalRV = findViewById(R.id.equalRV);
        equalRV.setLayoutManager(new LinearLayoutManager(this));
        optionList = new ArrayList<>();
        for (int i = 0; i < tripMembers.size(); i++) {
            optionList.add(new ChecklistOption(tripMembers.get(i), isNeeded.get(i) == 1));
        }
        PayeeListAdapter adapter = new PayeeListAdapter(optionList);
        equalRV.setAdapter(adapter);

        unequalRV = findViewById(R.id.unequalRV);
        unequalRV.setLayoutManager(new LinearLayoutManager(this));
        shareList = new ArrayList<>();
        for (int i = 0; i < tripMembers.size(); i++) {
            shareList.add(new SharelistOption(tripMembers.get(i), shares.get(i)));
        }
        ShareListAdapter adapter2 = new ShareListAdapter(shareList);
        unequalRV.setAdapter(adapter2);

        equalBtn = findViewById(R.id.equal_button);
        unequalBtn = findViewById(R.id.unequal_button);
        equalTV = findViewById(R.id.equal_text_view);
        unequalTV = findViewById(R.id.unequal_text_view);

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

        Button okayBtn = findViewById(R.id.okay_button);
        okayBtn.setOnClickListener(view -> {
            onBackPressed();
        });
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
        equalBtn.setBackgroundColor(getResources().getColor(R.color.green));
        equalBtn.setTextColor(getResources().getColor(R.color.white));
        unequalBtn.setBackgroundColor(getResources().getColor(R.color.white));
        unequalBtn.setTextColor(getResources().getColor(R.color.green));
        equalRV.setVisibility(View.VISIBLE);
        unequalRV.setVisibility(View.GONE);
        equalTV.setVisibility(View.VISIBLE);
        unequalTV.setVisibility(View.GONE);
    }

    private void selectUnequal() {
        unequalBtn.setBackgroundColor(getResources().getColor(R.color.green));
        unequalBtn.setTextColor(getResources().getColor(R.color.white));
        equalBtn.setBackgroundColor(getResources().getColor(R.color.white));
        equalBtn.setTextColor(getResources().getColor(R.color.green));
        unequalRV.setVisibility(View.VISIBLE);
        equalRV.setVisibility(View.GONE);
        unequalTV.setVisibility(View.VISIBLE);
        equalTV.setVisibility(View.GONE);
    }
}