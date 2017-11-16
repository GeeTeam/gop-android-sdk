package com.example.geeonepassdemo;

import android.animation.AnimatorSet;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by 谷闹年 on 2017/11/8.
 */
public class GTMMyView extends RelativeLayout {
    public GTMMyView(Context context) {
        super(context);
        init(context);
    }

    public GTMMyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GTMMyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private AnimatorSet animatorSet;
    private ImageView iv1, iv2, iv3, iv4, iv5, iv6;
   private TextView textView;
    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.gtm_ll_my_view, this);
        iv1 = (ImageView) view.findViewById(R.id.gtm_iv_success1);
        iv2 = (ImageView) view.findViewById(R.id.gtm_iv_success2);
        iv3 = (ImageView) view.findViewById(R.id.gtm_iv_success3);
        iv4 = (ImageView) view.findViewById(R.id.gtm_iv_success4);
        iv5 = (ImageView) view.findViewById(R.id.gtm_iv_success5);
        iv6 = (ImageView) view.findViewById(R.id.gtm_iv_success6);
        textView = (TextView) findViewById(R.id.tv_success_demo);

    }
  public void setText(String data){
      textView.setText(data);
  }
    public void start() {
        ObjectAnimator obiv1x = ObjectAnimator.ofFloat(iv1, "translationX", 0f, -225f);
        ObjectAnimator obiv1y = ObjectAnimator.ofFloat(iv1, "translationY", 0f, -70f);

        ObjectAnimator obiv2x = ObjectAnimator.ofFloat(iv2, "translationX", 0f, 290f);
        ObjectAnimator obiv2y = ObjectAnimator.ofFloat(iv2, "translationY", 0f, -160f);

        ObjectAnimator obiv3x = ObjectAnimator.ofFloat(iv3, "translationX", 0f, 215f);
        ObjectAnimator obiv3y = ObjectAnimator.ofFloat(iv3, "translationY", 0f, -70f);

        ObjectAnimator obiv4x = ObjectAnimator.ofFloat(iv4, "translationX", 0f, -200f);
        ObjectAnimator obiv4y = ObjectAnimator.ofFloat(iv4, "translationY", 0f, -170f);

        ObjectAnimator obiv5x = ObjectAnimator.ofFloat(iv5, "translationX", 0f, -300f);
        ObjectAnimator obiv5y = ObjectAnimator.ofFloat(iv5, "translationY", 0f, -160f);

        ObjectAnimator obiv6x = ObjectAnimator.ofFloat(iv6, "translationX", 0f, 170f);
        ObjectAnimator obiv6y = ObjectAnimator.ofFloat(iv6, "translationY", 0f, -170f);

        ValueAnimator mircleAnim = ValueAnimator.ofFloat(0, 0, 0, 255);
        mircleAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                iv1.setAlpha((Float) animation.getAnimatedValue());
                iv2.setAlpha((Float) animation.getAnimatedValue());
                iv3.setAlpha((Float) animation.getAnimatedValue());
                iv4.setAlpha((Float) animation.getAnimatedValue());
                iv5.setAlpha((Float) animation.getAnimatedValue());
                iv6.setAlpha((Float) animation.getAnimatedValue());
            }
        });
        animatorSet = new AnimatorSet();
        animatorSet.setDuration(500);
        animatorSet.playTogether(obiv1x, obiv1y,obiv2x,obiv2y,obiv3x,obiv3y,obiv4x,obiv4y,obiv5x,obiv5y,obiv6x,obiv6y, mircleAnim);
        animatorSet.start();
    }
}
