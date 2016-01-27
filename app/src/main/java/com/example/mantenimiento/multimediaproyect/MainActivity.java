package com.example.mantenimiento.multimediaproyect;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void intent_player(View v){
        Intent intent=new Intent(this, PlayerActivity.class);
        startActivityForResult(intent, RESULT_OK);
    }

    private void startActivityForResult(Intent intent) {

    }
}
