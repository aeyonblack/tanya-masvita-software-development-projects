package com.acronym.pika.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.acronym.pika.R;
import com.acronym.pika.models.User;
import com.acronym.pika.utils.FirebaseMethods;
import com.acronym.pika.viewholders.UserListViewHolder;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.ArrayList;
import java.util.List;

public class LoadPeopleAdapter extends RecyclerView.Adapter<UserListViewHolder> {

    private List<String> mUserIds = new ArrayList<>();
    private List<User> mUsers = new ArrayList<>();
    private DatabaseReference mDatabaseRef;
    private ChildEventListener childEventListener;
    private Context mContext;

    public LoadPeopleAdapter(Context context) {
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mContext = context;

        /*START - CHILD EVENT LISTENER*/
        ChildEventListener eventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                User user = dataSnapshot.getValue(User.class);
                if (!dataSnapshot.getKey().equals(FirebaseMethods.getUid())) {
                    mUserIds.add(dataSnapshot.getKey());
                    mUsers.add(user);
                    notifyItemInserted(mUserIds.size() - 1);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                User newUser = dataSnapshot.getValue(User.class);
                String uid = dataSnapshot.getKey();
                int index = mUserIds.indexOf(uid);
                if (index > -1) {
                    mUsers.set(index, newUser);
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
        /*END - CHILD EVENT LISTENER*/
        mDatabaseRef.child("users").addChildEventListener(eventListener);
        childEventListener = eventListener;
    }

    public void cleanUpListener() {
        if (childEventListener != null) {
            mDatabaseRef.child("users").removeEventListener(childEventListener);
        }
    }

    @NonNull
    @Override
    public UserListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        return new UserListViewHolder(inflater.inflate(R.layout.follow_list_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final UserListViewHolder holder, final int i) {
        User user = mUsers.get(i);
        holder.bindTo(user);
        holder.followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                followingUser(holder.getAdapterPosition());
                followUser(holder.getAdapterPosition());
            }
        });
        updateUI(holder,user,holder.getAdapterPosition());
    }

    @Override
    public int getItemCount() {
        return mUserIds.size();
    }

    private void followUser(int i) {
        mDatabaseRef.child("users").child(mUserIds.get(i)).runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                User user = mutableData.getValue(User.class);
                if (user == null) {
                    return Transaction.success(mutableData);
                }
                if (user.followers.containsKey(FirebaseMethods.getUid())) {
                    user.followersCount = user.followersCount - 1;
                    user.followers.remove(FirebaseMethods.getUid());
                }
                else {
                    user.followers.put(FirebaseMethods.getUid(), true);
                    user.followersCount = user.followersCount + 1;
                }
                mutableData.setValue(user);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

            }
        });
    }

    private void followingUser(final int i) {
        mDatabaseRef.child("users").child(FirebaseMethods.getUid()).runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                User user = mutableData.getValue(User.class);
                if (user == null) {
                    return Transaction.success(mutableData);
                }

                if (user.following.containsKey(mUserIds.get(i))) {
                    user.following.remove(mUserIds.get(i));
                    user.followingCount = user.followingCount - 1;
                    Toast.makeText(mContext, "Unfollowed", Toast.LENGTH_SHORT).show();
                }
                else {
                    user.following.put(mUserIds.get(i), true);
                    user.followingCount = user.followingCount + 1;
                    Toast.makeText(mContext, "Following", Toast.LENGTH_SHORT).show();
                }
                mutableData.setValue(user);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

            }
        });
    }

    private void updateUI(UserListViewHolder holder, User user, int i) {
        if (user.following.containsKey(mUserIds.get(i))) {
            holder.followButton.setText("Unfollow");
        }
        else {
            holder.followButton.setText("Follow");
        }
    }
}
