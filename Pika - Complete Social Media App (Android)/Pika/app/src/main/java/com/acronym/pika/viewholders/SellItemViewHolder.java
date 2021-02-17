package com.acronym.pika.viewholders;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.acronym.pika.R;
import com.acronym.pika.models.Item;
import com.acronym.pika.utils.GlideUtils;

public class SellItemViewHolder extends RecyclerView.ViewHolder {

    private ImageView sellItemImage;
    private ImageView sellerPhoto;
    private TextView sellItemTitle;
    private TextView sellItemPrice;
    private TextView sellerName;

    public SellItemViewHolder(@NonNull View itemView) {
        super(itemView);
        sellItemImage = itemView.findViewById(R.id.current_sell_item_image);
        sellerPhoto = itemView.findViewById(R.id.seller_profile_photo);
        sellItemTitle = itemView.findViewById(R.id.current_sell_item_title);
        sellItemPrice = itemView.findViewById(R.id.current_sell_item_price);
        sellerName = itemView.findViewById(R.id.seller_name);
    }

    public void bindTo(Item item) {
        sellerName.setText(item.sellerName);
        sellItemTitle.setText(item.title);
        sellItemPrice.setText(item.price);
        GlideUtils.loadDisplayPhoto(item.itemImage, sellItemImage, GlideUtils.NO_TRANSFORM);
        GlideUtils.loadDisplayPhoto(item.sellerPhoto, sellerPhoto, GlideUtils.CIRCLE_TRANSFORM);
    }

}
