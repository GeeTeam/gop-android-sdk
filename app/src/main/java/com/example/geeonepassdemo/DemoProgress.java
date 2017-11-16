package com.example.geeonepassdemo;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;

/**
 * Created by 谷闹年 on 2017/9/27.
 */
public class DemoProgress extends ProgressDialog {
    public DemoProgress(Context context) {
        super(context);
    }

    public DemoProgress(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init(getContext());
    }

    private void init(Context context) {
        setCancelable(false);
        setCanceledOnTouchOutside(false);

        setContentView(R.layout.gtop_dialog_layout);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(params);
    }

    @Override
    public void show() {
        super.show();
    }

}
