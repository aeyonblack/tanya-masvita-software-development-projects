package com.acronym.pika.models;

import com.acronym.pika.utils.TimestampUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Post {

    public String uid;
    public String textPost;
    public String bigTextPost;
    public String picCaption;
    public String author;
    public String authorPhoto;
    public String imagePost;
    public String timeStamp;
    public String backgroundColor;
    public int likesCount;
    public int dislikesCount;
    public int commentsCount;
    public int lolCount;
    public Map<String, Boolean> comments = new HashMap<>();
    public Map<String, Boolean> likes = new HashMap<>();
    public Map<String, Boolean> dislikes = new HashMap<>();
    public Map<String, Boolean> lols = new HashMap<>();


    public long createdDate;

    public Post() {
        // Default empty public constructor
    }

    /*Main Constructor*/
    public Post(String uid, String author, String authorPhoto) {
        createdDate = Calendar.getInstance().getTimeInMillis();
        this.uid = uid;
        this.author = author;
        this.authorPhoto = authorPhoto;

    }

    /*Color Post constructor*/
    public void setTextPost(String textPost) {
        this.textPost = textPost;
    }

    public void setBigTextPost(String bigTextPost) {
        this.bigTextPost = bigTextPost;
    }

    public void setAuthorPhoto(String authorPhoto) {
        this.authorPhoto = authorPhoto;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setImagePost(String imagePost) {
        this.imagePost = imagePost;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setPicCaption(String picCaption) {
        this.picCaption = picCaption;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("authorPhoto", authorPhoto);
        result.put("textPost", textPost);
        result.put("picCaption", picCaption);
        result.put("timeStamp", TimestampUtil.getFirebaseDateFormat().format(new Date(createdDate)));
        result.put("likesCount", likesCount);
        result.put("imagePost", imagePost);
        result.put("likes", likes);
        result.put("dislikesCount", dislikesCount);
        result.put("dislikes", dislikes);
        result.put("bigTextPost", bigTextPost);
        result.put("backgroundColor", backgroundColor);
        result.put("createdDate", createdDate);
        result.put("comments", comments);
        result.put("commentsCount", commentsCount);
        result.put("lols", lols);
        result.put("lolCount", lolCount);
        return result;
    }

}
