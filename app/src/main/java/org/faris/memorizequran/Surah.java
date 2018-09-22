package org.faris.memorizequran;

import android.support.annotation.NonNull;

public class Surah {

    private String name = "null";
    private boolean checked = false;

    public Surah(String name) {
        this.name = name;
        this.checked = false;
    }

    public String getName() {
        return name;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
