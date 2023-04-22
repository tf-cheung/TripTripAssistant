package com.example.tripassistant.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Expense implements Parcelable {
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

    private Expense(Parcel in) {
        payer = in.readString();
        description = in.readString();
        amount = in.readFloat();
        payees = new HashMap<>();
        in.readMap(payees, null);
        date = new Date(in.readString());
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

    @NonNull
    public String toString() {
        return "Payer: " + payer + "\n" +
                "Description: " + description + "\n" +
                "Amount: " + amount + "\n" +
                "Shares: " + payees.toString() + "\n" +
                "Date: " + date.toString() + "\n";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel out, int flags) {
        out.writeString(payer);
        out.writeString(description);
        out.writeFloat(amount);
        out.writeMap(payees);
        out.writeString(date.toString());
    }

    public static final Creator<Expense> CREATOR = new Creator<Expense>() {
        @Override
        public Expense createFromParcel(Parcel in) {
            return new Expense(in);
        }

        @Override
        public Expense[] newArray(int size) {
            return new Expense[size];
        }
    };
}
