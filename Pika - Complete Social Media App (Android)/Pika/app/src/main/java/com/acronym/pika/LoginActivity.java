package com.acronym.pika;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.acronym.pika.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class LoginActivity extends BaseActivity {

    // Member Variables
    private EditText mEmailField;
    private EditText mPasswordField;

    // Firebase
    private DatabaseReference mDatabaseRef;
    private FirebaseAuth mAuth;

    private final String TAG = LoginActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        // Firebase Auth and Database
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        // Text input fields
        mEmailField = findViewById(R.id.et1_email);
        mPasswordField = findViewById(R.id.et1_password);

        // Switch to sign up onclick
        final Button signUpButton = findViewById(R.id.bt_signUp);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(), SignUpActivity.class));
            }
        });

        final Button forgotPasswordButton = findViewById(R.id.bt_forgot_password);
        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getBaseContext(), "In development", Toast.LENGTH_LONG).show();
            }
        });

        // Login
        final Button loginButton = findViewById(R.id.bt_login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logIn();
            }
        });
    }


    private void logIn() {
        signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
    }

    private void signIn(final String email, final String password) {
        if (!validateForm()) {
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "OnComplete: " + task.isSuccessful());

                if (task.isSuccessful()) {
                    onAuthSuccess(Objects.requireNonNull(task.getResult()).getUser());
                }
                else {
                    Snackbar.make(findViewById(R.id.layout_activity_login),
                            "Failed to fetch user", Snackbar.LENGTH_INDEFINITE).setAction("Login", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            signIn(email, password);
                        }
                    });
                }
            }
        });

    }

    private void onAuthSuccess(FirebaseUser currentUser) {
        writeNewUser(currentUser.getUid(), currentUser.getEmail());
        startActivity(new Intent(getBaseContext(), MainActivity.class));
        finish();
    }

    private void writeNewUser(String uid, String email) {
        User user = new User(email);
        mDatabaseRef.child("users").child(uid).setValue(user);
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Email Required");
            valid = false;

        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Password Required");
            valid = false;

        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }
}
