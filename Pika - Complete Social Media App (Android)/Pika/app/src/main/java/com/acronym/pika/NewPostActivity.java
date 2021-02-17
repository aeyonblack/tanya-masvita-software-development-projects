package com.acronym.pika;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.acronym.pika.models.Post;
import com.acronym.pika.models.User;
import com.acronym.pika.utils.ColorUtils;
import com.acronym.pika.utils.GlideUtils;
import com.acronym.pika.utils.TimestampUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class NewPostActivity extends BaseActivity implements View.OnClickListener {

    private final String TAG = NewPostActivity.class.getSimpleName();

    /*Member variables*/
    private ImageView mProfilePhotoView;
    private ImageView mPostImageView;
    private TextView mPostField;
    private TextView mUsernameTextView;

    private Uri postImage;

    /*Database Reference*/
    private DatabaseReference mDatabaseRef;

    /*Storage Reference*/
    private StorageReference mStorageRef;

    private String mSelectedColor = ColorUtils.getColorAt(0);


    private ValueEventListener listener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            loadPhoto(dataSnapshot);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        /*Initialize database*/
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mDatabaseRef.child("users").child(getUid()).addValueEventListener(listener);

        /*initialize Storage*/
        mStorageRef = FirebaseStorage.getInstance().getReference("user-uploads");

        mProfilePhotoView = findViewById(R.id.np_userphoto_view);
        mPostImageView = findViewById(R.id.selected_img_view);
        mPostField = findViewById(R.id.et_post);
        mUsernameTextView = findViewById(R.id.new_post_username);

        /*Set onClick listeners*/
        findViewById(R.id.post_button).setOnClickListener(this);
        findViewById(R.id.finish_new_post).setOnClickListener(this);
        findViewById(R.id.open_gallery_img).setOnClickListener(this);


    }


    /*void show profile photo in image view*/
    void loadPhoto(DataSnapshot dataSnapshot) {
        User user = dataSnapshot.getValue(User.class);

        assert user != null;
        if (user.photo != null) {
            GlideUtils.loadDisplayPhoto(user.photo, mProfilePhotoView,
                    GlideUtils.CIRCLE_TRANSFORM);
        }
        mUsernameTextView.setText(user.name);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.finish_new_post:
                this.finish();
                break;
            case R.id.post_button:
                submitPost();
                break;
            case R.id.open_gallery_img:
                openGallery();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            assert data != null;
            Uri selectedImageUri = data.getData();
            setImagePost(selectedImageUri);
            if (selectedImageUri != null) {
                if (TextUtils.isEmpty(mPostField.getText().toString())) {
                    if (mPostField.getError() != null) {
                        mPostField.setError(null);
                    }
                    mPostField.setHint(getString(R.string.post_pic_hint));
                }
                // Display image in image view for preview
                GlideUtils.loadDisplayPhoto(selectedImageUri.toString(), mPostImageView,
                        GlideUtils.ROUNDED_CORNER);
            }
        }
    }

    private void submitPost() {
        final String post = mPostField.getText().toString();
        if (TextUtils.isEmpty(post) && getImagePost() == null) {
            mPostField.setError("Write a post");
            return;
        }

        setEditingEnabled(false);
        mDatabaseRef.child("users").child(getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                // [START-EXCLUDE]
                if (user == null) {
                    toast("Could not fetch user");
                }
                else {
                    if (getImagePost() != null && TextUtils.isEmpty(post)) {
                        toast("Posting photo only");
                        writeNewPost(getUid(), user.name, user.photo, null);
                    }
                    else {
                        writeNewPost(getUid(), user.name, user.photo, post);
                    }
                }
                // [END-EXCLUDE]
                finish();
                setEditingEnabled(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                setEditingEnabled(true);
            }
        });
    }

    /*[START]Write post*/
    private void writeNewPost(String uid, String author, String profilePhoto, String thePost) {
        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference().child("posts");

        String key = postRef.push().getKey();
        Post post = new Post(uid, thePost, author);

        if (getImagePost() != null) {
            uploadPostImage(getImagePost(), post, key);
        }
        else {
            toast("Posting...");
        }

        post.setAuthorPhoto(profilePhoto);
        post.setTimeStamp(TimestampUtil.getTimestamp());

        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/posts/" + key, postValues);
        childUpdates.put("/user-posts/" + uid + "/" + key, postValues);
        mDatabaseRef.updateChildren(childUpdates);

    }
    /*[END] Write post*/


    private void setEditingEnabled(boolean enabled) {
        if (!enabled) {
            findViewById(R.id.post_button).setVisibility(View.GONE);
        }
        else {
            findViewById(R.id.post_button).setVisibility(View.VISIBLE);
        }
    }

    private void setImagePost(Uri postImage) {
        this.postImage = postImage;
    }



    private Uri getImagePost() {
        return postImage;
    }



    /*Upload the image to storage*/
    private void uploadPostImage(Uri postImage, Post post, final String key) {
        final StorageReference postRef = mStorageRef.child(getUid()).child("image-posts")
                .child(key).child(Objects.requireNonNull(postImage.getLastPathSegment()));

        final Post newPost = post;
        postRef.putFile(postImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    toast("Upload complete");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getDownloadUrl(postRef, newPost, key);
                        }
                    }, 1000);

                }
                else {
                    toast("Upload failed");
                }
            }
        });
    }



    /*Get the url to download the image*/
    private void getDownloadUrl(StorageReference reference, final Post post, final String key) {
        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if (post != null) {
                    toast("Ok");
                    mDatabaseRef.child("posts").child(key).child("imagePost").setValue(uri.toString());
                    mDatabaseRef.child("user-posts").child(getUid()).child(key)
                            .child("imagePost").setValue(uri.toString());
                }
                else {
                    toast("null reference");
                }
                assert post != null;
                post.setImagePost(uri.toString());
                toast(uri.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                toast("Something unusual happened");
            }
        });
    }

    public void onFabClicked(View view) {
        switch (view.getId()) {
            case R.id.fab1:
                mSelectedColor = ColorUtils.getColorAt(0);
                break;
            case R.id.fab2:
                mSelectedColor = ColorUtils.getColorAt(1);
                break;
            case R.id.fab3:
                mSelectedColor = ColorUtils.getColorAt(2);
                break;
        }
    }
}
