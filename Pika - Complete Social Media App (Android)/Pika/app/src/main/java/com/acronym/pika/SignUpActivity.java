package com.acronym.pika;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

public class SignUpActivity extends BaseActivity implements View.OnClickListener {

    // Member Variables
    private Button mSignUpButton;
    private ProgressBar mSignUpProgress;

    // Text Fields
    private EditText mEmailField;
    private EditText mPassWordField;

    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseRef;

    /*New Variables*/
    private TextView mContinueV1;
    private TextView mContinueV2;
    private TextView mContinueV3;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private EditText mNameEditText;
    private EditText mPhoneEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        //FirebaseAuth and Database Reference
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        // Find views by id
        mSignUpButton = findViewById(R.id.su_signUpButton);
        mSignUpProgress = findViewById(R.id.signUp_progressbar);
        mEmailField = findViewById(R.id.et2_email);
        mPassWordField = findViewById(R.id.et2_password);

        /*New Variables*/
        mContinueV1 = findViewById(R.id.continue_bt_v1);
        mContinueV2 = findViewById(R.id.continue_bt_v2);
        mContinueV3 = findViewById(R.id.continue_bt_v3);
        mEmailEditText = findViewById(R.id.field_email);
        mPasswordEditText = findViewById(R.id.field_password);
        mNameEditText = findViewById(R.id.field_username);
        mPhoneEditText = findViewById(R.id.field_phone_number);

        // Set up onclick listeners
        mSignUpButton.setOnClickListener(this);
        findViewById(R.id.bt_verify_email).setOnClickListener(this);
        setStatusBarColor(ContextCompat.getColor(this, R.color.colorWhite));

        mContinueV1.setOnClickListener(this);

        mContinueV2.setOnClickListener(this);
        findViewById(R.id.verify_bt).setOnClickListener(this);
    }

    private void updateUsername() {
        findViewById(R.id.progress_v1).setVisibility(View.VISIBLE);
        mContinueV1.setText(null);
        FirebaseUser currentUser = getCurrentUser();
        toast(getResources().getString(R.string.updating));
        final String name = mNameEditText.getText().toString();
        UserProfileChangeRequest changeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();
        currentUser.updateProfile(changeRequest)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        // Load photo into image view for now
                        if (task.isSuccessful()) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    findViewById(R.id.progress_v1).setVisibility(View.GONE);
                                    showEmailPassword();
                                    mDatabaseRef.child(getUid()).child("name").setValue(name);
                                    Toast.makeText(getBaseContext(), "Completed Successfully",
                                            Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }, 3000);



                        }
                        else {
                            mContinueV1.setText("Continue");
                            findViewById(R.id.progress_v1).setVisibility(View.GONE);
                            toast("Something went wrong");
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.su_signUpButton:
                signUp();
                break;
            case R.id.bt_verify_email:
                verifyEmail();
                break;
            case R.id.continue_bt_v1:
                updateUsername();
                break;
            case R.id.continue_bt_v2:
                signUp();
                break;
            case R.id.verify_bt:
                verifyEmail();
                break;
        }
    }

    private void showEmailPassword() {
        findViewById(R.id.username_layout).setVisibility(View.GONE);
        findViewById(R.id.email_password_layout).setVisibility(View.VISIBLE);
    }

    private void showPhone() {
        findViewById(R.id.email_password_layout).setVisibility(View.GONE);
        findViewById(R.id.phone_layout).setVisibility(View.VISIBLE);
    }

    private void signUp() {
        if (!validateForm()) {
            return;
        }

        findViewById(R.id.progress_v2).setVisibility(View.VISIBLE);
        String email = mEmailEditText.getText().toString();
        String password = mEmailEditText.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        findViewById(R.id.progress_v2).setVisibility(View.GONE);

                        if (task.isSuccessful() && task.getResult() != null) {
                            onAuthSuccess(task.getResult().getUser());
                            showVerifyEmailLayout();
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

    private void showVerifyEmailLayout() {
        findViewById(R.id.verify_email_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.email_password_layout).setVisibility(View.GONE);
        TextView terms = findViewById(R.id.terms_tv);
        terms.setText("Verify your email address to continue. Unverified accounts will be shut down in 48 hours. This is done for security reasons and safety of user data.");
    }

    private void verifyEmail() {
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
                    }, 500);
                }
                else {
                    resend();
                }
            }
        });
    }

    private void onSuccess() {
        startActivity(new Intent(this, Main2Activity.class));
        finish();
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

    private void setEditingEnabled(boolean enabled) {
        mEmailField.setEnabled(enabled);
        mPassWordField.setEnabled(enabled);
        if (enabled) {
            mSignUpButton.setVisibility(View.VISIBLE);
        }
        else {
            mSignUpButton.setVisibility(View.GONE);
        }
    }

    private boolean validateForm() {
        boolean valid  = true;

        // Check the email
        String email = mEmailEditText.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailEditText.setError("Email Required");
            valid = false;
        }
        else {
            mEmailEditText.setError(null);
        }

        // Check the password
        String password = mPassWordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordEditText.setError("Password Required");
            valid = false;
        }
        else
        {
            mPasswordEditText.setError(null);
            if (password.length() < 8) {
                mPasswordEditText.setError("Password too Short");
                valid = false;
            }
            else {
                mPasswordEditText.setError(null);
            }
        }
        return valid;
    }
}
