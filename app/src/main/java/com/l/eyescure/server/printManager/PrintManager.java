package com.l.eyescure.server.printManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.l.eyescure.server.EyesModule;
import com.l.eyescure.server.ServerHelper;
import com.l.eyescure.server.StorageManager.SharedPreferencesUtil;
import com.l.eyescure.server.StorageManager.StorageManager;
import com.l.eyescure.server.callBack.INormalEventListener;
import com.l.eyescure.util.Constant;

import org.cups4j.CupsClient;
import org.cups4j.CupsPrinter;
import org.cups4j.PrintJob;
import org.cups4j.PrintJobAttributes;
import org.cups4j.PrintRequestResult;
import org.cups4j.PrinterAttributes;
import org.cups4j.WhichJobsEnum;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/9/5.
 */
public class PrintManager extends EyesModule {

    private static final String LOG_TAG = "eye_print";
    public static final String ACTION_CUPS = "com.tchip.printer.CupsNotify";
    CupsIntentReceiver mCupsReceiver = new CupsIntentReceiver();
    private Handler mHandler;
    private List<INormalEventListener> pinterChangedListeners = new ArrayList<>();
    private PrintJobMsg curJob = null;
    private PrinterInfo curJPrinter = null;
    public SharedPreferencesUtil util;

    private int printerType = 0;

    public int getPrinterType() {
        return printerType;
    }

    public void setPrinterType(int printerType) {
        this.printerType = printerType;
    }

    public PrintJobMsg getCurJob() {
        return curJob;
    }

    public PrinterInfo getCurJPrinter() {
        return curJPrinter;
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
        IntentFilter filter = new IntentFilter(ACTION_CUPS);
        mContext.registerReceiver(mCupsReceiver, filter);

        util = new SharedPreferencesUtil(mContext);
        //得到保存的打印方式
        syncPrinterType();

        mHandler = new Handler();
    }

    public void syncPrinterType() {
        int mPrinterType = util.readInt(Constant.PRINTER_TYPE_STR);
        if (mPrinterType < 0) {
            printerType = 0;
        } else {
            printerType = mPrinterType;
        }
    }

    @Override
    public void done(int step) {
        switch (step) {
            case 0: {
                mContext.unregisterReceiver(mCupsReceiver);
                break;
            }
        }
    }

