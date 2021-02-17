package com.acronym.playit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.acronym.playit.helpers.QuizAsyncTask;
import com.acronym.playit.helpers.QuizOpenHelper;

import java.util.Objects;

public class QuizActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String QUIZ_TYPE_EXTRA = "quiz_type_extra";

    /*Member variables*/
    private LinearLayout mLayoutMain;
    private EditText mNumericalAnswerField;
    private TextView mQuizView;
    private TextView mProgressUpdateTextView;
    private Button mNextQuizButton;
    private TextView mScoreView;
    private TextView mResultText;
    private CardView mResultView;

    private String mQuizType;
    /*Database*/
    private QuizOpenHelper mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        /*Toolbar*/
        Toolbar toolbar = findViewById(R.id.quiz_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        /*Database*/
        mDatabase = Playit.getDb();

        Intent intent = getIntent();
        mQuizType = intent.getStringExtra(QUIZ_TYPE_EXTRA);

        /*View Components*/
        mLayoutMain = findViewById(R.id.start_quiz_layout);
        Button mStartQuizButton = findViewById(R.id.start_quiz_button);
        mNumericalAnswerField = findViewById(R.id.numerical_answer_field);
        mNextQuizButton = findViewById(R.id.submit_numerical_answer);
        mScoreView = findViewById(R.id.score_view);
        mQuizView = findViewById(R.id.numerical_question_view);
        mResultText = findViewById(R.id.result_text);
        mResultView = findViewById(R.id.result_view);
        mProgressUpdateTextView = findViewById(R.id.progress_update);
        mStartQuizButton.setOnClickListener(this);
        mNextQuizButton.setOnClickListener(this);

        mResultView.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.start_quiz_button) {
            startQuiz();
        }
    }


    private void startQuiz() {
        mLayoutMain.setVisibility(View.INVISIBLE);
        findViewById(R.id.numerical_quiz_layout).setVisibility(View.VISIBLE);
        QuizAsyncTask task = new QuizAsyncTask(this, mDatabase, mQuizType,
                mQuizView, mNumericalAnswerField, mProgressUpdateTextView,
                mNextQuizButton, mScoreView, mResultText, mResultView);
        task.execute();
    }

}
