package com.acronym.pika.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.acronym.pika.R;
import com.acronym.pika.models.Post;
import com.acronym.pika.utils.FirebaseMethods;
import com.acronym.pika.viewholders.PostViewHolder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class PostsQueryAdapter extends RecyclerView.Adapter<PostViewHolder> {

    private List<String> mPostIds;
    private OnSetupViewListener mOnSetupViewListener;

    public PostsQueryAdapter(List<String> postIds, OnSetupViewListener setupViewListener) {
        if (!(postIds == null || postIds.isEmpty())) {
            mPostIds = postIds;
        }
        mOnSetupViewListener = setupViewListener;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        return new PostViewHolder(inflater.inflate(R.layout.post_list_item, viewGroup, false));
    }


    @Override
    public void onBindViewHolder(@NonNull final PostViewHolder holder, int i) {
        DatabaseReference ref = FirebaseMethods.getPostRef().child(mPostIds.get(i));
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                if (post != null) {
                    holder.bindTo(post);
                    mOnSetupViewListener.onSetupView(holder, post, holder.getAdapterPosition(),
                            dataSnapshot.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        ref.addValueEventListener(eventListener);
        holder.mPostRef= ref;
        holder.mPostListener = eventListener;
    }

    @Override
    public void onViewRecycled(@NonNull PostViewHolder holder) {
        super.onViewRecycled(holder);
        holder.mPostRef.removeEventListener(holder.mPostListener);
    }

    @Override
    public int getItemCount() {
        return mPostIds.size();
    }

    public interface OnSetupViewListener {
        void onSetupView(PostViewHolder holder, Post post, int position, String postKey);
    }

}
