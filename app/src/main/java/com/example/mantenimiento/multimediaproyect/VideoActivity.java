package com.example.mantenimiento.multimediaproyect;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.hardware.Camera;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorEventListener2;
import android.hardware.SensorManager;
import android.media.CamcorderProfile;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
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
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
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

//    private String video="http://www.ebookfrenzy.com/android_book/movie.mp4";
    private String video;

    private int ORIENTATION;

    private GestureDetector gestureDetector;

    //////////////////////////////////////////////////////////////////////////////

    private Camera mCamera;
    private CameraPreview mPreview;
    private MediaRecorder mediaRecorder;
    private Button capture, switchCamera;
    private Context myContext;
    private LinearLayout cameraPreview;
    private boolean cameraFront = false;

    /////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        gestureDetector = new GestureDetector(this, new GestureListener());

        ORIENTATION=this.getResources().getConfiguration().orientation;

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        myContext = this;

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
            progressDialog.setMessage("Loading...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);

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

            listView.setOnTouchListener(this);
            listView.setOnItemClickListener(this);
        }

        videoView.setOnPreparedListener(this);
        videoView.setOnTouchListener(this);

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

            loadList();
        }else{
//            videoPrepare();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
//        if(tabs.getCurrentTab()==1){
//            super.onSaveInstanceState(savedInstanceState);

//            if(videoView.isPlaying()){
//                videoView.pause();
//            }

            savedInstanceState.putInt("POSITION", videoView.getCurrentPosition());
            savedInstanceState.putString("VIDEO", video);
            ORIENTATION=this.getResources().getConfiguration().orientation;
            savedInstanceState.putInt("ORIENTATION", ORIENTATION);
            initComponents();
            if(ORIENTATION==1){
//                if(tabs.getCurrentTab()==1){
                    super.onSaveInstanceState(savedInstanceState);
//                }
            }else{
                super.onSaveInstanceState(savedInstanceState);
            }

            if(videoView.isPlaying()){
                videoView.pause();
            }

//            initComponents();
//        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        //super.onRestoreInstanceState(savedInstanceState);
        setContentView(R.layout.activity_video);
        super.onCreate(savedInstanceState);
        int position=savedInstanceState.getInt("POSITION");
        video=savedInstanceState.getString("VIDEO");
        ORIENTATION=savedInstanceState.getInt("ORIENTATION");

//        ORIENTATION=this.getResources().getConfiguration().orientation;

        initComponents();

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
        if(video!=null && video!=""){
            videoView.setVideoPath(video);
        }
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

        boolean initialized=false;

        //Tabs Operations
        switch (tabs.getCurrentTab()){
            case 0:
                if(mPreview!=null){mPreview.setVisibility(View.VISIBLE);}
                if(videoView.isPlaying()){if(mediaController.isShown()){mediaController.hide();}}
                initialize();
                mPreview.setVisibility(View.VISIBLE);
                initialized=true;
                videoView.pause();//suspend
                videoView.setVisibility(View.INVISIBLE);
                break;
            case 1:
                mPreview.setVisibility(View.INVISIBLE);
//                try {
//                    if(mCamera!=null){mCamera.setPreviewDisplay(null);}sdfsdf
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                videoView.setVisibility(View.VISIBLE);
                if(videoView.isPlaying()){videoView.start();}

                if(initialized){
                    mPreview.destroy();
                    initialized=false;
                }
                break;
            case 2:
                if(videoView.isPlaying()){if(mediaController.isShown()){mediaController.hide();}}

                if(initialized){
                    mPreview.destroy();
                    initialized=false;
                }
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

    ////////////////////////////////////////////////////////////////////////////////////

    private int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                cameraFront = true;
                break;
            }
        }
        return cameraId;
    }

    private int findBackFacingCamera() {
        int cameraId = -1;
        // Search for the back facing camera
        // get the number of cameras
        int numberOfCameras = Camera.getNumberOfCameras();
        // for every camera check
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                cameraFront = false;
                break;
            }
        }
        return cameraId;
    }

    public void onResume() {
        super.onResume();
        if (!hasCamera(myContext)) {
            Toast toast = Toast.makeText(myContext, "Sorry, your phone does not have a camera!", Toast.LENGTH_LONG);
            toast.show();
            finish();
        }
        if (mCamera == null) {
            // if the front facing camera does not exist
            if (findFrontFacingCamera() < 0) {
                Toast.makeText(this, "No front facing camera found.", Toast.LENGTH_LONG).show();
                switchCamera.setVisibility(View.GONE);
            }
            mCamera = Camera.open(findBackFacingCamera());
            mCamera.setDisplayOrientation(90);
            mPreview.refreshCamera(mCamera);
        }
    }

    public void initialize() {
        cameraPreview = (LinearLayout) findViewById(R.id.camera_preview);

        mPreview = new CameraPreview(myContext, mCamera);
        cameraPreview.addView(mPreview);

        capture = (Button) findViewById(R.id.button_capture);
        capture.setOnClickListener(captrureListener);

        switchCamera = (Button) findViewById(R.id.button_ChangeCamera);
        switchCamera.setOnClickListener(switchCameraListener);
    }

    View.OnClickListener switchCameraListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // get the number of cameras
            if (!recording) {
                int camerasNumber = Camera.getNumberOfCameras();
                if (camerasNumber > 1) {
                    // release the old camera instance
                    // switch camera, from the front and the back and vice versa

                    releaseCamera();
                    chooseCamera();
                } else {
                    Toast toast = Toast.makeText(myContext, "Sorry, your phone has only one camera!", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        }
    };

    public void chooseCamera() {
        // if the camera preview is the front
        if (cameraFront) {
            int cameraId = findBackFacingCamera();
            if (cameraId >= 0) {
                // open the backFacingCamera
                // set a picture callback
                // refresh the preview

                mCamera = Camera.open(cameraId);
                // mPicture = getPictureCallback();
                mPreview.refreshCamera(mCamera);
            }
        } else {
            int cameraId = findFrontFacingCamera();
            if (cameraId >= 0) {
                // open the backFacingCamera
                // set a picture callback
                // refresh the preview

                mCamera = Camera.open(cameraId);
                // mPicture = getPictureCallback();
                mPreview.refreshCamera(mCamera);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // when on Pause, release camera in order to be used from other
        // applications
        releaseCamera();
    }

    private boolean hasCamera(Context context) {
        // check if the device has camera
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

    boolean recording = false;
    View.OnClickListener captrureListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (recording) {
                // stop recording and release camera
                mediaRecorder.stop(); // stop the recording
                releaseMediaRecorder(); // release the MediaRecorder object
//                Toast.makeText(AndroidVideoCaptureExample.this, "Video captured!", Toast.LENGTH_LONG).show();
                recording = false;
            } else {
                if (!prepareMediaRecorder()) {
//                    Toast.makeText(AndroidVideoCaptureExample.this, "Fail in prepareMediaRecorder()!\n - Ended -", Toast.LENGTH_LONG).show();
                    finish();
                }
                // work on UiThread for better performance
                runOnUiThread(new Runnable() {
                    public void run() {
                        // If there are stories, add them to the table

                        try {
                            mediaRecorder.start();
                        } catch (final Exception ex) {
                            // Log.i("---","Exception in thread");
                        }
                    }
                });

                recording = true;
            }
        }
    };

    private void releaseMediaRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.reset(); // clear recorder configuration
            mediaRecorder.release(); // release the recorder object
            mediaRecorder = null;
            mCamera.lock(); // lock camera for later use
        }
    }

    private boolean prepareMediaRecorder() {

        mediaRecorder = new MediaRecorder();

        mCamera.unlock();
        mediaRecorder.setCamera(mCamera);

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_720P));

        mediaRecorder.setOutputFile("/sdcard/myvideo.mp4");
        mediaRecorder.setMaxDuration(600000); // Set max duration 60 sec.
        mediaRecorder.setMaxFileSize(50000000); // Set max file size 50M

        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException e) {
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            releaseMediaRecorder();
            return false;
        }
        return true;

    }

    private void releaseCamera() {
        // stop and release camera
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

}


