package com.bignerdranch.android.geoquiz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartingActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_GEO_QUIZ = 1;

    private Button startQuiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting);

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
}
