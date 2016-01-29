package com.example.mantenimiento.multimediaproyect;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.Switch;
import android.widget.Toast;

import java.io.IOException;

public class PlayerActivity extends Activity implements MediaController.MediaPlayerControl, MediaPlayer.OnPreparedListener, Switch.OnCheckedChangeListener, View.OnTouchListener {

    private MediaController mediaController;
    private MediaPlayer mediaPlayer;
    //private Handler handler;

    Uri uri;

    Switch switch_stream;
    EditText editText_stream;
    LinearLayout player_layout;

    private String audio;

    private static final int PICKFILE_RESULT_CODE_AUDIO=1;
    private static final int PICKFILE_RESULT_CODE_VIDEO=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        initViews();
        initOperations();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_player, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.player_filechooser:
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/*");
                startActivityForResult(Intent.createChooser(intent, "Selecciona un archivo de musica"), PICKFILE_RESULT_CODE_VIDEO);
                break;
        }
        return true;
    }

    public void initViews(){
        switch_stream= (Switch) findViewById(R.id.player_switchStream);
        editText_stream= (EditText) findViewById(R.id.editTextStream);
        player_layout= (LinearLayout) findViewById(R.id.player_mainLayout);
        initListeners();
    }

    public void initListeners(){
        switch_stream.setOnCheckedChangeListener(this);
        player_layout.setOnTouchListener(this);
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

//    public void fileChooserOnClickEvent(View v){
//        Intent intent=new Intent();
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        intent.setType("audio/*");
//        startActivityForResult(Intent.createChooser(intent, "Selecciona un archivo de musica"), PICKFILE_RESULT_CODE_AUDIO);
//    }

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
    public boolean onTouch(View v, MotionEvent event) {
        if(mediaPlayer!=null){
            if(mediaPlayer.isPlaying()){
                if(mediaController.isShown()){
                    mediaController.hide();
                }else{
                    mediaController.show();
                }
            }
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK){
             audio=data.getDataString();
             switch_stream.setChecked(false);
             initPlayer(audio);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(switch_stream.isChecked()){
            mediaPlayer.pause();
            initPlayer(editText_stream.getText().toString());
        }else{
            mediaPlayer.pause();
            mediaController.hide();
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