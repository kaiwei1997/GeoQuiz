package com.bignerdranch.android.geoquiz;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CheatActivity extends AppCompatActivity {

    private static final String EXTRA_ANSWER_IS_TRUE = "com.bignerdranch.android.geoquiz.answer_is-true";
    private static final String EXTRA_ANSWER_SHOWN = "res.answer_shown";
    private static final String EXTRA_IS_CHEATED = "com.bignerdranch.android.geoquiz.is-cheated";
    private static final String TAG = "CheatActivity";

    private boolean mAnswerIsTrue;
    private boolean mIsCheat = false;

    private TextView mAnswerTextView;
    private Button mShowAnswerButton;

    public static Intent newIntent(Context packageContext, boolean answerIsTrue, boolean cheated) {
        Intent intent = new Intent(packageContext, CheatActivity.class);
        intent.putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue);
        intent.putExtra(EXTRA_IS_CHEATED, cheated);
        return intent;
    }

    public static boolean wasAnswerShown(Intent result) {
        return result.getBooleanExtra(EXTRA_ANSWER_SHOWN, false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);

        mAnswerIsTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false);
        mIsCheat = getIntent().getBooleanExtra(EXTRA_IS_CHEATED, false);
        mAnswerTextView = (TextView) findViewById(R.id.answer_text_view);

        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(EXTRA_IS_CHEATED, false)) {
                cheat();
            }

        }

        if(mIsCheat ==true){
            cheat();
        }

        mShowAnswerButton = (Button) findViewById(R.id.show_answer_button);
        mShowAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cheat();
            }
        });
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
        savedInstanceState.putBoolean(EXTRA_IS_CHEATED, mIsCheat);
        savedInstanceState.putBoolean(EXTRA_ANSWER_IS_TRUE, mAnswerIsTrue);
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

    private void cheat() {
        mIsCheat = true;
        mAnswerTextView.setText(mAnswerIsTrue ? R.string.true_button : R.string.false_button);
        setAnswerShownResult(true);
    }
    private void setAnswerShownResult(boolean isAnswerShown) {
        Intent data = new Intent();
        data.putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown);
        setResult(RESULT_OK, data);
    }
}
