package net.chabibnr.beem;

import android.media.MediaPlayer;
import android.util.Log;

/**
 * Created by Chab on 5/1/2016.
 */
public interface PlayerEvent {

    public void onPlay();
    public void onProgressComplete();
    public void onPause();
    public void onStop();
    public void onProgress();
}