package com.acronym.pika.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.acronym.pika.R;
import com.acronym.pika.models.Item;
import com.acronym.pika.viewholders.SellItemViewHolder;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class SellItemAdapter extends RecyclerView.Adapter<SellItemViewHolder> {

    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;

    private List<Item> mItems = new ArrayList<>();

    public SellItemAdapter(Context context, DatabaseReference reference) {
        mDatabaseReference = reference;

        /*[START - CREATE CHILD EVENT LISTENER]*/
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Item item = dataSnapshot.getValue(Item.class);
                mItems.add(item);
                notifyItemInserted(mItems.size()-1);
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

            }
        };
        reference.addChildEventListener(childEventListener);
        mChildEventListener = childEventListener;
    }

    @NonNull
    @Override
    public SellItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        return new SellItemViewHolder(inflater.inflate(R.layout.current_sell_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SellItemViewHolder sellItemViewHolder, int i) {
        sellItemViewHolder.bindTo(mItems.get(i));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void cleanUpListener() {
        if (mChildEventListener != null) {
            mDatabaseReference.removeEventListener(mChildEventListener);
        }
    }

}
