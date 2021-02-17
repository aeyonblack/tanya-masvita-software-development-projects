package com.acronym.pika.viewholders;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.acronym.pika.R;
import com.acronym.pika.models.User;
import com.acronym.pika.utils.GlideUtils;

public class NearbyPeopleViewHolder extends RecyclerView.ViewHolder {

    private ImageView profilePhoto;
    private TextView username;

    public NearbyPeopleViewHolder(@NonNull View itemView) {
        super(itemView);
        profilePhoto = itemView.findViewById(R.id.near_user_photo);
        username = itemView.findViewById(R.id.near_user_name);
    }

    public void bindTo(User user) {
        GlideUtils.loadDisplayPhoto(user.photo, profilePhoto, GlideUtils.CIRCLE_TRANSFORM);
        username.setText(user.name);
    }
}
