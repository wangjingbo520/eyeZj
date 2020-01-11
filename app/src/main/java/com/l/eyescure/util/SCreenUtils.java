package com.l.eyescure.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Environment;
import android.view.View;

import com.l.eyescure.server.ServerHelper;
import com.orhanobut.logger.Logger;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.l.eyescure.server.StorageManager.StorageManager;

/**
 * @author w（C）
 * describe
 */
public class SCreenUtils {
    private static final String TAG = "=======>截屏了";

    public static String saveImage(Activity activity, View v, String name) {

        StorageManager storageManager = ((StorageManager) ServerHelper.getInstance().getModule(ServerHelper.MODULE_ID_STORAGE));
        Bitmap bitmap;
        //String path = '/'+Environment.DIRECTORY_DOWNLOADS + '/'+name;

        String path= storageManager.getInnerCachePath()+'/'+name+".jpg";

        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        bitmap = view.getDrawingCache();
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        try {
            bitmap = Bitmap.createBitmap(bitmap, location[0], location[1], v.getWidth(), v.getHeight());
            FileOutputStream fout = new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fout);
            return path;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Logger.e(TAG, "生成预览图片失败：" + e);
        } catch (IllegalArgumentException e) {
            Logger.e(TAG, "width is <= 0, or height is <= 0");
        } finally {
            // 清理缓存
            view.destroyDrawingCache();
        }
        return null;

    }
}
