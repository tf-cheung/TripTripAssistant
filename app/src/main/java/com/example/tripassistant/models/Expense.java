package com.example.tripassistant.models;

import java.util.HashMap;
import java.util.Map;

public class Expense {
    private Map<String, Float> payers = new HashMap<>();
    private Map<String, Float> payees = new HashMap<>();

    public Expense() {

    }

    public Expense(Map<String, Float> payers, Map<String, Float> payees) {
        this.payers = payers;
        this.payees = payees;
    }

    public Map<String, Float> getPayers() {
        return payers;
    }

    public Map<String, Float> getPayees() {
        return payees;
    }
}
