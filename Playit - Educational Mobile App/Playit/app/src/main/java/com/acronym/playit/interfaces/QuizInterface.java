package com.acronym.playit.interfaces;

public interface QuizInterface {
    void setQuestion(String question);
    void setAnswer(int answer);
    void setAnswer(String answer);
    boolean isLocked();
    String getQuestion();
    String getAnswer();
    int getNumericalAnswer();
}
