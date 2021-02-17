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

import com.acronym.pika.models.Item;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SellActivity extends BaseActivity{

    private ImageView mSellItemImage;
    private EditText mTitleField;
    private EditText mDescriptionField;
    private EditText mPriceField;

    /*Database References*/
    private DatabaseReference mDatabaseRef;

    /*Storage Reference*/
    private StorageReference mStorageRef;

    private Uri itemImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference("market-place-uploads");

        /*Initialize variables*/
        mTitleField = findViewById(R.id.item_title_et);
        mDescriptionField = findViewById(R.id.item_description_et);
        mPriceField = findViewById(R.id.item_price_et);

        mSellItemImage = findViewById(R.id.sell_item_image);
        Button mSellItemButton = findViewById(R.id.sell_button);

        Button mSnapButton = findViewById(R.id.snap_button);

        mSnapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        mSellItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sellItem();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            assert data != null;
            Uri imageUrl = data.getData();
            if (imageUrl != null) {
                setSellItemImage(imageUrl);
                GlideUtils.loadDisplayPhoto(imageUrl.toString(), mSellItemImage,
                        GlideUtils.NO_TRANSFORM);
            }
        }
    }

    private void sellItem() {
        if (!isValid()) {
            return;
        }

        if (getSellItemImage() == null) {
            toast("Take a pic of the item you are selling");
            return;
        }

        final String title = mTitleField.getText().toString();
        final String description = mDescriptionField.getText().toString();
        final String price = mPriceField.getText().toString();

        toast("Posting to sell...");
        mDatabaseRef.child("users").child(getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                if (user == null) {
                    toast("Could not fetch user");
                    return;
                }

                writeToDatabase(user, title, description, price);
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void writeToDatabase(User user, String title, String description, String price) {
        String key = mDatabaseRef.child("market-place").push().getKey();

        Item item = new Item(user.name, user.photo, title, description, price);
        uploadPostImage(getSellItemImage(), item, key);
        Map<String, Object> itemValues = item.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/market-place/" + key, itemValues);
        mDatabaseRef.updateChildren(childUpdates);
    }

    private void uploadPostImage(Uri itemImage, final Item item, final String key) {
        final StorageReference storageReference = mStorageRef.child(getUid()).child(key)
                .child(Objects.requireNonNull(itemImage.getLastPathSegment()));
        storageReference.putFile(itemImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    toast("Uploaded successfully");
                    handler(storageReference, item, key);
                }
            }
        });
    }

    private void uploadUserImage(Uri itemImage, Item item, String key) {
        final StorageReference storageReference = mStorageRef.child(getUid()).child(key)
                .child(Objects.requireNonNull(itemImage.getLastPathSegment()));
        storageReference.putFile(itemImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    toast("Uploaded successfully");
                }
            }
        });
    }

    private void handler(final StorageReference ref, final Item item, final String key) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                downloadUrl(ref, item, key);
            }
        },100);
    }

    private void downloadUrl(StorageReference ref, final Item item, final String key) {
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
               if (item != null) {
                   mDatabaseRef.child("market-place").child(key).child("itemImage")
                           .setValue(uri.toString());
                   item.setItemImage(uri.toString());
               }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                toast("failed to retrieve image");
            }
        });
    }

    /*Validate the form*/
    private boolean isValid() {
        boolean valid = true;
        String title = mTitleField.getText().toString();
        if (TextUtils.isEmpty(title)) {
            mTitleField.setError("Required");
            valid = false;
        }
        else {
            mTitleField.setError(null);
        }

        String description = mDescriptionField.getText().toString();
        if (TextUtils.isEmpty(description)) {
            mDescriptionField.setError("Required");
            valid = false;
        }
        else {
            mDescriptionField.setError(null);
        }

        String price = mPriceField.getText().toString();
        if (TextUtils.isEmpty(price)) {
            mPriceField.setError("Required");
            valid = false;
        }
        else {
            mPriceField.setError(null);
        }

        return valid;
    }

    private void setSellItemImage(Uri image) {
        this.itemImage = image;
    }

    private Uri getSellItemImage() {
        return itemImage;
    }
}
