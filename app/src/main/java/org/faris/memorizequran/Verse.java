package org.faris.memorizequran;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity
class Verse {

    @PrimaryKey(autoGenerate = true)
    public long uid;

    @ColumnInfo(name = "surah")
    public int surah;

    @ColumnInfo(name = "num")
    public int num;

    @ColumnInfo(name = "memorized")
    public boolean memorized;

    @Ignore
    private String text;

    public Verse(int surah, int num, boolean memorized) {
        this.surah = surah;
        this.num = num;
        this.memorized = memorized;
    }

    @Ignore
    Verse(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
