package com.acronym.pika;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.google.firebase.database.ChildEventListener;
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

public class NewPost2Activity extends BaseActivity implements View.OnClickListener {
    

    /*Member variables*/
    private TextView mPostField;
    private TextView mUsernameView;
    private TextView mPreviewTextView;
    private ImageView mProfilePhotoView;
    private ImageView mSelectedPostImageView;
    private RelativeLayout mImageHolderMain;
    private FrameLayout mImageHolder;
    private TextView mBigTextPostField;

    /*Database Reference*/
    private DatabaseReference mDatabaseRef;

    /*Storage Reference*/
    private StorageReference mStorageRef;

    /*Post Image Uri*/
    private Uri mImagePost;

    /*Selected post color*/
    private String selectedColor = ColorUtils.getColorAt(0);

    private Map<String, Object> mChildUpdates = new HashMap<>();


    private ValueEventListener listener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            loadUserData(dataSnapshot);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        /*Set support action bar*/
        Toolbar toolbar = findViewById(R.id.toolbar_new_post);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mDatabaseRef.child("users").child(getUid()).addValueEventListener(listener);

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");

        mUsernameView = findViewById(R.id.new_post_username);
        mProfilePhotoView = findViewById(R.id.np_userphoto_view);
        mPostField = findViewById(R.id.et_post);
        mSelectedPostImageView = findViewById(R.id.selected_img_view);
        mImageHolderMain = findViewById(R.id.main_holder);
        mImageHolder = findViewById(R.id.image_holder);
        mPreviewTextView = findViewById(R.id.tuned_post_text);
        mBigTextPostField = findViewById(R.id.big_text_field);

        /*Onclick*/
        findViewById(R.id.post_button).setOnClickListener(this);
        findViewById(R.id.open_gallery_img).setOnClickListener(this);
        findViewById(R.id.finish_new_post).setOnClickListener(this);


        /*On Text changed listener*/
        mPostField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPreviewTextView.setText(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }




