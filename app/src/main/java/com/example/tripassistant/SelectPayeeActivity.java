package com.example.tripassistant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.tripassistant.models.ChecklistOption;

import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.List;

public class SelectPayeeActivity extends AppCompatActivity {
    private int selectedTab;

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

        RecyclerView equalRV = findViewById(R.id.equalRV);
        equalRV.setLayoutManager(new LinearLayoutManager(this));
        List<ChecklistOption> optionList = new ArrayList<>();
        for (String member : tripMembers) optionList.add(new ChecklistOption(member, true));
        PayeeListAdapter adapter = new PayeeListAdapter(optionList);
        equalRV.setAdapter(adapter);

        RecyclerView unequalRV = findViewById(R.id.unequalRV);
        equalRV.setLayoutManager(new LinearLayoutManager(this));

        Button equalBtn = findViewById(R.id.equal_button), unequalBtn = findViewById(R.id.unequal_button);
        equalBtn.setOnClickListener(view -> {
            if (selectedTab == 1) {
                equalBtn.setBackgroundColor(getResources().getColor(R.color.black));
                equalBtn.setTextColor(getResources().getColor(R.color.white));
                unequalBtn.setBackgroundColor(getResources().getColor(R.color.white));
                unequalBtn.setTextColor(getResources().getColor(R.color.black));
                equalRV.setVisibility(View.VISIBLE);
                unequalRV.setVisibility(View.GONE);
                selectedTab = 0;
            }
        });

        unequalBtn.setOnClickListener(view -> {
            if (selectedTab == 0) {
                unequalBtn.setBackgroundColor(getResources().getColor(R.color.black));
                unequalBtn.setTextColor(getResources().getColor(R.color.white));
                equalBtn.setBackgroundColor(getResources().getColor(R.color.white));
                equalBtn.setTextColor(getResources().getColor(R.color.black));
                unequalRV.setVisibility(View.VISIBLE);
                equalRV.setVisibility(View.GONE);
                selectedTab = 1;
            }
        });
    }
}