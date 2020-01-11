package com.l.eyescure.util;

import android.app.Activity;
import android.content.Context;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Look on 2017/1/14.
 * 常用的方法集合
 */

public class JayUtils {

    /**
     * 隐藏软键盘
     * @param activity
     */
    public static void hideKeyboard(Activity activity){
        //隐藏软键盘
        InputMethodManager manager = ((InputMethodManager) activity
                .getSystemService(Activity.INPUT_METHOD_SERVICE));
        if (activity.getWindow().getAttributes().softInputMode
                != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (activity.getCurrentFocus() != null) {
                manager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    public static String getFileName(String pathandname){

        int start=pathandname.lastIndexOf("/");
//        int end=pathandname.lastIndexOf(".");
        if(start!=-1){
            return pathandname.substring(start+1,pathandname.length());
        }else{
            return null;
        }
    }

    /**
     * 将assets中文件复制到SD卡中
     */
    public static void assetsDataToSD(Context context,String fileName) {
        InputStream myInput;
        try {
            OutputStream myOutput = new FileOutputStream(fileName);
            myInput = context.getAssets().open("Send2Printer.apk");
            byte[] buffer = new byte[1024];
            int length = myInput.read(buffer);
            while (length > 0) {
                myOutput.write(buffer, 0, length);
                length = myInput.read(buffer);
            }

            myOutput.flush();
            myInput.close();
            myOutput.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
