package com.l.eyescure.util;

import android.text.TextUtils;
import android.widget.Toast;

import com.l.eyescure.application.MyApplication;

/**
 * Created by Look on 2016/12/28.
 * toast工具类
 */

public class ToastUtil {
    public static Toast mToast;

    public static void i(int id) {
        if (mToast == null)
            mToast = Toast.makeText(MyApplication.getInstance(), id,
                    Toast.LENGTH_SHORT);
        else
            mToast.setText(id);
        mToast.show();
    }

    public static void s(String str) {
        if (TextUtils.isEmpty(str))
            return;
        if (mToast == null)
            mToast = Toast.makeText(MyApplication.getInstance(), str,
                    Toast.LENGTH_SHORT);
        else
            mToast.setText(str);
        mToast.show();
    }
}
