package com.example.makemeaplaylist;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.loader.content.AsyncTaskLoader;

import java.util.ArrayList;

public class AILoader extends AsyncTaskLoader<ArrayList<Song>> {
    private static final String LOG_TAG = AILoader.class.getSimpleName();

    private final String mParamString;
    private final String mAskForString;
    private final ArrayList<Song> mList;
    private final int len;
    private final int type;

    private final String mKey;

    public AILoader(@NonNull Context context, String paramString, String askForString, String key) {
        super(context);

        mParamString = paramString;
        mAskForString = askForString;
        type = 1;
        mList = null;
        len = 10;
        mKey = key;
    }

    public AILoader(@NonNull Context context, ArrayList<Song> list, int len, String key) {
        super(context);

        mParamString = "paramString";
        mAskForString = "askForString";
        type = 2;
        mList = list;
        mKey = key;
        this.len = len;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public ArrayList<Song> loadInBackground() {
        ArrayList<String> ar;
        if (type == 1) {
            ar = NetworkUtils.runBasic(mAskForString, mParamString, mKey);
            String output = ar.toString();
            Log.d(LOG_TAG, output);
        } else {
            ar = NetworkUtils.runGivenPlaylist(mList, len, mKey);
            String output = ar.toString();
            Log.d(LOG_TAG, output);
        }
        return NetworkUtils.processOutput(ar.toString(), len);
    }
}
