package com.acronym.playit.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.acronym.playit.QuizActivity;
import com.acronym.playit.R;
import com.acronym.playit.utils.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChallengeFragment extends Fragment implements View.OnClickListener {


    /*Member Variables*/
    private TextView mMathBt;
    private TextView mLogicBt;
    private TextView mRiddleBt;
    private TextView mMysteryBt;
    private TextView mStoryBt;
    private TextView mProbabilityBt;
    private TextView mImpossibleBt;
    private TextView mScienceBt;
    private TextView mAnamorphicBt;
    private TextView mWhatAmIBt;

    public ChallengeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_challenge, container, false);
        mMathBt = rootView.findViewById(R.id.math_quiz);
        mAnamorphicBt = rootView.findViewById(R.id.anamorphic_quiz);
        mImpossibleBt = rootView.findViewById(R.id.impossible_quiz);
        mLogicBt = rootView.findViewById(R.id.logic_quiz);
        mScienceBt = rootView.findViewById(R.id.science_quiz);
        mWhatAmIBt = rootView.findViewById(R.id.what_am_i_quiz);
        mMysteryBt = rootView.findViewById(R.id.mystery_quiz);
        mStoryBt = rootView.findViewById(R.id.story_quiz);
        mRiddleBt = rootView.findViewById(R.id.riddle_quiz);
        mProbabilityBt = rootView.findViewById(R.id.probability_quiz);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMathBt.setOnClickListener(this);
        mAnamorphicBt.setOnClickListener(this);
        mLogicBt.setOnClickListener(this);
        mScienceBt.setOnClickListener(this);
        mImpossibleBt.setOnClickListener(this);
        mProbabilityBt.setOnClickListener(this);
        mWhatAmIBt.setOnClickListener(this);
        mStoryBt.setOnClickListener(this);
        mRiddleBt.setOnClickListener(this);
        mMysteryBt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.math_quiz:
                startActivity(Constants.MATH);
                break;
            case R.id.logic_quiz:
                startActivity(Constants.LOGIC);
                break;
            case R.id.anamorphic_quiz:
            case R.id.impossible_quiz:
            case R.id.probability_quiz:
            case R.id.science_quiz:
            case R.id.riddle_quiz:
            case R.id.story_quiz:
            case R.id.what_am_i_quiz:
            case R.id.mystery_quiz:
                Toast.makeText(getActivity(), "LOCKED", Toast.LENGTH_SHORT).show();
                break;
        }
    }


    private void startActivity(String value) {
        Intent intent = new Intent(getActivity(), QuizActivity.class);
        intent.putExtra(QuizActivity.QUIZ_TYPE_EXTRA, value);
        startActivity(intent);
    }

}
