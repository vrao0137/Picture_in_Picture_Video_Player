package com.example.pictureinpicturevideoplayer;

import android.app.PictureInPictureParams;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaMetadata;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
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
import androidx.appcompat.app.AppCompatActivity;

import com.example.pictureinpicturevideoplayer.databinding.ActivityPipplayBackBinding;

public class PIPPlayBackActivity extends AppCompatActivity {
    private static Context mContext;
    private final String TAG = PIPPlayBackActivity.class.getSimpleName();
    private ActivityPipplayBackBinding binding;

    public static final long MEDIA_ACTIONS_PLAY_PAUSE =
            PlaybackState.ACTION_PLAY
                    | PlaybackState.ACTION_PAUSE
                    | PlaybackState.ACTION_PLAY_PAUSE;

    public static final long MEDIA_ACTIONS_ALL =
            MEDIA_ACTIONS_PLAY_PAUSE
                    | PlaybackState.ACTION_SKIP_TO_NEXT
                    | PlaybackState.ACTION_SKIP_TO_PREVIOUS;

    private MediaSession mSession;
    MediaController mediaController;
    private Uri videoUri;

    /** The arguments to be used for Picture-in-Picture mode. */
    private PictureInPictureParams.Builder mPictureInPictureParamsBuilder;
    private VideoView videoView;
    private ImageButton pipBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPipplayBackBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mPictureInPictureParamsBuilder = new PictureInPictureParams.Builder();
        }
        binding.pipBtn.setOnClickListener(mOnClickListener);
        videoView = binding.videoView;
        pipBtn = binding.pipBtn;
        setVideoView(getIntent());
        initializeMediaSession();
    }

    private final View.OnClickListener mOnClickListener =
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (view.getId()) {
                        case R.id.pipBtn:
                            pictureInPictureMode();
                            break;
                    }
                }
            };

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
                Log.e(TAG, "onPrepared: Video Prepared, Playing....");
                mediaPlayer.start();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (mediaPlayer.isPlaying()) {
                        updatePlaybackState(
                                PlaybackState.STATE_PLAYING,
                                videoView.getCurrentPosition(),
                                videoView.getAudioSessionId());
                    } else {
                        updatePlaybackState(
                                PlaybackState.STATE_PAUSED,
                                videoView.getCurrentPosition(),
                                videoView.getAudioSessionId());
                    }
                }

            }
        });

    }

    private void pictureInPictureMode(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Rational aspectRation = new Rational(videoView.getWidth(), videoView.getHeight());
            mPictureInPictureParamsBuilder.setAspectRatio(aspectRation).build();
            enterPictureInPictureMode(mPictureInPictureParamsBuilder.build());
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

    private void updatePlaybackState(int state, int position, int mediaId) {
        long actions = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            actions = mSession.getController().getPlaybackState().getActions();
        }
        updatePlaybackState(state, actions, position, mediaId);
    }

    private void updatePlaybackState(int state, long playbackActions, int position, int mediaId) {
        PlaybackState.Builder builder =
                null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            builder = new PlaybackState.Builder()
                    .setActions(playbackActions)
                    .setActiveQueueItemId(mediaId)
                    .setState(state, position, 1.0f);
            mSession.setPlaybackState(builder.build());
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mSession.release();
                mSession = null;
            }
        }
    }


    private void initializeMediaSession() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            mSession = new MediaSession(this, TAG);
            mSession.setFlags(
                    MediaSession.FLAG_HANDLES_MEDIA_BUTTONS
                            | MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);
            mSession.setActive(true);

            MediaMetadata metadata = new MediaMetadata.Builder()
                    .putString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE, videoView.toString())
                    .build();
            mSession.setMetadata(metadata);

            MediaSessionCallback mMediaSessionCallback = null;
            mMediaSessionCallback = new MediaSessionCallback(videoView);

            mSession.setCallback(mMediaSessionCallback);

            int state =
                    videoView.isPlaying()
                            ? PlaybackState.STATE_PLAYING
                            : PlaybackState.STATE_PAUSED;
            updatePlaybackState(
                    state,
                    MEDIA_ACTIONS_ALL,
                    videoView.getCurrentPosition(),
                    videoView.getAudioSessionId());
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private class MediaSessionCallback extends MediaSession.Callback {

        private static final int PLAYLIST_SIZE = 2;

        private VideoView videoView;
        private int indexInPlaylist;

        public MediaSessionCallback(VideoView videoView) {
            this.videoView = videoView;
            indexInPlaylist = 1;
        }

        @Override
        public void onPlay() {
            videoView.start();
            updatePlaybackState(
                    PlaybackState.STATE_PLAYING,
                    PlaybackState.ACTION_PAUSE,
                    videoView.getCurrentPosition(),
                    videoView.getAudioSessionId());
        }

        @Override
        public void onPause() {
                videoView.pause();
                updatePlaybackState(
                        PlaybackState.STATE_PAUSED,
                        PlaybackState.ACTION_PLAY,
                        videoView.getCurrentPosition(),
                        videoView.getAudioSessionId());
        }

        @Override
        public void onSkipToNext() {
            super.onSkipToNext();
            videoView.start();
            if (indexInPlaylist < PLAYLIST_SIZE) {
                indexInPlaylist++;
                if (indexInPlaylist >= PLAYLIST_SIZE) {
                    updatePlaybackState(
                            PlaybackState.STATE_PLAYING,
                            MEDIA_ACTIONS_PLAY_PAUSE | PlaybackState.ACTION_SKIP_TO_PREVIOUS,
                            videoView.getCurrentPosition(),
                            videoView.getAudioSessionId());
                } else {
                    updatePlaybackState(
                            PlaybackState.STATE_PLAYING,
                            MEDIA_ACTIONS_ALL,
                            videoView.getCurrentPosition(),
                            videoView.getAudioSessionId());
                }
            }
        }

        @Override
        public void onSkipToPrevious() {
            super.onSkipToPrevious();
            videoView.start();
            if (indexInPlaylist > 0) {
                indexInPlaylist--;
                if (indexInPlaylist <= 0) {
                    updatePlaybackState(
                            PlaybackState.STATE_PLAYING,
                            MEDIA_ACTIONS_PLAY_PAUSE | PlaybackState.ACTION_SKIP_TO_NEXT,
                            videoView.getCurrentPosition(),
                            videoView.getAudioSessionId());
                } else {
                    updatePlaybackState(
                            PlaybackState.STATE_PLAYING,
                            MEDIA_ACTIONS_ALL,
                            videoView.getCurrentPosition(),
                            videoView.getAudioSessionId());
                }
            }
        }
    }
}