package com.arshad.tinytrivia;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.arshad.tinytrivia.data.Repository;
import com.arshad.tinytrivia.databinding.ActivityMainBinding;
import com.arshad.tinytrivia.model.Question;
import com.arshad.tinytrivia.model.Score;
import com.arshad.tinytrivia.util.Prefs;
import com.google.android.material.snackbar.Snackbar;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    List<Question> questionList;
    private ActivityMainBinding binding;
    private int currentQuestionIndex = 0;
    private int scoreCounter = 0;
    private Score score;
    private Prefs prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        score = new Score();
        prefs = new Prefs(MainActivity.this);

        // Retrieve the last state
        currentQuestionIndex = prefs.getState();

        binding.highestScoreText.setText(MessageFormat.format("Highest: {0}", String.valueOf(prefs.getHighestScore())));
        binding.scoreText.setText(MessageFormat.format("Current Score: {0}",
                String.valueOf(score.getScore())));

        // Storing API data in questionList to use it populating views.
        questionList = new Repository().getQuestions(questionArrayList -> {
                    binding.questionTextview.setText(questionArrayList.get(currentQuestionIndex)
                            .getAnswer());

                    updateCounter(questionArrayList);
                    // Setting visibility of views
                    binding.progressBar.setVisibility(View.GONE);
                    binding.loadingTextView.setVisibility(View.GONE);
                    binding.buttonFalse.setVisibility(View.VISIBLE);
                    binding.buttonNext.setVisibility(View.VISIBLE);
                    binding.cardView.setVisibility(View.VISIBLE);
                    binding.questionTextview.setVisibility(View.VISIBLE);
                    binding.buttonTrue.setVisibility(View.VISIBLE);
                    binding.textViewOutOf.setVisibility(View.VISIBLE);
                    binding.titleText.setVisibility(View.VISIBLE);
                    binding.scoreText.setVisibility(View.VISIBLE);
                    binding.highestScoreText.setVisibility(View.VISIBLE);
                }
        );

        binding.buttonNext.setOnClickListener(view -> getNextQuestion());
        binding.buttonTrue.setOnClickListener(view -> {
            checkAnswer(true);
            updateQuestion();
        });
        binding.buttonFalse.setOnClickListener(view -> {
            checkAnswer(false);
            updateQuestion();
        });
    }

    private void getNextQuestion() {
        currentQuestionIndex = (currentQuestionIndex + 1) % questionList.size();
        updateQuestion();
    }

    private void checkAnswer(boolean userChoseCorrect) {
        boolean answer = questionList.get(currentQuestionIndex).isAnswerTrue();
        int snackMessageId;
        if (userChoseCorrect == answer) {
            snackMessageId = R.string.correct_answer;
            fadeAnimation();
            addPoints();
        } else {
            deductPoints();
            snackMessageId = R.string.incorrect;
            shakeAnimation();
        }
        Snackbar.make(binding.cardView, snackMessageId, Snackbar.LENGTH_SHORT)
                .show();
    }

    private void updateCounter(ArrayList<Question> questionArrayList) {
        binding.textViewOutOf.setText(String.format(getString(R.string.text_formatted),
                currentQuestionIndex, questionArrayList.size()));
    }

    private void fadeAnimation() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setDuration(300);
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setRepeatMode(Animation.REVERSE);

        binding.cardView.setAnimation(alphaAnimation);

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                binding.questionTextview.setTextColor(Color.GREEN);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.questionTextview.setTextColor(Color.WHITE);
                getNextQuestion();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private void updateQuestion() {
        String question = questionList.get(currentQuestionIndex).getAnswer();
        binding.questionTextview.setText(question);
        updateCounter((ArrayList<Question>) questionList);
    }

    private void shakeAnimation() {
        Animation shake = AnimationUtils.loadAnimation(MainActivity.this,
                R.anim.shake_animation);
        binding.cardView.setAnimation(shake);

        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                binding.questionTextview.setTextColor(Color.RED);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.questionTextview.setTextColor(Color.WHITE);
                getNextQuestion();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private void deductPoints() {
        if (scoreCounter > 0) {
            scoreCounter -= 100;
            score.setScore(scoreCounter);
            binding.scoreText.setText(MessageFormat.format("Current Score: {0}",
                    String.valueOf(score.getScore())));
        } else {
            scoreCounter = 0;
            score.setScore(scoreCounter);
        }
    }

    private void addPoints() {
        scoreCounter += 100;
        score.setScore(scoreCounter);
        binding.scoreText.setText(String.valueOf(score.getScore()));
        binding.scoreText.setText(MessageFormat.format("Current Score: {0}",
                String.valueOf(score.getScore())));
    }

    @Override
    protected void onPause() {
        prefs.saveHighestScore(score.getScore());
        prefs.setState(currentQuestionIndex);
        super.onPause();
    }
}