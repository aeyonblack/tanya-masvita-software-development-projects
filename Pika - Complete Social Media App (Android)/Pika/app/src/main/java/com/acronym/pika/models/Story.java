package com.acronym.pika.models;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Story {

    public String uid;
    public String author;
    public long createdDate;
    public String imageUrl;
    public int viewsCount;
    public int likesCount;
    public Map<String, Boolean> views = new HashMap<>();
    public Map<String, Boolean> likes = new HashMap<>();

    public Story() {
        // Default empty constructor
    }

    public Story(String uid, String author, String imageUrl) {
        createdDate = Calendar.getInstance().getTimeInMillis();
        this.uid = uid;
        this.author = author;
        this.imageUrl = imageUrl;
    }


}
