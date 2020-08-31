package com.gmail.trivia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import com.gmail.trivia.data.QuestionBank;
import com.gmail.trivia.data.ResponseGotData;
import com.gmail.trivia.model.Question;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView questionTextView, showPointsTextView;
    private Button falseButton, trueButton;
    private CardView questionCard;
    private int questionNumber, questionCount = 0, points = 0;
    private List<Question> questionList;
    private Boolean buttonsClickable = true, resultsShown = false;
    private Boolean questionsLoaded = false;
    private SwipeDetection swipeDetection;
    private List<Integer> availableQuestionList;
    private int highScore;



    @SuppressLint("ClickableViewAccessibility")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        questionTextView = findViewById(R.id.questionTextView);
        showPointsTextView = findViewById(R.id.showPointsTextView);
        falseButton = findViewById(R.id.falseButton);
        trueButton = findViewById(R.id.trueButton);
        questionCard = findViewById(R.id.questionCard);

        falseButton.setOnClickListener(this);
        trueButton.setOnClickListener(this);

        swipeDetection = new SwipeDetection(this);
        questionCard.setOnTouchListener(swipeDetection);

        availableQuestionList = new ArrayList<>();
        getQuestionList();

        highScore = getHighScore();
    }

    public void getQuestionList()
    {
        questionList = new QuestionBank().getQuestions(new ResponseGotData() {
            public void finished(ArrayList<Question> questionDB) {
                int randomQuestion = (int) (Math.random()*questionList.size());
                questionNumber = randomQuestion;
               for(int i = 0; i < questionList.size(); i++)
               {
                   availableQuestionList.add(i);
               }
               questionsLoaded = true;
               showQuestion();
            }
        });
    }

    public void showQuestion()
    {
        if(questionsLoaded) {
            questionTextView.setTextSize(16);
            questionTextView.setText(questionList.get(availableQuestionList.get(questionNumber)).getQuestion());
        }
    }

    public void onClick(View v) {
        if(resultsShown)
        {
            showQuestion();
            resultsShown = false;
            return;
        }
        if(!buttonsClickable)
            return;
        int id = v.getId();
        switch(id)
        {
            case R.id.falseButton:
                onAnswer(false);
                break;
            case R.id.trueButton:
                onAnswer(true);
                break;
        }
    }

    public void onAnswer(Boolean answer)
    {
        buttonsClickable = false;
        questionCount++;
        if(answer == questionList.get(availableQuestionList.get(questionNumber)).isAnswer())
        {
            showCorrectAnimation();
            showPointsAnimation(true);
            availableQuestionList.remove(questionNumber);
            points+=10;
            questionTextView.setText("Correct!");
            new android.os.Handler().postDelayed(new Runnable() {
                public void run() {
                    showQuestion();
                    buttonsClickable = true;
                }
            }, 750);
        }
        else
        {
            showFalseAnimation();
            showPointsAnimation(false);
            availableQuestionList.remove(questionNumber);
            if(points > 0)
                points-=10;
            questionTextView.setText("Incorrect");
            new android.os.Handler().postDelayed(new Runnable() {
                public void run() {
                    showQuestion();
                    buttonsClickable = true;
                }
            }, 750);
        }
    }

    public void showCurrentResults()
    {
        questionTextView.setText("Answered " + questionCount + " questions\n\nPoints " + points + "\n\nHigh Score " + highScore);
        resultsShown = true;
    }

    public void showFalseAnimation()
    {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake_animation);
        questionCard.startAnimation(shake);

        shake.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
                questionTextView.setTextSize(24);
            }

            public void onAnimationEnd(Animation animation) {
            }

            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void showCorrectAnimation()
    {
        ObjectAnimator up = ObjectAnimator.ofFloat(questionTextView, "translationY", 0f, 100f, 50f, 0f, -50f, -100f, -50f, 0f);
        up.setDuration(750);
        questionTextView.setTextSize(24);
        up.start();
    }

    public void showPointsAnimation(boolean correct)
    {
        if(correct)
        {
            showPointsTextView.setText("+10");
//            ObjectAnimator up = ObjectAnimator.ofFloat(showPointsTextView, "translationY", 15f, 10f, 5f, 0f, -5f, -10f, -15f, -20f, -25f, -26f, -27f, -28f, -29f, -30f);
              ObjectAnimator up = ObjectAnimator.ofFloat(showPointsTextView, "translationY", 15f, -30f);
            up.setDuration(500);
            up.start();
        }
        else
        {
            showPointsTextView.setText("-10");
//            ObjectAnimator down = ObjectAnimator.ofFloat(showPointsTextView, "translationY", -15f, -10f, -5f, 0f, 5f, 10f, 15f, 20f, 25f, 26f, 27f, 28f, 29f, 30f);
            ObjectAnimator down = ObjectAnimator.ofFloat(showPointsTextView, "translationY", -15f, 30f);
            down.setDuration(500);
            down.start();
        }
        showPointsTextView.animate()
                .alpha(0f)
                .setDuration(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        showPointsTextView.setText("");
                        showPointsTextView.setAlpha(1f);
                    }
                });
    }

    public void addHighScore()
    {
        SharedPreferences highScore = getSharedPreferences("score", MODE_PRIVATE);
        SharedPreferences.Editor editor = highScore.edit();
        if(points > highScore.getInt("highScore", 0)) {
            editor.putInt("highScore", points);
            editor.apply();
        }
    }

    public int getHighScore()
    {
        SharedPreferences highScore = getSharedPreferences("score", MODE_PRIVATE);
        return highScore.getInt("highScore", 0);
    }

    protected void onPause() {
        super.onPause();
        addHighScore();
    }
    public void swipeProcessing(String gesture)
    {
        switch(gesture)
            {
                case "none":
                    if(resultsShown)
                    {
                        showQuestion();
                        resultsShown = false;
                        break;
                    }
                    else
                        showCurrentResults();
                    break;
                case "left":
                    questionNumber = ++questionNumber % availableQuestionList.size();
                    showQuestion();
                    resultsShown = false;
                    break;
                case "right":
                    if(questionNumber > 0)
                        questionNumber = --questionNumber % availableQuestionList.size();
                    else
                        questionNumber = questionList.size();
                    showQuestion();
                    resultsShown = false;
                    break;
            }
    }

    class SwipeDetection extends GestureDetector.SimpleOnGestureListener implements View.OnTouchListener {
        private GestureDetector detector;

        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if(Math.abs(velocityX) > Math.abs(velocityY))
            {
                if(e1.getX() > e2.getX())
                    swipeProcessing("left");
                else
                    swipeProcessing("right");
            }
            return true;
        }

        public boolean onSingleTapUp(MotionEvent e) {
            swipeProcessing("none");
            Log.d("TOUCH", "onSingleTapUp");
            return true;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            detector.onTouchEvent(event);
            return true;
        }

        public GestureDetector getDetector()
        {
            return detector;
        }

        public SwipeDetection(Context context)
        {
            this(context, null);
        }

        public SwipeDetection(Context context, GestureDetector gestureDetector)
        {
            if(gestureDetector == null)
                gestureDetector = new GestureDetector(context, this);
            this.detector = gestureDetector;
        }
    }
}




