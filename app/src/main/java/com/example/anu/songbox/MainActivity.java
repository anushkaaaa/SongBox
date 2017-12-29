package com.example.anu.songbox;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private MediaPlayer mediaPlayer;
    private ImageView artistImage;
    private TextView leftTime;
    private TextView rightTime;
    private SeekBar mseekbar;
    private Button prev;
    private Button play;
    private Button next;
    private Thread thread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpUI();
        mseekbar.setMax(mediaPlayer.getDuration());
        mseekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b){
                    mediaPlayer.seekTo(i);
                }
                SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");
                int currentPos = mediaPlayer.getCurrentPosition();
                int duration = mediaPlayer.getDuration();
                leftTime.setText(dateFormat.format(new Date(currentPos)));
                rightTime.setText(dateFormat.format(new Date(duration-currentPos)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }


    public void setUpUI(){

        mediaPlayer = new MediaPlayer();
        mediaPlayer = MediaPlayer.create(getApplicationContext(),R.raw.sugar);

        artistImage = (ImageView) findViewById(R.id.ovalId);
        leftTime = (TextView) findViewById(R.id.leftTimeId);
        rightTime = (TextView) findViewById(R.id.rightTimeId);
        mseekbar = (SeekBar) findViewById(R.id.mseekbarId);
        prev = (Button) findViewById(R.id.prevbutton);
        play = (Button) findViewById(R.id.playbutton);
        next = (Button) findViewById(R.id.nextbutton);

        prev.setOnClickListener(this);
        play.setOnClickListener(this);
        next.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.prevbutton:
                prevMusic();
                break;
            case R.id.playbutton:
                if(mediaPlayer.isPlaying()){
                    pauseMusic();
                }else {
                    startMusic();
                }
                break;
            case R.id.nextbutton:
                nextMusic();
                break;
        }
    }

    public void pauseMusic(){
        if(mediaPlayer != null){
            mediaPlayer.pause();
            play.setBackgroundResource(android.R.drawable.ic_media_play);
        }
    }
    public void startMusic(){
        if(mediaPlayer != null){
            mediaPlayer.start();
            updateThread();
            play.setBackgroundResource(android.R.drawable.ic_media_pause);
        }
    }
    public void prevMusic(){
        if(mediaPlayer.isPlaying()){
            mediaPlayer.seekTo(0);
        }
    }
    public void nextMusic(){
        if(mediaPlayer.isPlaying()){
            mediaPlayer.seekTo(mediaPlayer.getDuration());
        }
    }

    public void updateThread(){
        thread = new Thread(){
            @Override
            public void run() {
                try{

                    while (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        thread.sleep(50);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int newPosition = mediaPlayer.getCurrentPosition();
                                int newMax = mediaPlayer.getDuration();
                                mseekbar.setMax(newMax);
                                mseekbar.setProgress(newPosition);
                                leftTime.setText(String.valueOf(new java.text.SimpleDateFormat("mm:ss").format(new Date(mediaPlayer.getCurrentPosition()))));
                                rightTime.setText(String.valueOf(new java.text.SimpleDateFormat("mm:ss").format(new Date(mediaPlayer.getDuration()-mediaPlayer.getCurrentPosition()))));
                            }
                        });
                    }
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }
    //when you press back, the song will stop and resources will be released
    @Override
    protected void onDestroy() {
        if(mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        thread.interrupt();
        thread = null;
        super.onDestroy();
    }
}
