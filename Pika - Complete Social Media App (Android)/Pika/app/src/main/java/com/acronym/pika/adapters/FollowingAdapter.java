package com.acronym.pika.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import com.acronym.pika.R;
import com.acronym.pika.models.User;
import com.acronym.pika.utils.FirebaseMethods;
import com.acronym.pika.viewholders.FollowersViewHolder;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class FollowingAdapter extends RecyclerView.Adapter<FollowersViewHolder> {

    /*Member variables*/
    private List<User> mUsers = new ArrayList<>();
    private List<String> mUserIds = new ArrayList<>();
    private DatabaseReference mDatabaseRef;
    private ChildEventListener mEventListenerUsers;

    public FollowingAdapter(final Context context) {
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        ChildEventListener eventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    String uid = dataSnapshot.getKey();
                    mUsers.add(user);
                    mUserIds.add(uid);
                    notifyItemInserted(mUsers.size()-1);
                    Toast.makeText(context, "User exists", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(context, "User does not exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                User newUser = dataSnapshot.getValue(User.class);
                String uid = dataSnapshot.getKey();
                int index = mUserIds.indexOf(uid);
                if (index > -1) {
                    mUsers.set(index,newUser);
                    notifyItemChanged(index);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String uid = dataSnapshot.getKey();
                int index = mUserIds.indexOf(uid);
                if (index > -1) {
                    mUsers.remove(index);
                    mUserIds.remove(index);
                    notifyItemRemoved(index);
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mDatabaseRef.child("following").child(FirebaseMethods.getUid())
                .addChildEventListener(eventListener);
        mEventListenerUsers = eventListener;
    }

    @NonNull
    @Override
    public FollowersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        return new FollowersViewHolder(inflater.inflate(R.layout.following_item, viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull FollowersViewHolder followersViewHolder, int i) {
        User user = mUsers.get(i);
        followersViewHolder.bindTo(user);
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public void cleanUp() {
        if (mEventListenerUsers != null) {
            mDatabaseRef.child("following").child(FirebaseMethods.getUid())
                    .removeEventListener(mEventListenerUsers);
        }
    }

}
