package com.example.geeonepassdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SuccessActivity extends AppCompatActivity {
    private ImageView imageView;
    private GTMMyView gtmlayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);
        getWindow().setBackgroundDrawable(null);
        TextView textView = (TextView) findViewById(R.id.tv_success_title);
        findViewById(R.id.btn_back_success).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });
        imageView = (ImageView) findViewById(R.id.gtm_back_iv);
        gtmlayout = (GTMMyView) findViewById(R.id.gtm_view);
        if (new GTMTextUtils().getText().equals("注册")) {
            textView.setText("注册成功");
            gtmlayout.setText("注册成功");
        }else {
            textView.setText("登录成功");
            gtmlayout.setText("登录成功");
        }
        gtmlayout.start();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
