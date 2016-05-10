package net.chabibnr.beem;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.HashMap;

public class PlayerActivity extends AppCompatActivity {

    int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE =1;
    ListView mMusicListView;
    MusicModel musicModel;
    Context mContext;
    PlayerControl playerControl;
    MusicListAdapter musicListAdapter;

    /* Button */
    Button prevMusic, nextMusic, playPauseMusic, stopMusic;
    TextView currentTimeMusic, totalTimeMusic, currentPlayingTextInfo;
    SeekBar seekBarMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        mContext = this;
        musicModel = new MusicModel(mContext);
        playerControl = new PlayerControl(mContext,new PlayingEvent());

        prevMusic = (Button) findViewById(R.id.playerPrevButton);
        nextMusic = (Button) findViewById(R.id.playerNextButton);
        playPauseMusic = (Button) findViewById(R.id.playerPausePlayButton);
        stopMusic = (Button) findViewById(R.id.playerStop);
        currentTimeMusic = (TextView) findViewById(R.id.playerCurrentTime);
        totalTimeMusic = (TextView) findViewById(R.id.playerTotalTime);
        seekBarMusic = (SeekBar) findViewById(R.id.playerSeekBar);
        currentPlayingTextInfo = (TextView) findViewById(R.id.playerTextInfo);
        currentPlayingTextInfo.setText("Bee Music");
        //seekBarMusic.setEnabled(false);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {

            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (shouldShowRequestPermissionRationale(
                        android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // Explain to the user why we need to read the contacts
                }

                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
                // app-defined int constant

                return;
            }

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();

            // clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        musicListAdapter = getModel().getAdapter();
        mMusicListView = (ListView) findViewById(R.id.bee_music_list);
        mMusicListView.setAdapter(musicListAdapter);
        mMusicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                boolean isCurrent = (PlayerControl.getMusicPosition() != null && PlayerControl.getMusicPosition() == position);
                if(!isCurrent) {
                    HashMap<String, String> musicItem = getModel().getMusics().get(position);
                    String sId = musicItem.get(MusicModel.MUSIC_ATTR_ID);

                    playerControl.setMusicUri(Integer.parseInt(sId));
                    playerControl.play(position);
                }
            }
        });

        prevMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (PlayerControl.getMusicPosition() != null && PlayerControl.getMusicPosition() > 0) ? PlayerControl.getMusicPosition() - 1: 0;

                HashMap<String, String> musicItem = getModel().getMusics().get(position);
                String musicID = musicItem.get(MusicModel.MUSIC_ATTR_ID);
                playerControl.setMusicUri(Integer.parseInt(musicID));
                playerControl.play(position);
            }
        });

        nextMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (PlayerControl.getMusicPosition() != null && PlayerControl.getMusicPosition() < getModel().getCountMusicItem()) ? PlayerControl.getMusicPosition() + 1: 0;

                HashMap<String, String> musicItem = getModel().getMusics().get(position);
                String musicID = musicItem.get(MusicModel.MUSIC_ATTR_ID);
                playerControl.setMusicUri(Integer.parseInt(musicID));
                playerControl.play(position);
            }
        });

        playPauseMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(playerControl.isPlaying()){
                    playerControl.pause();
                    playPauseMusic.setText(getString(R.string.player_play));
                }else if(playerControl.getPercentageDuration() > 0){
                    playerControl.resume();
                    playPauseMusic.setText(getString(R.string.player_pause));
                }else {
                    int position = (PlayerControl.getMusicPosition() != null) ? PlayerControl.getMusicPosition() : 0;

                    HashMap<String, String> musicItem = getModel().getMusics().get(position);
                    String musicID = musicItem.get(MusicModel.MUSIC_ATTR_ID);
                    playerControl.setMusicUri(Integer.parseInt(musicID));
                    playerControl.play(position);
                }
            }
        });

        stopMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerControl.stop();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_player, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private MusicModel getModel(){
        return musicModel;
    }

    private class PlayingEvent implements PlayerControl.Event{
        HashMap<String, String> infoMusic;
        @Override
        public void onPlay() {
            Log.d("AAAA", "Playing Called");
            totalTimeMusic.setText(playerControl.getTimeDuration());
            seekBarMusic.setMax(100);
            playPauseMusic.setText("Pause");
            musicListAdapter.notifyDataSetChanged();

            //get info music
            infoMusic = getModel().getTrackInfo(PlayerControl.getMusicPosition());
            currentPlayingTextInfo.setText(infoMusic.get(MusicModel.MUSIC_ATTR_TITLE));

        }

        @Override
        public void onEnd() {
            Log.d("AAA", "END");
            currentTimeMusic.setText(playerControl.getTimeDuration());
            seekBarMusic.setProgress(100);

            if(PlayerControl.getMusicPosition() < musicModel.getCountMusicItem()){
                int position = (PlayerControl.getMusicPosition() != null) ? PlayerControl.getMusicPosition() + 1 : 0;
                HashMap<String, String> musicItem = getModel().getMusics().get(position);
                String musicID = musicItem.get(MusicModel.MUSIC_ATTR_ID);
                playerControl.setMusicUri(Integer.parseInt(musicID));
                playerControl.play(position);
                Log.d("AAA", "Next");
            } else {
                Log.d("AAA", "FAil");
            }
        }

        @Override
        public void onPause() {

        }

        @Override
        public void onResume() {

        }

        @Override
        public void onStop() {

        }

        @Override
        public void onPlaying() {
            if(playerControl.getPercentageDuration() < 100) {
                currentTimeMusic.setText(playerControl.getCurrentTimeDuration());
                seekBarMusic.setProgress(playerControl.getPercentageDuration());
                Log.d("AAA "+ PlayerControl.getMusicPosition(), "Percentage " + playerControl.getPercentageDuration());
            }
        }
    }
}
