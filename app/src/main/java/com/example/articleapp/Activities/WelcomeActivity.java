package com.example.articleapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.articleapp.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class WelcomeActivity extends AppCompatActivity {

    LottieAnimationView lottie1;
    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_welcome);

        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.welcome_text).setVisibility(View.VISIBLE);
                findViewById(R.id.welcome_text).setAnimation(AnimationUtils.loadAnimation(WelcomeActivity.this,R.anim.animate_fade_enter));
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(WelcomeActivity.this,LoginActivity.class));
                        finish();
                        Animatoo.animateSlideDown(WelcomeActivity.this);
                    }
                },400);
            }
        },2300);

        Date date = Calendar.getInstance().getTime();

        SimpleDateFormat formatterDate = new SimpleDateFormat("MMM dd, yyyy");
        SimpleDateFormat formatterTime = new SimpleDateFormat("hh-mm a");



        final String currentDate = formatterDate.format(date);
        final String currentTime = formatterTime.format(date);

        Log.d("Raju",currentDate);
        Log.d("Raju",currentTime);

    }
}