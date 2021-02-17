package com.acronym.pika.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.acronym.pika.CommentsActivity;
import com.acronym.pika.R;
import com.acronym.pika.models.Comment;
import com.acronym.pika.models.Post;
import com.acronym.pika.utils.ColorUtils;
import com.acronym.pika.utils.FirebaseMethods;
import com.acronym.pika.utils.GlideUtils;
import com.acronym.pika.viewholders.PostViewHolder;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostViewHolder> {

    /*Member variables*/
    private List<String> mPostIds = new ArrayList<>();
    private List<Post> mPosts = new ArrayList<>();
    private ChildEventListener mListener;
    private DatabaseReference mRef;
    private Context mContext;

    public PostsAdapter(Context context) {
        this.mContext = context;
        mRef = FirebaseDatabase.getInstance().getReference();
        ChildEventListener eventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Post post = dataSnapshot.getValue(Post.class);
                if (post != null) {
                    mPosts.add(post);
                    mPostIds.add(dataSnapshot.getKey());
                    notifyItemInserted(mPosts.size()-1);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Post newPost = dataSnapshot.getValue(Post.class);
                String key = dataSnapshot.getKey();
                int index = mPostIds.indexOf(key);
                if (index > -1) {
                    mPosts.set(index,newPost);
                    notifyItemChanged(index);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getKey();
                int index = mPostIds.indexOf(key);
                if (index > -1) {
                    mPosts.remove(index);
                    mPostIds.remove(index);
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

        mRef.child("posts").addChildEventListener(eventListener);
        mListener = eventListener;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        return new PostViewHolder(inflater.inflate(R.layout.post_list_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int i) {
        Post post = mPosts.get(i);
        final String key = mPostIds.get(i);
        holder.bindTo(post);
        updateUI(holder, post);
        getFirstComment(key, holder);
        final DatabaseReference globalPostRef = mRef.child("posts").child(key);
        final DatabaseReference userPostRef = mRef.child("user-posts").child(post.uid).child(key);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CommentsActivity.class);
                intent.putExtra(CommentsActivity.EXTRA_POST_KEY, key);
                mContext.startActivity(intent);
            }
        });
        holder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleLike(globalPostRef);
                toggleLike(userPostRef);
            }
        });
        holder.lolButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleLol(globalPostRef);
                toggleLol(userPostRef);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public void cleanUpListener() {
        if (mListener != null) {
            mRef.child("posts").removeEventListener(mListener);
        }
    }

    private void updateUI(PostViewHolder holder, Post post) {
        /*Update the like button*/
        if (post.likes.containsKey(FirebaseMethods.getUid())) {
            holder.likeImg.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_heart_fill));
            holder.likeImg.getDrawable().setTint(ColorUtils.getColor(mContext, R.color.colorHotRed));
            holder.reactionsCount.setTextColor(ColorUtils.getColor(mContext, R.color.colorHotRed));
        }
        else {
            holder.likeImg.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_heart));
            holder.likeImg.getDrawable().setTint(ColorUtils.getColor(mContext,R.color.colorBlack));
            holder.reactionsCount.setTextColor(ColorUtils.getColor(mContext, R.color.colorBlack));
        }

        /*Update LOL*/
        if (post.lols.containsKey(FirebaseMethods.getUid())) {
            holder.lolsImage.setColorFilter(ColorUtils.getColor(mContext, R.color.colorYellow));
            holder.lolTextView.setTextColor(ColorUtils.getColor(mContext, R.color.colorYellow));
        }
        else {
            holder.lolsImage.setColorFilter(ColorUtils.getColor(mContext, R.color.colorBlack));
            holder.lolTextView.setTextColor(ColorUtils.getColor(mContext, R.color.colorBlack));
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

                if (p.likes.containsKey(FirebaseMethods.getUid())) {
                    p.likesCount = p.likesCount - 1;
                    p.likes.remove(FirebaseMethods.getUid());
                }
                else {
                    p.likesCount = p.likesCount + 1;
                    p.likes.put(FirebaseMethods.getUid(), true);
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

    private void toggleLol(DatabaseReference ref) {
        ref.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                Post post = mutableData.getValue(Post.class);
                if (post == null) {
                    return Transaction.success(mutableData);
                }

                if (post.lols.containsKey(FirebaseMethods.getUid())) {
                    post.lols.remove(FirebaseMethods.getUid());
                    post.lolCount = post.lolCount - 1;
                }
                else {
                    post.lols.put(FirebaseMethods.getUid(), true);
                    post.lolCount = post.lolCount + 1;
                }
                mutableData.setValue(post);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

            }
        });
    }

    private void getFirstComment(String postKey, final PostViewHolder holder) {
        mRef.child("post_comments").child(postKey)
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

}
