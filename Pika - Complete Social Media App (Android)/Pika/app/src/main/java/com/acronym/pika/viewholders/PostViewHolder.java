package com.acronym.pika.viewholders;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.acronym.pika.R;
import com.acronym.pika.models.Post;
import com.acronym.pika.utils.GlideUtils;
import com.acronym.pika.utils.TimestampUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class PostViewHolder extends RecyclerView.ViewHolder {

    /*Database*/
    public DatabaseReference mPostRef;
    public ValueEventListener mPostListener;

    private ImageView imagePost;
    private ImageView authorPhoto;
    private FrameLayout imageHolder;
    private TextView postAuthorView;
    private TextView postTextView;
    private TextView timestampTextView;
    public TextView reactionsCount;
    private TextView dislikesCount;
    private TextView postBigText;
    private TextView picCaption;
    private TextView commentsCount;

    /*Like, dislike and comment button*/
    public FrameLayout likeButton;
    public FrameLayout dislikeButton;
    public FrameLayout commentButton;
    public FrameLayout lolButton;
    public ImageView likeImg;
    public ImageView dislikeImg;
    public ImageView lolsImage;
    public TextView firstCommentView;
    private TextView authorName;

    /*Like and dislike TextViews*/
    public TextView likeTextView;
    public TextView dislikeTextView;
    public TextView lolTextView;

     /*Comment items*/
    public TextView commentUsername;
    public ImageView commentPhoto;
    public RelativeLayout postCommentView;
    private View view1;
    private View view2;

    public PostViewHolder(@NonNull View itemView) {
        super(itemView);
        imagePost = itemView.findViewById(R.id.image_post_view);
        imageHolder = itemView.findViewById(R.id.post_image_holder);
        authorPhoto = itemView.findViewById(R.id.post_profilePhoto);
        postTextView = itemView.findViewById(R.id.post_thePost);
        timestampTextView = itemView.findViewById(R.id.post_timeStamp);
        postAuthorView = itemView.findViewById(R.id.post_author);
        likeButton = itemView.findViewById(R.id.like_bt);
        dislikeButton = itemView.findViewById(R.id.dislike_bt);
        commentButton = itemView.findViewById(R.id.comment_bt);
        reactionsCount = itemView.findViewById(R.id.likes_count);
        dislikesCount = itemView.findViewById(R.id.dislikes_count);
        likeTextView = itemView.findViewById(R.id.like_tv);
        dislikeTextView = itemView.findViewById(R.id.dislike_tv);
        likeImg = itemView.findViewById(R.id.like_img);
        dislikeImg = itemView.findViewById(R.id.dislike_img);
        postBigText = itemView.findViewById(R.id.the_postBigText);
        picCaption = itemView.findViewById(R.id.pic_caption_view);
        commentsCount = itemView.findViewById(R.id.comments_count);
        firstCommentView = itemView.findViewById(R.id.firstComments);
        commentPhoto = itemView.findViewById(R.id.post_commenter_photo);
        commentUsername = itemView.findViewById(R.id.post_commenter_name);
        postCommentView = itemView.findViewById(R.id.post_comment_view);
        view1 = itemView.findViewById(R.id.img_post_1l);
        view2 = itemView.findViewById(R.id.img_post_2l);
        lolButton = itemView.findViewById(R.id.lol_button);
        lolsImage = itemView.findViewById(R.id.lol_img);
        lolTextView = itemView.findViewById(R.id.lol_tv);
        authorName = itemView.findViewById(R.id.post_author3);
    }

    @SuppressLint("SetTextI18n")
    public void bindTo(Post post) {

        // Load the image only if it exists
        if (post.imagePost != null) {
            imagePost.setVisibility(View.VISIBLE);
            GlideUtils.loadDisplayPhoto(post.imagePost, imagePost, GlideUtils.NO_TRANSFORM);
            if (post.picCaption != null) {
                picCaption.setVisibility(View.VISIBLE);
                authorName.setVisibility(View.VISIBLE);
                authorName.setText(post.author.toLowerCase() + imagePost.getContext().getString(R.string.ellipses));
                picCaption.setText(post.picCaption);
            }
            else {
                picCaption.setVisibility(View.GONE);
                authorName.setVisibility(View.GONE);
            }
        }
        else {
            imagePost.setVisibility(View.GONE);
            picCaption.setVisibility(View.GONE);
            authorName.setVisibility(View.GONE);
        }

        /*Load the background color only if it exists*/
        if (post.bigTextPost != null) {
            imageHolder.setVisibility(View.VISIBLE);
            switch (post.backgroundColor) {
                case "grad_1":
                    imageHolder.setBackground(imageHolder.getContext().getDrawable(R.drawable.grad_1));
                    break;
                case "grad_2":
                    imageHolder.setBackground(imageHolder.getContext().getDrawable(R.drawable.grad_2));
                    break;
                case "grad_3":
                    imageHolder.setBackground(imageHolder.getContext().getDrawable(R.drawable.grad_3));
                    break;
                case "grad_4":
                    imageHolder.setBackground(imageHolder.getContext().getDrawable(R.drawable.grad_4));
                    break;
                case "grad_5":
                    imageHolder.setBackground(imageHolder.getContext().getDrawable(R.drawable.grad_5));
                    break;
                case "grad_6":
                    imageHolder.setBackground(imageHolder.getContext().getDrawable(R.drawable.grad_6));
                    break;
                case "grad_7":
                    imageHolder.setBackground(imageHolder.getContext().getDrawable(R.drawable.grad_7));
                    break;
                case "grad_8":
                    imageHolder.setBackground(imageHolder.getContext().getDrawable(R.drawable.grad_8));
                    break;
                case "grad_9":
                    imageHolder.setBackground(imageHolder.getContext().getDrawable(R.drawable.grad_9));
                    break;
                case "grad_10":
                    imageHolder.setBackground(imageHolder.getContext().getDrawable(R.drawable.grad_10));
                    break;
                    default:
                        int color = Color.parseColor(post.backgroundColor);
                        imageHolder.setBackgroundColor(color);
                        break;
            }
            postBigText.setText(post.bigTextPost);
        }
        else {
            imageHolder.setVisibility(View.GONE);
        }

        // Hide the post TextView if the post is null
        if (post.textPost == null) {
            postTextView.setVisibility(View.GONE);
        }
        else {
            if (postTextView.getVisibility() == View.GONE) {
                postTextView.setVisibility(View.VISIBLE);
            }
            postTextView.setText(post.textPost);
        }

        GlideUtils.loadDisplayPhoto(post.authorPhoto, authorPhoto, GlideUtils.CIRCLE_TRANSFORM);
        postAuthorView.setText(post.author.toLowerCase());
        postTextView.setText(post.textPost);

        /*Add a timestamp*/
        CharSequence date = TimestampUtil.getRelativeTimeSpanStringShort(postAuthorView.getContext(), post.createdDate);
        timestampTextView.setText(date);

        dislikesCount.setText("Dislikes: " +  String.valueOf(post.dislikesCount));

        if (post.likesCount > 1) {
            reactionsCount.setText(String.valueOf(post.likesCount) + " likes");
        }
        else if (post.likesCount == 1) {
            if (post.likes.containsKey(getUid())) {
                reactionsCount.setText("You liked this");
            } else {
                reactionsCount.setText(String.valueOf(post.likesCount) + " like");
            }
        }
        else {
            reactionsCount.setText(String.valueOf(post.likesCount) + " likes");
        }

        if (post.commentsCount == 0 || post.commentsCount > 1) {
            commentsCount.setText(String.valueOf(post.commentsCount) + " comments");
        }
        else {
            commentsCount.setText("1 comment");
        }

        if (post.lolCount > 1 || post.lolCount < 1) {
            lolTextView.setText(String.valueOf(post.lolCount) + " LOLS");
        }
        else {
            lolTextView.setText(String.valueOf(post.lolCount) + " LOL");
        }

    }

    private String getUid() {
        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }
}
