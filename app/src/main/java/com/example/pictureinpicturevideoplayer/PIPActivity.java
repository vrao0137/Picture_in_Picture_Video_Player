/*
package com.example.pictureinpicturevideoplayer;

import android.app.PendingIntent;
import android.app.PictureInPictureParams;
import android.app.RemoteAction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.graphics.drawable.Icon;
import android.media.MediaPlayer;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Rational;
import android.view.View;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pictureinpicturevideoplayer.databinding.ActivityPIPBinding;

import java.util.ArrayList;
import java.util.List;

public class PIPActivity extends AppCompatActivity {
    private static Context mContext;
    private final String TAG = PIPActivity.class.getSimpleName();
    private ActivityPIPBinding binding;

    private Uri videoUri;
    private ActionBar actionBar;

    private PictureInPictureParams.Builder pictureInPitureParam;
    private VideoView videoView;
    private ImageButton pipBtn;
    static int drw_previous;
    static int drw_next;
    static boolean isPlay = false;
    MediaController mediaController ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPIPBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        actionBar = getSupportActionBar();
        videoView = binding.videoView;
        pipBtn = binding.pipBtn;
        initialize();
    }

    private void initialize(){
        mContext = this;
        // Get and pass intent to a method that will handle video playback, intent contains url of video
        setVideoView(getIntent());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            pictureInPitureParam = new PictureInPictureParams.Builder();
        }
        // Handle click, enter PIP mode
        pipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pictureInPictureMode();
            }
        });
    }

    private void setVideoView(Intent intent){
        String videoURL = intent.getStringExtra("videoURL");
        Log.e(TAG,""+videoURL);

        // MediaController for play,Pause, seekBar, time etc
        mediaController = new MediaController(this);

        mediaController.setAnchorView(videoView);

        videoUri = Uri.parse(videoURL);

        Log.e(TAG, "videoUri:- " + videoURL);
        // Set media controller to videoView
        videoView.setMediaController(mediaController);
        // Set video uri to videoview
        videoView.setVideoURI(videoUri);
        // Add video prepare listener
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                // When video is ready, play it
                Log.e(TAG,"onPrepared: Video Prepared, Playing....");
                mediaPlayer.start();
            }
        });

    }

    private void pictureInPictureMode(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Rational aspectRation = new Rational(videoView.getWidth(), videoView.getHeight());
            pictureInPitureParam.setAspectRatio(aspectRation)
                    .build();
            enterPictureInPictureMode(pictureInPitureParam.build());
        }
    }


    protected static PendingIntent getPendingIntentPrevious(){
        Intent intentPrevious = new Intent(mContext, PIPActivity.class).setAction("actionprevious");
        PendingIntent pendingIntentPrevious = PendingIntent.getBroadcast(mContext, 0, intentPrevious, PendingIntent.FLAG_UPDATE_CURRENT);
        drw_previous = R.drawable.ic_previous;
        return pendingIntentPrevious;
    }

    protected static PendingIntent getPendingIntentPlay(){
        Intent intentPlay = new Intent(mContext, PIPActivity.class)
                .setAction("actionpause");
        PendingIntent pendingIntentPlay = PendingIntent.getBroadcast(mContext, 0, intentPlay, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntentPlay;
    }

    protected static PendingIntent getPendingIntentNext(){
        Intent intentNext = new Intent(mContext, PIPActivity.class).setAction("actionnext");
        PendingIntent pendingIntentNext = PendingIntent.getBroadcast(mContext, 0, intentNext, PendingIntent.FLAG_UPDATE_CURRENT);
        drw_next = R.drawable.ic_next;
        return pendingIntentNext;
    }

    private List<RemoteAction> createAction() {
        List<RemoteAction> list = new ArrayList<>();
      //  Intent intent = new Intent(PIPActivity.this, PIPActivity.class);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
           */
/* PendingIntent contentIntent = PendingIntent.getActivity(this, 0,intent, PendingIntent.FLAG_ONE_SHOT);*//*

            RemoteAction previousAction = new RemoteAction(Icon.createWithResource(PIPActivity.this,R.drawable.ic_previous),
                    "PREVIOUS","null",
                    getPendingIntentPrevious());

            RemoteAction pauseAction = new RemoteAction(Icon.createWithResource(PIPActivity.this,R.drawable.ic_pause),
                    "PAUSE","null",
                    getPendingIntentPlay());

            RemoteAction nextAction = new RemoteAction(Icon.createWithResource(PIPActivity.this,R.drawable.ic_next),
                    "NEXT","null",
                    getPendingIntentNext());

            list.add(previousAction);
            list.add(pauseAction);
            list.add(nextAction);
        }
        return list;
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (!isInPictureInPictureMode()){
                Log.e(TAG,"onUserLeaveHint: was not in PIP");
                pictureInPictureMode();
            }else {
                Log.e(TAG,"onUserLeaveHint: Already in PIP");
            }
        }
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
        if (isInPictureInPictureMode){
            Log.e(TAG,"onPictureInPictureModeChanged: Entered PIP");
            // Hide Pip button and actionBar
            pipBtn.setVisibility(View.GONE);
            //  actionBar.hide();
        }else {
            Log.e(TAG,"onPictureInPictureModeChanged: Exited PIP");
            // Show Pip button and actionBar
            pipBtn.setVisibility(View.VISIBLE);
          //  actionBar.show();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.e(TAG,"onNewIntent: Play new Video");
        setVideoView(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (videoView.isPlaying()){
            videoView.stopPlayback();
        }
    }
}*/
