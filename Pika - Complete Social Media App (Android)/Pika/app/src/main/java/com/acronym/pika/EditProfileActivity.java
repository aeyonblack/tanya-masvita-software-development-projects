package com.acronym.pika;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.acronym.pika.models.User;
import com.acronym.pika.utils.GlideUtils;
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

public class EditProfileActivity extends BaseActivity implements View.OnClickListener {

    private Button mEditBioButton;
    private Button mSaveBioButton;
    private EditText mBioEditText;
    private TextView mUserBioTextView;
    private ImageView mUserProfilePhotoView;

    private LinearLayout mBioInputLayout;

    /*Database Reference*/
    private DatabaseReference mDatabaseRef;
    private StorageReference mStorageRef;


    /*Profile Info textViews*/
    private TextView mFingerPrintTv,
            mStatusTv,
            mAgeTv,
            mLocationTv,
            mRelationshipTv,
            mMoodTv,
            mFollowingTv,
            mFollowersTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);


        if (!isNetworkAvailable()) {
            findViewById(R.id.nested_scroll_ep).setVisibility(View.INVISIBLE);
            findViewById(R.id.no_network_layout).setVisibility(View.VISIBLE);
        }
        else {
            findViewById(R.id.nested_scroll_ep).setVisibility(View.VISIBLE);
            findViewById(R.id.no_network_layout).setVisibility(View.GONE);
        }

        /*Initialize Database*/
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("users");
        mDatabaseRef.child(getUid()).addValueEventListener(listener);

        mStorageRef = FirebaseStorage.getInstance().getReference("user-uploads").child(getUid());
        /*Initialize view components*/
        init();
    }

    /*Initialize components*/
    void init() {
        mEditBioButton = findViewById(R.id.edit_bio_button);
        /*Member variables*/
        Button editPhotoButton = findViewById(R.id.edit_profile_pic_button);
        Button editProfileInfoButton = findViewById(R.id.edit_info_button);
        mSaveBioButton = findViewById(R.id.save_bio_bt);
        mBioInputLayout = findViewById(R.id.bio_input_layout);
        mUserBioTextView  = findViewById(R.id.edit_profile_bio_tv);
        mBioEditText = findViewById(R.id.user_bio_input_filled);
        mUserProfilePhotoView = findViewById(R.id.edit_profile_image);

        // Set onClick listeners
        editProfileInfoButton.setOnClickListener(this);
        editPhotoButton.setOnClickListener(this);
        mEditBioButton.setOnClickListener(this);
        mSaveBioButton.setOnClickListener(this);

        /*USER INFO VIEWS*/
        mFingerPrintTv = findViewById(R.id.fingerprint2);
        mStatusTv = findViewById(R.id.status2);
        mAgeTv = findViewById(R.id.age2);
        mLocationTv = findViewById(R.id.location2);
        mRelationshipTv = findViewById(R.id.relation2);
        mMoodTv = findViewById(R.id.mood2);
        mFollowersTv = findViewById(R.id.followers2);
        mFollowingTv = findViewById(R.id.following2);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            Uri photoUri = data != null ? data.getData() : null;
            if (photoUri != null) {
                uploadPhoto(photoUri);
            }
        }
    }

    /*Query data*/
    ValueEventListener listener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            User user = dataSnapshot.getValue(User.class);
            assert user != null;

            /*Finally just load a few things*/
            loadData(user);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_profile_pic_button:
                editPhoto();
                //TODO 3- edit photo
                break;
            case R.id.edit_bio_button:
                setEditMode(true);
                //TODO 1-edit bio
                break;
            case R.id.edit_info_button:
                //TODO 2-edit info
                editInfo();
                break;
            case R.id.save_bio_bt:
                saveBio();
                break;
        }
    }

    /*Modify general user info*/
    void editInfo() {
        startActivity(new Intent(this, EditProfileInfo.class));
    }


    /*Save modified bio*/
    void saveBio() {

        /*Set new bio*/
        String bio = mBioEditText.getText().toString();
        if (TextUtils.isEmpty(bio)) {
            mBioEditText.setError("Set a bio");
            return;
        }

        mBioEditText.setError(null);
        mDatabaseRef.child(getUid()).child("bio").setValue(bio)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                setEditMode(false);
            }
        });
    }


    /*Load already available user info*/
    void loadData(User user) {
        mUserBioTextView.setText(user.bio);

        mFingerPrintTv.setText(getUid());
        mStatusTv.setText(user.status);
        mAgeTv.setText(String.valueOf(user.age));
        mLocationTv.setText(user.location);
        mRelationshipTv.setText(user.relationship);
        mMoodTv.setText(user.mood);
        mFollowingTv.setText(String.valueOf(user.followingCount));
        mFollowersTv.setText(String.valueOf(user.followersCount));
        GlideUtils.loadDisplayPhoto(user.photo, mUserProfilePhotoView, GlideUtils.CIRCLE_TRANSFORM);

        if (user.photo != null) {
            toast("Photo Available");
        }
        else {
            toast("No photo to display");
        }
    }


    void downloadPhoto() {
        findViewById(R.id.edit_profile_uploading_photo).setVisibility(View.VISIBLE);
        mStorageRef.child("profile-photo").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                toast("Download Complete");
                findViewById(R.id.edit_profile_uploading_photo).setVisibility(View.GONE);
                mDatabaseRef.child(getUid()).child("photo").setValue(uri.toString());
                GlideUtils.loadDisplayPhoto(uri.toString(), mUserProfilePhotoView, GlideUtils.CIRCLE_TRANSFORM);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                findViewById(R.id.edit_profile_uploading_photo).setVisibility(View.GONE);
                toast("Download failed");
            }
        });
    }


    /*Show or hide specific layouts*/
    void setEditMode(boolean editMode) {
        if (editMode) {
            mBioInputLayout.setVisibility(View.VISIBLE);
            mSaveBioButton.setVisibility(View.VISIBLE);
            mEditBioButton.setVisibility(View.INVISIBLE);
            mUserBioTextView.setVisibility(View.INVISIBLE);
        }
        else {
            mBioInputLayout.setVisibility(View.INVISIBLE);
            mSaveBioButton.setVisibility(View.INVISIBLE);
            mEditBioButton.setVisibility(View.VISIBLE);
            mUserBioTextView.setVisibility(View.VISIBLE);
            mBioEditText.setText(null);
        }
    }


    /*Change profile photo*/
    void editPhoto() {
        openGallery();
    }

    /*Upload to and retrieve photo from storage*/
    void uploadPhoto(final Uri photoUri) {

        final StorageReference globalPhotosRef = FirebaseStorage.getInstance()
                .getReference("user-uploads").child(getUid())
                .child("photos").child("pika." + System.currentTimeMillis());

        StorageReference profilePhotoRef = FirebaseStorage.getInstance()
                .getReference("user-uploads").child(getUid()).child("profile-photo");

        findViewById(R.id.edit_profile_uploading_photo).setVisibility(View.VISIBLE);
        profilePhotoRef.putFile(photoUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                findViewById(R.id.edit_profile_uploading_photo).setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    toast("Profile Photo Updated");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            longToast("Finalizing...");
                            downloadPhoto();
                            uploadToGlobalStorage(photoUri, globalPhotosRef);
                        }
                    }, 1000);
                }
                else {
                    toast("Upload failed");
                }
            }
        });

        longToast("Starting upload session 2...");
        globalPhotosRef.putFile(photoUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    longToast("Upload session 2 complete");
                }
            }
        });

    }

    void uploadToGlobalStorage(Uri photoUri, StorageReference ref) {
        ref.putFile(photoUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    toast("Finalizing complete");
                }
                else {
                    toast("Something happened before I could finish");
                }
            }
        });
    }

}
