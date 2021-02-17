package com.acronym.pika.viewholders;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.acronym.pika.R;
import com.acronym.pika.models.MenuItem;

public class MenuItemViewHolder extends RecyclerView.ViewHolder {

    /*Views*/
    private ImageView itemImage;
    private TextView itemName;

    public MenuItemViewHolder(@NonNull View itemView) {
        super(itemView);
        itemImage = itemView.findViewById(R.id.menu_item_img);
        itemName = itemView.findViewById(R.id.menu_item_name);
    }

    public void bindTo(MenuItem item) {
        itemName.setText(item.getItemName());
        itemImage.setImageResource(item.getImageResource());
    }
}
