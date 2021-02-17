package com.acronym.playit.helpers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.acronym.playit.R;
import com.acronym.playit.models.Quiz;

public class CompletedQuizAdapter extends RecyclerView.Adapter<QuizCompletedViewHolder> {

    private Context mContext;
    private QuizOpenHelper mDatabase;
    public CompletedQuizAdapter(Context context, QuizOpenHelper db) {
        this.mContext = context;
        mDatabase = db;
    }

    @NonNull
    @Override
    public QuizCompletedViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        return new QuizCompletedViewHolder(inflater.inflate(R.layout.quiz_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull QuizCompletedViewHolder quizCompletedViewHolder, int i) {
        Quiz quiz = mDatabase.queryCompleted(i);
        quizCompletedViewHolder.bindTo(mContext, quiz);
    }

    @Override
    public int getItemCount() {
        return mDatabase.completedCount();
    }

}
