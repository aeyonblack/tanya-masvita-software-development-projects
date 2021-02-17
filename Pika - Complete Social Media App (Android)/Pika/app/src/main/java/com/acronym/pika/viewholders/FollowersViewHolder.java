package com.acronym.pika.viewholders;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.acronym.pika.R;
import com.acronym.pika.models.User;
import com.acronym.pika.utils.GlideUtils;

public class FollowersViewHolder extends RecyclerView.ViewHolder {

    private TextView username;
    private ImageView profilePhoto;

    public FollowersViewHolder(@NonNull View itemView) {
        super(itemView);
        username = itemView.findViewById(R.id.following_username);
        profilePhoto = itemView.findViewById(R.id.following_profile_pic);
    }

    public void bindTo(User user) {
        username.setText(user.name);
        GlideUtils.loadDisplayPhoto(user.photo, profilePhoto, GlideUtils.CIRCLE_TRANSFORM);
    }

}
