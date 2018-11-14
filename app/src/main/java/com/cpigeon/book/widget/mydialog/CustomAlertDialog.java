package com.cpigeon.book.widget.mydialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.DisplayMetrics;
import android.view.Window;

import com.cpigeon.book.R;

/**
 * 自定义dialog 可以设置圆角
 * Created by Administrator on 2018/3/29.
 */

public class CustomAlertDialog extends Dialog {

    public CustomAlertDialog(@NonNull Context context) {
        super(context);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    public CustomAlertDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    public CustomAlertDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
        int displayWidth = dm.widthPixels;
        int displayHeight = dm.heightPixels;
        android.view.WindowManager.LayoutParams p = getWindow().getAttributes();  //获取对话框当前的参数值
        p.width = (int) (displayWidth * 0.80);    //宽度设置为屏幕的0.55
//        p.height = (int) (displayHeight * 0.28);    //高度设置为屏幕的0.28
        getWindow().setAttributes(p);     //设置生效

        this.getWindow().setBackgroundDrawableResource(R.drawable.dialog_corner_bg);
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }
}
