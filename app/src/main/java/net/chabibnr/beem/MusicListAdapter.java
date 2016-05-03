package net.chabibnr.beem;

import android.content.Context;
import android.database.Cursor;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Chab on 5/1/2016.
 */
public class MusicListAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater = null;

    public MusicListAdapter(Context context, ArrayList<HashMap<String, String>> d)
    {
        super();
        mContext=context;
        data  = d;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (convertView == null)
            vi = inflater.inflate(R.layout.item_music_list, null);

        TextView musicTitle = (TextView) vi.findViewById(R.id.item_music_title);
        TextView musicId = (TextView) vi.findViewById(R.id.item_music_id);
        TextView musicArtist = (TextView) vi.findViewById(R.id.item_music_artist);
        ImageView musicCurrentPlaying = (ImageView) vi.findViewById(R.id.item_music_image_playing);

        if((PlayerControl.getMusicPosition() != null) && position == PlayerControl.getMusicPosition()){
            Log.d("Music Adapter", "" + position);
            musicCurrentPlaying.setVisibility(View.VISIBLE);

        }else {
            musicCurrentPlaying.setVisibility(View.GONE);
        }

        HashMap<String, String> musicItem = new HashMap<String, String>();
        musicItem = data.get(position);

        musicTitle.setText(musicItem.get(MusicModel.MUSIC_ATTR_TITLE));
        musicId.setText(musicItem.get(MusicModel.MUSIC_ATTR_ID));
        musicArtist.setText(musicItem.get(MusicModel.MUSIC_ATTR_ARTIST));
        return vi;
    }
}
