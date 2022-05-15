package com.arshad.tinytrivia.data;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.arshad.tinytrivia.controller.AppController;
import com.arshad.tinytrivia.model.Question;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class Repository {

    ArrayList<Question> questionArrayList = new ArrayList<>();
    String url = "https://raw.githubusercontent.com/curiousily/simple-quiz/master/script/statements-data.json";

    // Gets all questions from API and store them in list of type Question class.
    public List<Question> getQuestions(final AnswerListAsyncResponse callBack) {

        // JSON request to fetch data from API
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
                url, null, response -> {
            for (int i = 0; i < response.length(); i++) {
                try {
                    Question question = new Question(response.getJSONArray(i).get(0).toString(),
                            response.getJSONArray(i).getBoolean(1));

                    // Add questions to arraylist
                    questionArrayList.add(question);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            // This will pass question list to AnswerListAsyncResponse interface to use it MainActivity.
            // It also make sure that when we initialize interface its object hold question list.
            if (null != callBack) callBack.processFinished(questionArrayList);
        }, Throwable::printStackTrace);

        // Adding JSON request to request queue
        AppController.getInstance().addToRequestQueue(jsonArrayRequest);

        return questionArrayList;
    }
}