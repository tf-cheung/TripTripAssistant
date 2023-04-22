package com.example.tripassistant.models;

public class SharelistOption {
    private final String label;
    private String amount;

    public SharelistOption(String label, String amount) {
        this.label = label;
        this.amount = amount;
    }

    public String getLabel() {
        return label;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
