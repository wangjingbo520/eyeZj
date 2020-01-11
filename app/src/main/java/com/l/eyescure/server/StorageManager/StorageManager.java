package com.l.eyescure.server.StorageManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;

import com.l.eyescure.application.MyApplication;
import com.l.eyescure.server.EyesModule;
import com.l.eyescure.server.ServerHelper;
import com.l.eyescure.server.callBack.INormalChangeCallback;
import com.l.eyescure.server.callBack.ISavePathChangeCallback;
import com.l.eyescure.server.callBack.IStorageChangeCallback;
import com.l.eyescure.server.cureManage.CureInfoManage;
import com.l.eyescure.server.pdfManager.StorageInfo;
import com.l.eyescure.server.printManager.PrintUnit;
import com.l.eyescure.util.Constant;
import com.l.eyescure.util.StorageUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/9/4.
 */
public class StorageManager extends EyesModule {
    private List<StorageInfo> allStorages = new ArrayList<>();
    private List<IStorageChangeCallback> storageChangeListeners = new ArrayList<>();
    private List<INormalChangeCallback> tfChangeListeners = new ArrayList<>();
    private List<ISavePathChangeCallback> savePathListeners = new ArrayList<>();
    private Handler mHandler;
    private String savePath;
    private String tfPath;
    private String printEdgeValue;
    private SharedPreferencesUtil shardUnit;
    private String cachePath = null;
    private String SavePdfPath = null;
    private static File cache;

    public List<StorageInfo> getAllStorages() {
        if (allStorages.size() == 0) {
            getCurAllStorageMsg(1);
        }
        return allStorages;
    }

    public String getDefaultUsbPath() {
        StorageUtil ss = new StorageUtil();
        List<StorageInfo> mUsbList = ss.getAllUsbPath(mContext);

        if (mUsbList.size() == 0) {
            return null;
        }

        String path = mUsbList.get(0).path;
        return path;
    }

    public String getTfPath() {
        if (TextUtils.isEmpty(tfPath)) {
            tfPath = shardUnit.readString(Constant.STORAGE_TF_PATH);
        }
        return tfPath;
    }

    public String getSavePath() {
        if (TextUtils.isEmpty(savePath)) {
            savePath = shardUnit.readString(Constant.STORAGE_USB_PATH);
        }
        return savePath;
    }

    public void setPrintEdgeValue(PrintUnit edgeValue) {
        if (!TextUtils.isEmpty(printEdgeValue) && printEdgeValue.equals(edgeValue))
            return;
        int left = edgeValue.getLeft();
        int right = edgeValue.getRight();
        int top = edgeValue.getTop();
        int bottom = edgeValue.getBottom();

        String str_value = String.valueOf(left) + "," + String.valueOf(right)
                + "," + String.valueOf(top) + "," + String.valueOf(bottom);

        printEdgeValue = str_value;
        //记忆当前的位置
        shardUnit.saveString(Constant.STORAGE_PRINT_EDGE, printEdgeValue);
    }

    public PrintUnit getPrintEdgeValue() {
        if (TextUtils.isEmpty(printEdgeValue)) {
            printEdgeValue = shardUnit.readString(Constant.STORAGE_PRINT_EDGE);
        }

        if (TextUtils.isEmpty(printEdgeValue)) {
            PrintUnit printUnit = new PrintUnit(15, 10, 15, 10);
            return printUnit;
        } else {

            String[] values = printEdgeValue.split(",");
            if (values.length != 4)
                return null;

            int left = Integer.valueOf(values[0]);
            int right = Integer.valueOf(values[1]);
            int top = Integer.valueOf(values[2]);
            int bottom = Integer.valueOf(values[3]);

            PrintUnit printUnit = new PrintUnit(left, top, right, bottom);
            return printUnit;
        }
    }

    @Override
    public void init(int step) {
        switch (step) {
            case 0: {
                init();
                break;
            }
        }
    }

    private void init() {
        mHandler = new Handler();
        shardUnit = new SharedPreferencesUtil(mContext);

        File cache = new File(Environment.getExternalStorageDirectory(), Constant.cache);
        if (!cache.exists()) {
            cache.mkdirs();
        }
        cachePath = cache.getPath();

        //获取到当前所有的USB信息
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.USB_ACTION);
        filter.addAction(Constant.TF_MOUNTED_ACTION);
        filter.addAction(Constant.TF_EJECT_ACTION);
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        filter.addDataScheme("file");
        mContext.registerReceiver(usBroadcastReceiver, filter);

        dataInit();

