package com.himanshu.musicapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.himanshu.musicapp.MusicPlayActivity;
import com.himanshu.musicapp.R;
import com.himanshu.musicapp.model.MusicModel;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<MusicModel> listItems;
    private Context context;

    public MyAdapter(List<MusicModel> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_music, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final MusicModel model = listItems.get(position);

        holder.titleText.setText(model.getTitle());
        holder.artistText.setText(model.getArtist());
        holder.durationText.setText(secondToTimer(model.getDuration()));
        Glide.with(context).load(model.getImage()).into(holder.imageView);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MusicPlayActivity.class);
                intent.putExtra("MUSIC", model);
                intent.putExtra("type","json");
                v.getContext().startActivity(intent);
            }
        });
    }

    private String secondToTimer(long second) {
        String timeString = "", minuteString, secondString;
        int hours = 0;

        int sec = (int) second % 60;
        int min = (int) second / 60;
        if (min > 60) {
            hours += (min / 60);
            min = min % 60;
        }

        if (min < 10) {
            minuteString = "0" + min;
        } else{
            minuteString = String.valueOf(min);
        }

        if (sec < 10) {
            secondString = "0" + sec;
        } else{
            secondString = String.valueOf(sec);
        }

        if (hours > 1) {
            timeString = timeString + hours + ":" + minuteString + ":" + secondString;
        } else {
            timeString = timeString + minuteString + ":" + secondString;
        }
        return timeString;

    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView titleText, artistText, durationText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_music_image);
            titleText = itemView.findViewById(R.id.item_music_title);
            artistText = itemView.findViewById(R.id.item_music_artist);
            durationText = itemView.findViewById(R.id.item_music_duration);
        }
    }
}

