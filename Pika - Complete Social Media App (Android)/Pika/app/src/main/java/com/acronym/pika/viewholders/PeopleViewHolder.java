package com.acronym.pika.viewholders;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.acronym.pika.R;
import com.acronym.pika.models.User;
import com.acronym.pika.utils.GlideUtils;

public class PeopleViewHolder extends RecyclerView.ViewHolder {

    private ImageView profilePhoto;
    private TextView username;
    private TextView userAge;
    private TextView userLocation;
    private TextView userBio;
    private TextView userRelationship;
    public Button followButton;

    public PeopleViewHolder(@NonNull View itemView) {
        super(itemView);
        profilePhoto = itemView.findViewById(R.id.user_profile_photo);
        username = itemView.findViewById(R.id.user_name_tv);
        userAge = itemView.findViewById(R.id.user_age_tv);
        userLocation = itemView.findViewById(R.id.user_location_tv);
        userBio = itemView.findViewById(R.id.user_bio_tv);
        userRelationship = itemView.findViewById(R.id.user_relationship_tv);
    }

    public void bindTo(User user, View.OnClickListener onClickListener) {
        GlideUtils.loadDisplayPhoto(user.photo, profilePhoto, GlideUtils.CIRCLE_TRANSFORM);
        username.setText("@" + user.name.replaceFirst(" ", "_"));
        userAge.setText("Age " + String.valueOf(user.age));
        userLocation.setText(user.location);
        userRelationship.setText("Relationship: " + user.relationship);
        userBio.setText(user.bio);
    }
}
