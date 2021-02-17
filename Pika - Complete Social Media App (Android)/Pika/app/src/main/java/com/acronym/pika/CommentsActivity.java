package com.acronym.pika;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.acronym.pika.adapters.CommentAdapter;
import com.acronym.pika.models.Comment;
import com.acronym.pika.models.Post;
import com.acronym.pika.models.User;
import com.acronym.pika.utils.GlideUtils;
import com.acronym.pika.utils.TimestampUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

public class CommentsActivity extends BaseActivity {


    /*Member variables*/
    private ImageView mPostAuthorPhoto;
    private ImageView mCommenterPhoto;
    private EditText mCommentField;
    private RecyclerView mCommentsRecyclerView;
    private CommentAdapter mAdapter;

    /*Database Reference*/
    private DatabaseReference mDatabaseRef;
    private DatabaseReference mPostRef;
    private DatabaseReference mCommentRef;

    private ValueEventListener mPostListener;

    private String mPostKey;

    public static final String EXTRA_POST_KEY = "post_key";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        String postKey = getIntent().getStringExtra(EXTRA_POST_KEY);

        if (postKey == null) {
            throw new IllegalArgumentException("Must pass post key first");
        }
        mPostKey = postKey;
        /*Initialize database*/
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mPostRef = mDatabaseRef.child("posts").child(postKey);
        mCommentRef = mDatabaseRef.child("post_comments").child(postKey);


        ImageView dropCommentView = findViewById(R.id.drop_comment_img);
        mPostAuthorPhoto = findViewById(R.id.post_author_photo_comments);
        mCommenterPhoto = findViewById(R.id.commenter_photo);
        mCommentField = findViewById(R.id.comment_input_field);
        mCommentsRecyclerView = findViewById(R.id.comments_recycler);
        mCommentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        /*Retrieve the photo from the database*/
        mDatabaseRef.child("users").child(getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                assert user != null;
                if (user.photo != null) {
                    GlideUtils.loadDisplayPhoto(user.photo, mCommenterPhoto, GlideUtils.CIRCLE_TRANSFORM);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Do nothing for now
            }
        });


        /*Drop comment onClick*/
        dropCommentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dropComment();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        /*Get post author photo*/
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                assert post != null;
                GlideUtils.loadDisplayPhoto(post.authorPhoto, mPostAuthorPhoto,
                        GlideUtils.CIRCLE_TRANSFORM);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Do nothing
            }
        };

        mPostRef.addValueEventListener(postListener);
        mPostListener = postListener;
        /*Set the adapter*/
        mAdapter = new CommentAdapter(this, mCommentRef);
        mCommentsRecyclerView.setAdapter(mAdapter);
    }



    /*Submit the comment*/
    private void dropComment() {
        final String uid = getUid();
        mDatabaseRef.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                /*Get the author info*/
                User commentAuthor = dataSnapshot.getValue(User.class);
                assert commentAuthor != null;
                String authorName = commentAuthor.name;
                String authorPhoto = commentAuthor.photo;

                /*Create a new comment object*/
                String commentText = mCommentField.getText().toString();
                String timestamp = TimestampUtil.getTimestamp();
                Comment comment = new Comment(uid, authorName, commentText, authorPhoto,timestamp);
                /*Push the comment*/
                mCommentRef.push().setValue(comment);
                addToComments(uid);
                mCommentField.setText(null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addToComments(final String uid) {
        mDatabaseRef.child("posts").child(mPostKey).runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                Post post = mutableData.getValue(Post.class);
                if (post == null) {
                    return Transaction.success(mutableData);
                }

                post.comments.put(uid, true);
                post.commentsCount = post.commentsCount + 1;
                mutableData.setValue(post);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                // Do nothing
            }
        });
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mPostListener != null) {
            mPostRef.removeEventListener(mPostListener);
        }
        mAdapter.cleanUpListener();
    }
}