package com.bignerdranch.android.geoquiz;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class SumarryResultActivity extends AppCompatActivity {

    public final static String KEY_EXTRA_SCORE = "com.bignerdranch.android.geoquiz.total_score";
    public final static String KEY_EXTRA_ANSWERED = "com.bignerdranch.android.geoquiz.total_answered_question";
    public final static String KEY_EXTRA_CHEAT = "com.bignerdranch.android.geoquiz.total_cheat_attempts";
    public final static String KEY_EXTRA_MISS = "com.bignerdranch.android.geoquiz.total_miss_attempts";
    public final static String KEY_EXTRA_TIMED_USED = "com.bignerdranch.android.geoquiz.total_time_used";

    private int answered_question;
    private int score;
    private int cheat_attempts;
    private int miss_attempts;
    private String time_used;

    private TextView tv_questionAnswered;
    private TextView tv_totalScore;
    private TextView tv_totalCheat;
    private TextView tv_totalMiss;
    private TextView tv_timeUsed;

    public static Intent newIntent(Context packageContext, int answered, int score, int cheat, int miss, String timeUsed) {
        Intent intent = new Intent(packageContext, SumarryResultActivity.class);
        intent.putExtra(KEY_EXTRA_ANSWERED, answered);
        intent.putExtra(KEY_EXTRA_SCORE, score);
        intent.putExtra(KEY_EXTRA_CHEAT, cheat);
        intent.putExtra(KEY_EXTRA_MISS, miss);
        intent.putExtra(KEY_EXTRA_TIMED_USED,timeUsed);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sumarry_result);

        if (savedInstanceState != null) {
            answered_question = getIntent().getIntExtra(KEY_EXTRA_ANSWERED, 0);
            score = getIntent().getIntExtra(KEY_EXTRA_SCORE, 0);
            cheat_attempts = getIntent().getIntExtra(KEY_EXTRA_CHEAT, 0);
            miss_attempts = getIntent().getIntExtra(KEY_EXTRA_MISS, 0);
            time_used = getIntent().getStringExtra(KEY_EXTRA_TIMED_USED);
        }

        answered_question = getIntent().getIntExtra(KEY_EXTRA_ANSWERED, 0);
        score = getIntent().getIntExtra(KEY_EXTRA_SCORE, 0);
        cheat_attempts = getIntent().getIntExtra(KEY_EXTRA_CHEAT, 0);
        miss_attempts = getIntent().getIntExtra(KEY_EXTRA_MISS, 0);
        time_used = getIntent().getStringExtra(KEY_EXTRA_TIMED_USED);

        tv_questionAnswered = (TextView) findViewById(R.id.summary_total_answered);
        tv_totalScore = (TextView) findViewById(R.id.summary_total_score);
        tv_totalCheat = (TextView) findViewById(R.id.summary_total_cheat);
        tv_totalMiss = (TextView) findViewById(R.id.summary_total_miss);
        tv_timeUsed = (TextView)findViewById(R.id.summary_time_used);

        tv_questionAnswered.setText(String.valueOf(answered_question));
        tv_totalScore.setText(String.valueOf(score));
        tv_totalCheat.setText(String.valueOf(cheat_attempts));
        tv_totalMiss.setText(String.valueOf(miss_attempts));
        tv_timeUsed.setText(time_used);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt(KEY_EXTRA_ANSWERED, answered_question);
        savedInstanceState.putInt(KEY_EXTRA_SCORE, score);
        savedInstanceState.putInt(KEY_EXTRA_CHEAT, cheat_attempts);
        savedInstanceState.putInt(KEY_EXTRA_MISS, miss_attempts);
        savedInstanceState.putString(KEY_EXTRA_TIMED_USED, time_used);
    }
}
