package com.hritik.callmanager;

public class Model {

    private boolean isSelected;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEditTextValue(String editTextValue) {
        this.name = editTextValue;
    }

    public boolean getSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}