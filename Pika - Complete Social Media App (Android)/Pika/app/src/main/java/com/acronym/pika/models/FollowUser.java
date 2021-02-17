package com.acronym.pika.models;

public class FollowUser {

    public String uid;
    public String name;
    public String email;
    public String photo;
    public String bio;
    public String age;
    public String location;

    public FollowUser() {
        // Default public constructor
    }

    public FollowUser(String uid, String name, String email, String photo) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.photo= photo;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
