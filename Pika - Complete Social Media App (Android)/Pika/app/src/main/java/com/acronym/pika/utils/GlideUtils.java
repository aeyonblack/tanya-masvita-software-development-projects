package com.acronym.pika.utils;

import android.widget.ImageView;

import com.acronym.pika.transformations.CircleTransform;
import com.acronym.pika.transformations.RoundTransform;
import com.bumptech.glide.Glide;

public class GlideUtils {

    public static final int CIRCLE_TRANSFORM = 0;
    public static final int ROUNDED_CORNER = 1;
    public static final int NO_TRANSFORM = 2;
    private static final float RADIUS = 40f;


    /*for loading user profile photo*/
    public static void loadDisplayPhoto(String image, ImageView view, int transform) {
        if (transform == CIRCLE_TRANSFORM) {
            Glide.with(view.getContext())
                    .load(image)
                    .transform(new CircleTransform(view.getContext()))
                    .into(view);
        }
        else if (transform == ROUNDED_CORNER) {
            Glide.with(view.getContext())
                    .load(image)
                    .asBitmap()
                    .into(RoundTransform.getRoundedImageTarget(view.getContext(), view, RADIUS));
        }
        else if (transform == NO_TRANSFORM) {
            Glide.with(view.getContext())
                    .load(image)
                    .asBitmap()
                    .into(view);
        }

    }

}
