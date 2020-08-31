package com.gmail.trivia.model;

public class Question {
    private String question;
    private boolean answer;

    public String getQuestion() {
        return question;
    }

    public boolean isAnswer() {
        return answer;
    }

    public Question(String question, boolean answer)
    {
        this.question = question;
        this.answer = answer;
    }
    public Question() { }



    public void setQuestion(String question) {
        this.question = question;
    }
    public void setAnswer(boolean answer) {
        this.answer = answer;
    }

}


