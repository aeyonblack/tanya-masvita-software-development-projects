package com.acronym.playit.helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.acronym.playit.R;
import com.acronym.playit.models.Quiz;

public class QuizCompletedViewHolder extends RecyclerView.ViewHolder {

    private TextView quizView;
    private TextView quizTagView;
    private TextView solutionView;
    private ImageView gotItView;

    QuizCompletedViewHolder(@NonNull View itemView) {
        super(itemView);
        quizView = itemView.findViewById(R.id.quiz_tv);
        quizTagView = itemView.findViewById(R.id.quiz_tag);
        solutionView = itemView.findViewById(R.id.solution_tv);
        gotItView = itemView.findViewById(R.id.got_it_iv);
    }

    @SuppressLint("SetTextI18n")
    void bindTo(Context context, Quiz quiz) {
        quizView.setText(quiz.getQuiz());
        quizTagView.setText(quiz.getType());
        solutionView.setText("Solution : " + quiz.getSolution());
        if (quiz.getGotIt() == 1) {
            gotItView.setBackground(context.getDrawable(R.drawable.correct_bg));
            gotItView.setImageResource(R.drawable.ic_check_black_24dp);
        }
        else if (quiz.getGotIt() == 0) {
            gotItView.setBackground(context.getDrawable(R.drawable.wrong_bg));
            gotItView.setImageResource(R.drawable.ic_clear_black_24dp);
        }
    }

}
