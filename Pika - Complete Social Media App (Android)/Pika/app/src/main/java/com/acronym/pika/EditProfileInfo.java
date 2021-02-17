package com.acronym.pika;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.acronym.pika.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class EditProfileInfo extends BaseActivity {

    private EditText mAgeField;
    private EditText mMoodField;
    private TextView mRelationship;
    private RadioGroup mRadioGroup;
    private Button mChangeRelationship;
    private String currentAge;
    private String currentRelationship;
    private String currentMood;

    /*Database Reference*/
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_info);

        Toolbar toolbar = findViewById(R.id.edit_info_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        /*Initialize*/
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("users");
        mDatabaseReference.child(getUid()).addValueEventListener(listener);

        mAgeField = findViewById(R.id.age_edit_et);
        mMoodField = findViewById(R.id.mood_edit_et);
        mRadioGroup = findViewById(R.id.radio_group);
        mRelationship = findViewById(R.id.relation_tv);
        mChangeRelationship = findViewById(R.id.change_relation_bt);

        Button mSaveButton = findViewById(R.id.save_info_bt);

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeInfoToDatabase();
            }
        });

        mChangeRelationship.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEditing(true);
            }
        });



    }

    private void setEditing(boolean enable) {
        if (enable) {
            mRadioGroup.setVisibility(View.VISIBLE);
            mChangeRelationship.setVisibility(View.GONE);
            mRelationship.setVisibility(View.GONE);
        }
        else {
            mRadioGroup.setVisibility(View.GONE);
            mChangeRelationship.setVisibility(View.VISIBLE);
            mRelationship.setVisibility(View.VISIBLE);
        }
    }

    ValueEventListener listener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            User user = dataSnapshot.getValue(User.class);
            assert user != null;
            String relationship = user.relationship;

            /*Get relationship*/
            mRelationship.setText(relationship);
            /*Load age and mood*/
            mAgeField.setText(String.valueOf(user.age));
            mMoodField.setText(String.valueOf(user.mood));
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    /*Add this user info to database*/
    private void writeInfoToDatabase() {

        int age = Integer.parseInt(mAgeField.getText().toString());
        String mood = mMoodField.getText().toString();
        int checkedId = mRadioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(checkedId);

        String relationship = radioButton.getText().toString();

        if (TextUtils.isEmpty(String.valueOf(age))) {
            mAgeField.setError("required");
        }
        else {
            mAgeField.setError(null);
            mDatabaseReference.child(getUid()).child("age").setValue(age);
        }

        if (TextUtils.isEmpty(mood)) {
            mMoodField.setError("required");
        }
        else {
            mDatabaseReference.child(getUid()).child("mood").setValue(mood);
        }
        mDatabaseReference.child(getUid()).child("relationship").setValue(relationship)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                setEditing(false);
            }
        });

    }
}
