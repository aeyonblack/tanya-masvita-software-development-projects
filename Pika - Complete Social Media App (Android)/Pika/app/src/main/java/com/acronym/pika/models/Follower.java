package com.acronym.pika.models;

import java.util.HashMap;
import java.util.Map;

/*Create by Tanyaradzwa 02/04.2019*/

public class Follower {

    public String uid;
    public String name;
    public String photo;
    public int followersCount;
    public int followingCount;
    public Map<String, Boolean> followers = new HashMap<>();
    public Map<String, Boolean> following = new HashMap<>();
    public String relationship;
    public int age;
    public String location;

    public Follower() {
        // Default empty constructor
    }

    public Follower(String uid, String name, String photo) {
        this.uid = uid;
        this.name = name;
        this.photo = photo;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }
}
