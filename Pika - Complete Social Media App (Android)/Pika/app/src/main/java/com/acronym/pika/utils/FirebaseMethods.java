package com.acronym.pika.utils;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.acronym.pika.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class FirebaseMethods {

    private static User user;
    private static DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference();

    public static String getUid() {
        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }

    public static String getEmail() {
        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
    }

    private static DatabaseReference getBaseRef() {
        return FirebaseDatabase.getInstance().getReference();
    }

    public static DatabaseReference getPostRef() {
        return getBaseRef().child("posts");
    }

    public static DatabaseReference getCurrentUserRef() {
        return getUsersRef().child(getUid());
    }

    public static DatabaseReference getUsersRef() {
        return getBaseRef().child("users");
    }

    public static DatabaseReference getFeedRef() {
        return getBaseRef().child("feed");
    }

    public static DatabaseReference getCommentsRef() {
        return getBaseRef().child("post_comments");
    }
}
