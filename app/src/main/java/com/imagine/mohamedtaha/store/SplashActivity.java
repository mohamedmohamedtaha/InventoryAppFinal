package com.imagine.mohamedtaha.store;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashActivity extends AppCompatActivity {
    private static boolean splashLoad = false;
    ImageView imageViewSplash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!splashLoad) {
            setContentView(R.layout.activity_splash);
            imageViewSplash = (ImageView) findViewById(R.id.imageSplash);
            Animation animationImage = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_splash);

            imageViewSplash.startAnimation(animationImage);

            int splashTime = 1750;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashActivity.this, Add_Category_Fragment_ContentProvider.class));
                    finish();
                }
            }, splashTime);
            splashLoad = true;

        } else {
            Intent goToMainActivity = new Intent(SplashActivity.this, Add_Category_Fragment_ContentProvider.class);
            startActivity(goToMainActivity);
            finish();
        }
    }
}
