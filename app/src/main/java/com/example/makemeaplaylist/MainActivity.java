package com.example.makemeaplaylist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Song>>, AdapterView.OnItemSelectedListener {
    private String mAskFor;
    private String mParam;

    private String mKey;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ApplicationInfo ai = null;
        try {
            ai = getApplicationContext().getPackageManager()
                    .getApplicationInfo(getApplicationContext().getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
        Object value = ai.metaData.get("keyValue");
        mKey = (String) value;

        if (LoaderManager.getInstance(this).getLoader(0) != null) {
            LoaderManager.getInstance(this).initLoader(0, null, this);
        }

        final Button button = findViewById(R.id.button);
        button.setOnClickListener(v -> {
            // Code here executes on main thread after user presses button
            onButtonPress();
        });

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_frame, new OneTextView());
        ft.commit();

        Spinner spinner = findViewById(R.id.spinner);
        if (spinner != null) {
            spinner.setOnItemSelectedListener(this);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, getResources().getStringArray(R.array.dropdown));
        adapter.setDropDownViewResource(R.layout.spinner_item);

        assert spinner != null;
        spinner.setAdapter(adapter);

        mAskFor = "Genre";
        mParam = "Jazz";
    }

    public void onButtonPress() {
        if (!mAskFor.equals("complete")) {
            OneTextView fragment = (OneTextView)
                    getSupportFragmentManager().findFragmentById(R.id.fragment_frame);
            assert fragment != null;
            EditText edit = (EditText) fragment.requireView().findViewById(R.id.editView);
            String text = edit.getText().toString();

            if (!text.equals("")) {
                mParam = text;
            }

            Bundle keyBundle = new Bundle();
            keyBundle.putString("paramString", mParam);
            keyBundle.putString("askForString", mAskFor);
            LoaderManager.getInstance(this).restartLoader(0, keyBundle, this);
        } else {
            TenTextView fragment = (TenTextView) getSupportFragmentManager().findFragmentById(R.id.fragment_frame);
            assert fragment != null;
            View fragmentView = fragment.requireView();
            EditText[] et = {fragmentView.findViewById(R.id.songName1), fragmentView.findViewById(R.id.songName2),
                    fragmentView.findViewById(R.id.songName3), fragmentView.findViewById(R.id.songName4), fragmentView.findViewById(R.id.songName5),
                    fragmentView.findViewById(R.id.artist1), fragmentView.findViewById(R.id.artist2), fragmentView.findViewById(R.id.artist3),
                    fragmentView.findViewById(R.id.artist4), fragmentView.findViewById(R.id.artist5)};
            String[] names = new String[5];
            String[] artists = new String[5];
            for (int i = 0; i < 10; i++) {
                if (i < 5) {
                    names[i] = et[i].getText().toString();
                } else {
                    artists[i - 5] = et[i].getText().toString();
                }
            }

            Bundle keyBundle = new Bundle();
            keyBundle.putStringArray("list1", names);
            keyBundle.putStringArray("list2", artists);
            LoaderManager.getInstance(this).restartLoader(1, keyBundle, this);
        }
    }

    @NonNull
    @Override
    public Loader<ArrayList<Song>> onCreateLoader(int id, @Nullable Bundle args) {
        if (id == 0) {
            String paramString = "Jazz";
            String askForString = "Genre";

            if (args != null) {
                paramString = args.getString("paramString");
                askForString = args.getString("askForString");
            }

            return new AILoader(this, askForString, paramString, mKey);
        } else {
            String[] names = new String[5];
            String[] artists = new String[5];

            if (args != null) {
                names = args.getStringArray("list1");
                artists = args.getStringArray("list2");
            }

            ArrayList<Song> songs = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                if (names[i] != null && !names[i].trim().equals("") && artists[i] != null && !artists[i].trim().equals("")) {
                    songs.add(new Song(artists[i], names[i]));
                }
            }

            return new AILoader(this, songs, songs.size() + 5, mKey);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<Song>> loader, ArrayList<Song> songList) {
        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> artists = new ArrayList<>();

        songList.forEach(s -> {
            names.add(s.getName());
            artists.add(s.getArtist());
        });

        Intent intent = new Intent(this, OutputActivity.class);
        intent.putStringArrayListExtra("names", names);
        intent.putStringArrayListExtra("artists", artists);
        startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        if (position == 0) {
            if (mAskFor.equals("complete")) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_frame, new OneTextView());
                ft.commit();
                getSupportFragmentManager().executePendingTransactions();
            }
            OneTextView fragment = (OneTextView)
                    getSupportFragmentManager().findFragmentById(R.id.fragment_frame);
            assert fragment != null;
            EditText edit = (EditText) fragment.requireView().findViewById(R.id.editView);
            edit.setHint("Enter a genre");
            mAskFor = "Genre";
        } else if (position == 1) {
            if (mAskFor.equals("complete")) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_frame, new OneTextView());
                ft.commit();
                getSupportFragmentManager().executePendingTransactions();
            }
            OneTextView fragment = (OneTextView)
                    getSupportFragmentManager().findFragmentById(R.id.fragment_frame);
            assert fragment != null;
            EditText edit = (EditText) fragment.requireView().findViewById(R.id.editView);
            edit.setHint("Enter an artist");
            mAskFor = "Artist";
        } else if (position == 2) {
            if (!mAskFor.equals("complete")) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_frame, new TenTextView());
                ft.commit();
                getSupportFragmentManager().executePendingTransactions();
            }
            mAskFor = "complete";
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<Song>> loader) {

    }
}