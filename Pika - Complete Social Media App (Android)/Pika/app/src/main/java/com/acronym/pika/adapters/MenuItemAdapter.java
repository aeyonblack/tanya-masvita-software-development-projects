package com.acronym.pika.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.acronym.pika.R;
import com.acronym.pika.models.MenuItem;
import com.acronym.pika.viewholders.MenuItemViewHolder;

import java.util.ArrayList;

public class MenuItemAdapter extends RecyclerView.Adapter<MenuItemViewHolder> {

    private ArrayList<MenuItem> menuItems;
    private Context mContext;

    public MenuItemAdapter(Context context, ArrayList<MenuItem> menuItems) {
        this.menuItems = menuItems;
        this.mContext = context;
    }

    @NonNull
    @Override
    public MenuItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        return new MenuItemViewHolder(inflater.inflate(R.layout.menu_list_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MenuItemViewHolder menuItemViewHolder, int i) {
        menuItemViewHolder.bindTo(menuItems.get(i));
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }
}
