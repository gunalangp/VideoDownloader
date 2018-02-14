package com.videodownloader.fb;

import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;

import com.squareup.picasso.Picasso;


import retrofit2.http.Url;

public class PlayerActivity extends AppCompatActivity {

    String file;
    VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null)
            file = bundle.getString("file");


    }



    @Override
    public void onBackPressed() {
            super.onBackPressed();
    }
}
