package com.acronym.pika.viewholders;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.acronym.pika.R;
import com.acronym.pika.models.Comment;
import com.acronym.pika.utils.GlideUtils;
import com.acronym.pika.utils.TimestampUtil;

public class CommentViewHolder extends RecyclerView.ViewHolder {

    /*Member variables*/
    private TextView commentAuthor;
    private TextView comment;
    private TextView timestamp;
    private ImageView commentAuthorPhoto;

    /*User reactions*/
    public ImageView likeImg;
    public ImageView dislikeImg;
    public ImageView loveImg;
    private TextView likesCountTv;
    private TextView dislikesCountTv;
    private TextView loveCountTv;

    public CommentViewHolder(@NonNull View itemView) {
        super(itemView);
        commentAuthor = itemView.findViewById(R.id.comment_author);
        comment = itemView.findViewById(R.id.comment);
        timestamp = itemView.findViewById(R.id.comment_timestamp);
        commentAuthorPhoto = itemView.findViewById(R.id.comment_authorPhoto);
        likeImg = itemView.findViewById(R.id.comment_like_img);
        dislikeImg = itemView.findViewById(R.id.comment_dislikes_img);
        loveImg = itemView.findViewById(R.id.comment_love_img);
        likesCountTv = itemView.findViewById(R.id.comment_likes_tv);
        dislikesCountTv = itemView.findViewById(R.id.comment_dislikes_tv);
        loveCountTv = itemView.findViewById(R.id.comment_love_tv);
    }

    public void bindTo(Comment comment) {
        this.comment.setText(comment.comment);
        commentAuthor.setText(comment.author);
        likesCountTv.setText(String.valueOf(comment.likesCount));
        dislikesCountTv.setText(String.valueOf(comment.dislikesCount));
        loveCountTv.setText(String.valueOf(comment.loveCount));
        GlideUtils.loadDisplayPhoto(comment.authorPhoto, commentAuthorPhoto, GlideUtils.CIRCLE_TRANSFORM);
        CharSequence date = TimestampUtil.getRelativeTimeSpanStringShort(commentAuthor.getContext(), comment.createdDate);
        timestamp.setText(date);
    }
}
