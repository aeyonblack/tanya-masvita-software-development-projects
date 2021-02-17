package com.acronym.pika.fragments;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.acronym.pika.EditProfileActivity;
import com.acronym.pika.R;
import com.acronym.pika.adapters.FollowingAdapter;
import com.acronym.pika.models.User;
import com.acronym.pika.utils.GlideUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * The profile fragment constitutes of the user's details
 * All one needs to know about the user is right here
 * The name, bio and profile photo are at the top and every other details below that
 * With time the profile will be a bit more advanced than it currently is.
 * The profile will be redefined to actually resemble a digital human being
 * Nothing @Facebook has ever done or any other social media that  I knw of...
 */


public class ProfileFragment extends Fragment {

    private TextView mBioTextView;
    private TextView mUsernameTextView;
    public ImageView mProfilePhoto;

    /*Profile Info textViews*/
    private TextView mFingerPrintTv,
                     mStatusTv,
                     mAgeTv,
                     mLocationTv,
                     mRelationshipTv,
                     mMoodTv,
                     mFollowingTv,
                     mFollowersTv;

    /*User following list recycler*/
    private RecyclerView mRecyclerView;
    private FollowingAdapter mFollowingAdapter;



    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        mUsernameTextView = rootView.findViewById(R.id.profile_username);
        mBioTextView = rootView.findViewById(R.id.profile_user_bio);

        mFingerPrintTv = rootView.findViewById(R.id.fingerprint);
        mStatusTv = rootView.findViewById(R.id.status);
        mAgeTv = rootView.findViewById(R.id.age);
        mLocationTv = rootView.findViewById(R.id.location);
        mRelationshipTv = rootView.findViewById(R.id.relation);
        mMoodTv = rootView.findViewById(R.id.mood);
        mFollowersTv = rootView.findViewById(R.id.followers);
        mFollowingTv = rootView.findViewById(R.id.following);
        mProfilePhoto = rootView.findViewById(R.id.profile_profilePhoto);
        mRecyclerView = rootView.findViewById(R.id.user_following_list);
        mRecyclerView.setHasFixedSize(true);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @SuppressLint("NewApi")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL, false));
        mFollowingAdapter = new FollowingAdapter(getActivity());
        mRecyclerView.setAdapter(mFollowingAdapter);
        addListener();
        Objects.requireNonNull(getActivity()).findViewById(R.id.edit_profile_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), EditProfileActivity.class));
            }
        });

    }

    void addListener() {
        DatabaseReference profileRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(getUid());

        profileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                assert user != null;
                mBioTextView.setText(user.bio);
                mUsernameTextView.setText(user.name);
                GlideUtils.loadDisplayPhoto(user.photo,mProfilePhoto, GlideUtils.CIRCLE_TRANSFORM);

                if (TextUtils.isEmpty(mBioTextView.getText().toString()) ||
                        TextUtils.isEmpty(mUsernameTextView.getText().toString())) {
                            Objects.requireNonNull(getActivity()).findViewById(R.id.loading_tv)
                                    .setVisibility(View.VISIBLE);
                }
                else {
                    Objects.requireNonNull(getActivity()).findViewById(R.id.loading_tv).setVisibility(View.GONE);
                }

                /*Set Additional user information*/
                loadInfo(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private String getUid() {
        return FirebaseAuth.getInstance().getUid();
    }

    private void loadInfo(User user) {
        mFingerPrintTv.setText(getUid());
        mStatusTv.setText(user.status);
        mAgeTv.setText(String.valueOf(user.age));
        mLocationTv.setText(user.location);
        mRelationshipTv.setText(user.relationship);
        mMoodTv.setText(user.mood);
        mFollowingTv.setText(String.valueOf(user.followingCount));
        mFollowersTv.setText(String.valueOf(user.followersCount));

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStop() {
        super.onStop();
        mFollowingAdapter.cleanUp();
    }

}
