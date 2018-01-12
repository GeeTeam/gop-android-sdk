package com.example.geeonepassdemo;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.geetest.onepass.GOPGeetestUtils;




import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SendMessageActivity extends AppCompatActivity {
    private TextView textView, tvphone;
    private EditText editText;
    private String custom, phone, process_id, message_id;
    /**
     * 服务器配置的checkMessageUrl的接口
     */
    public static final String GOP_CHECK_MSG = "https://onepass.geetest.com/check_message.php";
    private ImageView imageView;
    private CountDownTimer countDownTimer;
    /**
     * 用于短信验证的map
     */
    private Map<String, String> mapcheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
        getWindow().setBackgroundDrawable(null);
        TextView tvTitle = (TextView) findViewById(R.id.tv_msg_title);
        tvTitle.setText(new GTMTextUtils().getText());
        init();
        countDownTimer = new CountDownTimer(60000 + 500, 1000) {

            @Override

            public void onTick(long millisUntilFinished) {

                textView.setText("(" + millisUntilFinished / 1000 + "秒)");

            }

            @Override
            public void onFinish() {
                textView.setText("(0秒)");
            }
        }.start();
    }

    private void init() {
        textView = (TextView) findViewById(R.id.tv_msg);
        tvphone = (TextView) findViewById(R.id.tv_phone);
        imageView = (ImageView) findViewById(R.id.gtm_back_iv);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }

        });
        editText = (EditText) findViewById(R.id.et_msg);
        Intent intent = getIntent();
        custom = intent.getStringExtra("custom");
        phone = intent.getStringExtra("phone");
        process_id = intent.getStringExtra("process_id");
        message_id = intent.getStringExtra("message_id");
        tvphone.setText(phone);
        //进行短信验证
        findViewById(R.id.btn_msg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
                Map<String, String> map = new HashMap<>(8);
                map.put("message_number", editText.getText().toString());
                map.put("custom", custom);
                map.put("phone", phone);
                map.put("process_id", process_id);
                map.put("message_id", message_id);
                mapcheck = map;
                //将上面的五个参数传到配置的check_msg接口进行短信认证
                new GtmCheckTask().execute();
            }
        });
    }

    /**
     * 验证短信
     */
    private class GtmCheckTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            return GOPGeetestUtils.gopCheckMsg(GOP_CHECK_MSG, mapcheck, "utf-8");
        }

        @Override
        protected void onPostExecute(String params) {
            if (TextUtils.isEmpty(params)) {
                toastUtil("验证异常");
            } else {
                try {
                    JSONObject jsonObject = new JSONObject(params);
                    int result = jsonObject.getInt("result");
                    if (result == GOPGeetestUtils.GOP_RESULT_SUCCESS) {
                        //验证成功，进行页面跳转
                        toastUtil("success");
                        startActivity(new Intent(getApplicationContext(), SuccessActivity.class));
                    } else if (result == GOPGeetestUtils.GOP_RESULT_ARREARS) {
                        toastUtil("您已经欠费");
                    } else {
                        toastUtil("验证失败");
                    }

                } catch (JSONException e) {
                    toastUtil("验证异常");
                }
            }

        }
    }

    /**
     * toast工具
     *
     * @param str
     */
    private void toastUtil(String str) {
        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
