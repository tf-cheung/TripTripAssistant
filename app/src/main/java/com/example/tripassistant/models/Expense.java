package com.example.tripassistant.models;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Expense {
    private String payer, description;
    private float amount;
    private Map<String, Float> payees;
    private Date date;

    public Expense() {

    }

    public Expense(String payer, String description, float amount, Map<String, Float> payees, Date date) {
        this.payer = payer;
        this.description = description;
        this.amount = amount;
        this.payees = payees;
        this.date = date;
    }

    public String getPayer() {
        return payer;
    }

    public String getDescription() {
        return description;
    }

    public float getAmount() {
        return amount;
    }

    public Map<String, Float> getPayees() {
        return payees;
    }

    public Date getDate() {
        return date;
    }
}
