package com.bignerdranch.android.geoquiz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Button mTrueButton;
    private Button mFalseButton;
    private ImageButton mPreviousbutton;
    private Button mNextButton;
    private TextView mQuestionTextView;

    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final String KEY_ANSWER_INDEX = "answered_index";

    private Question[] mQuestionBank = new Question[]{
            new Question(R.string.question_australia, true),
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true),
    };

    private int mCurrentIndex = 0;

    private ArrayList<Integer> mAnsweredQuestions = new ArrayList<>();

    private int mNumberOfCorrect = 0;
    private int mNumberofIncorrect = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            mAnsweredQuestions = savedInstanceState.getIntegerArrayList(KEY_ANSWER_INDEX);
        }

        //challenge 2.1
        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextQuestion();
            }
        });

        mTrueButton = (Button) findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * Challenge 1
                 Toast myToast;
                 myToast = Toast.makeText(MainActivity.this,R.string.correct_toast,Toast.LENGTH_SHORT);
                 myToast.setGravity(Gravity.TOP,100,100);
                 myToast.show();
                 **/

                mTrueButton.setClickable(false);
                mFalseButton.setClickable(false);
                checkAnswer(true);
                mAnsweredQuestions.add(mCurrentIndex);
            }
        });
        mFalseButton = (Button) findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(MainActivity.this,R.string.incorrect_toast,Toast.LENGTH_SHORT).show();
                mTrueButton.setClickable(false);
                mFalseButton.setClickable(false);
                checkAnswer(false);
                mAnsweredQuestions.add(mCurrentIndex);
            }
        });

        mNextButton = (Button) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextQuestion();
            }
        });

        mPreviousbutton = (ImageButton) findViewById(R.id.previous_button);
        mPreviousbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousQuestion();
            }
        });

        updateQuestion();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState");
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
        savedInstanceState.putIntegerArrayList(KEY_ANSWER_INDEX, mAnsweredQuestions);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }

    private void nextQuestion() {
        mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
        updateQuestion();
    }


    //challenge 2.2
    private void previousQuestion() {

        /**mCurrentIndex = (5 + mCurrentIndex) % mQuestionBank.length;
         updateQuestion();**/
        if (mCurrentIndex > 0) {
            mCurrentIndex -= 1;
            updateQuestion();
        } else {
            mCurrentIndex = mQuestionBank.length - 1;
            updateQuestion();
        }
    }

    private void updateQuestion() {
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
        if (mAnsweredQuestions.contains(mCurrentIndex)) {
            mTrueButton.setClickable(false);
            mFalseButton.setClickable(false);
        } else {
            mTrueButton.setClickable(true);
            mFalseButton.setClickable(true);
        }
    }

    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();

        int messageResID = 0;

        if (userPressedTrue == answerIsTrue) {
            messageResID = R.string.correct_toast;
            mNumberOfCorrect += 1;
        } else {
            messageResID = R.string.incorrect_toast;
            mNumberofIncorrect += 1;
        }

        Toast.makeText(this, messageResID, Toast.LENGTH_SHORT).show();

        if ((mNumberOfCorrect + mNumberofIncorrect) == mQuestionBank.length) {
            double mark = ((double) mNumberOfCorrect / (double) mQuestionBank.length) * 100;
            Toast.makeText(MainActivity.this,
                    getString(R.string.amount_of_correct_answers) + Integer.toString(mNumberOfCorrect) + "\n" +
                            getString(R.string.amount_of_incorrect_answers) + Integer.toString(mNumberofIncorrect) + "\n" +
                            getString(R.string.final_mark) + String.format("%.2f", mark)
                    , Toast.LENGTH_SHORT).show();
        }
    }
}
