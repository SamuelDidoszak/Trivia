package com.gmail.trivia.data;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonRequest;
import com.gmail.trivia.controller.AppController;
import com.gmail.trivia.model.Question;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class QuestionBank {
    private final String URL = "https://raw.githubusercontent.com/curiousily/simple-quiz/master/script/statements-data.json";
    private ArrayList<Question> questionArrayList= new ArrayList<>();

    public List<Question> getQuestions(final ResponseGotData callBack)
    {

        JsonArrayRequest request = new JsonArrayRequest(
                JsonRequest.Method.GET, URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                //Question tempQuestion = new Question();   not working properly here   (1)
                for(int i = 0; i < response.length(); i++)
                {
                    try {
                        Question tempQuestion = new Question();     //it has to be here (2)
                        tempQuestion.setQuestion(response.getJSONArray(i).getString(0));
                        tempQuestion.setAnswer(response.getJSONArray(i).getBoolean(1));
                        questionArrayList.add(tempQuestion);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if(callBack != null)
                    callBack.finished(questionArrayList);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("JSON.ERROR", error.toString());
            }
        });
        AppController.getInstance().addToRequestQueue(request);
        return questionArrayList;
    }
}
