package com.example.remart;

import android.speech.tts.UtteranceProgressListener;
import android.os.Bundle;
import android.content.Intent;
import android.os.Handler;

import android.speech.tts.TextToSpeech;

import android.util.Log;

//import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 2000;

    //private int MY_DATA_CHECK_CODE = 0;
    private TextToSpeech textToSpeech;
    String credits ="Developed by Brian Soh";

    private static final String TAG = SplashActivity.class.getName();

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onDone(String utteranceId) {
                    Log.d(TAG, "Done:  " + utteranceId);
                    //Intent homeIntent = new Intent(CinemaList.this, MovieList.class);
                    //startActivity(homeIntent);
                }

                @Override
                public void onError(String utteranceId) {
                    Log.e(TAG, "Error:  " + utteranceId);
                }

                @Override
                public void onStart(String utteranceId) {
                    Log.i(TAG, "Started:  " + utteranceId);
                }
            });
            textToSpeech.speak(credits, TextToSpeech.QUEUE_ADD, null);
        } else {
            Log.e(TAG, "Failed");
        }
    }


    @Override
    protected void onPause() {
        if(textToSpeech != null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onPause();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //Intent checkTTSIntent = new Intent();
        //checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        //startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);
        textToSpeech = new TextToSpeech(this, this);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(i);
                finish();

            }
        }, SPLASH_TIME_OUT);


    }

}
