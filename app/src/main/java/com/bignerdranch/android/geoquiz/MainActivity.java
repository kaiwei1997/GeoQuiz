package com.bignerdranch.android.geoquiz;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private Button mTrueButton;
    private Button mFalseButton;
    private Button mResetButton;
    private Button mCheatButton;
    private ImageButton mPreviousbutton;
    private Button mNextButton;
    private TextView mQuestionTextView;
    private TextView mTokenLeft;

    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final int REQUEST_CODE_CHEAT = 0;
    private static final String KEY_ANSWER_INDEX = "answered_index";
    private static final String KEY_ANSWER_CORRECT = "correct_question";
    private static final String KEY_ANSWER_INCORRECT = "incorrect_question";
    private static final String KEY_CHEAT_BANK = "cheat_bank";
    private static final String KEY_TOKEN_LEFT = "cheat_token_left";


    private Question[] mQuestionBank = new Question[]{
            new Question(R.string.question_australia, true),
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true),
    };

    private int mCurrentIndex = 0;

    private HashMap<Integer, Boolean> mCheatBankMap = new HashMap<>();

    private ArrayList<Integer> mAnsweredQuestions = new ArrayList<>();

    private int mNumberOfCorrect = 0;
    private int mNumberOfIncorrect = 0;

    private int mCheatTokenLeft = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            mAnsweredQuestions = savedInstanceState.getIntegerArrayList(KEY_ANSWER_INDEX);
            mNumberOfCorrect = savedInstanceState.getInt(KEY_ANSWER_CORRECT, 0);
            mNumberOfIncorrect = savedInstanceState.getInt(KEY_ANSWER_INCORRECT, 0);
            mCheatBankMap = (HashMap<Integer, Boolean>) savedInstanceState.getSerializable(KEY_CHEAT_BANK);
            mCheatTokenLeft = savedInstanceState.getInt(KEY_TOKEN_LEFT, 3);
        }

        //challenge 2.1
        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextQuestion();
            }
        });

        mTokenLeft = (TextView) findViewById(R.id.cheat_token);
        mTokenLeft.setText(getString(R.string.cheat_token_left) + String.valueOf(mCheatTokenLeft));

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

                mTrueButton.setEnabled(false);
                mFalseButton.setEnabled(false);
                checkAnswer(true);
                mAnsweredQuestions.add(mCurrentIndex);
            }
        });
        mFalseButton = (Button) findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(MainActivity.this,R.string.incorrect_toast,Toast.LENGTH_SHORT).show();
                mTrueButton.setEnabled(false);
                mFalseButton.setEnabled(false);
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

        mResetButton = (Button) findViewById(R.id.reset_button);
        mResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        mCheatButton = (Button) findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCheatTokenLeft > 0) {
                    boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                    boolean isCheated;
                    if (mCheatBankMap.get(mCurrentIndex) != null && mCheatBankMap.get(mCurrentIndex)) {
                        isCheated = true;
                    } else {
                        isCheated = false;
                    }
                    Intent intent = CheatActivity.newIntent(MainActivity.this, answerIsTrue, isCheated);
                    startActivityForResult(intent, REQUEST_CODE_CHEAT);
                } else {
                    mCheatButton.setEnabled(false);
                }

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
        savedInstanceState.putInt(KEY_ANSWER_CORRECT, mNumberOfCorrect);
        savedInstanceState.putInt(KEY_ANSWER_INCORRECT, mNumberOfIncorrect);
        savedInstanceState.putSerializable(KEY_CHEAT_BANK, mCheatBankMap);
        savedInstanceState.putInt(KEY_TOKEN_LEFT, mCheatTokenLeft);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_CODE_CHEAT) {
            if (data == null) {
                return;
            }
            mCheatBankMap.put(mCurrentIndex, CheatActivity.wasAnswerShown(data));
            mCheatTokenLeft--;
            mTokenLeft.setText(getString(R.string.cheat_token_left) + String.valueOf(mCheatTokenLeft));
        }
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
            mTrueButton.setEnabled(false);
            mFalseButton.setEnabled(false);
            Toast.makeText(MainActivity.this, R.string.answered_question, Toast.LENGTH_SHORT).show();
        } else {
            mTrueButton.setEnabled(true);
            mFalseButton.setEnabled(true);
        }
    }

    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();

        int messageResID = 0;

        if (mCheatBankMap.get(mCurrentIndex) != null && mCheatBankMap.get(mCurrentIndex)) {
            messageResID = R.string.judgment_toast;
            if (answerIsTrue == userPressedTrue) {
                mNumberOfCorrect += 1;
            } else {
                mNumberOfIncorrect += 1;
            }
        } else {
            if (answerIsTrue == userPressedTrue) {
                mNumberOfCorrect += 1;
                messageResID = R.string.correct_toast;
            } else {
                mNumberOfIncorrect += 1;
                messageResID = R.string.incorrect_toast;
            }
        }

        Toast.makeText(this, messageResID, Toast.LENGTH_SHORT).show();

        if ((mNumberOfCorrect + mNumberOfIncorrect) == mQuestionBank.length) {
            double mark = ((double) mNumberOfCorrect / (double) mQuestionBank.length) * 100;
            Toast.makeText(MainActivity.this,
                    getString(R.string.amount_of_correct_answers) + Integer.toString(mNumberOfCorrect) + "\n" +
                            getString(R.string.amount_of_incorrect_answers) + Integer.toString(mNumberOfIncorrect) + "\n" +
                            getString(R.string.final_mark) + String.format("%.2f", mark) + getString(R.string.percent)
                    , Toast.LENGTH_SHORT).show();
            mResetButton.setVisibility(View.VISIBLE);
        }
    }
}
