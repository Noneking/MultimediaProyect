package com.example.mantenimiento.multimediaproyect;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
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
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.TabHost;
import android.widget.TextView;
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

public class VideoActivity extends Activity implements TabHost.OnTabChangeListener, MediaPlayer.OnPreparedListener, View.OnTouchListener, ListView.OnItemClickListener{

    Resources res;

    TabHost tabs;
    TabHost.TabSpec spec;

    VideoView videoView;
    MediaController mediaController;

    ActionBar actionBar;
    ProgressDialog progressDialog;
    ListView listView;
    LinearLayout video_layout_tab_recordVideo;
    LinearLayout video_layout_tab_playVideo;
    LinearLayout video_layout_tab_streamVideo;

    private int currentTab;

    private static final int PICKFILE_RESULT_CODE_VIDEO=1;

    private FullList fullList;

    private String video="http://www.ebookfrenzy.com/android_book/movie.mp4";

    private int ORIENTATION;

    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        gestureDetector = new GestureDetector(this, new GestureListener());

        ORIENTATION=this.getResources().getConfiguration().orientation;

        initComponents();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
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

    private void initComponents() {
        actionBar=getActionBar();
        if(ORIENTATION==1){
            tabs= (TabHost) findViewById(R.id.video_tabHost);
            listView= (ListView) findViewById(R.id.video_listView);
            video_layout_tab_recordVideo= (LinearLayout) findViewById(R.id.video_tab_recordVideo);
            video_layout_tab_playVideo= (LinearLayout) findViewById(R.id.video_tab_playVideo);
            video_layout_tab_streamVideo= (LinearLayout) findViewById(R.id.video_tab_streamVideo);

            progressDialog=new ProgressDialog(this);
            progressDialog.setMessage("Buffering...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);

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

            video_layout_tab_recordVideo.setOnTouchListener(this);
            video_layout_tab_playVideo.setOnTouchListener(this);
            video_layout_tab_streamVideo.setOnTouchListener(this);
        }

        videoView.setOnPreparedListener(this);
        videoView.setOnTouchListener(this);
        listView.setOnTouchListener(this);
        listView.setOnItemClickListener(this);

        initOperations();
    }

    private void initOperations() {
        if(ORIENTATION==1){
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
        }else{
            videoPrepare();
        }
        loadList();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        if(tabs.getCurrentTab()==1){
            super.onSaveInstanceState(savedInstanceState);

            if(videoView.isPlaying()){
                videoView.pause();
            }

            savedInstanceState.putInt("POSITION", videoView.getCurrentPosition());
            savedInstanceState.putString("VIDEO", video);
            ORIENTATION=this.getResources().getConfiguration().orientation;
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int position= savedInstanceState.getInt("POSITION");
        video=savedInstanceState.getString("VIDEO");

        ORIENTATION=this.getResources().getConfiguration().orientation;

        videoPrepare();
        videoView.seekTo(position);
    }

//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        //myOrientationEventListener.disable();
//    }

    private void loadList() {
        fullList=new FullList();
        listView.setAdapter(new VideoListAdapter(this, fullList.getArrayList()) {
            @Override
            public void onEntrada(Object entrada, View view) {
                ImageView img= (ImageView) view.findViewById(R.id.item_imageView);
                img.setBackgroundResource(R.drawable.twitch_128x128);
                TextView titulo= (TextView) view.findViewById(R.id.item_textViewTitle);
                titulo.setText(((VideoItemList) entrada).getTitulo());
                TextView subtitulo= (TextView) view.findViewById(R.id.item_textViewSubTitle);
                subtitulo.setText(((VideoItemList) entrada).getSubtitulo());
            }
        });
    }

    public void videoPrepare(){
        progressDialog.show();
        mediaController=new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.setVideoPath(video);
        videoView.requestFocus();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        videoView.start();
        progressDialog.dismiss();
        tabs.setCurrentTab(1);
        currentTab=1;
    }

//    public void videoStart(){
//        videoView.start();
//    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(tabs.getCurrentTab()==1){
            if(videoView.isPlaying()) {
                if (mediaController.isShown()) {
                    mediaController.hide();
                } else {
                    mediaController.show();
                }
            }
        }
        return gestureDetector.onTouchEvent(event);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PICKFILE_RESULT_CODE_VIDEO:
                if(resultCode==RESULT_OK){
                    video=data.getDataString();
                    videoView.setVideoPath(data.getDataString());
                    videoPrepare();
                }
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        VideoItemList vil= (VideoItemList) listView.getItemAtPosition(position);
        video=vil.getSubtitulo();
        tabs.setCurrentTab(1);
        videoPrepare();
    }

    private final class GestureListener extends SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight();
                        } else {
                            onSwipeLeft();
                        }
                    }
                    result = true;
                }
                else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        onSwipeBottom();
                    } else {
                        onSwipeTop();
                    }
                }
                result = true;

            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }
    }

    public void onSwipeTop() {
        //Toast.makeText(this, "top", Toast.LENGTH_SHORT).show();
        animationBottom();
    }
    public void onSwipeRight() {
        //Toast.makeText(this, "right", Toast.LENGTH_SHORT).show();
        animationLeft();
    }
    public void onSwipeLeft() {
        //Toast.makeText(this, "left", Toast.LENGTH_SHORT).show();
        animationRight();
    }
    public void onSwipeBottom() {
        //Toast.makeText(this, "bottom", Toast.LENGTH_SHORT).show();
        animationTop();
    }

    @Override
    public void onTabChanged(String tabId) {

        currentTab=tabs.getCurrentTab();

        //Tabs Operations
        switch (tabs.getCurrentTab()){
            case 0:
                if(videoView.isPlaying()){if(mediaController.isShown()){mediaController.hide();}}
//                videoView.pause();//suspend
                break;
            case 1:
                if(videoView.isPlaying()){videoView.start();}
                break;
            case 2:
                if(videoView.isPlaying()){if(mediaController.isShown()){mediaController.hide();}}
                break;
        }
    }

    public void animationRight(){
//        Toast.makeText(this,"animationRight",Toast.LENGTH_SHORT).show();
        if(tabs.getCurrentTab()>2){
            tabs.setCurrentTab(0);
        }else{
            tabs.setCurrentTab(tabs.getCurrentTab()+1);
        }
    }

    public void animationLeft(){
//        Toast.makeText(this,"animationLeft",Toast.LENGTH_SHORT).show();
        if(tabs.getCurrentTab()==0){
            tabs.setCurrentTab(2);
        }else{
            tabs.setCurrentTab(tabs.getCurrentTab()-1);
        }
    }

    public void animationTop(){

    }

    public void animationBottom(){

    }

}


