package com.acronym.pika.transformations;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.widget.ImageView;

import com.bumptech.glide.request.target.BitmapImageViewTarget;

public class RoundTransform {

    public static BitmapImageViewTarget getRoundedImageTarget(final Context context, final ImageView target, final float radius) {

        return new BitmapImageViewTarget(target) {
            @Override
            protected void setResource(Bitmap resource) {
                super.setResource(resource);
                RoundedBitmapDrawable roundedBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                roundedBitmapDrawable.setCornerRadius(radius);
                target.setImageDrawable(roundedBitmapDrawable);
            }
        };
    }
}
