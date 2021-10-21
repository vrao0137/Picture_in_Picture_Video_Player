package com.example.pictureinpicturevideoplayer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pictureinpicturevideoplayer.databinding.ActivityMainBinding;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private final String TAG = MainActivity.class.getSimpleName();
    private ActivityMainBinding binding;
    Context context;

    // Video urls/Links
    private static final String videoUrlOne = "https://youtu.be/_BqOnkNpJvA";
    private static final String videoUrlSecond = "https://youtu.be/PT2_F-1esPk";
    private static final String videoUrlThird = "https://youtu.be/T3E9Wjbq44E";
    String newPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initialize();
    }

    private void initialize(){
        context = this;
        binding.videoOneBTN.setOnClickListener(this);
        binding.videoTwoBTN.setOnClickListener(this);
        binding.videoThreeBTN.setOnClickListener(this);
        /*File dir = Environment.getExternalStorageDirectory();
        String path = dir.getAbsolutePath();
        newPath = "file://storage/emulated/0/Why_Did_i_Answer.mp4";
        Log.e(TAG,"path:- "+path+"/Why_Did_i_Answer.mp4");*/
    }

    private void playVideo(String videoUrl){
        // Intent to start activity with video url
        Intent intent = new Intent(MainActivity.this, PIPPlayBackActivity.class);
        intent.putExtra("videoURL",videoUrl);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.videoOneBTN:
                playVideo("android.resource://" + getPackageName() + "/" + R.raw.why_did_i_answer);
                break;
        }
    }
}