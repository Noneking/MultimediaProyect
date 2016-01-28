package com.example.mantenimiento.multimediaproyect;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorEventListener2;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.MediaController;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class VideoActivity extends Activity implements TabHost.OnTabChangeListener, MediaPlayer.OnPreparedListener, View.OnTouchListener {

    Resources res;

    TabHost tabs;
    TabHost.TabSpec spec;

    VideoView videoView;
    MediaController mediaController;

    ActionBar actionBar;

    private View currentTabView;
    private View previewTabView;
    private int currentTab;

    private static final int PICKFILE_RESULT_CODE_VIDEO=1;

    private String video="http://www.ebookfrenzy.com/android_book/movie.mp4";
    //private String video="http://usher.justin.tv/api/channel/hls/officialgetright.m3u8?allow_source=true&token=%7B%22user_id%22%3Anull%2C%22channel%22%3A%22officialgetright%22%2C%22expires%22%3A1453987314%2C%22chansub%22%3A%7B%22view_until%22%3A1924905600%2C%22restricted_bitrates%22%3A%5B%5D%7D%2C%22private%22%3A%7B%22allowed_to_view%22%3Atrue%7D%2C%22privileged%22%3Afalse%2C%22source_restricted%22%3Afalse%7D&sig=5afab3abd8907236b60b518c00b13d6f7197fc7d";
    private File file;
    private FileWriter fw;
    private FileReader fr;
    private FileOutputStream fos;
    private FileInputStream fis;
    private InputStreamReader isr;
    private BufferedReader br;

    private String fileName="video_save.txt";
    private int ORIENTATION;

    private static final int ANIMATION_TIME = 240;

    OrientationEventListener mOrientationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        ORIENTATION=this.getResources().getConfiguration().orientation;
        writeFile();
        readFile();

        initComponents();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("POSITION", videoView.getCurrentPosition());
        savedInstanceState.putString("VIDEO", video);
        ORIENTATION=this.getResources().getConfiguration().orientation;
        restartActivity();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int position= savedInstanceState.getInt("POSITION");
        video=savedInstanceState.getString("VIDEO");
        //readFile();
        videoPrepare();
        videoView.seekTo(position);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //myOrientationEventListener.disable();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_video, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.video_filechooser:
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("media/*");
                startActivityForResult(Intent.createChooser(intent, "Selecciona un archivo de musica"), PICKFILE_RESULT_CODE_VIDEO);
                break;
        }
        return true;
    }

    private void restartActivity() {

    }

    private void initComponents() {
        actionBar=getActionBar();
        if(ORIENTATION==1){
            tabs= (TabHost) findViewById(R.id.video_tabHost);
            actionBar.show();
        }else {
            actionBar.hide();
        }
        videoView = (VideoView) findViewById(R.id.video_videoView);

        initListeners();
    }

    private void initListeners() {
        if(ORIENTATION==1){
            tabs.setOnTabChangedListener(this);
        }

        videoView.setOnPreparedListener(this);
        videoView.setOnTouchListener(this);

        initOperations();
    }

    private void initOperations() {
        readFile();
        if(ORIENTATION==1){
            currentTabView=tabs.getCurrentView();
            previewTabView=tabs.getCurrentView();
            res=getResources();
            tabs.setup();

            //Record Tab
            spec=tabs.newTabSpec("RECORD");
            spec.setContent(R.id.video_tab_recordVideo);
            spec.setIndicator("", res.getDrawable(R.drawable.instagram_512x512));
            tabs.addTab(spec);

            //Play Tab
            spec=tabs.newTabSpec("PLAY");
            spec.setContent(R.id.video_tab_playVideo);
            spec.setIndicator("", res.getDrawable(R.drawable.youtube_512x512));
            tabs.addTab(spec);

            //Stream Tab
            spec=tabs.newTabSpec("STREAM");
            spec.setContent(R.id.video_tab_streamVideo);
            spec.setIndicator("", res.getDrawable(R.drawable.twitch_512x512));
            tabs.addTab(spec);

            tabs.setCurrentTab(1);
            currentTab=1;
            previewTabView=tabs.getCurrentView();
        }else{
            videoPrepare();
        }
    }

    @Override
    public void onTabChanged(String tabId) {
        boolean paused=false;

        currentTabView=tabs.getCurrentView();
        if(tabs.getCurrentTab()>currentTab){
            previewTabView.setAnimation(outToLeftAnimation());
            currentTabView.setAnimation(inFromRightAnimation());
        }else{
            //previewTabView.setAnimation(outToRightAnimation());
            //currentTabView.setAnimation(inFromLeftAnimation());
        }
        previewTabView=currentTabView;
        currentTab=tabs.getCurrentTab();

        //Tabs Operations
        switch (tabs.getCurrentTab()){
            case 0:
                videoView.pause();//suspend
                paused=true;
                break;
            case 1:
                if(paused==true){
                    videoView.start();
                }else {
                    videoPrepare();
                }
                break;
            case 2:
                videoView.pause();
                paused=true;
                break;
        }
    }

    public void videoPrepare(){
        mediaController=new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.setVideoPath(video);
    }

    public void videoStart(){
        videoView.start();
    }

    private Animation inFromRightAnimation()
    {
        Animation inFromRight = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 1.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
        Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
        return setProperties(inFromRight);
    }

    private Animation outToRightAnimation()
    {
        Animation outToRight = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 1.0f,
        Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
        return setProperties(outToRight);
    }

    private Animation inFromLeftAnimation()
    {
        Animation inFromLeft = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, -1.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
        Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
        return setProperties(inFromLeft);
    }

    private Animation outToLeftAnimation()
    {
        Animation outtoLeft = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, -1.0f,
        Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
        return setProperties(outtoLeft);
    }

    private Animation setProperties(Animation animation)
    {
        animation.setDuration(ANIMATION_TIME);
        animation.setInterpolator(new AccelerateInterpolator());
        return animation;
    }

    public void writeFile(){
        try {
            file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/", fileName);
            fw=new FileWriter(file);
            fw.write(video);
            //fw.append(video);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

//        try {
//            file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/", fileName);
//            fos = openFileOutput(fileName, this.MODE_PRIVATE);
//            fos.write(video.getBytes());
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                fos.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }

    public void readFile(){
        try {
            file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/", fileName);
            fr=new FileReader(file);
            br=new BufferedReader(fr);
            String text="";
            //while((text = br.readLine()) != null) {
                text=br.readLine();
            //}
            video=text;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    fr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

//        try {
//            file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/", fileName);
//            fis = this.openFileInput(fileName);
//            isr=new InputStreamReader(fis);
//            br=new BufferedReader(isr);
//            StringBuilder sb = new StringBuilder();
//            String line;
//            while ((line = br.readLine()) != null) {
//                sb.append(line);
//            }
//            video=line;
//            br.close();
//            isr.close();
//            fis.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        videoStart();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(mediaController.isShown()){
            mediaController.hide();
        }else{
            mediaController.show();
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PICKFILE_RESULT_CODE_VIDEO:
                if(resultCode==RESULT_OK){
                    video=data.getDataString();
                    videoView.setVideoPath(data.getDataString());
                    writeFile();
                    videoPrepare();
                }
                break;
        }
    }
}