        getPrintEdgeValue();
    }

    private void dataInit(){
//        getSavePath();
        getCurAllStorageMsg(1);
//        getTfPath();
    }

    private void getCurAllStorageMsg(int type) {
        StorageUtil ss = new StorageUtil();
        List<StorageInfo> mUsbList = ss.getAllUsbPath(mContext);

        allStorages.clear();
        for (int i = 0; i < mUsbList.size(); i++) {
            StorageInfo mInfo = mUsbList.get(i);
            if (mInfo != null) {
                String path = mInfo.path;
                if (type == 1) {
                    if (path.equals(savePath)) {
                        mInfo.isChecked = true;
                    }
                } else {
                    if (path.equals(tfPath)) {
                        mInfo.isChecked = true;
                    }
                }
                allStorages.add(mInfo);
            }
        }
    }

    private BroadcastReceiver usBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constant.USB_ACTION)
                    || action.equals(UsbManager.ACTION_USB_ACCESSORY_ATTACHED)
                    || action.equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)
                    || action.equals(UsbManager.ACTION_USB_DEVICE_DETACHED)
                    || action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshUsbStorageInfo();
                    }
                }, 3000);  //延时的原因是需要这段时间后查询才准确
            } else if (action.equals(Constant.TF_MOUNTED_ACTION)) {
                refreshTFStorageInfo(true);
            } else if (action.equals(Constant.TF_EJECT_ACTION)) {
                refreshTFStorageInfo(false);
            }
        }
    };

    private void refreshTFStorageInfo(boolean bMount) {
        CureInfoManage cureInfoManage = (CureInfoManage) ServerHelper.getInstance().getModule(ServerHelper.MODULE_ID_CUREINFO);
        if (bMount) {
            if (cureInfoManage != null) {
                cureInfoManage.setDaoConfigDir(false);
            }

            dataInit();
        } else {
            int type = -1;
            if (cureInfoManage != null) {
                type = cureInfoManage.getInfoMode;
            }

            if (type == 0){
                MyApplication.getInstance().showToastStr("请不要拔出TF卡，否则本地模式数据将无法存储！");
            }
        }

        if (tfChangeListeners != null) {
            for (INormalChangeCallback listener : tfChangeListeners) {
                listener.onChanged(bMount);
            }
        }
    }

    private void refreshUsbStorageInfo() {
        getCurAllStorageMsg(1);
        if (storageChangeListeners != null) {
            for (IStorageChangeCallback listener : storageChangeListeners) {
                listener.onChanged(allStorages);
            }
        }
    }

    @Override
    public void done(int step) {

    }

    //触发界面刷新监听
    public void registerStorageInfoChangedCallback(IStorageChangeCallback listener) {
        if (listener == null) {
            return;
        }
        if (!storageChangeListeners.contains(listener)) {
            storageChangeListeners.add(listener);
        }
    }

    public void unregisterStorageInfoChangedCallback(IStorageChangeCallback listener) {
        if (listener == null) {
            return;
        }
        if (storageChangeListeners.contains(listener)) {
            storageChangeListeners.remove(listener);
        }
    }

    //触发界面刷新监听
    public void registerTfInfoChangedCallback(INormalChangeCallback listener) {
        if (listener == null) {
            return;
        }
        if (!tfChangeListeners.contains(listener)) {
            tfChangeListeners.add(listener);
        }
    }

    public void unregisterTfInfoChangedCallback(INormalChangeCallback listener) {
        if (listener == null) {
            return;
        }
        if (tfChangeListeners.contains(listener)) {
            tfChangeListeners.remove(listener);
        }
    }

    //触发界面刷新监听
    public void registerSavePathChangedCallback(ISavePathChangeCallback listener) {
        if (listener == null) {
            return;
        }
        if (!savePathListeners.contains(listener)) {
            savePathListeners.add(listener);
        }
    }

    public void unregisterSavePathChangedCallback(ISavePathChangeCallback listener) {
        if (listener == null) {
            return;
        }
        if (savePathListeners.contains(listener)) {
            savePathListeners.remove(listener);
        }
    }

    public void saveUsbPath(String path) {
        if (!TextUtils.isEmpty(savePath) && savePath.equals(path))
            return;
        savePath = path;
        //记忆当前的位置
        shardUnit.saveString(Constant.STORAGE_USB_PATH, path);
        //通知所有想知道的对象
        if (savePathListeners != null) {
            for (ISavePathChangeCallback listener : savePathListeners) {
                listener.onChanged(1, savePath);
            }
        }
    }

    public boolean IsSameSdCard() {
        String path = getHandwareSd();
        if (TextUtils.isEmpty(path)
                || TextUtils.isEmpty(tfPath))
            return true;
        if (tfPath.equals(path)) {
            return true;
        } else {
            return false;
        }
    }

    public String getHandwareSd() {
        StorageUtil ss = new StorageUtil();
        String path = ss.getSDPath(mContext);
        if (!TextUtils.isEmpty(path)) {
            return path;
        }
        return null;
    }

    public void saveTFPath(int type, String path) {
        if (type == 0) {
            path = getHandwareSd();
        }

        if (!TextUtils.isEmpty(tfPath) && tfPath.equals(path))
            return;
        tfPath = path;
        //记忆当前的位置
        shardUnit.saveString(Constant.STORAGE_TF_PATH, path);
        //通知所有想知道的对象
        if (savePathListeners != null) {
            for (ISavePathChangeCallback listener : savePathListeners) {
                listener.onChanged(0, savePath);
            }
        }
    }

    public String getInnerCachePath() {
        return cachePath;
    }

    public String getExtendCachePath() {
        return cachePath;
    }

    private void creatCashPdfFile() {
        File cache = new File(Environment.getExternalStorageDirectory(), Constant.PdfCache);
        if (!cache.exists()) {
            cache.mkdirs();
        }
        SavePdfPath = cache.getPath();
    }

    public String getSavePdfPath() {
        //防止用户换了usb，每次都要去检查一下，在应用中检查
        boolean bFindUsb = false;
        if (TextUtils.isEmpty(SavePdfPath)) {
            bFindUsb = true;
        } else {
            File mFile = new File(SavePdfPath);
            if (!mFile.exists()) {
                bFindUsb = true;
            }
        }

        if (bFindUsb) {
            creatCashPdfFile();
        }
        return SavePdfPath;
    }

    public void setSavePdfPath(File savePdfFile) {
        File cache = new File(savePdfFile, Constant.PdfCache);
        if (!cache.exists()) {
            cache.mkdirs();
        }
        SavePdfPath = cache.getPath();
    }
}
