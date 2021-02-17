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
import com.acronym.pika.viewholders.PeopleViewHolder;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class PeopleAdapter extends RecyclerView.Adapter<PeopleViewHolder> {

    /*Member variables*/
    private DatabaseReference mDatabaseRef;
    private ChildEventListener mChildEventListener;
    private List<User> mUserList = new ArrayList<>();

    public PeopleAdapter(final Context context, DatabaseReference reference) {
        mDatabaseRef = reference;

        /*START - CREATE CHILD EVENT LISTENER*/
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                User user = dataSnapshot.getValue(User.class);
                mUserList.add(user);
                notifyItemInserted(mUserList.size()-1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "Could not fetch user", Toast.LENGTH_SHORT).show();
            }
        };
        reference.addChildEventListener(childEventListener);
        /*END - CREATE CHILD EVENT LISTENER*/

        mChildEventListener = childEventListener;
    }

    @NonNull
    @Override
    public PeopleViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        return new PeopleViewHolder(inflater.inflate(R.layout.people_around_list_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PeopleViewHolder peopleViewHolder, int i) {
        peopleViewHolder.bindTo(mUserList.get(i), new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    public void cleanUpListener() {
        // TODO...
        if (mChildEventListener != null) {
            mDatabaseRef.removeEventListener(mChildEventListener);
        }
    }
}
