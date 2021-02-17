package com.acronym.pika.models;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Comment {

    public String uid;
    public String author;
    public String comment;
    public String timestamp;
    public long createdDate;
    public String authorPhoto;
    public int likesCount;
    public int dislikesCount;
    public int loveCount;
    public Map<String, Boolean> likes = new HashMap<>();
    public Map<String, Boolean> dislikes = new HashMap<>();
    public Map<String, Boolean> love = new HashMap<>();

    public Comment() {
        /*Default empty constructor*/
    }

    public Comment(String uid, String author, String comment, String authorPhoto, String timestamp) {
        this.uid = uid;
        this.author = author;
        this.comment = comment;
        this.authorPhoto = authorPhoto;
        this.timestamp = timestamp;
        createdDate = Calendar.getInstance().getTimeInMillis();

    }


}