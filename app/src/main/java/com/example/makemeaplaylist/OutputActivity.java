package com.example.makemeaplaylist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

public class OutputActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle);

        RecyclerView recyclerView = findViewById(R.id.recycler);
        Bundle bundle = getIntent().getExtras();

        ArrayList<String> names = bundle.getStringArrayList("names");
        ArrayList<String> artists = bundle.getStringArrayList("artists");

        ArrayList<Song> songArrayList = new ArrayList<>();

        for (int i = 0; i < names.size(); i++) {
            songArrayList.add(new Song(artists.get(i), names.get(i)));
        }

        SongAdapter songAdapter = new SongAdapter(songArrayList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(songAdapter);
    }
}