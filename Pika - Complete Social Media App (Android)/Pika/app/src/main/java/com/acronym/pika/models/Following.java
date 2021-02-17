package com.acronym.pika.models;

import java.util.HashMap;
import java.util.Map;

/*Created by Tanyaradzwa 02/04.2019*/

public class Following {

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

    public Following() {
        // Default public constructor
    }

    public Following(String uid, String username, String photo){
        this.uid = uid;
        this.name = username;
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
