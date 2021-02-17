package com.acronym.playit;

import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.acronym.playit.fragments.ChallengeFragment;
import com.acronym.playit.fragments.HomeFragment;
import com.acronym.playit.fragments.MenuFragment;
import com.acronym.playit.fragments.PracticeFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        ViewPager pager = findViewById(R.id.pager);
        TabLayout tabLayout = findViewById(R.id.tabLayout_main);

        FragmentStatePagerAdapter adapter = new FragmentStatePagerAdapter
                (getSupportFragmentManager()) {

            Fragment[] fragments = new Fragment[] {
                    new HomeFragment(),
                    new ChallengeFragment(),
                    new PracticeFragment(),
                    new MenuFragment()
            };

            String[] titles =new String[] {
                    "Home",
                    "Play",
                    "Review",
                    "Menu"
            };

            @Override
            public Fragment getItem(int i) {
                return fragments[i];
            }

            @Override
            public int getCount() {
                return fragments.length;
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return titles[position];
            }
        };
        pager.setAdapter(adapter);
        tabLayout.setSmoothScrollingEnabled(true);
        tabLayout.setupWithViewPager(pager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_score:
                /*TODO: VIEW SCORE*/
                break;
            case R.id.action_settings:
                break;
            case R.id.action_clear:
                /*TODO: CLEAR ALL DATA*/
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
