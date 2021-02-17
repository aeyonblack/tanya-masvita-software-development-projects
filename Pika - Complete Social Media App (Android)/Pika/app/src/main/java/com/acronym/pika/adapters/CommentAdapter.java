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
import com.acronym.pika.models.Comment;
import com.acronym.pika.viewholders.CommentViewHolder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CommentAdapter extends RecyclerView.Adapter<CommentViewHolder> {


    private DatabaseReference mDatabaseRef;
    private ChildEventListener mChildEventListener;


    private List<String> mCommentIds = new ArrayList<>();
    private List<Comment> mComments = new ArrayList<>();

    public CommentAdapter(final Context context, DatabaseReference databaseReference) {
        mDatabaseRef = databaseReference;
        /*[START - CREATE CHILD EVENT LISTENER]*/
        ChildEventListener eventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Comment comment = dataSnapshot.getValue(Comment.class);
                mCommentIds.add(dataSnapshot.getKey());
                mComments.add(comment);
                notifyItemInserted(mComments.size()-1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Comment newComment = dataSnapshot.getValue(Comment.class);
                String commentKey = dataSnapshot.getKey();

                int commentIndex = mCommentIds.indexOf(commentKey);
                if (commentIndex > -1) {
                    mComments.set(commentIndex, newComment);
                    notifyItemChanged(commentIndex);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String commentKey = dataSnapshot.getKey();

                int commentIndex = mCommentIds.indexOf(commentKey);
                if (commentIndex > -1) {
                    mCommentIds.remove(commentIndex);
                    mComments.remove(commentIndex);
                    notifyItemRemoved(commentIndex);
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                /*Nothing to do here*/
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "Could not fetch comments", Toast.LENGTH_SHORT).show();
            }
        };
        databaseReference.addChildEventListener(eventListener);
        /*[END - CREATE CHILD EVENT LISTENER]*/

        mChildEventListener = eventListener;
    }


    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        return new CommentViewHolder(inflater.inflate(R.layout.comment_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder commentViewHolder, int i) {
        Comment comment = mComments.get(i);
        commentViewHolder.bindTo(comment);
        // updateUI(comment, commentViewHolder);
        /*Onclick*/
        commentViewHolder.likeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleLike();
            }
        });

        /*Onclick*/
        commentViewHolder.dislikeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleDislike();
            }
        });

        /*Onclick*/
        commentViewHolder.loveImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleLove();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }


    /*Clean up listener when done*/
    public void cleanUpListener() {
        if (mChildEventListener != null) {
            mDatabaseRef.removeEventListener(mChildEventListener);
        }
    }








    /*User clicked the like button*/
    private void toggleLike() {

    }



    /*User clicked the dislike button*/
    private void toggleDislike() {
        mDatabaseRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                Comment comment = mutableData.getValue(Comment.class);
                if (comment == null) {
                    return Transaction.success(mutableData);
                }

                if (comment.dislikes.containsKey(getUid())) {
                    comment.dislikesCount = comment.dislikesCount - 1;
                    comment.dislikes.remove(getUid());
                }
                else {
                    comment.dislikesCount = comment.dislikesCount + 1;
                    comment.dislikes.put(getUid(), true);
                }
                mutableData.setValue(comment);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

            }
        });
    }



    /*User clicked the love button*/
    private void toggleLove() {
        mDatabaseRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                Comment comment = mutableData.getValue(Comment.class);
                if (comment == null) {
                    return Transaction.success(mutableData);
                }

                if (comment.love.containsKey(getUid())) {
                    comment.loveCount = comment.loveCount - 1;
                    comment.love.remove(getUid());
                }
                else {
                    comment.loveCount = comment.loveCount + 1;
                    comment.love.put(getUid(), true);
                }
                mutableData.setValue(comment);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

            }
        });
    }

    private String getUid() {
       return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }
}
