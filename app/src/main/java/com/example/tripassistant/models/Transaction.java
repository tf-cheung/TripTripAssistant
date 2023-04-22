package com.example.tripassistant.models;

import androidx.annotation.NonNull;

import java.text.DecimalFormat;

public class Transaction {
    private DecimalFormat df = new DecimalFormat("#.##");
    private final String payer, payee;
    private final float amount;

    public Transaction(String payer, String payee, float amount) {
        this.payer = payer;
        this.payee = payee;
        this.amount = amount;
    }

    public String getPayer() {
        return payer;
    }

    public String getPayee() {
        return payee;
    }

    public float getAmount() {
        return amount;
    }

    @NonNull
    public String toString() {
        return payee + " owes " + df.format(amount) + " to " + payer;
    }
}
