package com.acronym.pika;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.google.firebase.auth.FirebaseAuth;

public class WelcomeActivity extends BaseActivity {

    private LinearLayout mParent;
    private ScrollView mContent;
    private ImageView mLogoMain;

    private Handler handler = new Handler();

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            mParent.setGravity(Gravity.CENTER_HORIZONTAL);
            mLogoMain.setBottom(0);
            mLogoMain.setTop(getTop());
            mContent.setVisibility(View.VISIBLE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // find views by id's
        Button loginButton = findViewById(R.id.login_button);
        mParent = findViewById(R.id.welcome_main);
        mContent = findViewById(R.id.welcome_content);
        mLogoMain = findViewById(R.id.pika_logo);

        setTransparentStatusBar();

        handler.postDelayed(runnable, 5000);

        // Start Login onclick
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(), LoginActivity.class));
                //  finish();
            }
        });

        // Start Sign up onclick
        final Button signUpButton = findViewById(R.id.bt_signUp);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(), SignUpActivity.class));
                finish();
            }
        });

        findViewById(R.id.sign_up_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), CreateAccountActivity.class));
                finish();
            }
        });

        // Check if users is already signed in
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null && auth.getCurrentUser().getDisplayName() != null) {
            startActivity(new Intent(this, Main2Activity.class));
            finish();
        }

    }

    private int getTop() {
        return  0;
    }

}
