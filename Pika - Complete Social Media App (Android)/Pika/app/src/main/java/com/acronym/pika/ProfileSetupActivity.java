package com.acronym.pika;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.acronym.pika.utils.GlideUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ProfileSetupActivity extends BaseActivity implements View.OnClickListener {


    // Member variables
    private EditText mNameField;
    private ImageView mUserPhoto;
    private ProgressBar mProgressBar;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        mStorageRef = FirebaseStorage.getInstance().getReference("user-uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("users");
        // Initialize components
        init();
        setTransparentStatusBar();

        getPermissions();
    }


    /*Get read and write permissions*/
    private void getPermissions() {
        if (ActivityCompat
                .checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, GALLERY_REQUEST);

        }
    }


    /*Initialize Components*/
    private void init() {
        mNameField = findViewById(R.id.et_userDisplayName1);
        mUserPhoto = findViewById(R.id.iv_userDisplayPhoto1);
        mProgressBar = findViewById(R.id.photoUpdate_pb);

        /*Onclick Listeners*/
        findViewById(R.id.continue_button1).setOnClickListener(this);
        findViewById(R.id.continue_button2).setOnClickListener(this);
        findViewById(R.id.snatch_button).setOnClickListener(this);
        findViewById(R.id.continue_button3).setOnClickListener(this);
        findViewById(R.id.final_button).setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            assert data != null;
            Uri imageUri = data.getData();
            if (imageUri != null) {
                updateUserProfile();
                uploadPhoto(imageUri);
            }
        }
    }

    private void finishProfileSetUp() {
        startActivity(new Intent(this, Main2Activity.class));
        finish();
    }


    /*Update user name and add it to database*/
    private void updateUserProfile() {
        findViewById(R.id.updating_profile_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.layout2_username).setVisibility(View.GONE);
        FirebaseUser currentUser = getCurrentUser();
        toast(getResources().getString(R.string.updating));
        final String name = mNameField.getText().toString();
        UserProfileChangeRequest changeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();
        currentUser.updateProfile(changeRequest)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        // Load photo into image view for now
                        if (task.isSuccessful()) {
                            findViewById(R.id.continue_button3).setVisibility(View.VISIBLE);

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mDatabaseRef.child(getUid()).child("name").setValue(name);
                                    findViewById(R.id.updating_profile_layout).setVisibility(View.GONE);
                                    findViewById(R.id.final_layout).setVisibility(View.VISIBLE);
                                    Toast.makeText(getBaseContext(), "Completed Successfully",
                                            Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }, 3000);



                        }
                        else {
                            toast("Something went wrong");
                        }
                    }
                });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.continue_button1:
                findViewById(R.id.layout2_welcome).setVisibility(View.GONE);
                findViewById(R.id.layout2_username).setVisibility(View.VISIBLE);
                break;
            case R.id.continue_button2:
                String name = mNameField.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    mNameField.setError("Name required");
                    return;
                }
                updateUserProfile();
                break;
            case R.id.snatch_button:
                findViewById(R.id.snatch_button).setVisibility(View.GONE);
                openGallery();
                break;
            case R.id.continue_button3:
                findViewById(R.id.layout2_userphoto).setVisibility(View.GONE);
                findViewById(R.id.final_layout).setVisibility(View.VISIBLE);
                break;
            case R.id.final_button:
                finishProfileSetUp();
                break;

        }
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
                } else {
                    toast("Upload failed");
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

    void downloadPhoto() {
        mProgressBar.setVisibility(View.VISIBLE);
        mStorageRef.child("profile-photo").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                toast("Download Complete");
                mProgressBar.setVisibility(View.GONE);
                mDatabaseRef.child(getUid()).child("photo").setValue(uri.toString());
                GlideUtils.loadDisplayPhoto(uri.toString(), mUserPhoto, GlideUtils.CIRCLE_TRANSFORM);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                findViewById(R.id.edit_profile_uploading_photo).setVisibility(View.GONE);
                toast("Download failed");
            }
        });
    }
}
