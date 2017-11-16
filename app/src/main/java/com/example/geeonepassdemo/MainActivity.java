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


import com.geetest.onepass.GOPHttpUtils;
import com.geetest.onepass.geetest.GOPGeetestUtils;
import com.geetest.onepass.geetest.GOPListenerUtils;


import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private Button button;
    private TextView textView;
    private ImageView imageView;

    /**
     * geetest的工具类
     */
    private GOPGeetestUtils gtmGeetestUtils;
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
    private ProgressDialog progressDialog;
    private DemoProgress demoProgress;
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
    }

    private void init() {

        //初始化gtmGeetestUtils
        gtmGeetestUtils = GOPGeetestUtils.getInstance(MainActivity.this);

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
                                new GtAppDlgTask().execute();
                            }
                        });
                        builder.create().show();
                    } else {
                        progressDialog = ProgressDialog.show(MainActivity.this, null, "验证加载中", true, true);
                        new GtAppDlgTask().execute();
                    }

                } else {
                    textView.setVisibility(View.VISIBLE);
                    editText.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);

                }
            }
        });
    }

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

    private class GtAppDlgTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {

            return GOPHttpUtils.readContentFromGet(CAPTCHA_URL);

        }

        @Override
        protected void onPostExecute(String parmas) {

            if (TextUtils.isEmpty(parmas)) {
                toastUtil("请在网络稳定下重试");
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
            } else {

                /**   只有api1不为null的时候才能进行继续操作
                 *    第一个参数为上下文
                 *    第二个参数为要传给sdk内部的结果，格式为{"success": 1,"challenge": "85b5d5a9e255c32a37fd3a2d551983c6","gt": "019924a82c70bb123aae90d483087f94", "new_captcha": true}
                 *    第三参数为填写的手机号
                 *    第四个参数为customid
                 *    第五个参数为回调
                 */
                gtmGeetestUtils.getOnePass(MainActivity.this, parmas, editText.getText().toString(), CUSTOM_ID, new GOPListenerUtils() {
                    @Override
                    public void gopOnError(String s) {
                        //执行整个流程中的错误信息
                        toastUtil(s);
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        if (demoProgress != null) {
                            demoProgress.dismiss();
                        }
                    }

                    @Override
                    public void gopOnReady(boolean b) {
                        //自定义加载框的开关，true的时候为验证码出来，进行加载框的关闭，false的时候为验证码验证成功，加载框重新加载
                        if (b) {
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                            }
                        } else {
                            demoProgress = new DemoProgress(MainActivity.this, R.style.CustomDialog);
                            demoProgress.show();

                        }
                    }

                    @Override
                    public void gopOnResult(Map<String, String> result) {
                        //处理网关的结果，真机认证，同时将返回在result进行post请求用于拿到结果
                        getMap = result;
                        Log.i("TTTTTTT",result.toString());
                        new GtmNewTask().execute();
                    }

                    @Override
                    public void gopOnSendMsg(boolean canSendMsg, Map<String, String> result) {
                        //处理发送短信的通知，将这四个参数传到发短信页面进行短信验证
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        if (demoProgress != null) {
                            demoProgress.dismiss();
                        }
                        if (canSendMsg) {
                            Intent intent = new Intent(getApplicationContext(), SendMessageActivity.class);
                            intent.putExtra("custom", result.get("custom"));
                            intent.putExtra("phone", result.get("phone"));
                            intent.putExtra("process_id", result.get("process_id"));
                            intent.putExtra("message_id", result.get("message_id"));
                            startActivity(intent);
                        } else {
                            toastUtil("自定义短信");
                        }
                    }
                });
            }

        }
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
                gtmGeetestUtils.verifyMsg();
            } else {
                Log.i("TTTTTTT",params);
                try {
                    JSONObject jsonObject = new JSONObject(params);
                    int result = jsonObject.getInt("result");
                    if (result == GOPGeetestUtils.GTM_RESULT_SUCCESS) {
                        //验证成功，进入验证成功页面
                        toastUtil("success");
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        if (demoProgress != null) {
                            demoProgress.dismiss();
                        }
                        startActivity(new Intent(getApplicationContext(), SuccessActivity.class));
                    } else if (result == GOPGeetestUtils.GTM_RESULT_ARREARS) {
                        toastUtil("您已经欠费");
                    } else {
                        //结果异常则发送短信
                        gtmGeetestUtils.verifyMsg();
                    }
                } catch (Exception e) {
                    //结果异常则发送短信
                    gtmGeetestUtils.verifyMsg();
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
        //销毁的时候执行
        gtmGeetestUtils.cancelUtils();

    }
}
