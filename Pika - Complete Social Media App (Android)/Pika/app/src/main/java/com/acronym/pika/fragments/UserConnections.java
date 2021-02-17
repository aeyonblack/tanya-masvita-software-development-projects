package com.acronym.pika.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.acronym.pika.R;
import com.acronym.pika.models.User;
import com.acronym.pika.utils.FirebaseMethods;
import com.acronym.pika.viewholders.FollowersViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserConnections extends Fragment {

    private DatabaseReference mDatabaseRef;
    private RecyclerView mRecyclerView;
    private FirebaseRecyclerAdapter<User, FollowersViewHolder> mAdapter;

    public UserConnections() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_user_connections, container, false);
        mRecyclerView = rootView.findViewById(R.id.following_recycler);
        mRecyclerView.setHasFixedSize(true);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        loadFollowing();
        mRecyclerView.setAdapter(mAdapter);
    }

    private void loadFollowing() {
        Query query = mDatabaseRef.child("following").child(FirebaseMethods.getUid());
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();
        mAdapter = new FirebaseRecyclerAdapter<User, FollowersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FollowersViewHolder holder, int position, @NonNull User model) {
                holder.bindTo(model);
            }

            @NonNull
            @Override
            public FollowersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new FollowersViewHolder(inflater.inflate(R.layout.following_item, viewGroup, false));
            }
        };
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }
}
