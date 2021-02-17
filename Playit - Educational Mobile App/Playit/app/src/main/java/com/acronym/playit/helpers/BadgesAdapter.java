package com.acronym.playit.helpers;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.acronym.playit.R;
import com.acronym.playit.models.Badge;

public class BadgesAdapter extends RecyclerView.Adapter<BadgesAdapter.BadgesViewHolder> {

    private QuizOpenHelper mDatabase;

    public BadgesAdapter(QuizOpenHelper db) {
        mDatabase = db;
    }

    @NonNull
    @Override
    public BadgesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        return new BadgesViewHolder(inflater.inflate(R.layout.badge_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BadgesViewHolder badgesViewHolder, int i) {
        Badge badge = mDatabase.queryBadge(i);
        if (badge != null)
        badgesViewHolder.bindTo(badge);
    }

    @Override
    public int getItemCount() {
        return mDatabase.progressQuery(0).getBadgeCount();
    }

    class BadgesViewHolder extends RecyclerView.ViewHolder {

        private ImageView badge;
        private TextView name;

        BadgesViewHolder(@NonNull View itemView) {
            super(itemView);
            badge = itemView.findViewById(R.id.the_badge);
            name = itemView.findViewById(R.id.badge_name);
        }

        void bindTo(Badge badge) {
            name.setText(badge.getName());
            this.badge.getBackground().setTint(Color.parseColor(badge.getColor()));
            switch (badge.getName()) {
                case "LEVEL 2":
                    this.badge.setImageResource(R.drawable.medal_no_3);
                    break;
                case "LEVEL 3":
                    this.badge.setImageResource(R.drawable.medal_no_2);
                    break;
                case "LEVEL 4":
                    this.badge.setImageResource(R.drawable.medal_no_1);
                    break;
                case "LEVEL 5":
                    this.badge.setImageResource(R.drawable.certificate_1);
                    break;
                case "STUDENT":
                    this.badge.setImageResource(R.drawable.student);
                    break;
                case "CHAMP":
                    this.badge.setImageResource(R.drawable.diploma_1);
                    break;
                case "STARFISH":
                    this.badge.setImageResource(R.drawable.starfish);
                    break;
                case "THE WITCHER":
                    this.badge.setImageResource(R.drawable.trophy_playit_2);
                    break;
                case "EINSTEIN":
                    this.badge.setImageResource(R.drawable.winner);
                    break;

            }
        }
    }
}
