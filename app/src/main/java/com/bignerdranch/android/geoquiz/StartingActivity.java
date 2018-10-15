package com.bignerdranch.android.geoquiz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class StartingActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_GEO_QUIZ = 1;

    public static final String SHARED_PREFS = "shared_prefs";
    public static final String KEY_HIGH_SCORE ="key_highScore";

    private Button startQuiz;
    private TextView tv_highScore;

    private int highScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting);

        tv_highScore = (TextView)findViewById(R.id.text_view_high_score);

        loadHighScore();

        startQuiz = (Button)findViewById(R.id.start_quiz_button);
        startQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start_quiz();
            }
        });
    }

    private void start_quiz(){
        Intent intent = new Intent(StartingActivity.this, MainActivity.class);
        startActivityForResult(intent, REQUEST_CODE_GEO_QUIZ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        if(requestCode == REQUEST_CODE_GEO_QUIZ){
            if(resultCode == RESULT_OK){
                int score = data.getIntExtra(MainActivity.EXTRA_SCORE,0);
                if(score > highScore){
                    updateHighScore(score);
                }
            }
        }
    }

    private void loadHighScore(){
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        highScore = prefs.getInt(KEY_HIGH_SCORE, 0);
        tv_highScore.setText(getString(R.string.high_score) + String.valueOf(highScore));
    }

    private void updateHighScore(int newHighScore){
        highScore = newHighScore;
        tv_highScore.setText(getString(R.string.high_score) + String.valueOf(highScore));

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_HIGH_SCORE, highScore);
        editor.apply();
    }
}