    private void loadUserData(DataSnapshot dataSnapshot) {
        User user = dataSnapshot.getValue(User.class);
        assert user != null;
        if (user.photo != null) {
            GlideUtils.loadDisplayPhoto(user.photo, mProfilePhotoView, GlideUtils.CIRCLE_TRANSFORM);
        }
        mUsernameView.setText(user.name);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            assert data != null;
            Uri selectedImage = data.getData();
            setImagePost(selectedImage);
            if (selectedImage != null) {
                mImageHolderMain.setVisibility(View.GONE);
                if (TextUtils.isEmpty(mPostField.getText().toString())) {
                    if (mPostField.getError() != null) {
                        mPostField.setError(null);
                    }
                    toast(getString(R.string.post_pic_hint));
                    mPostField.setHint(getString(R.string.post_pic_hint));
                }
                GlideUtils.loadDisplayPhoto(selectedImage.toString(), mSelectedPostImageView,
                        GlideUtils.ROUNDED_CORNER);
            }
        }
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.post_button:
                submitPost();
                break;
            case R.id.finish_new_post:
                finish();
                break;
            case R.id.open_gallery_img:
                openGallery();
                break;
        }
    }

    // Change design

    private void submitPost() {
        final String post = mPostField.getText().toString();
        final String bigTextPost = mBigTextPostField.getText().toString();
        if (TextUtils.isEmpty(post) && getImagePost() == null && TextUtils.isEmpty(bigTextPost)) {
            mPostField.setError("Write Post");
            return;
        }

        setEditingEnabled(false);
        toast("Posting..");

        final String userId = getUid();
        mDatabaseRef.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                if (user == null) {
                    toast("Can't fetch user");
                }
                else {
                    if (getImagePost() != null && TextUtils.isEmpty(post)) {
                        writeNewPost(user, userId, user.name, user.photo, null);
                    }
                    else if (getImagePost() != null && !TextUtils.isEmpty(post)) {
                        writeNewPost(user, userId, user.name, user.photo, null);
                    }
                    else {
                        if (TextUtils.isEmpty(bigTextPost)) {
                            writeNewPost(user, userId, user.name, user.photo, post);
                        }
                        else {
                            writeNewPost(user,userId, user.name, user.photo, bigTextPost);
                        }
                    }
                }

                setEditingEnabled(true);
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                setEditingEnabled(true);
            }
        });
    }


    private void writeNewPost(User user,String userId, String name, String photo, String textPost) {

        String key = mDatabaseRef.child("posts").push().getKey();
        Post post = new Post(userId, name, photo);
        if (!selectedColor.equals(ColorUtils.getColorAt(0))) {
            post.setBigTextPost(textPost);
            post.setTextPost(null);
        }
        else {
            post.setTextPost(textPost);
            post.setBigTextPost(null);
        }

        if (getImagePost() != null) {
            uploadPostImage(getImagePost(), post, key);
            String caption = mPostField.getText().toString();
            if (textPost == null && !TextUtils.isEmpty(caption)) {
                post.setPicCaption(caption);
            }
        }

        post.setTimeStamp(TimestampUtil.getTimestamp());
        post.setBackgroundColor(selectedColor);

        Map<String, Object> postValues = post.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> feedUpdates = updateFeed(key, userId, postValues);
        childUpdates.put("/posts/" + key, postValues);
        childUpdates.put("/feed/" + userId  + "/" + key, postValues);
        childUpdates.put("/user-posts/" + userId + "/" + key, postValues);
        mDatabaseRef.updateChildren(childUpdates);
        mDatabaseRef.updateChildren(feedUpdates);
    }


    /*Update My feed together with everyone else who is following*/
    private Map<String,Object> updateFeed(final String postKey, final String userId, final Map<String, Object> postValues) {

        mDatabaseRef.child("followers").child(userId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    mChildUpdates.put("/feed/" + dataSnapshot.getKey() + "/" + postKey, postValues);
                }
                // This does function properly
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
        });

        return mChildUpdates;
    }

    private void uploadPostImage(Uri imagePost, final Post post, final String key) {
        final StorageReference postRef = mStorageRef.child(getUid()).child("photos")
                .child(key).child(Objects.requireNonNull(imagePost.getLastPathSegment()));

        postRef.putFile(imagePost).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    toast("Upload Successful");
                    handler(postRef, post, key);
                }

            }
        });
    }

    void handler(final StorageReference reference, final Post post, final String key) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                downloadUrl(reference, post, key);
            }
        },1000);
    }

    private void downloadUrl(StorageReference reference, final Post post, final String key) {
        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if (post != null) {
                    mDatabaseRef.child("posts").child(key).child("imagePost").setValue(uri.toString());
                    mDatabaseRef.child("user-posts").child(getUid()).child(key)
                            .child("imagePost").setValue(uri.toString());
                    mDatabaseRef.child("feed").child(getUid()).child(key).child("imagePost")
                            .setValue(uri.toString());
                    addImageToFeed(uri.toString(), key);
                }
                else {
                    toast("Could not fetch post");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                toast("Failed to retrieve image");
            }
        });
    }

    private void addImageToFeed(final String imageUrl, final String key) {
        mDatabaseRef.child("following").child(getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (snapshot.getKey() != null) {
                            mDatabaseRef.child("feed").child(snapshot.getKey())
                                    .child(key).child("imagePost")
                                    .setValue(imageUrl);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void setEditingEnabled(boolean enabled) {
        if (!enabled) {
            findViewById(R.id.post_button).setVisibility(View.GONE);
        }
        else {
            findViewById(R.id.post_button).setVisibility(View.VISIBLE);
        }
    }



    private void setImagePost(Uri imagePost) {
        this.mImagePost = imagePost;
    }



    private Uri getImagePost() {
        return mImagePost;
    }


    /*Change Image holder background on fab selected*/
    public void onFabClicked(View view) {
        switch (view.getId()) {
            case R.id.fab1:
                selectedColor = ColorUtils.getColorAt(0);
                if (!isInvisibleHolder()) {
                    mImageHolderMain.setVisibility(View.GONE);
                    hidePostField(false);
                }
                break;
            case R.id.fab2:
                if (isInvisibleHolder()) {
                    mImageHolderMain.setVisibility(View.VISIBLE);
                    hidePostField(true);
                }
                selectedColor = ColorUtils.getColorAt(1);
                mImageHolder.setBackgroundColor(Color.parseColor(selectedColor));
                break;
            case R.id.fab3:
                if (isInvisibleHolder()) {
                    mImageHolderMain.setVisibility(View.VISIBLE);
                    hidePostField(true);
                }
                selectedColor = ColorUtils.getColorAt(2);
                mImageHolder.setBackgroundColor(Color.parseColor(selectedColor));
                break;
            case R.id.fab4:
                if (isInvisibleHolder()) {
                    mImageHolderMain.setVisibility(View.VISIBLE);
                    hidePostField(true);
                }
                selectedColor = ColorUtils.getColorAt(3);
                mImageHolder.setBackgroundColor(Color.parseColor(selectedColor));
                break;
            case R.id.fab5:
                if (isInvisibleHolder()) {
                    mImageHolderMain.setVisibility(View.VISIBLE);
                    hidePostField(true);
                }
                selectedColor = ColorUtils.getColorAt(4);
                mImageHolder.setBackgroundColor(Color.parseColor(selectedColor));
                break;
            case R.id.fab6:
                if (isInvisibleHolder()) {
                    mImageHolderMain.setVisibility(View.VISIBLE);
                    hidePostField(true);
                }
                selectedColor = ColorUtils.getColorAt(5);
                mImageHolder.setBackgroundColor(Color.parseColor(selectedColor));
                break;
            case R.id.fab7:
                if (isInvisibleHolder()) {
                    mImageHolderMain.setVisibility(View.VISIBLE);
                    hidePostField(true);
                }
                selectedColor = ColorUtils.getColorAt(6);
                mImageHolder.setBackgroundColor(Color.parseColor(selectedColor));
                break;
            case R.id.fab8:
                if (isInvisibleHolder()) {
                    mImageHolderMain.setVisibility(View.VISIBLE);
                    hidePostField(true);
                }
                selectedColor = "grad_1";
                mImageHolder.setBackground(getDrawable(R.drawable.grad_1));
                break;
            case R.id.fab9:
                if (isInvisibleHolder()) {
                    mImageHolderMain.setVisibility(View.VISIBLE);
                    hidePostField(true);
                }
                selectedColor = ColorUtils.getColorAt(8);
                mImageHolder.setBackgroundColor(Color.parseColor(selectedColor));
                break;
            case R.id.fab10:
                if (isInvisibleHolder()) {
                    mImageHolderMain.setVisibility(View.VISIBLE);
                    hidePostField(true);
                }
                selectedColor = ColorUtils.getColorAt(9);
                mImageHolder.setBackgroundColor(Color.parseColor(selectedColor));
                break;
            case R.id.fab11:
                if (isInvisibleHolder()) {
                    mImageHolderMain.setVisibility(View.VISIBLE);
                    hidePostField(true);
                }
                selectedColor = ColorUtils.getColorAt(10);
                mImageHolder.setBackgroundColor(Color.parseColor(selectedColor));
                break;
            case R.id.fab12:
                if (isInvisibleHolder()) {
                    mImageHolderMain.setVisibility(View.VISIBLE);
                    hidePostField(true);
                }
                selectedColor = ColorUtils.getColorAt(11);
                mImageHolder.setBackgroundColor(Color.parseColor(selectedColor));
                break;
            case R.id.fab13:
                if (isInvisibleHolder()) {
                    mImageHolderMain.setVisibility(View.VISIBLE);
                    hidePostField(true);
                }
                selectedColor = "grad_1";
                mImageHolder.setBackground(getDrawable(R.drawable.grad_1));
                break;
            case R.id.fab14:
                if (isInvisibleHolder()) {
                    mImageHolderMain.setVisibility(View.VISIBLE);
                    hidePostField(true);
                }
                selectedColor = ColorUtils.getColorAt(13);
                mImageHolder.setBackgroundColor(Color.parseColor(selectedColor));
                break;
            case R.id.fab15:
                if (isInvisibleHolder()) {
                    mImageHolderMain.setVisibility(View.VISIBLE);
                    hidePostField(true);
                }
                selectedColor = ColorUtils.getColorAt(14);
                mImageHolder.setBackgroundColor(Color.parseColor(selectedColor));
                break;
            case R.id.fab16:
                if (isInvisibleHolder()) {
                    mImageHolderMain.setVisibility(View.VISIBLE);
                    hidePostField(true);
                }
                selectedColor = ColorUtils.getColorAt(15);
                mImageHolder.setBackgroundColor(Color.parseColor(selectedColor));
                break;
            case R.id.fab17:
                if (isInvisibleHolder()) {
                    mImageHolderMain.setVisibility(View.VISIBLE);
                    hidePostField(true);
                }
                selectedColor = ColorUtils.getColorAt(16);
                mImageHolder.setBackgroundColor(Color.parseColor(selectedColor));
                break;
            case R.id.fab18:
                if (isInvisibleHolder()) {
                    mImageHolderMain.setVisibility(View.VISIBLE);
                    hidePostField(true);
                }
                selectedColor = ColorUtils.getColorAt(17);
                mImageHolder.setBackgroundColor(Color.parseColor(selectedColor));
                break;
            case R.id.fab19:
                if (isInvisibleHolder()) {
                    mImageHolderMain.setVisibility(View.VISIBLE);
                    hidePostField(true);
                }
                selectedColor = ColorUtils.getColorAt(18);
                mImageHolder.setBackgroundColor(Color.parseColor(selectedColor));
                break;
            case R.id.fab20:
                if (isInvisibleHolder()) {
                    mImageHolderMain.setVisibility(View.VISIBLE);
                    hidePostField(true);
                }
                selectedColor = ColorUtils.getColorAt(19);
                mImageHolder.setBackgroundColor(Color.parseColor(selectedColor));
                break;
        }
    }

    private boolean isInvisibleHolder() {
        return mImageHolderMain.getVisibility() != View.VISIBLE;
    }

    private void hidePostField(boolean hide) {
        if (hide) {
            mPostField.setVisibility(View.GONE);
        }
        else {
            mPostField.setVisibility(View.VISIBLE);
        }
    }

    public void onImageBackgroundClicked(View view) {
        switch (view.getId()) {
            case R.id.grad_1:
                if (isInvisibleHolder()) {
                    mImageHolderMain.setVisibility(View.VISIBLE);
                    hidePostField(true);
                }
                selectedColor = "grad_1";
                mImageHolder.setBackground(getDrawable(R.drawable.grad_1));
                break;
            case R.id.grad_2:
                if (isInvisibleHolder()) {
                    mImageHolderMain.setVisibility(View.VISIBLE);
                    hidePostField(true);
                }
                selectedColor = "grad_2";
                mImageHolder.setBackground(getDrawable(R.drawable.grad_2));
                break;
            case R.id.grad_3:
                if (isInvisibleHolder()) {
                    mImageHolderMain.setVisibility(View.VISIBLE);
                    hidePostField(true);
                }
                selectedColor = "grad_3";
                mImageHolder.setBackground(getDrawable(R.drawable.grad_3));
                break;
            case R.id.grad_4:
                if (isInvisibleHolder()) {
                    mImageHolderMain.setVisibility(View.VISIBLE);
                    hidePostField(true);
                }
                selectedColor = "grad_4";
                mImageHolder.setBackground(getDrawable(R.drawable.grad_4));
                break;
            case R.id.grad_5:
                if (isInvisibleHolder()) {
                    mImageHolderMain.setVisibility(View.VISIBLE);
                    hidePostField(true);
                }
                selectedColor = "grad_5";
                mImageHolder.setBackground(getDrawable(R.drawable.grad_5));
                break;
            case R.id.grad_6:
                if (isInvisibleHolder()) {
                    mImageHolderMain.setVisibility(View.VISIBLE);
                    hidePostField(true);
                }
                selectedColor = "grad_6";
                mImageHolder.setBackground(getDrawable(R.drawable.grad_6));
                break;
            case R.id.grad_7:
                if (isInvisibleHolder()) {
                    mImageHolderMain.setVisibility(View.VISIBLE);
                    hidePostField(true);
                }
                selectedColor = "grad_7";
                mImageHolder.setBackground(getDrawable(R.drawable.grad_7));
                break;

            case R.id.grad_8:
                if (isInvisibleHolder()) {
                    mImageHolderMain.setVisibility(View.VISIBLE);
                    hidePostField(true);
                }
                selectedColor = "grad_8";
                mImageHolder.setBackground(getDrawable(R.drawable.grad_8));
                break;
            case R.id.grad_9:
                if (isInvisibleHolder()) {
                    mImageHolderMain.setVisibility(View.VISIBLE);
                    hidePostField(true);
                }
                selectedColor = "grad_9";
                mImageHolder.setBackground(getDrawable(R.drawable.grad_9));
                break;
            case R.id.grad_10:
                if (isInvisibleHolder()) {
                    mImageHolderMain.setVisibility(View.VISIBLE);
                    hidePostField(true);
                }
                selectedColor = "grad_10";
                mImageHolder.setBackground(getDrawable(R.drawable.grad_10));
                break;
        }
    }
}
