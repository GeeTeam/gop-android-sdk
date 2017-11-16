package com.example.geeonepassdemo;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class LoginActivity extends AppCompatActivity {
    private ImageView imageView;
    private LinearLayout linear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        getWindow().setBackgroundDrawable(null);
        findViewById(R.id.gtm_btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GTMTextUtils().setText("快捷登录");
                startActivity(new Intent(getApplicationContext(), MainActivity.class));

            }
        });
        findViewById(R.id.gtm_btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GTMTextUtils().setText("注册");
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }

    private void init() {
        imageView = (ImageView) findViewById(R.id.gtm_iv);
        linear = (LinearLayout) findViewById(R.id.gtm_ll);
        ObjectAnimator animator = ObjectAnimator.ofFloat(imageView, "translationY", 0.0f, 0.0f, 0.0f, -280.0f);
        ValueAnimator val = ValueAnimator.ofFloat(0.0f, 0.0f, 0.0f, 1.0f);
        val.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                linear.setAlpha((Float) animation.getAnimatedValue());
            }
        });
        AnimatorSet animset = new AnimatorSet();
        animset.setDuration(1500);
        animset.playTogether(animator, val);
        animset.start();


    }

}
