package com.acronym.playit.helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.acronym.playit.R;
import com.acronym.playit.models.Quiz;
import com.acronym.playit.models.User;

import java.util.ArrayList;

@SuppressWarnings("FieldCanBeLocal")
@SuppressLint("StaticFieldLeak")
public class QuizAsyncTask extends AsyncTask<Void, Integer, ArrayList<Quiz>> {

    private QuizOpenHelper mDatabase;
    private String quiz_type;
    private Context mContext;
    private TextView mQuizView;
    private EditText mSolutionField;
    private TextView mProgressUpdateView;
    private Button mNextQuizButton;
    private TextView mScoreView;
    private TextView mResultText;
    private CardView mResultView;

    private ArrayList<Integer> mCompleted = new ArrayList<>();
    private ArrayList<Quiz> mQuizzes;
    private final long DURATION_MILLIS = 4000;
    private final long ONE_SECOND = 1000;
    private int mCurrentScore = 0;
    private User mUser;


    public QuizAsyncTask(Context context, QuizOpenHelper database, String quiz_type,
                         TextView quizView, EditText solution, TextView progressUpdateView,
                         Button button, TextView scoreView,
                         TextView resultText, CardView resultView) {
        this.mDatabase = database;
        this.quiz_type = quiz_type;
        this.mContext = context;
        this.mQuizView = quizView;
        this.mScoreView = scoreView;
        this.mSolutionField = solution;
        this.mProgressUpdateView = progressUpdateView;
        this.mNextQuizButton = button;
        this.mResultText = resultText;
        this.mResultView = resultView;

        mNextQuizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCompleted.size() == 5) {
                    playAgain();
                }
                else {
                    playGame(mQuizzes);
                }
            }
        });
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected ArrayList<Quiz> doInBackground(Void... voids) {
        ArrayList<Quiz> quizzes = new ArrayList<>();
        queryQuizzes(quizzes, quiz_type);
        queryUserProgress();
        return quizzes;
    }

    @Override
    protected void onPostExecute(ArrayList<Quiz> quizzes) {
        super.onPostExecute(quizzes);
        playGame(quizzes);
    }

    private void queryQuizzes(ArrayList<Quiz> quizzes, String quiz_type) {
        for (int i = 0; i < mDatabase.count(); i++) {
            Quiz quiz = mDatabase.query(i);
            if (quiz.getType().equals(quiz_type)) {
                quizzes.add(quiz);
            }
        }
        mQuizzes = quizzes;
    }

    private void queryUserProgress() {
        mUser = mDatabase.progressQuery(0);
    }


    private void playGame(final ArrayList<Quiz> quizzes) {

        int position = (int)(Math.random()*10);

        while (mCompleted.contains(position)) {
            position = (int)(Math.random()*10);
        }

        Quiz quiz = quizzes.get(position);
        nextQuiz(quiz);
        mCompleted.add(position);

        if (mCompleted.size() == 5) {
            mNextQuizButton.setText(mContext.getString(R.string.play_again));
            mDatabase.updateCurrentBest(mUser.getId(), mCurrentScore);
        }
        else {
            mNextQuizButton.setText(mContext.getString(R.string.next));
        }
    }


    private void nextQuiz(final Quiz currentQuiz) {
        final MediaPlayer correct = MediaPlayer.create(mContext, R.raw.correct_answer);
        final MediaPlayer wrong = MediaPlayer.create(mContext, R.raw.wrong);
        new CountDownTimer(DURATION_MILLIS*currentQuiz.getScore()+ ONE_SECOND,
                ONE_SECOND) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long millisUntilFinished) {
                if (mResultView.getVisibility() == View.VISIBLE) {
                    mResultView.setVisibility(View.GONE);
                }
                mNextQuizButton.setEnabled(false);
                mQuizView.setText(currentQuiz.getQuiz());
                mProgressUpdateView.setText("Time Remaining : " + millisUntilFinished/1000);
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFinish() {
                mResultView.setVisibility(View.VISIBLE);
                String answer = mSolutionField.getText().toString();
                if (answer.equals(currentQuiz.getSolution())) {
                    mCurrentScore += currentQuiz.getScore();
                    mResultText.setTextColor(ContextCompat.getColor(mContext, R.color.colorGreen));
                    correct.start();
                    mResultText.setText("Correct!");
                    mDatabase.insertCompletedQuiz(currentQuiz.getQuiz(),
                            currentQuiz.getSolution(), currentQuiz.getType(), 1);
                }
                else {
                    mResultText.setTextColor(ContextCompat.getColor(mContext, R.color.colorRed));
                    wrong.start();
                    mResultText.setText("Wrong!");
                    mDatabase.insertCompletedQuiz(currentQuiz.getQuiz(), currentQuiz.getSolution(),
                            currentQuiz.getType(), 0);
                }
                mNextQuizButton.setEnabled(true);
                mSolutionField.setText(null);
                mScoreView.setText(String.valueOf(mCurrentScore) + "xp");
                mDatabase.updateTotalScore(mUser.getId(), currentQuiz.getScore());
                mDatabase.updateCurrentBest(mUser.getId(), mCurrentScore);
            }

        }.start();
    }

    @SuppressLint("SetTextI18n")
    private void playAgain() {
        mDatabase.updateCurrentBest(mUser.getId(), mCurrentScore);
        mCompleted.clear();
        mCurrentScore = 0;
        mScoreView.setText(String.valueOf(mCurrentScore) + "xp");
        playGame(mQuizzes);
    }


}
