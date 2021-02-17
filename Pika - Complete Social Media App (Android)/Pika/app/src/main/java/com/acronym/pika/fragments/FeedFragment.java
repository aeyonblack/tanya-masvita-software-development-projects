package com.acronym.pika.fragments;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.acronym.pika.CommentsActivity;
import com.acronym.pika.R;
import com.acronym.pika.models.Comment;
import com.acronym.pika.models.Post;
import com.acronym.pika.models.User;
import com.acronym.pika.utils.ColorUtils;
import com.acronym.pika.utils.GlideUtils;
import com.acronym.pika.viewholders.NearbyPeopleViewHolder;
import com.acronym.pika.viewholders.PostViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
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
public class FeedFragment extends Fragment {


    /*DatabaseReference*/
    private DatabaseReference mDatabaseRef;

    private FirebaseRecyclerAdapter<Post, PostViewHolder> mAdapter;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private FirebaseRecyclerAdapter<User, NearbyPeopleViewHolder> mNearByAdapter;
    private final String LAYOUT_POSITION = "layoutPosition";

    public FeedFragment() {
        // Required empty public constructor
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_post_list, container, false);

        /*Database Reference*/
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mRecyclerView = rootView.findViewById(R.id.messages_list);
        mRecyclerView.setHasFixedSize(true);
        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();

        if (mAdapter != null) {
            mAdapter.startListening();
        }

        if (mNearByAdapter != null) {
            mNearByAdapter.startListening();
        }
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /*Load posts*/
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setStackFromEnd(true);
        mLinearLayoutManager.setReverseLayout(true);
        mLinearLayoutManager.setSmoothScrollbarEnabled(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        if (savedInstanceState != null) {
            Toast.makeText(getActivity(), "Saved instance state positive", Toast.LENGTH_SHORT).show();
            int position = (int)savedInstanceState.getSerializable(LAYOUT_POSITION);
            mRecyclerView.scrollToPosition(position);
        }
        else {
            Toast.makeText(getActivity(), "No saved instance state", Toast.LENGTH_SHORT).show();
        }
        loadData();


    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Toast.makeText(getActivity(), "onSaveInstanceState", Toast.LENGTH_SHORT).show();
        outState.putSerializable(LAYOUT_POSITION, getScrollPosition());
    }

    private int getScrollPosition() {
        return ((LinearLayoutManager) Objects.requireNonNull(mRecyclerView.getLayoutManager()))
                .findFirstVisibleItemPosition();
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            int pos = (int)savedInstanceState.getSerializable(LAYOUT_POSITION);
            mRecyclerView.scrollToPosition(pos);
        }
    }

    private int offset;
    private int positionIndex;
    @Override
    public void onPause() {
        super.onPause();
        positionIndex = mLinearLayoutManager.findFirstVisibleItemPosition();
        View startView = mRecyclerView.getChildAt(0);
        offset = (startView == null) ? 0 : (startView.getTop() - mRecyclerView.getPaddingTop());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (positionIndex != -1) {
            mLinearLayoutManager.scrollToPositionWithOffset(positionIndex, offset);
        }
    }

    private void loadData() {
        // Query from this database
        Query postsQuery = mDatabaseRef.child("posts");

        FirebaseRecyclerOptions<Post> options = new FirebaseRecyclerOptions.Builder<Post>()
                .setQuery(postsQuery, Post.class)
                .build();
        mAdapter = new FirebaseRecyclerAdapter<Post, PostViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PostViewHolder holder, int position,
                                            @NonNull final Post model) {

                final DatabaseReference postRef = getRef(position);

                final String postKey = postRef.getKey();
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startCommentActivity(postKey);
                    }
                });

                updateUI(holder, model);

                /*Database References where post is written*/
                final DatabaseReference globalPostRef = mDatabaseRef.child("posts")
                        .child(Objects.requireNonNull(postRef.getKey()));
                final DatabaseReference userPostRef = mDatabaseRef.child("user-posts")
                        .child(model.uid).child(postRef.getKey());

                holder.commentButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startCommentActivity(postKey);
                    }
                });


                holder.likeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toggleLike(globalPostRef);
                        toggleLike(userPostRef);
                    }
                });

                holder.bindTo(model);

                getFirstComment(postKey, holder);
            }

            @NonNull
            @Override
            public PostViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new PostViewHolder(inflater.inflate(R.layout.post_list_item, viewGroup, false));
            }
        };

        mRecyclerView.setAdapter(mAdapter);
    }

    private void startCommentActivity(String postKey) {
        Intent intent = new Intent(getActivity(), CommentsActivity.class);
        intent.putExtra(CommentsActivity.EXTRA_POST_KEY, postKey);
        startActivity(intent);
    }

    private void getFirstComment(String postKey, final PostViewHolder holder) {
        mDatabaseRef.child("post_comments").child(postKey)
                .addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //noinspection LoopStatementThatDoesntLoop
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.exists()) {
                        Comment comment = snapshot.getValue(Comment.class);
                        if (comment != null) {
                            holder.postCommentView.setVisibility(View.VISIBLE);
                            holder.firstCommentView.setText(comment.comment);
                            holder.commentUsername.setText(comment.author.toLowerCase());
                            GlideUtils.loadDisplayPhoto(comment.authorPhoto, holder.commentPhoto,
                                    GlideUtils.CIRCLE_TRANSFORM);
                        }
                        else {
                            holder.postCommentView.setVisibility(View.GONE);
                        }
                    }
                    break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    /*Check if user liked or disliked this*/
    private void updateUI(PostViewHolder holder, Post post) {

        /*Update the like button*/
        if (post.likes.containsKey(getUid())) {
            holder.likeImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_heart_fill));
            holder.likeImg.getDrawable().setTint(ColorUtils.getColor(getActivity(), R.color.colorHotRed));
            holder.reactionsCount.setTextColor(ColorUtils.getColor(getActivity(), R.color.colorHotRed));
        }
        else {
            holder.likeImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_heart_outline));
            holder.likeImg.getDrawable().setTint(ColorUtils.getColor(getActivity(),R.color.colorBlack));
            holder.reactionsCount.setTextColor(ColorUtils.getColor(getActivity(), R.color.colorBlack));
        }

    }

    /*Add user to people who liked this post*/
    private void toggleLike(DatabaseReference ref) {
        ref.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                Post p = mutableData.getValue(Post.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }

                if (p.likes.containsKey(getUid())) {
                    p.likesCount = p.likesCount - 1;
                    p.likes.remove(getUid());
                }
                else {
                    p.likesCount = p.likesCount + 1;
                    p.likes.put(getUid(), true);
                }

                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                // Do nothing for now
            }
        });
    }

    private String getUid() {
        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }


    @Override
    public void onStop() {
        super.onStop();

        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }
}
