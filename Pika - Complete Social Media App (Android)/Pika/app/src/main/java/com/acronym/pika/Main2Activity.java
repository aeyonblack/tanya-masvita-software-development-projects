package com.acronym.pika;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.acronym.pika.fragments.CameraFragment;
import com.acronym.pika.fragments.FeedFragment;
import com.acronym.pika.fragments.MenuFragment;
import com.acronym.pika.fragments.PeopleFragment;
import com.acronym.pika.fragments.ProfileFragment;
import com.acronym.pika.fragments.SearchFragment;
import com.acronym.pika.utils.ColorUtils;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class Main2Activity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


        if (getCurrentUser() == null) {
            startActivity(new Intent(this,WelcomeActivity.class));
            finish();
        }

        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        final Toolbar toolbar = findViewById(R.id.toolbar_main2);
        setSupportActionBar(toolbar);

        FragmentStatePagerAdapter adapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            private Fragment[] fragments = new Fragment[] {
                    new FeedFragment(),
                    new SearchFragment(),
                    new ProfileFragment(),
                    new CameraFragment(),
                    new PeopleFragment(),
                    new MenuFragment()
            };

            @Override
            public Fragment getItem(int i) {
                return fragments[i];
            }

            @Override
            public int getCount() {
                return fragments.length;
            }
        };

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.chevron));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_search_outline));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.another_person));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_cam_web));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_people_outline_black_24dp));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_menu));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setSmoothScrollingEnabled(true);

        tabLayout.setSelected(true);

        Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.chevron_fill);

        // [START] Filter tab icons with different colors
        for (int i = 0; i < adapter.getCount(); i++) {
            if (i == 5) {
                Objects.requireNonNull(Objects.requireNonNull(tabLayout.getTabAt(5))
                        .getIcon()).setColorFilter(ColorUtils.getColor
                        (this, R.color.colorLightGray), PorterDuff.Mode.SRC_IN);
                break;
            }
            if (i != 3) {
                if (i == 2) {
                    continue;
                }
                Objects.requireNonNull(Objects.requireNonNull(tabLayout.getTabAt(i)).getIcon())
                        .setColorFilter(ColorUtils.getColor
                                (this, R.color.colorBlack), PorterDuff.Mode.SRC_IN);
            }
        }
        // [END --- ]

        final ViewPager pager = findViewById(R.id.viewpager);
        pager.setAdapter(adapter);
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager.setCurrentItem(tab.getPosition());
                /*Change toolbar title*/
                switch (tab.getPosition()) {
                    case 0:
                        toolbar.setTitle("Home");
                        tab.setIcon(R.drawable.chevron_fill);
                        Objects.requireNonNull(tab.getIcon())
                                .setColorFilter(ColorUtils.getColor(Main2Activity.this
                                        , R.color.colorDarkBlue), PorterDuff.Mode.SRC_IN);
                        break;
                    case 1:
                        toolbar.setTitle("Explore");
                        tab.setIcon(R.drawable.ic_search_fill);
                        Objects.requireNonNull(tab.getIcon())
                                .setColorFilter(ColorUtils.getColor(Main2Activity.this
                                        , R.color.colorDarkBlue), PorterDuff.Mode.SRC_IN);
                        break;
                    case 2:
                        toolbar.setTitle("Profile");
                        Objects.requireNonNull(tab.getIcon())
                                .setColorFilter(ColorUtils.getColor(Main2Activity.this
                                        , R.color.colorDarkBlue), PorterDuff.Mode.SRC_IN);
                        break;
                    case 3:
                        toolbar.setTitle("Snap");
                        tab.setIcon(R.drawable.ic_cam_web);
                        Objects.requireNonNull(tab.getIcon())
                                .setColorFilter(ColorUtils.getColor(Main2Activity.this
                                        , R.color.colorDarkBlue), PorterDuff.Mode.SRC_IN);
                        break;
                    case 4:
                        toolbar.setTitle("Find People");
                        tab.setIcon(R.drawable.ic_people_black_24dp);
                        Objects.requireNonNull(tab.getIcon())
                                .setColorFilter(ColorUtils.getColor(Main2Activity.this
                                , R.color.colorDarkBlue), PorterDuff.Mode.SRC_IN);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Objects.requireNonNull(tab.getIcon()).setColorFilter(ColorUtils
                        .getColor(Main2Activity.this,
                        R.color.colorLightGray), PorterDuff.Mode.SRC_IN);
                switch (tab.getPosition()) {
                    case 0:
                        tab.setIcon(R.drawable.chevron);
                        tab.getIcon().setColorFilter(ColorUtils.getColor(Main2Activity.this, R.color.colorBlack),
                                PorterDuff.Mode.SRC_IN);
                        break;
                    case 1:
                        tab.setIcon(R.drawable.ic_search_outline);
                        tab.getIcon().setColorFilter(ColorUtils.getColor(Main2Activity.this, R.color.colorBlack),
                                PorterDuff.Mode.SRC_IN);
                        break;
                    case 2:
                        tab.setIcon(R.drawable.another_person);
                        /*tab.getIcon().setColorFilter(ColorUtils.getColor(Main2Activity.this, R.color.colorBlack),
                                PorterDuff.Mode.SRC_IN);*/
                        break;
                    case 3:
                        tab.setIcon(R.drawable.ic_cam_web);
                        /*tab.getIcon().setColorFilter(ColorUtils.getColor(Main2Activity.this, R.color.colorBlack),
                                PorterDuff.Mode.SRC_IN);*/
                        break;
                    case 4:
                        tab.setIcon(R.drawable.ic_people_outline_black_24dp);
                        tab.getIcon().setColorFilter(ColorUtils
                                .getColor(Main2Activity.this,R.color.colorBlack),
                                PorterDuff.Mode.SRC_IN);
                        break;
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nav_drawer, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_new_post) {
            startActivity(new Intent(this, NewPost2Activity.class));
        }
        else if (item.getItemId() == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, WelcomeActivity.class));
        }
        else if (item.getItemId() == R.id.action_camera) {
            // TODO: Create the complete camera
            toast("In Progress");
        }
        return super.onOptionsItemSelected(item);
    }

}
