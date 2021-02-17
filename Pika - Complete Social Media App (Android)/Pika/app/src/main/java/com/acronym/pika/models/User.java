package com.acronym.pika.models;

import java.util.HashMap;
import java.util.Map;

public class User {

    public String uid;
    public String email;
    public String name;
    public String status;
    public String photo;
    public String bio;
    public String mood;
    public String relationship;
    public String location;
    public int age;
    public int phone;

    /*Likes*/
    public Map<String, Boolean> likes = new HashMap<>();
    public int likesCount;

    /*Following*/
    public Map<String, Boolean> following = new HashMap<>();
    public int followingCount;

    /*Followers*/
    public Map<String, Boolean> followers = new HashMap<>();
    public int followersCount;

    /*Posts*/
    public Map<String, Boolean> posts = new HashMap<>();

    public User() {
        // Default empty constructor
    }

    public User(String email) {
        this.email = email;
        this.status = "Public";
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public Map<String,Object> toMap() {
        Map<String,Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("name", name);
        result.put("status", status);
        result.put("photo", photo);
        result.put("bio", bio);
        result.put("mood", mood);
        result.put("relationship", relationship);
        result.put("age", age);
        result.put("location", location);
        result.put("following", following);
        result.put("followingCount", followingCount);
        result.put("followers", followers);
        result.put("followersCount", followersCount);
        result.put("posts",posts);
        result.put("phone",phone);
        return result;
    }
}
