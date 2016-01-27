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
import android.widget.MediaController;
import android.widget.Toast;

import java.io.IOException;

public class PlayerActivity extends Activity implements MediaController.MediaPlayerControl {

    private MediaController mediaController;
    private MediaPlayer mediaPlayer;
    private Handler handler;

    private static final int PICKFILE_RESULT_CODE_AUDIO=1;
    private static final int PICKFILE_RESULT_CODE_VIDEO=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        mediaPlayer = new MediaPlayer();
        mediaController = new MediaController(this);
        mediaController.setMediaPlayer(this);
        mediaController.setAnchorView(findViewById(R.id.player_mainLayout));
        handler = new Handler();

        try {
            mediaPlayer.setDataSource(this, Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.metodo_para_escapar));
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                handler.post(new Runnable() {
                    public void run() {
                        mediaController.show(20000);
                        mediaPlayer.start();
                    }
                });
            }
        });

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
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        handler.post(new Runnable() {
                            public void run() {
                                mediaController.show(20000);
                                mediaPlayer.start();
                            }
                        });
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void fileChooserOnClickEvent(View v){
        //mediaPlayer.stop();
        //Intent intent=new Intent(this, FileChooser.class);
        //startActivityForResult(intent, RESULT_OK);
        Intent intent=new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        startActivityForResult(Intent.createChooser(intent, "Selecciona un archivo de musica"), PICKFILE_RESULT_CODE_AUDIO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case PICKFILE_RESULT_CODE_AUDIO:
                Toast.makeText(this, "LLEGO..", Toast.LENGTH_SHORT).show();
                if(resultCode==RESULT_OK){
                    Uri uri=data.getData();
                    try {
                        mediaPlayer.setDataSource(this, uri);
                        mediaPlayer.prepare();
                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                handler.post(new Runnable() {
                                    public void run() {
                                        mediaController.show(20000);
                                        mediaPlayer.start();
                                    }
                                });
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case PICKFILE_RESULT_CODE_VIDEO:

                break;
        }
    }

    private void startActivityForResult(Intent intent) {
        //Log.println(1, "TAG", "HE LLEGADO AL PLAYERACTIVITY");
        //Bundle bundle=intent.getBundleExtra("DATA");
        //String msn=bundle.getString("DATA");
        //Toast.makeText(this, "File Clicked: " + msn, Toast.LENGTH_SHORT).show();
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