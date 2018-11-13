package org.faris.memorizequran;

public class Surah {

    private String name;
    private int versesCount;

    Surah(String name, int versesCount) {
        this.name = name;
        this.versesCount = versesCount;
    }

    public String getName() {
        return name;
    }

    public int getVersesCount() {
        return versesCount;
    }
}
