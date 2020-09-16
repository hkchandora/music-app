package com.himanshu.musicapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.himanshu.musicapp.model.MusicModel;

import java.io.File;

public class MusicPlayActivity extends AppCompatActivity {

    private MusicModel model;

    private ImageView playerImage, downloadImageBtn;
    private TextView playerTitle, playerArtist, playerPosition, playerDuration;
    private SeekBar seekBar;
    private ImageView btnBackward, btnForward, btnPlay, btnPause;

    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();

    private double startTime = 0;
    private double finalTime = 0;
    private int forwardTime = 5000;
    private int backwardTime = 5000;
    private String type = "";
    private String localData;

    private static final int PERMISSION_STORAGE_CODE= 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_play);

        playerImage = findViewById(R.id.player_image);
        playerTitle = findViewById(R.id.player_title);
        playerArtist = findViewById(R.id.player_artist);
        playerPosition = findViewById(R.id.player_position);
        playerDuration = findViewById(R.id.player_duration);
        seekBar = findViewById(R.id.player_seek_bar);
        btnBackward = findViewById(R.id.player_backward);
        btnForward = findViewById(R.id.player_forward);
        btnPlay = findViewById(R.id.player_play);
        btnPause = findViewById(R.id.player_pause);
        downloadImageBtn = findViewById(R.id.downloadImageBtn);

        mediaPlayer = new MediaPlayer();
        seekBar.setMax(100);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            type = getIntent().getExtras().getString("type");
        }

        if (type.equals("json")) {
            fetchJsonData();
        } else if (type.equals("local")) {
            fetchLocalData();
        }


        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnPause.setVisibility(View.VISIBLE);
                btnPlay.setVisibility(View.GONE);
                mediaPlayer.start();
                updateSeekBar();

            }
        });

        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnPause.setVisibility(View.GONE);
                btnPlay.setVisibility(View.VISIBLE);
                handler.removeCallbacks(updater);
                mediaPlayer.pause();
            }
        });

        btnForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalTime = mediaPlayer.getDuration();
                startTime = mediaPlayer.getCurrentPosition();
                int temp = (int) startTime;

                if ((temp + forwardTime) <= finalTime) {
                    startTime = startTime + forwardTime;
                    mediaPlayer.seekTo((int) startTime);
                }
            }
        });

        btnBackward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalTime = mediaPlayer.getDuration();
                startTime = mediaPlayer.getCurrentPosition();

                int temp = (int) startTime;

                if ((temp - backwardTime) > 0) {
                    startTime = startTime - backwardTime;
                    mediaPlayer.seekTo((int) startTime);
                }
            }
        });

        prepareMediaPlayer();

        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                SeekBar seekbar2 = (SeekBar) v;
                int playPosition = (mediaPlayer.getDuration() / 100) * seekbar2.getProgress();
                mediaPlayer.seekTo(playPosition);
                playerPosition.setText(secondToTimer(mediaPlayer.getCurrentPosition()));
                return false;
            }
        });

        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                seekBar.setSecondaryProgress(percent);
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                seekBar.setProgress(0);
                btnPlay.setVisibility(View.VISIBLE);
                btnPause.setVisibility(View.GONE);
                playerPosition.setText("00:00");
                mediaPlayer.reset();
                prepareMediaPlayer();
            }
        });
    }

    private void fetchLocalData() {
        downloadImageBtn.setVisibility(View.GONE);

        String title = getIntent().getExtras().getString("title");
        String artist = getIntent().getExtras().getString("artist");
        localData = getIntent().getExtras().getString("data");

        Glide.with(this).load(R.drawable.music).into(playerImage);
        playerTitle.setText(title);
        playerArtist.setText(artist);
    }

    private void fetchJsonData() {
        downloadImageBtn.setVisibility(View.VISIBLE);

        model = (MusicModel) getIntent().getSerializableExtra("MUSIC");

        Glide.with(this).load(model.getImage()).into(playerImage);
        playerTitle.setText(model.getTitle());
        playerArtist.setText(model.getArtist());
        playerDuration.setText(secondToTimer(model.getDuration()));
    }

    private void prepareMediaPlayer() {
        try {
            if(type.equals("json")) {
                mediaPlayer.setDataSource(model.getSource());
            } else if (type.equals("local")){
                mediaPlayer = MediaPlayer.create(this, Uri.parse(localData));
                playerDuration.setText(milliSecondToTimer(mediaPlayer.getDuration()));
            }
            mediaPlayer.prepare();

        } catch (Exception exception) {
//            Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private Runnable updater = new Runnable() {
        @Override
        public void run() {
            updateSeekBar();
            long currentDuration = mediaPlayer.getCurrentPosition();
            playerPosition.setText(milliSecondToTimer(currentDuration));
        }
    };

    private void updateSeekBar() {
        if (mediaPlayer.isPlaying()) {
            seekBar.setProgress((int) (((float) mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration()) * 100));
            handler.postDelayed(updater, 1000);
        }
    }

    private String secondToTimer(long second) {
        String timeString = "", minuteString, secondString;
        int hours = 0;

        int sec = (int) second % 60;
        int min = (int) second / 60;
        if (min > 60) {
            hours += (min / 60);
            min = min % 60;
        }

        if (min < 10) {
            minuteString = "0" + min;
        } else {
            minuteString = String.valueOf(min);
        }

        if (sec < 10) {
            secondString = "0" + sec;
        } else {
            secondString = String.valueOf(sec);
        }

        if (hours > 1) {
            timeString = timeString + hours + ":" + minuteString + ":" + secondString;
        } else {
            timeString = timeString + minuteString + ":" + secondString;
        }
        return timeString;

    }

    private String milliSecondToTimer(long milliSecond) {
        String timeString = "", secondString, minuteString;

        int hours = (int) (milliSecond / (1000 * 60 * 60));
        int minutes = (int) (milliSecond % (1000 * 60 * 60)) / (1000 * 60);
        int second = (int) (milliSecond % (1000 * 60 * 60) % (1000 * 60) / 1000);

        if (hours > 0) {
            timeString = hours + ":";
        }
        if (second < 10) {
            secondString = "0" + second;
        } else {
            secondString = "" + second;
        }

        if (minutes < 10) {
            minuteString = "0" + minutes;
        } else {
            minuteString = "" + minutes;
        }
        timeString = timeString + minuteString + ":" + secondString;
        return timeString;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.reset();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }
    }

    public void downloadSong(View view){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED){
                //Permission Denied, request it
                String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                //Show popup for runtime permission
                requestPermissions(permissions, PERMISSION_STORAGE_CODE);
            } else {
                //permission already granted, perform download
                startDownloading();
            }
        } else {
            //System os is loss than marshmallow, perform download
        }
    }

    private void startDownloading() {

        //get Url/text from edit text
        String url = model.getSource().trim();

        //create download request
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        //allow type of network to download files
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setTitle("Download"); //Set title to notification
        request.setDescription(model.getTitle()+" Downloading"); //Set description to notification

        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        //get song title as file name
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, ""+model.getTitle()+".mp3");

        //get download server and enque file
        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);

    }

    //handle permission result

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case PERMISSION_STORAGE_CODE:{
                if(grantResults.length>0  && grantResults[0] ==
                PackageManager.PERMISSION_GRANTED){
                    //Permission granted from popup, perform download
                    startDownloading();
                } else{
                    //Permission denied from popup, show error message
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();

                }
            }
        }

    }
}