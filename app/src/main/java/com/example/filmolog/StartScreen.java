package com.example.filmolog;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class StartScreen extends AppCompatActivity {
    private static int SPLASH_TİME_OUT=4000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);
        hideactionBar();
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run(){
                Intent homeIntent=new Intent(StartScreen.this,MovieUpdate.class);
                startActivity(homeIntent);
                finish();
            }
        },SPLASH_TİME_OUT);


    }
    private void hideactionBar() {
        //video açıldığında action barı gizler
        getSupportActionBar().hide();
    }
}