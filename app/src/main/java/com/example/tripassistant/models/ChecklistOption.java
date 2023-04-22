package com.example.tripassistant.models;

public class ChecklistOption {
    private String label;
    private boolean checked;

    public ChecklistOption(String label, boolean checked) {
        this.label = label;
        this.checked = checked;
    }

    public String getLabel() {
        return label;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
