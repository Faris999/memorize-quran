package org.faris.memorizequran;

public class User {

    private String id;
    private String name;
    private int score;

    public User(String id, String name, int score) {
        this.id = id;
        this.name = name;
        this.score = score;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }
}
