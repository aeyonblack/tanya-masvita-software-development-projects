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
import android.widget.Toast;

import com.acronym.pika.R;
import com.acronym.pika.models.User;
import com.acronym.pika.utils.FirebaseMethods;
import com.acronym.pika.viewholders.UserListViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class PeopleFragment extends Fragment {


    /*Member variables*/
    private RecyclerView mRecycler;

    /*Database Reference*/
    private DatabaseReference mDatabaseRef;

    private FirebaseRecyclerAdapter<User, UserListViewHolder> mAdapter;

    public PeopleFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_people, container, false);
        /*Initialize*/
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();

        mRecycler = rootView.findViewById(R.id.userList_recycler);
        mRecycler.setHasFixedSize(true);
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
        mRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        loadUsers();
        // mPeopleAdapter = new LoadPeopleAdapter(getActivity());
        // mRecycler.setAdapter(mPeopleAdapter);
    }

    private void loadUsers() {
        Query query = mDatabaseRef.child("users");
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();
        mAdapter = new FirebaseRecyclerAdapter<User, UserListViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UserListViewHolder holder, int position, @NonNull final User model) {
                DatabaseReference usersRef = getRef(position);

                final DatabaseReference usersDb = mDatabaseRef.child("users")
                        .child(Objects.requireNonNull(usersRef.getKey()));

                holder.followButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setFollowing(usersDb);
                        addFollowers(model);
                    }
                });

                updateUI(holder, model);
                holder.bindTo(model);

            }

            @NonNull
            @Override
            public UserListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new UserListViewHolder(inflater.inflate(R.layout.follow_list_item, viewGroup, false));
            }
        };

        mRecycler.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private void updateUI(UserListViewHolder holder, User model) {
        if (model.followers.containsKey(FirebaseMethods.getUid())) {
            holder.hideFollowButton(true);
            holder.followingImage.setVisibility(View.VISIBLE);
        }
        else {
            holder.followingImage.setVisibility(View.GONE);
            holder.hideFollowButton(false);
        }

    }

    private void setFollowing(DatabaseReference ref) {
        ref.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                User user = mutableData.getValue(User.class);
                if (user == null) {
                    return Transaction.success(mutableData);
                }

                if (user.followers.containsKey(FirebaseMethods.getUid())) {
                    user.followers.remove(FirebaseMethods.getUid());
                    user.followersCount = user.followersCount -1;
                    writeFollowingToDatabase(user, false);
                }
                else {
                    user.followers.put(FirebaseMethods.getUid(), true);
                    user.followersCount = user.followersCount + 1;
                    writeFollowingToDatabase(user, true);
                }

                mutableData.setValue(user);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

            }
        });
    }

    private void writeFollowingToDatabase(final User user, final boolean write) {
        mDatabaseRef.child("users").child(FirebaseMethods.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User currentUser = dataSnapshot.getValue(User.class);
                if (currentUser != null) {
                    if (write) {
                        mDatabaseRef.child("followers").child(user.uid).child(currentUser.uid)
                                .setValue(currentUser);
                    }
                    else {
                        mDatabaseRef.child("followers").child(user.uid).child(currentUser.uid)
                                .removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addFollowers(final User model) {
        mDatabaseRef.child("users").child(FirebaseMethods.getUid()).runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                User currentUser = mutableData.getValue(User.class);
                if (currentUser == null) {
                    return Transaction.success(mutableData);
                }

                if (currentUser.following.containsKey(model.uid)) {
                    currentUser.following.remove(model.uid);
                    currentUser.followingCount = currentUser.followingCount - 1;
                    mDatabaseRef.child("following").child(FirebaseMethods.getUid())
                            .child(model.uid).removeValue();
                }
                else {
                    currentUser.following.put(model.uid,true);
                    currentUser.followingCount = currentUser.followingCount + 1;
                    mDatabaseRef.child("following").child(FirebaseMethods.getUid())
                            .child(model.uid).setValue(model);
                }

                mutableData.setValue(currentUser);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            Toast.makeText(getActivity(), "onStop:People", Toast.LENGTH_SHORT).show();
            mAdapter.stopListening();
        }
    }


}
