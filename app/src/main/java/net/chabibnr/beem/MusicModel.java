package net.chabibnr.beem;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Chab on 5/1/2016.
 */
public class MusicModel {

    static final String MUSIC_ATTR_TITLE = "MUSIC_ATTR_TITLE";
    static final String MUSIC_ATTR_ARTIST = "MUSIC_ATTR_ARTIST";
    static final String MUSIC_ATTR_ID = "MUSIC_ATTR_ID";

    Context mContext;


    //private HashMap<String, String> smusicLists = new HashMap<String, String>();
    private ArrayList<HashMap<String, String>> musicLists = new ArrayList<HashMap<String, String>>();

    public MusicModel(Context c) {
        mContext = c;
        getTrackList();
    }

    public MusicListAdapter getAdapter() {
        return new MusicListAdapter(mContext, getMusics());
    }

    public MusicModel addMusic(String _id, String _title, String _artist) {
        HashMap<String, String> musicMap = new HashMap<String, String>();
        musicMap.put(MUSIC_ATTR_ID, _id);
        musicMap.put(MUSIC_ATTR_TITLE, _title);
        musicMap.put(MUSIC_ATTR_ARTIST, _artist);
        musicLists.add(musicMap);
        return this;
    }

    public int getCountMusicItem()
    {
        return getMusics().size();
    }

    public HashMap<String, String> getTrackInfo(int index)
    {
        HashMap<String, String> musicItem = new HashMap<String, String>();
        musicItem = getMusics().get(index);
        return musicItem;
    }

    private void getTrackList() {
        try {
            Cursor cursor = mContext.getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    null,
                    null,
                    null,
                    MediaStore.Audio.Media.TITLE + " ASC");

            cursor.moveToFirst();
            while (cursor.isAfterLast() == false) {
                //musicLists[position] = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                // Whatever else you need
                addMusic(
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID)),
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)),
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                );
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /* *** GETTER *** */
    public ArrayList<HashMap<String, String>> getMusics() {
        return musicLists;
    }
}
