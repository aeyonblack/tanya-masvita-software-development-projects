package com.acronym.playit.models;

public class Quiz {

    private int id;
    private String quiz;
    private String solution;
    private int score;
    private int status;
    private String type;
    private int gotIt;

    public Quiz() {
        /*Default Constructor*/
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setQuiz(String quiz) {
        this.quiz = quiz;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setGotIt(int gotIt) {
        this.gotIt = gotIt;
    }

    public int getId() {
        return id;
    }

    public String getQuiz() {
        return quiz;
    }

    public String getSolution() {
        return solution;
    }

    public int getScore() {
        return score;
    }

    public String getType() {
        return type;
    }

    public int getGotIt() {
        return gotIt;
    }

}
