package com.l.eyescure.util;

import android.content.Context;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.util.Log;

import com.l.eyescure.server.pdfManager.StorageInfo;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/4.
 */
public class StorageUtil {
    public static List<StorageInfo> listAllDisk(Context context) {
        ArrayList<StorageInfo> storages = new ArrayList<StorageInfo>();
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        try {
            Class<?>[] paramClasses = {};
            Method getVolumeList = StorageManager.class.getMethod("getVolumeList", paramClasses);
            Object[] params = {};
            Object[] invokes = (Object[]) getVolumeList.invoke(storageManager, params);

            if (invokes != null) {
                StorageInfo info = null;
                for (int i = 0; i < invokes.length; i++) {
                    Object obj = invokes[i];
                    Method getPath = obj.getClass().getMethod("getPath", new Class[0]);
                    String path = (String) getPath.invoke(obj, new Object[0]);
                    info = new StorageInfo(path,"USB");

                    Method getVolumeState = StorageManager.class.getMethod("getVolumeState", String.class);
                    String state = (String) getVolumeState.invoke(storageManager, info.path);
                    info.state = state;

                    Method isRemovable = obj.getClass().getMethod("isRemovable", new Class[0]);
                    info.isRemoveable = ((Boolean) isRemovable.invoke(obj, new Object[0])).booleanValue();
                    storages.add(info);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        storages.trimToSize();
        return storages;
    }

    public static List<StorageInfo> getAvaliableStorage(List<StorageInfo> infos) {
        List<StorageInfo> storages = new ArrayList<StorageInfo>();
        for (StorageInfo info : infos) {
            File file = new File(info.path);
            if (file.exists()) {
                if (file.isDirectory()) {
                    if (file.canWrite()) {
                        if (info.isMounted()) {
                            if (info.isRemoveable) {
                                storages.add(info);
                            }
                        }
                    }
                }
            }
        }

        return storages;
    }

    private final String TAG = "mdqbyusb";

    public List<StorageInfo> getAllStorageInfo(Context context) {
        List<StorageInfo> list = listAllDisk(context);
        for (StorageInfo info : list) {
            Log.e(TAG, info.toString());
        }
        Log.e(TAG, "-----------------");
        List<StorageInfo> infos = getAvaliableStorage(list);
        for (StorageInfo info : infos) {
            Log.e(TAG, info.toString());
        }
        Log.e(TAG, "-----------------");
        Log.e(TAG, "Environment.getExternalStorageDirectory(): " + Environment.getExternalStorageDirectory());
        return infos;
    }

    public List<StorageInfo> getAllUsbPath(Context context) {
        List<StorageInfo> usbs = new ArrayList<>();

        List<StorageInfo> ll = getAllStorageInfo(context);
        if (ll == null || ll.size() == 0) {
            return usbs;
        }

        for (int i = 0; i < ll.size(); i++) {
            String mPath = ll.get(i).path;
            if (mPath.indexOf("usb") != -1){
                File f = new File(mPath);
//                File[] files = f.listFiles();// 列出所有文件
//                // 将所有文件存入list中
//                if (files != null) {
//                    for (int j = 0; j < files.length; j++) {
//                        File file = files[j];
//                        if (!file.getName().equals("baidu")) {
                            String mUsbPath = f.getAbsolutePath();
                            String mUsbName = f.getName();

                            StorageInfo usb_entity = new StorageInfo(mUsbPath,mUsbName);
                            usbs.add(usb_entity);
//                        }
//                    }
//                }
            }
        }
        return usbs;
    }

    public String getSDPath(Context context) {
        StorageUtil ss = new StorageUtil();
        List<StorageInfo> ll = ss.getAllStorageInfo(context);
        if (ll.size() == 0) {
            return null;
        }

        int OK_index = -1;
        for (int i = 0; i < ll.size(); i++) {
            if ((ll.get(i).path).indexOf("internal_sd") != -1)
                continue;
            if ((ll.get(i).path).indexOf("sd") != -1)
                OK_index = i;
        }

        if (OK_index < 0) {
            return null;
        }

        String dir = ll.get(OK_index).path;
        return dir;
    }

}
