package com.arshad.tinytrivia.data;
import com.arshad.tinytrivia.model.Question;

import java.util.ArrayList;
// Interface will allow to get response to combine with MainActivity from Repository class.
// It works as link piece which hold data from Repository and make it available at MainActivity.
public interface AnswerListAsyncResponse {
    void processFinished(ArrayList<Question> questionArrayList);
}
