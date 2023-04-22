package com.example.tripassistant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.tripassistant.models.ChecklistOption;

import java.util.ArrayList;
import java.util.List;

public class SelectPayerActivity extends AppCompatActivity {

    private List<String> tripMembers;
    private PayerListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_payer);

        ImageButton backBtn = findViewById(R.id.back_button);
        backBtn.setOnClickListener(view -> {
            onBackPressed();
        });

        Intent intent = getIntent();
        tripMembers = intent.getStringArrayListExtra("tripMembers");
        int selectedItem = intent.getIntExtra("currIndex", -1);

        List<ChecklistOption> optionList = new ArrayList<>();
        for (String member : tripMembers) {
            optionList.add(new ChecklistOption(member, false));
        }

        RecyclerView memberRV = findViewById(R.id.memberRV);
        memberRV.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PayerListAdapter(this, optionList, selectedItem);
        memberRV.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        int selectedIndex = adapter.getSelectedItem();
        if (selectedIndex == -1) {
            Toast.makeText(SelectPayerActivity.this, "Payer has yet to select",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent();
        intent.putExtra("selectedIndex", selectedIndex);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }
}