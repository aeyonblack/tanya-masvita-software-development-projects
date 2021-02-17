package com.acronym.playit.models;

public class User {

    private int id;
    private int totalScore;
    private int currentBest;
    private int totalCompleted;
    private int level;
    private String bestWeapon;
    private int count;

    public User() {
        /*Default empty constructor*/
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public void setCurrentBest(int currentBest) {
        this.currentBest = currentBest;
    }

    public void setTotalCompleted(int totalCompleted) {
        this.totalCompleted = totalCompleted;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setBestWeapon(String bestWeapon) {
        this.bestWeapon = bestWeapon;
    }

    public void setBadgeCount(int count) {
        this.count = count;
    }

    public int getId() {
        return id;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public int getCurrentBest() {
        return currentBest;
    }

    public int getTotalCompleted() {
        return totalCompleted;
    }

    public int getLevel() {
        return level;
    }

    public String getBestWeapon() {
        return bestWeapon;
    }

    public int getBadgeCount() {
        return count;
    }


}
