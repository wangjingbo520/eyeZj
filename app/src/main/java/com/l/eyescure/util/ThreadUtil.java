package com.l.eyescure.util;

import android.os.Looper;
import android.util.Log;

public class ThreadUtil {

    public static boolean isMainThread() {
        boolean isMain = Looper.getMainLooper().getThread().getId() == Thread.currentThread().getId();
        Log.e("=====>", "是主线程吗: " + isMain);
        return isMain;
    }
}
