package com.example.geeonepassdemo;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.geetest.onepass.BaseGOPListener;
import com.geetest.onepass.GOPGeetestUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    /**
     * 控件
     */
    private EditText editText;
    private Button button;
    private TextView textView;
    private ImageView imageView;
    /**
     * onepass的监听类
     */
    private BaseGOPListener baseGOPListener;

    /**
     * 服务器配置的verifyUrl接口,简称checkgateway
     */
    public static final String GOP_VERIFYURL = "https://onepass.geetest.com/check_gateway.php";
    /**
     * 配置的customid
     */
    private static final String CUSTOM_ID = "";
    /**
     * 进度条
     */
    private ProgressDialog progressDialog;

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
        initGop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        //TODO 必须调用，释放资源，销毁的时候执行
        GOPGeetestUtils.getInstance().cancelUtils();
    }

    /**
     * 初始化控件
     */
    private void init() {
        progressDialog = new DemoProgress(this);
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
                if (Utils.chargePhoneNum(editText.getText().toString())) {
                    textView.setVisibility(View.INVISIBLE);
                    editText.getBackground().clearColorFilter();
                    if (!Utils.haveIntent(MainActivity.this)) {
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
                                // 如果只接入onepass传入null,否则传入验证码返回的validate
                                openOnePass(null);
                            }
                        });
                        builder.create().show();
                    }else {
                        // 如果只接入onepass传入null,否则传入验证码返回的validate
                        openOnePass(null);
                    }

                } else {
                    textView.setVisibility(View.VISIBLE);
                    editText.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
                }
            }
        });
    }

    /**
     * onepass的方法，执行onepass只需拿到验证码的validate，兼容所有公版验证码
     *
     * @param validate
     */
    private void openOnePass(String validate) {
        progressDialog.show();
        /**
         *    第一参数为填写的手机号
         *    第二个参数为验证后的validate
         *    第三个参数为customid
         *    第四个参数为回调
         */
        GOPGeetestUtils.getInstance().getOnePass(editText.getText().toString(), validate, CUSTOM_ID, baseGOPListener);
    }

    /**
     * 初始化onepass
     */
    private void initGop() {
        GOPGeetestUtils.getInstance().init(MainActivity.this);
        /**
         * 初始化onepass监听类(必须实现的有四个接口，处理流程中实现的问题)
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
            public void gopOnResult(String result) {
                /**
                 * 验证成功的回调
                 */
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                Log.i(TAG, result);
                Intent intent = new Intent(getApplicationContext(), SuccessActivity.class);
                startActivity(intent);
            }

            /**
             * 返回0为成功1为失败，int类型。可以重写此方法，自定义解析checkgateway返回值
             * @param jsonObject
             * @return
             */
            @Override
            public int gopOnAnalysisVerifyUrl(JSONObject jsonObject) {
                /**
                 * 返回VerifyUrl的请求结果，并拿到result值回传给sdk
                 * 默认为：
                 *  try {
                 return var1.getInt("result");
                 }    catch (JSONException var3) {
                 var3.printStackTrace();
                 return 1;
                 }
                 */
                try {
                    Log.i(TAG, jsonObject.toString());
                    return super.gopOnAnalysisVerifyUrl(jsonObject);
                }catch (Exception e){
                    Log.i(TAG, e.toString());
                    return 1;
                }
            }

            @Override
            public String gopOnVerifyUrl() {
                /**
                 * 回传给sdk内部使用的VerifyUrl(必填),具体参考服务端接入文档
                 */
                return GOP_VERIFYURL;
            }

            @Override
            public Map<String, String> gopOnVerifyUrlHeaders() {
                // verifyUrl接口传入header对象
                HashMap<String, String> map = new HashMap<>();
                // map.put("Content-Type","application/json;charset=UTF-8");
                map.put("Content-Type", "application/x-www-form-urlencoded");
                return null;
            }

            @Override
            public Map<String, String> gopOnVerifyUrlBody() {
                // verifyUrl接口传入form数据对象,如果没有需要传入的数据可以返回为null，注意gopOnVerifyUrlJsonBody必须返回为null
                HashMap<String, String> map = new HashMap<>();
                // map.put("test","test");
                return null;
            }

            @Override
            public Map<String, String> gopOnVerifyUrlJsonBody() {
                // verifyUrl接口传入json数据对象，如果没有需要传入数据返回一个未put数据map，注意gopOnVerifyUrlBody必须返回为null
                HashMap<String, String> map = new HashMap<>();
                // map.put("test","test");
                return null;
            }

            /**
             * @param b 是否需要自定义短信
             * @param map 使用极验短信服务返回参数
             * @param jsonObject 发送短信原因，具体参考GitHub文档
             */
            @Override
            public void gopOnSendMsg(boolean b, Map<String, String> map, JSONObject jsonObject) {
                /**
                 * 发短信原因（JSON形式）
                 *
                 * 数据格式为json
                 * error_code与error
                 */
                Log.i(TAG, jsonObject.toString());
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
     * toast工具
     *
     * @param str
     */
    private void toastUtil(String str) {
        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
    }
}
