package net.chabibnr.beem;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;

import java.io.IOException;

/**
 * Created by Chab on 5/1/2016.
 */
public class PlayerControl {

    private Uri currentMusicUri;
    MediaPlayer mediaPlayer = null;
    private Context mContext;
    static Integer currentSelectedMusic = null;
    PlayerControl playerControl;
    Utilities playerUtil;
    Event mediaPlayerControlEvent;

    public PlayerControl(Context c, Event e) {
        allDeclaration(c);
        mediaPlayerControlEvent = e;
    }

    public PlayerControl(Context c) {
        allDeclaration(c);
    }

    private void allDeclaration(Context c) {
        playerUtil = new Utilities();
        mContext = c;
        playerControl = this;
    }

    public PlayerControl setMusicUri(int idMusic) {
        currentMusicUri = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, "" + idMusic);
        return this;
    }

    public static void setMusicPosition(int idMusic) {
        currentSelectedMusic = idMusic;
    }

    public void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            setRunningProgress(false);
        }
        if (mediaPlayerControlEvent != null) {
            mediaPlayerControlEvent.onPause();
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            setRunningProgress(false);
        }

        if (mediaPlayerControlEvent != null) {
            mediaPlayerControlEvent.onStop();
        }
    }

    public void resume() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
            setRunningProgress(true);
        }

        if (mediaPlayerControlEvent != null) {
            mediaPlayerControlEvent.onResume();
        }
    }

    public void play() {
        play(0);
    }

    public void play(int currentIndexIfPlayList) {
        setRunningProgress(false);
        PlayerControl.setMusicPosition(currentIndexIfPlayList);
        //System.exit(0);
        final Uri musicPath = getMusicUri();
        //Uri defaultRingtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        } else {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
            } catch (Exception e) {

            }
            mediaPlayer.release();
            mediaPlayer = new MediaPlayer();
        }

        try {

            mediaPlayer.setDataSource(mContext, musicPath);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {

                    if (mediaPlayerControlEvent != null)
                        mediaPlayerControlEvent.onEnd();

                    //setRunningProgress(false);
                    mp.release();
                }
            });

            mediaPlayer.start();
            if (mediaPlayerControlEvent != null)
                mediaPlayerControlEvent.onPlay();

            progress();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private Handler mHandler = new Handler();
    protected Runnable onTimeTaskChange;

    public void progress() {
        onTimeTaskChange = new Runnable() {
            @Override
            public void run() {
                Log.d("AAA", "Progress Refresh :" + getCurrentTimeDuration() + ": of :" + getTimeDuration());
                if (mediaPlayerControlEvent != null)
                    mediaPlayerControlEvent.onPlaying();

                mHandler.postDelayed(onTimeTaskChange, 1000);
            }
        };
        setRunningProgress(true);
    }

    public void setRunningProgress(boolean running) {
        if (running) {
            mHandler.postDelayed(onTimeTaskChange, 1000);
        } else {
            mHandler.removeCallbacks(onTimeTaskChange);
        }
    }

    public Uri getMusicUri() {
        return currentMusicUri;
    }

    public static Integer getMusicPosition() {
        return currentSelectedMusic;
    }

    public long getCurrentDuration() {
        return mediaPlayer != null ? mediaPlayer.getCurrentPosition() : 0;
    }

    public long getDuration() {
        return mediaPlayer != null ? mediaPlayer.getDuration() : 0;
    }

    public String getTimeDuration() {
        return playerUtil.milliSecondsToTimer(getDuration());
    }

    public String getCurrentTimeDuration() {
        return playerUtil.milliSecondsToTimer(getCurrentDuration());
    }

    public int getPercentageDuration() {
        return playerUtil.getProgressPercentage(getCurrentDuration(), getDuration());
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    public class Utilities {

        /**
         * Function to convert milliseconds time to
         * Timer Format
         * Hours:Minutes:Seconds
         */
        public String milliSecondsToTimer(long milliseconds) {
            String finalTimerString = "";
            String secondsString = "";

            // Convert total duration into time
            int hours = (int) (milliseconds / (1000 * 60 * 60));
            int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
            int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
            // Add hours if there
            if (hours > 0) {
                finalTimerString = hours + ":";
            }

            // Prepending 0 to seconds if it is one digit
            if (seconds < 10) {
                secondsString = "0" + seconds;
            } else {
                secondsString = "" + seconds;
            }

            finalTimerString = finalTimerString + minutes + ":" + secondsString;

            // return timer string
            return finalTimerString;
        }

        /**
         * Function to get Progress percentage
         *
         * @param currentDuration
         * @param totalDuration
         */
        public int getProgressPercentage(long currentDuration, long totalDuration) {
            Double percentage = (double) 0;

            long currentSeconds = (int) (currentDuration / 1000);
            long totalSeconds = (int) (totalDuration / 1000);

            // calculating percentage
            percentage = (((double) currentSeconds) / totalSeconds) * 100;

            // return percentage
            return percentage.intValue();
        }

        /**
         * Function to change progress to timer
         *
         * @param progress      -
         * @param totalDuration returns current duration in milliseconds
         */
        public int progressToTimer(int progress, int totalDuration) {
            int currentDuration = 0;
            totalDuration = (int) (totalDuration / 1000);
            currentDuration = (int) ((((double) progress) / 100) * totalDuration);

            // return current duration in milliseconds
            return currentDuration * 1000;
        }
    }

    public interface Event {

        public void onPlay();

        public void onPlaying();

        public void onEnd();

        public void onPause();

        public void onResume();

        public void onStop();
    }

}
