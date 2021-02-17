package com.acronym.playit;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.acronym.playit.fragments.ChallengeFragment;
import com.acronym.playit.fragments.HomeFragment;
import com.acronym.playit.fragments.MenuFragment;

public class Main2Activity extends AppCompatActivity {


    private Toolbar mToolbar;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    openFragment(new HomeFragment());
                    mToolbar.setTitle("Home");
                    return true;
                case R.id.navigation_explore:
                    openFragment(new ChallengeFragment());
                    mToolbar.setTitle("Explore");
                    return true;
                case R.id.navigation_menu:
                    openFragment(new MenuFragment());
                    mToolbar.setTitle("Menu");
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        openFragment(new HomeFragment());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_score) {
            startActivity(new Intent(this, StatsActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void openFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

}
