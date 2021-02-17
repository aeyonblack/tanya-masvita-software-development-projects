package com.acronym.playit;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.acronym.playit.helpers.BadgesAdapter;
import com.acronym.playit.helpers.QuizOpenHelper;
import com.acronym.playit.models.User;

import java.util.Objects;

public class StatsActivity extends AppCompatActivity {

    // PRIMARY DATABASE
    private QuizOpenHelper mDatabase;

    private TextView mTotalScoreView;
    private TextView mCurrentBestView;
    private TextView mLevelView;
    private TextView mQuizzesCompleted;
    private TextView mBestWeapon;
    private TextView mBadgesCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);


        // [START - Initialize Database]
        mDatabase = Playit.getDb();
        // [END - Initialize Database]


        // Setup the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_stats);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // [START- Initialize all views on the screen]
        mTotalScoreView = findViewById(R.id.total_score_stats);
        mCurrentBestView = findViewById(R.id.current_best_stats);
        mLevelView = findViewById(R.id.level_tv_stats);
        mQuizzesCompleted = findViewById(R.id.quizzes_completed_stats);
        mBestWeapon = findViewById(R.id.best_weapon_stats);
        mBadgesCount = findViewById(R.id.total_badges_stats);
        // [END]


        // Setup the RecyclerView and necessary adapters to hold the data
        RecyclerView mBadgesList = findViewById(R.id.badges_list);
        mBadgesList.setHasFixedSize(true);
        BadgesAdapter adapter = new BadgesAdapter(mDatabase);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setStackFromEnd(true);
        manager.setReverseLayout(true);
        mBadgesList.setLayoutManager(manager);
        mBadgesList.setAdapter(adapter);

        // Update the stats, populate the views with all scores and achievements
        setUpProgress();

    }

    /**
     * The method basically gets data from the database to
     * display as stats
     */
    @SuppressLint("SetTextI18n")
    private void setUpProgress() {
        User user = mDatabase.progressQuery(0);
        mTotalScoreView.setText(String.valueOf(user.getTotalScore()) + "xp");
        mCurrentBestView.setText(String.valueOf(user.getCurrentBest()) + "xp");
        mLevelView.setText(String.valueOf(user.getLevel()));
        mQuizzesCompleted.setText(String.valueOf(user.getTotalCompleted()));
        mBestWeapon.setText(user.getBestWeapon());
        String text = user.getBadgeCount() == 1 ? " badge" : " badges";
        mBadgesCount.setText(String.valueOf(user.getBadgeCount()) + text);
    }

}
