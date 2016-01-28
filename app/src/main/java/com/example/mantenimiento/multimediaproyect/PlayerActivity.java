package com.example.mantenimiento.multimediaproyect;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.Switch;
import android.widget.Toast;

import java.io.IOException;

public class PlayerActivity extends Activity implements MediaController.MediaPlayerControl, MediaPlayer.OnPreparedListener, Switch.OnCheckedChangeListener {

    private MediaController mediaController;
    private MediaPlayer mediaPlayer;
    //private Handler handler;

    Uri uri;

    Switch switch_url;
    Switch switch_stream;
    EditText editText_url;
    EditText editText_stream;

    private static final int PICKFILE_RESULT_CODE_AUDIO=1;
    private static final int PICKFILE_RESULT_CODE_VIDEO=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        initViews();
        initOperations();
    }

    public void initViews(){
        switch_url= (Switch) findViewById(R.id.player_switchUrl);
        switch_stream= (Switch) findViewById(R.id.player_switchStream);
        editText_url= (EditText) findViewById(R.id.editTextUrl);
        editText_stream= (EditText) findViewById(R.id.editTextStream);
        initListeners();
    }

    public void initListeners(){
        switch_url.setOnCheckedChangeListener(this);
        switch_stream.setOnCheckedChangeListener(this);
    }

    public void initOperations(){
        mediaPlayer = new MediaPlayer();
        mediaController = new MediaController(this);
        mediaController.setMediaPlayer(this);
        mediaController.setAnchorView(findViewById(R.id.player_mainLayout));


        mediaPlayer.setOnPreparedListener(this);

        if(getIntent().getExtras()!=null){
            Bundle bundle=getIntent().getExtras();
            String url=bundle.getString("DATA");
            Uri uri=Uri.parse(url);
            try {
                mediaPlayer = new MediaPlayer();
                mediaController = new MediaController(this);
                mediaController.setMediaPlayer(this);
                mediaController.setAnchorView(findViewById(R.id.player_mainLayout));
                mediaPlayer.setDataSource(this, uri);
                mediaPlayer.prepare();
                mediaPlayer.setOnPreparedListener(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void fileChooserOnClickEvent(View v){
        Intent intent=new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        startActivityForResult(Intent.createChooser(intent, "Selecciona un archivo de musica"), PICKFILE_RESULT_CODE_AUDIO);
    }

    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            mediaController.show(20000);
            mediaPlayer.start();
        }
    };

    @Override
    public void onPrepared(MediaPlayer mp) {
        Handler handler = new Handler();
        handler.post(runnable);
    }

    public void initPlayer(String url){
        initOperations();
        //mediaPlayer.pause();
        mediaPlayer.stop();
        //mediaPlayer.release();
        try {
            if(url.equals("")){
                mediaPlayer.setDataSource(this, uri);
            }else{
                mediaPlayer.setDataSource(this, Uri.parse(url));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PICKFILE_RESULT_CODE_AUDIO:
                if(resultCode==RESULT_OK){
                    uri=data.getData();
                    switch_url.setChecked(false);
                    switch_stream.setChecked(false);
                    initPlayer("");
                }
                break;
            case PICKFILE_RESULT_CODE_VIDEO:

                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.player_switchUrl:
                if(switch_url.isChecked()){
                    mediaPlayer.pause();
                    switch_stream.setChecked(false);
                    initPlayer(editText_url.getText().toString());
                }
                break;
            case R.id.player_switchStream:
                if(switch_stream.isChecked()){
                    mediaPlayer.pause();
                    switch_url.setChecked(false);
                    initPlayer(editText_stream.getText().toString());
                }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    @Override
    public void start() {
        mediaPlayer.start();
    }

    @Override
    public void pause() {
        if(mediaPlayer.isPlaying())
            mediaPlayer.pause();
    }

    @Override
    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    @Override
    public void seekTo(int pos) {
        mediaPlayer.seekTo(pos);
    }

    @Override
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mediaController.show();
        return false;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }
}