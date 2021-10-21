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
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initialize();
    }

    private void initialize(){
        context = this;
        binding.playVideoBtn.setOnClickListener(this);
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
            case R.id.playVideoBtn:
                Log.e(TAG,"Video_URL:- "+"android.resource://" + getPackageName() + "/" + R.raw.why_did_i_answer);
                playVideo("android.resource://" + getPackageName() + "/" + R.raw.why_did_i_answer);
                break;
        }
    }
}