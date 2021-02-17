package com.acronym.pika.models;

public class MenuItem {
    private String itemName;
    private int imageResource;
    private int color;

    public MenuItem(String itemName, int imageResource, int color) {
        this.itemName = itemName;
        this.imageResource = imageResource;
        this.color = color;
    }

    public MenuItem(String itemName, int imageResource) {
        this.itemName = itemName;
        this.imageResource = imageResource;
    }

    public String getItemName() {
        return itemName;
    }

    public int getImageResource() {
        return imageResource;
    }

    public int getColor() {
        return color;
    }
}
