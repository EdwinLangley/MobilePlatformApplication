package com.example.edwin.neighbourhooddiary;

import android.content.Intent;
import android.media.Image;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

public class Splash extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 2000;
    private ImageView img;
    boolean testTrigger = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.activity_splash);

        img = (ImageView) findViewById(R.id.imageView);

        RotateAnimation rotateAnimation = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        rotateAnimation.setDuration(400);
        rotateAnimation.setRepeatCount(0);
        img.startAnimation(rotateAnimation);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(testTrigger == false){
                    Intent mainIntent = new Intent(Splash.this, WelcomeScreen.class);
                    Splash.this.startActivity(mainIntent);
                    Splash.this.finish();
                } else{
                    Intent testIntent = new Intent(Splash.this, NFCTester.class);
                    startActivity(testIntent);
                    Splash.this.finish();
                }

            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    public void openMapPage(View view){
        testTrigger = true;
    }
}
