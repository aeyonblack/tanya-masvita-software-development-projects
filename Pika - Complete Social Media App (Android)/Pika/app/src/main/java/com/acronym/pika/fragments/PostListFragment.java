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
import com.acronym.pika.adapters.PostsAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostListFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private PostsAdapter mAdapter;

    public PostListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_post_list, container, false);
        mRecyclerView = rootView.findViewById(R.id.messages_list);
        mRecyclerView.setHasFixedSize(true);
        return rootView;
    }



    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setSmoothScrollbarEnabled(true);
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);
        mAdapter = new PostsAdapter(getActivity());
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.cleanUpListener();
    }

}
