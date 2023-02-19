package com.example.balancemanager.settings;

import java.io.Serializable;

public abstract class Settings implements Serializable {
    private static final long serialVersionUID = 4L;
    protected final String displayName;
    protected boolean checked;

    protected Settings(String displayName) {
        this.displayName = displayName;
        this.checked = false;
    }
    public String getDisplayName() {
        return displayName;
    }
    public boolean isChecked() {
        return checked;
    }
    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
