package com.gmail.trivia.data;

import com.gmail.trivia.model.Question;

import java.util.ArrayList;

public interface ResponseGotData {
    void finished(ArrayList<Question> questionDB);
}
