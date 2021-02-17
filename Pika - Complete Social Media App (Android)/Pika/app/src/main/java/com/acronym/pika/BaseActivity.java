package com.acronym.pika;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class BaseActivity extends AppCompatActivity {

    public final int GALLERY_REQUEST = 210;

    /*Make the status bar transparent*/
    public void setTransparentStatusBar() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    /*Change the status and navigation bar to a specified color */
    public void setStatusBarColor(int color) {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.setStatusBarColor(color);
        window.setNavigationBarColor(color);
    }

    /*Change the status bar color only*/
    public void setWindowColor(int color) {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.setStatusBarColor(color);
    }


    /*Open gallery*/
    public void openGallery() {
        try {
            // Get permissions
            if (ActivityCompat
                    .checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, GALLERY_REQUEST);

            }
            // If permissions granted start activity
            else {
                startActivityForResult(new Intent()
                        .setAction(Intent.ACTION_GET_CONTENT)
                        .setType("image/*"), GALLERY_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /*get current firebase user*/
    public FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    /*Get user id*/
    public String getUid() {
        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }

    /*Toast message summarized*/
    public void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /*Longer toast*/
    public void longToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }


    /*Check internet connection*/
    public boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        assert manager != null;
        NetworkInfo info = manager.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }
}
