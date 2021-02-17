package com.acronym.pika.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.acronym.pika.R;
import com.acronym.pika.SellActivity;
import com.acronym.pika.models.Item;
import com.acronym.pika.viewholders.SellItemViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

/**
 * A simple {@link Fragment} subclass.
 */
public class TradeFragment extends Fragment {

    private Button mSellItemButton;

    private DatabaseReference mDatabaseReference;
    private FirebaseRecyclerAdapter<Item, SellItemViewHolder> mAdapter;
    private RecyclerView mRecyclerView;

    private static final String KEY_LAYOUT_POSITION = "layoutPosition";

    public TradeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_trade, container, false);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mSellItemButton = rootView.findViewById(R.id.sell_item_button);
        mRecyclerView = rootView.findViewById(R.id.items_for_sale_list);
        return  rootView;
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

        // Saved instance state
        if (savedInstanceState != null) {
            int position = (int)savedInstanceState.getSerializable(KEY_LAYOUT_POSITION);
            mRecyclerView.scrollToPosition(position);
        }

        mSellItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SellActivity.class));
            }
        });
        loadData();

        mRecyclerView.setAdapter(mAdapter);
    }

    private void loadData() {
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setSmoothScrollbarEnabled(true);
        manager.setStackFromEnd(true);
        manager.setReverseLayout(true);
        mRecyclerView.setLayoutManager(manager);

        Query query = mDatabaseReference.child("market-place");

        FirebaseRecyclerOptions<Item> options = new FirebaseRecyclerOptions.Builder<Item>()
                .setQuery(query, Item.class)
                .build();

        mAdapter = new FirebaseRecyclerAdapter<Item, SellItemViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull SellItemViewHolder holder, int position, @NonNull Item model) {
                holder.bindTo(model);
            }

            @NonNull
            @Override
            public SellItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new SellItemViewHolder(inflater.inflate(R.layout.current_sell_item, viewGroup, false));
            }
        };
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        int position = getScrollPosition();
        outState.putSerializable(KEY_LAYOUT_POSITION, position);
    }

    private int getScrollPosition() {
        int position = 0;
        if (mRecyclerView != null && mRecyclerView.getLayoutManager() != null) {
            position = ((LinearLayoutManager)mRecyclerView.getLayoutManager())
                    .findFirstVisibleItemPosition();
        }
        return position;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }
}
