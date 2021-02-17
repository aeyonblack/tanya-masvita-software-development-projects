package com.acronym.pika.viewholders;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.acronym.pika.R;
import com.acronym.pika.models.User;
import com.acronym.pika.utils.FirebaseMethods;
import com.acronym.pika.utils.GlideUtils;

public class UserListViewHolder extends RecyclerView.ViewHolder {

    private ImageView userImage;
    private TextView userName;
    private TextView userLocation;
    public Button followButton;
    public RelativeLayout userSearchRel;
    public Button unFollowButton;
    public ImageView followingImage;

    public UserListViewHolder(@NonNull View itemView) {
        super(itemView);
        userImage = itemView.findViewById(R.id.follow_profile_pic);
        userName = itemView.findViewById(R.id.follow_user_name);
        userLocation = itemView.findViewById(R.id.follow_user_location);
        followButton = itemView.findViewById(R.id.follow_button);
        userSearchRel = itemView.findViewById(R.id.user_search_rel);
        unFollowButton = itemView.findViewById(R.id.unfollow_button);
        followingImage = itemView.findViewById(R.id.following_icon_indicator);
    }

    public void bindTo(User user) {
        userName.setText(user.name);
        userLocation.setText(user.location);
        GlideUtils.loadDisplayPhoto(user.photo, userImage, GlideUtils.CIRCLE_TRANSFORM);

        if (user.uid.equals(FirebaseMethods.getUid())) {
            followButton.setVisibility(View.GONE);
            userName.setText(userName.getContext().getString(R.string.me_));
        }

    }

    public void hideFollowButton(boolean hide) {
        if (hide) {
            followButton.setText("Unfollow");
        }
        else {
            followButton.setText("Follow");
        }
    }


}
