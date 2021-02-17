package com.acronym.pika;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.acronym.pika.fragments.FeedFragment;
import com.acronym.pika.fragments.PeopleFragment;
import com.acronym.pika.fragments.ProfileFragment;
import com.acronym.pika.fragments.SearchFragment;
import com.acronym.pika.fragments.TradeFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Objects;

public class MainActivity extends BaseActivity implements View.OnClickListener {


    /*Member variables*/
    private FloatingActionButton fabNewPost;
    private FloatingActionButton fabEditProfile;
    private NestedScrollView mNestedScroll;
    private BottomNavigationView mBottomNav;

    private FusedLocationProviderClient mClient;

    private final int LOCATION_REQUEST = 1024;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Objects.requireNonNull(getSupportActionBar()).setTitle("Home");
                    openFragment(new FeedFragment());
                    hideNewPostItem(false);
                    return true;
                case R.id.navigation_search:
                    Objects.requireNonNull(getSupportActionBar()).setTitle("Explore");
                    openFragment(new SearchFragment());
                    hideNewPostItem(true);
                    return true;
                case R.id.navigation_profile:
                    Objects.requireNonNull(getSupportActionBar()).setTitle("Profile");
                    openFragment(new ProfileFragment());
                    hideNewPostItem(true);
                    return true;
                case R.id.navigation_people:
                    Objects.requireNonNull(getSupportActionBar()).setTitle("People");
                    openFragment(new PeopleFragment());
                    hideNewPostItem(true);
                    return true;
                case R.id.navigation_menu:
                    Objects.requireNonNull(getSupportActionBar()).setTitle("Trade");
                    openFragment(new TradeFragment());
                    hideNewPostItem(true);
                    return true;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (getCurrentUser() == null) {
            startActivity(new Intent(this, WelcomeActivity.class));
            finish();
        }

        /*Set the toolbar*/
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);


        /*Open Feed Fragment*/
        openFragment(new FeedFragment());

        fabEditProfile = findViewById(R.id.editProfileFab);
        fabNewPost = findViewById(R.id.new_post_fab);
        mNestedScroll = findViewById(R.id.nested_scrollView);
        findViewById(R.id.new_post_fab).setOnClickListener(this);
        fabEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,
                        EditProfileActivity.class));
            }
        });


        // Bottom navigation
        BottomNavigationView navigation = findViewById(R.id.navigation);
        mBottomNav = navigation;
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        listenForScroll();

        mClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST);
            return;
        }
        mClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    /*Toast that I have found your location*/
                    toast("Managed to retrieve your location");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                    toast("Failed with exception:" + e.toString());
            }
        });
    }


    void openFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.temp_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nav_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new_post:
                startActivity(new Intent(this, NewPost2Activity.class));
                break;
            case R.id.action_camera:
                startActivity(new Intent(this, FullscreenActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.new_post_fab:

                startActivity(new Intent(this, NewPost2Activity.class));
                break;
            case R.id.editProfileFab:
                Intent intent = new Intent(this, EditProfileActivity.class);
                startActivity(intent);
                break;
        }
    }



    @SuppressLint("NewApi")
    void listenForScroll() {
        mNestedScroll.setOnScrollChangeListener
                (new View.OnScrollChangeListener() {
                    @Override
                    public void onScrollChange(View v, int scrollX, int scrollY,
                                               int oldScrollX, int oldScrollY) {
                        if (scrollY > oldScrollY) {
                            fabEditProfile.hide();
                            fabNewPost.hide();
                        }
                        else {
                            if (mBottomNav.getSelectedItemId() == R.id.navigation_home) {
                                fabNewPost.show();
                                if (fabEditProfile.isShown())
                                    fabEditProfile.hide();
                            }
                            else if (mBottomNav.getSelectedItemId() == R.id.navigation_profile) {
                                fabEditProfile.show();
                                if (fabNewPost.isShown())
                                    fabNewPost.hide();
                            }
                            else {
                                fabNewPost.hide();
                                fabEditProfile.hide();
                            }
                        }
                    }
                });
    }

    private void hideNewPostItem(boolean hide) {
        if (hide) {
            findViewById(R.id.action_new_post).setVisibility(View.GONE);
        }
        else {
            findViewById(R.id.action_new_post).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST) {
            if (!(grantResults.length > 0) && !(grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                toast("Can't access location");
            }
        }
    }
}
