package com.acronym.pika.utils;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;

import java.util.Random;

public class ColorUtils {

    /*ColorArray*/
    private static String[] colorArray = {
            "#ffffff",
            "#F55555",
            "#F761A1",
            "#43CBFF",
            "#623AA2",
            "#002661",
            "#6018DC",
            "#3CD500",
            "#3C8CE7",
            "#00cc99",
            "#cc99ff",
            "#43E97B",
            "#ffcc00",
            "#F83600",
            "#537895",
            "#CC208E",
            "#FA709A",
            "#32CCBC",
            "#C32BAC",
            "#333131"
    };

    /*Get color from array*/
    public static String getColorAt(int index) {
        return colorArray[index];
    }

    /*Generate a random color*/
    public static int getRandomColor() {
        Random random = new Random();
        int r = random.nextInt(255);
        int g = random.nextInt(255);
        int b = random.nextInt(255);

        return Color.rgb(r,g,b);
    }

    public static int getColor(Context context, int color) {
        return ContextCompat.getColor(context, color);
    }

}
