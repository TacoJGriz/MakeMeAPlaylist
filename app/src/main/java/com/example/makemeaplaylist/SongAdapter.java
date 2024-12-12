package com.example.makemeaplaylist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import android.widget.TextView;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {

    private final ArrayList<Song> songArrayList;
    private final int type;

    // Constructor
    public SongAdapter(ArrayList<Song> songArrayList) {
        this.songArrayList = songArrayList;
        if(!songArrayList.get(0).getArtist().equals("") && songArrayList.get(0).getArtist() != null) {
            type = 1;
        } else {
            type = 2;
        }
    }

    @NonNull
    @Override
    public SongAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (type == 2) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_output2, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_output, parent, false);
        }
        return new ViewHolder(view, type);
    }

    @Override
    public void onBindViewHolder(@NonNull SongAdapter.ViewHolder holder, int position) {
        Song song = songArrayList.get(position);
        if (type == 1) {
            holder.songName.setText(song.getName());
            holder.artist.setText(song.getArtist());
        } else {
            holder.songText.setText(song.toString());
        }
    }

    @Override
    public int getItemCount() {
        return songArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView songName;
        private final TextView songText;
        private final TextView artist;

        public ViewHolder(View itemView, int type) {
            super(itemView);
            if(type == 1) {
                songName = itemView.findViewById(R.id.songName);
                artist = itemView.findViewById(R.id.artist);
                songText = null;
            } else {
                songName = null;
                artist = null;
                songText = itemView.findViewById(R.id.songText);
            }
        }
    }
}
