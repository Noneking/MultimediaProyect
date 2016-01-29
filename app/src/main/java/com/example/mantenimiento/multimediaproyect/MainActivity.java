package com.example.mantenimiento.multimediaproyect;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public void intent_player(View v){
        Intent intent=new Intent(this, PlayerActivity.class);
        startActivityForResult(intent, RESULT_OK);
    }

    public void intent_record(View v){
        Intent intent=new Intent(this, RecordActivity.class);
        startActivityForResult(intent, RESULT_OK);
    }

    public void intent_video(View v){
        Intent intent=new Intent(this, VideoActivity.class);
        startActivityForResult(intent, RESULT_OK);
    }

    private void startActivityForResult(Intent intent) {

    }
}
