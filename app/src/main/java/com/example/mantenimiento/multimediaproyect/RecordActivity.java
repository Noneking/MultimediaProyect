package com.example.mantenimiento.multimediaproyect;

import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import java.io.IOException;

public class RecordActivity extends Activity implements ImageButton.OnClickListener {

    ImageButton imageButtonStop;
    ImageButton imageButtonPlayPause;
    EditText editText;

    private MediaRecorder myAudioRecorder;
    private String outputFile = null;

    private String playAndStop="PLAY";//PAUSE

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        initComponents();
    }

    public void initComponents(){
        imageButtonStop= (ImageButton) findViewById(R.id.record_imageButtonStop);
        imageButtonPlayPause= (ImageButton) findViewById(R.id.record_imageButtonPlayPause);
        editText= (EditText) findViewById(R.id.record_editText_fileName);
        initListeners();
    }

    private void initListeners() {
        imageButtonStop.setOnClickListener(this);
        imageButtonPlayPause.setOnClickListener(this);
        initActions();
    }

    private void initActions() {
        imageButtonStop.setEnabled(false);
        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+editText.getText()+".mp3";
        Log.d("myTag", Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+editText.getText()+".mp3");
        myAudioRecorder=new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myAudioRecorder.setOutputFile(outputFile);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.record_imageButtonStop:
                imageButtonStop.setEnabled(false);
                myAudioRecorder.stop();
                myAudioRecorder.release();
                imageButtonPlayPause.setBackground(getResources().getDrawable(R.mipmap.record_play));
                playAndStop="PLAY";
                initComponents();
                break;
            case R.id.record_imageButtonPlayPause:
                switch(playAndStop){
                    case "PLAY":
                        try {
                            initComponents();
                            myAudioRecorder.prepare();
                            myAudioRecorder.start();
                            imageButtonStop.setEnabled(true);
                            imageButtonPlayPause.setBackground(getResources().getDrawable(R.mipmap.record_pause));
                            playAndStop="PAUSE";
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "PAUSE":
                        myAudioRecorder.stop();
                        imageButtonPlayPause.setBackground(getResources().getDrawable(R.mipmap.record_play));
                        playAndStop="PLAY";
                        break;
                }
                break;
        }
    }
}
