package com.acronym.pika;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.acronym.pika.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class CreateAccountActivity extends BaseActivity implements View.OnClickListener {

    /*Database Reference*/
    private DatabaseReference mDatabaseRef;
    private FirebaseAuth mAuth;

    /*TextFields*/
    private EditText mNameField;
    private EditText mEmailField;
    private EditText mPasswordField;
    private TextView mTerms;

    /*layouts*/
    private RelativeLayout mNameLayout;
    private RelativeLayout mEmailPasswordLayout;
    private RelativeLayout mVerifyEmailLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        mNameField = findViewById(R.id.field_username);
        mEmailField = findViewById(R.id.field_email);
        mPasswordField = findViewById(R.id.field_password);
        mNameLayout = findViewById(R.id.username_layout);
        mEmailPasswordLayout = findViewById(R.id.email_password_layout);
        mVerifyEmailLayout = findViewById(R.id.verify_email_layout);
        mTerms = findViewById(R.id.terms_tv);

        /*Onclick listeners*/
        findViewById(R.id.update_name_bt).setOnClickListener(this);
        findViewById(R.id.sign_up_bt).setOnClickListener(this);
        findViewById(R.id.verify_email).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.update_name_bt:
                updateUsername();
                break;
            case R.id.sign_up_bt:
                signUp();
                break;
            case R.id.verify_email:
                verifyEmail();
                break;
        }
    }

    private void showNameLayout() {
        mNameLayout.setVisibility(View.VISIBLE);
        mEmailPasswordLayout.setVisibility(View.GONE);
    }

    private void showVerifyEmailLayout() {
        mNameLayout.setVisibility(View.GONE);
        mVerifyEmailLayout.setVisibility(View.VISIBLE);
        mTerms.setText(getResources().getString(R.string.verify_email_terms));
    }

    private void updateUsername() {
        if (!isValidName()) {
            return;
        }

        findViewById(R.id.pb_update_name).setVisibility(View.VISIBLE);
        findViewById(R.id.update_name_tv).setVisibility(View.GONE);
        toast(getResources().getString(R.string.updating));

        FirebaseUser currentUser = getCurrentUser();
        final String name = mNameField.getText().toString();
        UserProfileChangeRequest changeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();
        currentUser.updateProfile(changeRequest)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    findViewById(R.id.pb_update_name).setVisibility(View.GONE);
                                    showVerifyEmailLayout();
                                    mDatabaseRef.child("users").child(getUid()).child("name").setValue(name);
                                }
                            }, 3000);

                        }
                        else {
                            findViewById(R.id.progress_v1).setVisibility(View.GONE);
                            toast("Something went wrong");
                        }
                    }
                });
    }

    private boolean isValidName() {
        boolean valid = true;
        String name = mNameField.getText().toString();
        if (TextUtils.isEmpty(name)) {
            mNameField.setError("Required");
            valid = false;
        }
        else {
            mNameField.setError(null);
        }
        return valid;
    }

    private boolean isValidForm() {
        boolean valid = true;
        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Email Required");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        // Check the password
        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Password Required");
            valid = false;
        } else {
            mPasswordField.setError(null);
            if (password.length() < 8) {
                mPasswordField.setError("Password too Short");
                valid = false;
            } else {
                mPasswordField.setError(null);
            }
        }
        return valid;

    }

    private void signUp() {
        if (!isValidForm()) {
            return;
        }

        findViewById(R.id.pb_sign_up).setVisibility(View.VISIBLE);
        findViewById(R.id.sign_up).setVisibility(View.GONE);
        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful() && task.getResult() != null) {
                            onAuthSuccess(task.getResult().getUser());
                            showNameLayout();
                        }
                        else {
                            retry();
                        }
                    }
                });
    }

    private void onAuthSuccess(FirebaseUser user) {
        writeNewUser(user.getUid(), user.getEmail());
    }

    private void writeNewUser(String uid, String email) {
        User user = new User(email);
        int age = 0;
        String relationship = "Specify your relationship status";
        String mood = "Add a mood to look cool";
        String status = "Public";
        String location = "Hatfield, Pretoria";
        String bio = "Write a little about yourself";
        user.uid = uid;
        user.age = age;
        user.relationship = relationship;
        user.mood = mood;
        user.status = status;
        user.location = location;
        user.bio = bio;
        user.following.put(uid, true);
        Map<String, Object> userValues = user.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/users/" + uid, userValues);
        mDatabaseRef.updateChildren(childUpdates);
    }

    private void retry() {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.layout_signUp),
                "Something happened", Snackbar.LENGTH_INDEFINITE)
                .setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        signUp();
                    }
                });
        snackbar.show();
    }

    private void verifyEmail() {
        findViewById(R.id.pb_verify_email).setVisibility(View.VISIBLE);
        findViewById(R.id.verify_email_tv).setVisibility(View.GONE);
        findViewById(R.id.sending_verification).setVisibility(View.VISIBLE);
        final FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Snackbar.make(findViewById(R.id.layout_signUp),
                            "Email Verification sent to "
                                    + user.getEmail(), Snackbar.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            onSuccess();
                        }
                    }, 1000);
                }
                else {
                    resend();
                }
            }
        });
    }

    private void resend() {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.layout_signUp),
                "Email Verification not sent", Snackbar.LENGTH_INDEFINITE)
                .setAction("Resend", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        verifyEmail();
                    }
                });
        snackbar.show();
    }

    private void onSuccess() {
        startActivity(new Intent(this, Main2Activity.class));
        finish();
    }


}
