package com.bignerdranch.android.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.v4.math.MathUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_SCORE = "extra_score";
    private static final long COUNTDOWN_IN_MILLS = 15000;
    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final int REQUEST_CODE_CHEAT = 0;
    private static final int REQUEST_CODE_SUMMARY =1;
    private static final String KEY_ANSWER_INDEX = "answered_index";
    private static final String KEY_ANSWER_CORRECT = "correct_question";
    private static final String KEY_CHEAT_BANK = "cheat_bank";
    private static final String KEY_TOKEN_LEFT = "cheat_token_left";
    private static final String KEY_QUESTION_TOTAL = "no_total_question";
    private static final String KEY_ANSWERED_QUESTION = "no_answered_question";
    private static final String KEY_TIME_LEFT = "time_left";
    private static final String KEY_TIME_LEFT_BANK = "time_left_map";
    private Button mTrueButton;
    private Button mFalseButton;
    private Button mResetButton;
    private Button mCheatButton;
    private ImageButton mPreviousButton;
    private Button mNextButton;
    private TextView mQuestionTextView;
    private TextView mTokenLeft;
    private TextView score;
    private TextView answered_question;
    private TextView tv_countDown;
    private TextView tv_missedQuestion;
    private Button mSummaryButton;
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

    private HashMap<Integer, Long> mTimeMillsLeftBank = new HashMap<>();

    private int questionCountTotal;

    private int noAnsweredQuestion = 0;

    private int mNumberOfCorrect = 0;

    private int mNumberOfMissQuestion = 0;

    private int mCheatTokenLeft = 3;

    private long mBackPressedTime;

    private ColorStateList textColorDefaultCountDown;

    private CountDownTimer mCountDownTimer;
    private long mTimeLeftInMills;
    private long mUsedTimeInMills;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_main);

        questionCountTotal = mQuestionBank.length;

        mResetButton = (Button) findViewById(R.id.reset_button);
        answered_question = (TextView) findViewById(R.id.text_view_question_count);
        score = (TextView) findViewById(R.id.text_view_score);
        tv_countDown = (TextView) findViewById(R.id.count_down_timer);
        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        mTokenLeft = (TextView) findViewById(R.id.cheat_token);
        mTrueButton = (Button) findViewById(R.id.true_button);
        mFalseButton = (Button) findViewById(R.id.false_button);
        mNextButton = (Button) findViewById(R.id.next_button);
        mPreviousButton = (ImageButton) findViewById(R.id.previous_button);
        mResetButton = (Button) findViewById(R.id.reset_button);
        mCheatButton = (Button) findViewById(R.id.cheat_button);
        tv_missedQuestion = (TextView) findViewById(R.id.missed_question);
        mSummaryButton = (Button)findViewById(R.id.summary_button);

        textColorDefaultCountDown = tv_countDown.getTextColors();

        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            mAnsweredQuestions = savedInstanceState.getIntegerArrayList(KEY_ANSWER_INDEX);
            mNumberOfCorrect = savedInstanceState.getInt(KEY_ANSWER_CORRECT, 0);
            mCheatBankMap = (HashMap<Integer, Boolean>) savedInstanceState.getSerializable(KEY_CHEAT_BANK);
            mCheatTokenLeft = savedInstanceState.getInt(KEY_TOKEN_LEFT, 0);
            questionCountTotal = savedInstanceState.getInt(KEY_QUESTION_TOTAL, 0);
            noAnsweredQuestion = savedInstanceState.getInt(KEY_ANSWERED_QUESTION, 0);
            mTimeLeftInMills = savedInstanceState.getLong(KEY_TIME_LEFT);
            mTimeMillsLeftBank = (HashMap<Integer, Long>) savedInstanceState.getSerializable(KEY_TIME_LEFT_BANK);

            if (questionCountTotal == noAnsweredQuestion) {
                mResetButton.setVisibility(View.VISIBLE);
            }
        }

        answered_question.setText(getString(R.string.no_answered_question) + String.valueOf(mAnsweredQuestions.size()) + "/" + String.valueOf(questionCountTotal));
        tv_missedQuestion.setText(getString(R.string.no_miss_question) + String.valueOf(mNumberOfMissQuestion));
        score.setText(getString(R.string.score) + mNumberOfCorrect);

        //challenge 2.1
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextQuestion();
            }
        });

        mTokenLeft.setText(getString(R.string.cheat_token_left) + String.valueOf(mCheatTokenLeft));

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
                checkAnswer(String.valueOf(true));
            }
        });

        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(MainActivity.this,R.string.incorrect_toast,Toast.LENGTH_SHORT).show();
                checkAnswer(String.valueOf(false));
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mCountDownTimer != null) {
                    mCountDownTimer.cancel();
                }
                nextQuestion();
            }
        });

        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCountDownTimer != null) {
                    mCountDownTimer.cancel();
                }
                previousQuestion();
            }
        });

        mResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetQuiz();
            }
        });

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
                    mCountDownTimer.cancel();
                    Intent intent = CheatActivity.newIntent(MainActivity.this, answerIsTrue, isCheated);
                    startActivityForResult(intent, REQUEST_CODE_CHEAT);
                } else {
                    mCheatButton.setEnabled(false);
                }

            }
        });

        mSummaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = SumarryResultActivity.newIntent(MainActivity.this, noAnsweredQuestion, mNumberOfCorrect, 3-mCheatTokenLeft, mNumberOfMissQuestion);
                startActivityForResult(intent,REQUEST_CODE_SUMMARY);
            }
        });

        updateQuestion();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!mAnsweredQuestions.contains(mCurrentIndex)) {
            startCountDown();
        } else {
            updateCountDownTimerText();
        }

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
        if (mCountDownTimer != null) {
            mTimeMillsLeftBank.put(mCurrentIndex, mTimeLeftInMills);
            mCountDownTimer.cancel();
        }
        mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
        updateQuestion();
    }


    //challenge 2.2
    private void previousQuestion() {
        /**mCurrentIndex = (5 + mCurrentIndex) % mQuestionBank.length;
         updateQuestion();**/
        if (mCountDownTimer != null) {
            mTimeMillsLeftBank.put(mCurrentIndex, mTimeLeftInMills);
            mCountDownTimer.cancel();
        }
        if (mCurrentIndex > 0) {
            mCurrentIndex -= 1;
            updateQuestion();
        } else {
            mCurrentIndex = mQuestionBank.length - 1;
            updateQuestion();
        }
    }

    /**
     * private void shuffleQuestion(){
     * for(int i =0; i <mQuestionBank.length; i++){
     * int index = (int)(Math.random() * mQuestionBank.length);
     * <p>
     * Question temp = mQuestionBank[i];
     * mQuestionBank[i] = mQuestionBank[index];
     * mQuestionBank[index] = temp;
     * }
     * }
     **/

    private void updateQuestion() {
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
        if (mAnsweredQuestions.contains(mCurrentIndex)) {
            mTrueButton.setEnabled(false);
            mFalseButton.setEnabled(false);
            mCheatButton.setEnabled(false);
            Toast.makeText(MainActivity.this, R.string.answered_question, Toast.LENGTH_SHORT).show();
            mTimeLeftInMills = mTimeMillsLeftBank.get(mCurrentIndex);
            updateCountDownTimerText();
        } else {
            mTrueButton.setEnabled(true);
            mFalseButton.setEnabled(true);
            mCheatButton.setEnabled(true);

            if (mTimeMillsLeftBank.get(mCurrentIndex) != null) {
                mTimeLeftInMills = mTimeMillsLeftBank.get(mCurrentIndex);
                startCountDown();
            } else {
                mTimeLeftInMills = COUNTDOWN_IN_MILLS;
                startCountDown();
            }
        }
    }

    private void startCountDown() {
        mCountDownTimer = new CountDownTimer(mTimeLeftInMills, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMills = millisUntilFinished;
                updateCountDownTimerText();
            }

            @Override
            public void onFinish() {
                mTimeLeftInMills = 0;
                updateCountDownTimerText();
                checkAnswer(null);
            }
        }.start();
    }

    private void updateCountDownTimerText() {
        int minutes = (int) (mTimeLeftInMills / 1000) / 60;
        int seconds = (int) (mTimeLeftInMills / 1000) % 60;

        String time_formatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        tv_countDown.setText(time_formatted);

        if (mTimeLeftInMills < 5000) {
            tv_countDown.setTextColor(Color.RED);
        } else {
            tv_countDown.setTextColor(textColorDefaultCountDown);
        }
    }

    private void checkAnswer(String userPressedTrue) {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();

        int messageResID = 0;

        mCountDownTimer.cancel();
        mTimeMillsLeftBank.put(mCurrentIndex,mTimeLeftInMills);

        mAnsweredQuestions.add(mCurrentIndex);
        noAnsweredQuestion = mAnsweredQuestions.size();

        mTrueButton.setEnabled(false);
        mFalseButton.setEnabled(false);
        mCheatButton.setEnabled(false);

        if (userPressedTrue == null) {
            messageResID = R.string.miss_question;
            mNumberOfMissQuestion += 1;
        } else {
            if (mCheatBankMap.get(mCurrentIndex) != null && mCheatBankMap.get(mCurrentIndex)) {
                messageResID = R.string.judgment_toast;
                if (String.valueOf(answerIsTrue) == userPressedTrue) {
                    mNumberOfCorrect += 1;
                }
            } else {
                if (String.valueOf(answerIsTrue) == userPressedTrue) {
                    messageResID = R.string.correct_toast;
                    mNumberOfCorrect += 1;
                } else {
                    messageResID = R.string.incorrect_toast;
                }
            }
        }

        Toast.makeText(this, messageResID, Toast.LENGTH_SHORT).show();

        score.setText(getString(R.string.score) + String.valueOf(mNumberOfCorrect));

        tv_missedQuestion.setText(getString(R.string.no_miss_question) + String.valueOf(mNumberOfMissQuestion));

        answered_question.setText(getString(R.string.no_answered_question) +
                String.valueOf(noAnsweredQuestion) +
                "/" + String.valueOf(questionCountTotal));

        if (questionCountTotal == noAnsweredQuestion) {
            double mark = ((double) mNumberOfCorrect / (double) mQuestionBank.length) * 100;
            Toast.makeText(MainActivity.this,
                    getString(R.string.amount_of_correct_answers) + Integer.toString(mNumberOfCorrect) + "\n" +
                            getString(R.string.final_mark) + String.format("%.2f", mark) + getString(R.string.percent) +
                    "\n"+ getString(R.string.total_used_time) + calculateUsedTime()
                    , Toast.LENGTH_SHORT).show();
            mResetButton.setVisibility(View.VISIBLE);
        }
    }

    private void resetQuiz() {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_SCORE, mNumberOfCorrect);
        setResult(RESULT_OK, intent);
        finish();

    }

    private String calculateUsedTime(){
        long sumOfMills = 0;
        for(int i =0; i < mTimeMillsLeftBank.size(); i++){
            sumOfMills += mTimeMillsLeftBank.get(i);
        }
        mUsedTimeInMills = (questionCountTotal * COUNTDOWN_IN_MILLS) - (sumOfMills);

        int minutes = (int) (mUsedTimeInMills / 1000) / 60;
        int seconds = (int) (mUsedTimeInMills / 1000) % 60;

        String time_formatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        return time_formatted;
    }

    @Override
    public void onBackPressed() {
        if (mBackPressedTime + 2000 > System.currentTimeMillis()) {
            resetQuiz();
        } else {
            Toast.makeText(MainActivity.this, R.string.back_pressed, Toast.LENGTH_SHORT).show();
        }

        mBackPressedTime = System.currentTimeMillis();
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
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
        if(mCountDownTimer!= null){
            mCountDownTimer.cancel();
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy() called");
        super.onDestroy();
        if(mCountDownTimer!= null){
            mCountDownTimer.cancel();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState");
        mTimeMillsLeftBank.put(mCurrentIndex,mTimeLeftInMills);
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
        savedInstanceState.putIntegerArrayList(KEY_ANSWER_INDEX, mAnsweredQuestions);
        savedInstanceState.putInt(KEY_ANSWER_CORRECT, mNumberOfCorrect);
        savedInstanceState.putSerializable(KEY_CHEAT_BANK, mCheatBankMap);
        savedInstanceState.putInt(KEY_TOKEN_LEFT, mCheatTokenLeft);
        savedInstanceState.putInt(KEY_QUESTION_TOTAL, questionCountTotal);
        savedInstanceState.putInt(KEY_ANSWERED_QUESTION, noAnsweredQuestion);
        savedInstanceState.putLong(KEY_TIME_LEFT, mTimeLeftInMills);
        savedInstanceState.putSerializable(KEY_TIME_LEFT_BANK, mTimeMillsLeftBank);
    }
}
