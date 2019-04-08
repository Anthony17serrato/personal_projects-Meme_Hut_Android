package gp.whatuwant.anthony.social.media.untitled.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import java.net.URI;

import gp.whatuwant.anthony.social.media.untitled.R;
import life.knowledge4.videotrimmer.K4LVideoTrimmer;
import life.knowledge4.videotrimmer.interfaces.OnK4LVideoListener;
import life.knowledge4.videotrimmer.interfaces.OnProgressVideoListener;
import life.knowledge4.videotrimmer.interfaces.OnTrimVideoListener;
import life.knowledge4.videotrimmer.interfaces.OnRangeSeekBarListener;
import life.knowledge4.videotrimmer.view.RangeSeekBarView;

public class TrimActivity extends AppCompatActivity implements OnTrimVideoListener, OnK4LVideoListener{

    K4LVideoTrimmer videoTrimmer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trim);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String path= getIntent().getStringExtra("uri");
        videoTrimmer = ((K4LVideoTrimmer) findViewById(R.id.timeLine));
        if (videoTrimmer != null) {
            videoTrimmer.setVideoURI(Uri.parse(path));
            videoTrimmer.setMaxDuration(180);
            videoTrimmer.setOnTrimVideoListener(this);
            videoTrimmer.setOnK4LVideoListener(this);
            videoTrimmer.setDestinationPath("/storage/emulated/0/DCIM/MemeHut/");
            videoTrimmer.setVideoInformationVisibility(true);
        }

    }

    @Override
    public void onTrimStarted() {

    }

    @Override
    public void getResult(final Uri uri) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("trim_result", uri);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void cancelAction() {
        videoTrimmer.destroy();
        finish();
    }

    @Override
    public void onError(String message) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(TrimActivity.this, "Unknown Error", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onVideoPrepared() {

    }
}
