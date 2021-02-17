package com.acronym.playit.fragments;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.acronym.playit.Playit;
import com.acronym.playit.R;
import com.acronym.playit.helpers.CompletedQuizAdapter;
import com.acronym.playit.helpers.QuizOpenHelper;
import com.acronym.playit.models.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    private TextView mTotalScoreView;
    private TextView mQuizzesCompletedView;
    private TextView mLevelView;
    private TextView mCurrentBestView;
    private TextView mBestWeaponView;
    private RecyclerView mRecyclerView;

    private QuizOpenHelper mDatabase;

    public HomeFragment() {
        // Required empty public constructor
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        mTotalScoreView = rootView.findViewById(R.id.total_score);
        mQuizzesCompletedView = rootView.findViewById(R.id.quizzes_completed);
        mLevelView = rootView.findViewById(R.id.level_tv);
        mCurrentBestView = rootView.findViewById(R.id.current_best);
        mBestWeaponView = rootView.findViewById(R.id.best_weapon);
        mRecyclerView = rootView.findViewById(R.id.activity_list);
        mDatabase = Playit.getDb();

        return rootView;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        User user = mDatabase.progressQuery(0);
        mTotalScoreView.setText(String.valueOf(user.getTotalScore()) + "xp");
        mQuizzesCompletedView.setText(String.valueOf(user.getTotalCompleted()));
        mLevelView.setText(String.valueOf(user.getLevel()));
        mCurrentBestView.setText(String.valueOf(user.getCurrentBest()) + "xp");
        mBestWeaponView.setText(String.valueOf(user.getBestWeapon()));

        CompletedQuizAdapter adapter = new CompletedQuizAdapter(getActivity(), mDatabase);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setStackFromEnd(true);
        manager.setReverseLayout(true);
        manager.setSmoothScrollbarEnabled(true);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(adapter);
    }


}