    private class CupsIntentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String event = intent.getStringExtra("event");
            Log.e(LOG_TAG, "Event: " + event);
            Log.e(LOG_TAG, "  message: " + intent.getStringExtra("message") + "\n");
            switch (event) {
                case "PrinterStateChanged": {
                    printerStateChanged(intent);
                    break;
                }
                case "JobCreated": {
                    jobStateChanged(intent, 1);
                    break;
                }
                case "JobCompleted": {
                    jobStateChanged(intent, 2);
                    break;
                }
                case "JobStopped": {
                    jobStateChanged(intent, 3);
                    break;
                }
                case "JobState": {
                    jobStateChanged(intent, 4);
                    break;
                }
            }
        }
    }

    private void jobStateChanged(Intent intent, int cmd) {
        int id = intent.getIntExtra("job-id", -1);
        String name = intent.getStringExtra("job-name");
        String reason = intent.getStringExtra("job-state-reasons");

        Log.e(LOG_TAG, "  job-id: " + id + "\n" +
                "  job-name: " + name + "\n" +
                "  job-state-reasons: " + reason + "\n");

        name = intent.getStringExtra("printer-name");
        id = intent.getIntExtra("printer-state", -1);
        reason = intent.getStringExtra("printer-state-reasons");
        boolean bAccept = intent.getBooleanExtra("printer-is-accepting-jobs", false);

        Log.e(LOG_TAG, "  printer-name: " + name + "\n" +
                "  printer-state: " + id + "\n" +
                "  printer-state-reasons: " + reason + "\n" +
                "  printer-is-accepting-jobs: " + bAccept + "\n");

        PrinterInfo printer = new PrinterInfo(name, id, reason, bAccept);

        PrintJobMsg job = new PrintJobMsg(cmd, id, name, reason, printer);

        notifyPrinterJobStateChange(job);
    }

    private void printerStateChanged(Intent intent) {
        String name = intent.getStringExtra("printer-name");
        int id = intent.getIntExtra("printer-state", -1);
        String reason = intent.getStringExtra("printer-state-reasons");
        boolean bAccept = intent.getBooleanExtra("printer-is-accepting-jobs", false);

        Log.e(LOG_TAG, "  printer-name: " + name + "\n" +
                "  printer-state: " + id + "\n" +
                "  printer-state-reasons: " + reason + "\n" +
                "  printer-is-accepting-jobs: " + bAccept + "\n");

        PrinterInfo printer = new PrinterInfo(name, id, reason, bAccept);
        notifyPrinterStateChange(printer);
    }

    private void notifyPrinterJobStateChange(PrintJobMsg value) {
        if (pinterChangedListeners != null) {
            for (INormalEventListener listener : pinterChangedListeners) {
                listener.onValueEvent(0, value);
            }
        }
    }

    private void notifyPrinterStateChange(PrinterInfo value) {
        if (pinterChangedListeners != null) {
            for (INormalEventListener listener : pinterChangedListeners) {
                listener.onValueEvent(1, value);
            }
        }
    }

    //触发界面刷新监听
    public void registerPrinterChangedCallback(INormalEventListener listener) {
        if (listener == null) {
            return;
        }
        if (!pinterChangedListeners.contains(listener)) {
            pinterChangedListeners.add(listener);
        }
    }

    public void unRegisterPrinterChangedCallback(INormalEventListener listener) {
        if (listener == null) {
            return;
        }
        if (pinterChangedListeners.contains(listener)) {
            pinterChangedListeners.remove(listener);
        }
    }

    public void PrintDocument(final String filePath, final INormalEventListener callback) {
        if (callback == null)
            return;

        if (TextUtils.isEmpty(filePath)) {
            Log.i(LOG_TAG, "image path is null,can not print!! ");
            callback.onErrEvent(Constant.ERR_FILE_PATH_EMPTY);
            return;
        }

        StorageManager storageManager = (StorageManager) ServerHelper.getInstance().getModule(ServerHelper.MODULE_ID_STORAGE);
        final PrintUnit printUnit = storageManager.getPrintEdgeValue();

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                int left = printUnit.getLeft();
                int top = printUnit.getTop();
                int right = printUnit.getRight();
                int bottom = printUnit.getBottom();
                left = left * 720 / 254;       //此处的单位是point，需要由mm转化过来  1 point = 1/72 inch    1 inch = 25.4 mm  所以转换方式为left * 720 / 254
                top = top * 720 / 254;
                right = right * 720 / 254;
                bottom = bottom * 720 / 254;

                int mleft = left;
                int mtop = top;
                int mright = right;
                int mbottom = bottom;

                if (Constant.PRINT_SCREEM_TYPE == 0) {   //竖屏的话要顺时针翻转90度，对应的值也要变化
                    mleft = bottom;
                    mtop = left;
                    mright = top;
                    mbottom = right;
                }

                String quality = printUnit.getQuality();
                String size = printUnit.getSize();
                String path = filePath;

                File file = new File(path);
                if (file == null) {
                    callback.onErrEvent(Constant.ERR_FILE_NOT_EXIT);
                    return;
                }

                try {
                    FileInputStream fileInputStream = new FileInputStream(file);

                    //CupsPrinter printer = new CupsPrinter(new URL(getPrinterUrl()), PRINTER_NAME, true);
                    CupsPrinter printer = new CupsClient().getDefaultPrinter();
                    String userName = CupsClient.DEFAULT_USER;

                    // Build print job
                    org.cups4j.PrintJob printJob = new org.cups4j.PrintJob.Builder(fileInputStream)
                            .pageSize(size)
                            .pageMargin(mleft, mtop, mright, mbottom)
                            .printQuality(PrintJob.Builder.PrintQuality.Standard)
                            .orientation(PrintJob.Builder.Orientation.LANDSCAPE)
                            //.mediaType(PrintJobMsg.Builder.MediaType.Plain)
                            //.printingDirection(PrintJobMsg.Builder.PrintingDirection.Unidirectional)
                            .build();

                    PrintRequestResult printRequestResult = printer.print(printJob);
                    if (printRequestResult.isSuccessfulResult()) {
                        int jobID = printRequestResult.getJobId();
                        Log.i(LOG_TAG, "File " + file + " sent to " + printer.getPrinterURL() + " with job ID " + jobID);
                        callback.onValueEvent(0, jobID);
                    } else {
                        // you might throw an exception or try to retry printing the job
                        Log.i(LOG_TAG, "print error! status code: " + printRequestResult.getResultCode() +
                                " status description: " + printRequestResult.getResultDescription());
                        callback.onErrEvent(Constant.ERR_CMD_EXECUDT_FAILED);
                    }
                } catch (Exception e) {
                    Log.e(LOG_TAG, e.toString(), e);
                    callback.onErrEvent(Constant.ERR_CMD_EXECUDT_FAILED);
                }
            }
        });
    }

    public void queryJobStatus(final int jobId, final INormalEventListener callback) {
        if (callback == null)
            return;

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                PrintJobAttributes result;
                try {
                    CupsClient cupsClient = new CupsClient();
                    result = cupsClient.getJobAttributes(jobId);
                    callback.onValueEvent(0, result);
                } catch (Exception e) {
                    Log.e(LOG_TAG, e.toString(), e);
                    callback.onErrEvent(Constant.ERR_CMD_EXECUDT_FAILED);
                }
                callback.onErrEvent(Constant.ERR_CMD_EXECUDT_FAILED);
            }
        });
    }

    public void cancelPrintJob(final int jobId, final INormalEventListener callback) {
        if (callback == null)
            return;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Boolean result;
                try {
                    CupsClient cupsClient = new CupsClient();
                    result = cupsClient.cancelJob(jobId);
                    callback.onValueEvent(0, result);
                    return;
                } catch (Exception e) {
                    Log.e(LOG_TAG, e.toString(), e);
                    callback.onErrEvent(Constant.ERR_CMD_EXECUDT_FAILED);
                }
            }
        });
    }

    public void getPrinterAllJobs(final INormalEventListener callback) {
        if (callback == null)
            return;

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                List<PrintJobAttributes> result = new ArrayList<>();
                try {
                    Log.i(LOG_TAG, "=== List Jobs ===");
                    CupsClient cupsClient = new CupsClient();
                    // CupsPrinter printer = new CupsPrinter(new URL(getPrinterUrl()), PRINTER_NAME, true);
                    CupsPrinter printer = cupsClient.getDefaultPrinter();
                    String userName = CupsClient.DEFAULT_USER;
                    //  Use WhichJobsEnum.ALL if want to list all
                    List<PrintJobAttributes> jobs = cupsClient.getJobs(printer, WhichJobsEnum.NOT_COMPLETED, userName, false);

                    for (PrintJobAttributes att : jobs) {
                        PrintJobAttributes a = cupsClient.getJobAttributes(userName, att.getJobID());
                        Log.i(LOG_TAG, "Printer Job: #" + a.getJobID() + ", Name:" + a.getJobName() + ", State:" + a.getJobState() +
                                ", PrinterURL:" + a.getPrinterURL() + ", User:" + a.getUserName());
                        result.add(a);
                    }
                    callback.onValueEvent(0, result);
                } catch (Exception e) {
                    Log.e(LOG_TAG, e.toString(), e);
                    callback.onErrEvent(Constant.ERR_CMD_EXECUDT_FAILED);
                }
            }
        });
    }

    public void queryDefaultPrinterInfo(final INormalEventListener callback) {
        if (callback == null)
            return;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.i(LOG_TAG, "=== Get Printer Attributes ===");

                    CupsClient cupsClient = new CupsClient();
                    // CupsPrinter printer = new CupsPrinter(new URL(getPrinterUrl()), PRINTER_NAME, true);
                    CupsPrinter printer = cupsClient.getDefaultPrinter();
                    PrinterAttributes attr = cupsClient.getPrinterAttributes(printer);
                    Log.i(LOG_TAG, "Printer Status: " + attr.getPrinterName() + ", State:" + attr.getPrinterState() +
                            ", Reason:" + attr.getPrinterStateReasons() +
                            ", Message:" + attr.getPrinterStateMessage());
                    callback.onValueEvent(0, attr);
                } catch (Exception e) {
                    Log.e(LOG_TAG, e.toString(), e);
                    callback.onErrEvent(Constant.ERR_CMD_EXECUDT_FAILED);
                }
            }
        });
    }
}
