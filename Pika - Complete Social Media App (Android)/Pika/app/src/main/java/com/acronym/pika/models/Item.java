package com.acronym.pika.models;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Item {

    public String sellerName;
    public String sellerPhoto;
    public String title;
    public String description;
    public String price;
    public String itemImage;
    public long createdDate;

    public Item() {
        // Default constructor
    }

    public Item(String sellerName, String sellerPhoto, String title, String description, String price) {
        createdDate = Calendar.getInstance().getTimeInMillis();
        this.sellerPhoto = sellerPhoto;
        this.sellerName = sellerName;
        this.title = title;
        this.description = description;
        this.price = price;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("title", title);
        result.put("description", description);
        result.put("price", price);
        result.put("createdDate", createdDate);
        result.put("sellerName", sellerName);
        result.put("sellerPhoto", sellerPhoto);
        result.put("itemImage", itemImage);
        return result;
    }

    public void setItemImage(String itemImage) {
        this.itemImage = itemImage;
    }

}
