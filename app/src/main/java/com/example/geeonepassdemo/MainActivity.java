package com.example.geeonepassdemo;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.audiofx.BassBoost;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.geetest.onepass.BaseGOPListener;
import com.geetest.onepass.GOPGeetestUtils;
import com.geetest.onepass.GOPHttpUtils;

import com.geetest.sdk.GT3GeetestListener;
import com.geetest.sdk.GT3GeetestUtils;


import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    /**
     * 控件
     */
    private EditText editText;
    private Button button;
    private TextView textView;
    private ImageView imageView;

    /**
     * onepass的工具类
     */
    private GOPGeetestUtils gopGeetestUtils;
    /**
     * onepass的监听类
     */
    private BaseGOPListener baseGOPListener;

    /**
     * testbutton的工具类
     */
    private GT3GeetestUtils gt3GeetestUtils;
    /**
     * testbutton监听类
     */
    private GT3GeetestListener gt3GeetestListener;
    /**
     * 服务器配置的verifyUrl接口
     */
    public static final String GTM_GATEWAY = "https://onepass.geetest.com/check_gateway.php";
    /**
     * 服务器配置的configUrl接口,格式为{"success": 1,"challenge": "85b5d5a9e255c32a37fd3a2d551983c6","gt": "019924a82c70bb123aae90d483087f94", "new_captcha": true}
     */
    private static final String CAPTCHA_URL = "http://www.geetest.com/demo/gt/register-fullpage";
    /**
     * 配置的customid
     */
    private static final String CUSTOM_ID = "";
    /**
     * 进度条
     */
    private ProgressDialog progressDialog;

    /**
     * 新map保存结果用于校验结果
     */
    private Map<String, String> getMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setBackgroundDrawable(null);
        TextView textView = (TextView) findViewById(R.id.gtop_title_name);
        textView.setText(new GTMTextUtils().getText());
        //拿到这个权限可以更方便的进行网关验证
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
        } else {
            //TODO
        }
        init();
        initGT3();
        initGop();
    }

    /**
     * 初始化控件
     */
    private void init() {
        editText = (EditText) findViewById(R.id.et);
        button = (Button) findViewById(R.id.btn);
        textView = (TextView) findViewById(R.id.gtm_check_num_tv);
        imageView = (ImageView) findViewById(R.id.gtm_back_iv);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //检测手机号格式
                if (chargePhoneNum(editText.getText().toString())) {
                    textView.setVisibility(View.INVISIBLE);
                    editText.getBackground().clearColorFilter();
                    if (!haveIntent(MainActivity.this)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage("检测到未开启移动数据，建议手动开启进行操作");
                        builder.setTitle("提示");
                        builder.setPositiveButton("好的,我这就去开启", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.setNegativeButton("不好，我就不开", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                progressDialog = ProgressDialog.show(MainActivity.this, null, "验证加载中", true, true);
                                /**
                                 * testbutton开启
                                 */
                                gt3GeetestUtils.getGeetest(MainActivity.this, CAPTCHA_URL, null, gt3GeetestListener);
                            }
                        });
                        builder.create().show();
                    } else {
                        progressDialog = ProgressDialog.show(MainActivity.this, null, "验证加载中", true, true);
                        /**
                         * testbutton开启
                         */
                        gt3GeetestUtils.getGeetest(MainActivity.this, CAPTCHA_URL, null, gt3GeetestListener);
                    }

                } else {
                    textView.setVisibility(View.VISIBLE);
                    editText.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);

                }
            }
        });
    }

    /**
     * 初始化testbutton,此处为验证码的demo，如果需要，请自行申请验证码
     */
    private void initGT3() {
        gt3GeetestUtils = GT3GeetestUtils.getInstance(MainActivity.this);
        /**
         * 初始化textbutton监听类
         */
        gt3GeetestListener = new GT3GeetestListener() {
            @Override
            public void gt3DialogOnError(String s) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                /**
                 * 方法为调用之后后续流程不会走，为报错的时候所走的方法
                 */
                gt3GeetestUtils.cancelAllTask();
                toastUtil(s);
            }

            @Override
            public boolean gtSetIsCustom() {
                /**
                 * 返回true表示自定义api2
                 */
                return true;
            }

            @Override
            public void gt3GetDialogResult(boolean b, String s) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    /**
                     * 拿到验证码的validate
                     */
                    openOnePass(jsonObject.getString("geetest_validate"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

    }

    /**
     * 初始化onepass
     */
    private void initGop() {
        gopGeetestUtils = GOPGeetestUtils.getInstance(MainActivity.this);
        /**
         * 初始化onepass监听类
         */
        baseGOPListener = new BaseGOPListener() {
            @Override
            public void gopOnError(String s) {
                /**
                 * 过程中的错误
                 */
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                toastUtil(s);
            }

            @Override
            public void gopOnResult(Map<String, String> map) {
                /**
                 * 发送参数进行校验
                 */
                getMap = map;
                new GtmNewTask().execute();
            }

            @Override
            public void gopOnSendMsg(boolean b, Map<String, String> map) {
                /**
                 * 短信分发接口
                 */
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                if (b) {
                    Intent intent = new Intent(getApplicationContext(), SendMessageActivity.class);
                    intent.putExtra("custom", map.get("custom"));
                    intent.putExtra("phone", map.get("phone"));
                    intent.putExtra("process_id", map.get("process_id"));
                    intent.putExtra("message_id", map.get("message_id"));
                    startActivity(intent);
                } else {
                    toastUtil("自定义短信");
                }
            }
        };
    }

    /**
     * onepass的方法，执行onepass只需拿到验证码的validate，兼容所有公版验证码
     *
     * @param validate
     */
    private void openOnePass(String validate) {
        /**
         *    第一参数为填写的手机号
         *    第二个参数为验证后的validate
         *    第三个参数为customid
         *    第四个参数为回调
         */
        gopGeetestUtils.getOnePass(editText.getText().toString(), validate, CUSTOM_ID, baseGOPListener);
    }


    /**
     * 判断数据网络是否开启
     *
     * @param context
     * @return
     */
    private boolean haveIntent(Context context) {
        boolean mobileDataEnabled = false; // Assume disabled
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            Class cmClass = Class.forName(cm.getClass().getName());
            Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
            method.setAccessible(true); // Make the method callable
            // get the setting for "mobile data"
            mobileDataEnabled = (Boolean) method.invoke(cm);
        } catch (Exception e) {
            // Some problem accessible private API
            // TODO do whatever error handling you want here
        }
        return mobileDataEnabled;
    }


    /**
     * 自定义请求
     */
    private class GtmNewTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            //网络请求需要自己写
            return GOPHttpUtils.submitPostData2(GTM_GATEWAY, getMap, "utf-8");
        }

        @Override
        protected void onPostExecute(String params) {
            if (TextUtils.isEmpty(params)) {
                //结果异常则发送短信
                gopGeetestUtils.sendMsg();
            } else {
                try {
                    JSONObject jsonObject = new JSONObject(params);
                    int result = jsonObject.getInt("result");
                    if (result == GOPGeetestUtils.GOP_RESULT_SUCCESS) {
                        //验证成功，进入验证成功页面
                        toastUtil("success");
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }

                        startActivity(new Intent(getApplicationContext(), SuccessActivity.class));
                    } else if (result == GOPGeetestUtils.GOP_RESULT_ARREARS) {
                        toastUtil("您已经欠费");
                    } else {
                        //结果异常则发送短信
                        gopGeetestUtils.sendMsg();
                    }
                } catch (Exception e) {
                    //结果异常则发送短信
                    gopGeetestUtils.sendMsg();
                }
            }

        }

    }

    /**
     * 判断手机号合法性
     *
     * @param phoneNumber 手机号
     * @return
     */
    public boolean chargePhoneNum(String phoneNumber) {
        String regExp = "^((13[0-9])|(15[^4])|(18[0-9])|(17[0-8])|(14[5-9])|(19[8,9])|)\\d{8}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(phoneNumber);
        return m.matches();
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
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        //销毁的时候执行
        gopGeetestUtils.cancelUtils();
        gt3GeetestUtils.cancelUtils();


    }
}
