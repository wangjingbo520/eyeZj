package com.l.eyescure.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.apkfuns.logutils.LogUtils;
import com.l.eyescure.R;
import com.l.eyescure.activity.adapter.CureStepListAdapter;
import com.l.eyescure.server.ServerHelper;
import com.l.eyescure.server.SqlManage.localsql.DbCureDetailEntity;
import com.l.eyescure.server.SqlManage.localsql.DbCureStepEntity;
import com.l.eyescure.server.SqlManage.localsql.DbPatientEntity;
import com.l.eyescure.server.StorageManager.StorageManager;
import com.l.eyescure.server.callBack.INormalEventListener;
import com.l.eyescure.server.cureManage.CureInfoManage;
import com.l.eyescure.server.cureManage.CureStepInfo;
import com.l.eyescure.server.printManager.PrintManager;
import com.l.eyescure.util.Constant;
import com.l.eyescure.util.DateUtil;
import com.l.eyescure.util.FileUtils;
import com.l.eyescure.util.SaveCurrentImg;
import com.l.eyescure.util.ToastUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CureStepActivity extends Activity implements View.OnClickListener {
    private String cureID, patientID,SavedFileName,SavedPatientName;
    private TextView cure_step_notice;
    private Button cure_step_back_print, cure_step_print;
    private ListView cure_step_listView;
    private CureStepListAdapter cureStepListAdapter;
    private CureInfoManage cureInfoManage;
    private List<CureStepInfo> cureSteps = new ArrayList<>();
    private Context mContext;
    private ImageView backView;
    private TextView titleView;
    private LinearLayout backLayout;
    private PrintManager printManager;
    private LinearLayout screenLayout;
    private String filePath = null;   //明细文件路径
    private Handler mHandler;
    private TextView lookLeftTime, lookRightTime;
    TextView lookPrintNumber;
    TextView lookPrintName;
    TextView lookPrintCureName;
    TextView lookPrintCureDate;
    TextView lookPrintHowOld;
    TextView lookPrintSex;
    private Button cure_step_export_file,cure_step_save_file;
    private StorageManager mStorage;
    private ProgressDialog myDialog; // 保存进度框

    private static final int PDF_SAVE_START = 1;// 保存PDF文件的开始意图
    private static final int PDF_SAVE_RESULT = 2;// 保存PDF文件的结果开始意图
    private static final int PDF_NOT_SD_STORAGE = 3;// 保存PDF文件没有SD存储设备
    private static final int PDF_NOT_USB_STORAGE = 4;// 保存PDF文件没有USB存储设备

    private int prinftExe = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cure_step);
        mContext = this;

        mHandler = new Handler();
        cureInfoManage = (CureInfoManage) ServerHelper.getInstance().getModule(ServerHelper.MODULE_ID_CUREINFO);
        assert cureInfoManage != null;

        printManager = (PrintManager) ServerHelper.getInstance().getModule(ServerHelper.MODULE_ID_PRINT);
        assert (printManager != null);

        cureID = getIntent().getStringExtra("curid");
        patientID = getIntent().getStringExtra("patientID");
        SavedFileName = getIntent().getStringExtra("SavedFileName");
        SavedPatientName = getIntent().getStringExtra("SavedPatientName");

        prinftExe = getIntent().getIntExtra("prinftExe",0);

        lookLeftTime = (TextView) findViewById(R.id.look_left_time);
        lookRightTime = (TextView) findViewById(R.id.look_right_time);
        lookPrintNumber = (TextView) findViewById(R.id.look_print_number);
        lookPrintName = (TextView) findViewById(R.id.look_print_name);
        lookPrintCureName = (TextView) findViewById(R.id.look_print_cure_name);
        lookPrintCureDate = (TextView) findViewById(R.id.look_print_cure_date);
        lookPrintHowOld = (TextView) findViewById(R.id.look_print_how_old);
        lookPrintSex = (TextView) findViewById(R.id.look_print_sex);

        cure_step_back_print = (Button) findViewById(R.id.cure_step_back_print);
        cure_step_print = (Button) findViewById(R.id.cure_step_print);

        cure_step_export_file= (Button) findViewById(R.id.cure_step_export_file);
        cure_step_save_file= (Button) findViewById(R.id.cure_step_save_file);

        cure_step_listView = (ListView) findViewById(R.id.cure_step_listView);
        cure_step_notice = (TextView) findViewById(R.id.cure_step_notice);
        backLayout = (LinearLayout) findViewById(R.id.view_base_back_ll);
        backView = (ImageView) findViewById(R.id.view_base_back);
        titleView = (TextView) findViewById(R.id.view_base_title);
        screenLayout = (LinearLayout) findViewById(R.id.cure_step_screen_layout);

        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        cure_step_back_print.setOnClickListener(this);
        cure_step_print.setOnClickListener(this);
        cure_step_export_file.setOnClickListener(this);
        cure_step_save_file.setOnClickListener(this);
        titleView.setText("查看打印");

        initProgress();
    }

    /**
     * 初始化识别进度框
     */
    private void initProgress() {
        myDialog = new ProgressDialog(this, ProgressDialog.THEME_HOLO_LIGHT);
        myDialog.setIndeterminateDrawable(getResources().getDrawable(
                R.drawable.progress_ocr));
        myDialog.setMessage("正在保存PDF文件...");
        myDialog.setCanceledOnTouchOutside(false);
        myDialog.setCancelable(false);
    }

    private void initData() {
	if(getCureInfoManage()!=null){
        getCureInfoManage().getOneCureDetail(cureID, new INormalEventListener() {
            @Override
            public void onValueEvent(int key, Object value) {
                if (value == null) {
                    LogUtils.e("未获取到该id的病例数据");
                } else {
                    final DbCureDetailEntity dbCureDetailEntity = (DbCureDetailEntity) value;
                    if (TextUtils.isEmpty(dbCureDetailEntity.getCureId())) {
                        LogUtils.e("未获取到该id的病例数据");
                    } else {
                        filePath = dbCureDetailEntity.getFilePath();
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            lookLeftTime.setText(DateUtil.parseSencond(dbCureDetailEntity.getLeftTime()));
                            lookRightTime.setText(DateUtil.parseSencond(dbCureDetailEntity.getRightTime()));
                            lookPrintNumber.setText("编号：" + dbCureDetailEntity.getNumber());
                            lookPrintCureName.setText("医师：" + dbCureDetailEntity.getDoctorName());
                            lookPrintCureDate.setText("日期：" + dbCureDetailEntity.getCureDate());
                        }
                    });
                }
            }

            @Override
            public void onErrEvent(int errCode) {
                LogUtils.e("未获取到该id的病例数据");
            }
        });
        getCureInfoManage().getOnePatientDetail(patientID, new INormalEventListener() {
            @Override
            public void onValueEvent(int key, Object value) {
                if (value == null) {
                    LogUtils.e("未获取到病人数据");
                } else {
                    final DbPatientEntity dbPatientEntity = (DbPatientEntity) value;
                    if (TextUtils.isEmpty(dbPatientEntity.getName())) {
                        LogUtils.e("未获取到病人数据");
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            lookPrintName.setText("姓名：" + dbPatientEntity.getName());
                            lookPrintHowOld.setText("年龄：" + (DateUtil.getCurrentYear() - Integer.valueOf(dbPatientEntity.getBirthday().substring(0, 4))) + "");
                            lookPrintSex.setText("性别：" + dbPatientEntity.getSex());
                        }
                    });
                }
            }

            @Override
            public void onErrEvent(int errCode) {
                LogUtils.e("未获取到病人数据");
            }
        });
		}
    }


    @Override
    protected void onResume() {
        super.onResume();
        initData();

        initList(cureID);

        if(prinftExe == 1){
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    printf();
                }
            },1000);
        }
    }

    public CureInfoManage getCureInfoManage() {
        if (cureInfoManage != null)
            return cureInfoManage;
        else {
            cureInfoManage = (CureInfoManage) ServerHelper.getInstance().getModule(ServerHelper.MODULE_ID_CUREINFO);
            return cureInfoManage;
        }
    }

    public PrintManager getPrintManager() {
        if (printManager != null)
            return printManager;
        else {
            printManager = (PrintManager) ServerHelper.getInstance().getModule(ServerHelper.MODULE_ID_PRINT);
            return printManager;
        }
    }

    private void initList(String cureID) {
        getCureInfoManage().getCureStep(cureID, new INormalEventListener() {
            @Override
            public void onValueEvent(int key, final Object value) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (value == null) {
                            cure_step_notice.setText("没有获取到治疗明细");
                            return;
                        }
                        DbCureStepEntity mEntity = (DbCureStepEntity) value;
                        String msg = mEntity.getStep_str();
                        if (TextUtils.isEmpty(msg)) {
                            cure_step_notice.setText("没有治疗明细信息");
                            return;
                        }
                        String[] allMsg = msg.split(";");
                        if (allMsg.length == 0) {
                            cure_step_notice.setText("没有治疗明细信息");
                            return;
                        }

                        cureSteps.clear();
                        for (int i = 0; i < allMsg.length; i++) {
                            String oneMsg = allMsg[i];
                            if (oneMsg.length() != 9) {
                                continue;
                            }
                            parseStep(oneMsg);
                        }

                        if (cureStepListAdapter == null) {
                            cureStepListAdapter = new CureStepListAdapter(mContext, cureSteps);
                            cure_step_listView.setAdapter(cureStepListAdapter);
                        } else {
                            cureStepListAdapter.notifyDataSetChanged();
                        }

                    }
                });
            }

            @Override
            public void onErrEvent(int errCode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cure_step_notice.setText("没有获取到治疗明细");
                    }
                });
            }
        });
    }

    private void parseStep(String oneMsg) {
        CureStepInfo m_step = new CureStepInfo();

        String num_str = oneMsg.substring(0, 3);
        int num = Integer.valueOf(num_str);
        m_step.setNum(num);

        String LorR_str = oneMsg.substring(3, 4);
        m_step.setLorR(LorR_str);

        String Mode_str = oneMsg.substring(4, 5);
        m_step.setMode(Mode_str);

        String Time_str = oneMsg.substring(5, 9);
        m_step.setTime(Time_str);

        cureSteps.add(m_step);
    }

    public StorageManager getStorageManage() {
        if (mStorage != null)
            return mStorage;
        else {
            mStorage = (StorageManager) ServerHelper.getInstance().getModule(ServerHelper.MODULE_ID_STORAGE);
            return mStorage;
        }
    }

    private void printf(){
        int mPrinterType = getPrintManager().getPrinterType();
        if (mPrinterType == 0) {
            printPatientPdf();
        } else if (mPrinterType == 1) {

            filePath = SaveCurrentImg.getScreenHot(screenLayout, cureID);
            if (TextUtils.isEmpty(filePath))
                return;

            Uri uri = getImageContentUri(mContext, filePath);

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.putExtra(Intent.EXTRA_SUBJECT, "Share");
            intent.putExtra(Intent.EXTRA_TEXT, "eyescure");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(Intent.createChooser(intent, "请选择HP打印服务插件"));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cure_step_back_print: {
                finish();
                break;
            }
            case R.id.cure_step_print: {
                printf();
                break;
            }
            case R.id.cure_step_export_file:{
                savePatientPdf(1);
                break;
            }
            case R.id.cure_step_save_file:{
                if(getCureInfoManage().getInfoMode == 1){
                    handler.sendEmptyMessage(PDF_SAVE_START);
                    handler.sendEmptyMessageDelayed(PDF_SAVE_RESULT, 1500);
                }else {
                    savePatientPdf(0);
                }
                break;
            }
        }
    }

    private void savePatientPdf(final int saveType) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                filePath = SaveCurrentImg.getScreenHot(screenLayout, patientID + "_" + "step");
                if (TextUtils.isEmpty(filePath))
                    return;

                SaveToPdf(filePath, saveType);
            }
        }).start();
    }

    private void SaveToPdf(String bitmapPath, int sType) {
        ArrayList<String> imageUrllist = new ArrayList<String>();
        imageUrllist.add(bitmapPath);
        final String pdfUrl = getSavePath(sType);
        if (TextUtils.isEmpty(pdfUrl))
            return;
        handler.sendEmptyMessage(PDF_SAVE_START);
        try {
            FileUtils.getInstance(mContext).imgToPdf(bitmapPath, pdfUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        handler.sendEmptyMessageDelayed(PDF_SAVE_RESULT, 1000);
    }

    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case PDF_SAVE_START:
                    if (!myDialog.isShowing())
                        myDialog.show();
                    break;

                case PDF_SAVE_RESULT:
                    if (myDialog.isShowing())
                        myDialog.dismiss();
                    ToastUtil.s("保存成功");
                    break;
                case PDF_NOT_SD_STORAGE: {
                    showNoticeDialog(0, "未检测到存储卡，数据将无法存储！");
                    break;
                }

                case PDF_NOT_USB_STORAGE: {
                    showNoticeDialog(1, "没有检测到存储设备，无法导出，请您插入存储设备！");
                    break;
                }
            }
            return false;
        }
    });

    protected void showNoticeDialog(final int type, String showMsg) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(CureStepActivity.this, R.style.AlertDialogCustom);
        builder.setTitle("系统通知");
        builder.setMessage(showMsg);
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                // Intent i = new Intent(getActivity(), SetSavePdfPathActivity.class);
                // i.putExtra("savetype", type);
                // startActivity(i);
            }
        });
        builder.show();
    }

    private String getSavePath(int sType) {
        long time = System.currentTimeMillis();
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String dir = null;
        switch (sType) {
            case 0: {
                dir = getSDPath();
                break;
            }
            case 1: {
                dir = getUsbPath();
                break;
            }
        }

        if (TextUtils.isEmpty(dir)) {
            return null;
        }

        File saveFile = new File(dir, SavedFileName);
        switch (sType) {
            case 0: {
                saveFile = new File(dir, SavedFileName);
                break;
            }
            case 1: {
                saveFile = new File(dir, Constant.PdfCache);
                break;
            }
        }

        if (!saveFile.exists()) {
            if (!saveFile.mkdir()) {
                return null;
            }
        }
        final String pdf_address = saveFile.getPath() + File.separator + SavedPatientName + "_" + sdf.format(date) + "_2"+".pdf";
        return pdf_address;
    }

    private String getSDPath() {
//        String path = mStorage.getTfPath();
//        if (TextUtils.isEmpty(path)) {
//            //弹框提示，没有设置存储路径
//            showNoticeDialog(0, "您没有设置默认保存路径，点击设置将到设置界面设置！");
//        }
        String path = getStorageManage().getHandwareSd();
        if (TextUtils.isEmpty(path)) {
            //弹框提示，没有设置存储路径
            handler.sendEmptyMessage(PDF_NOT_SD_STORAGE);
        }
        return path;
    }

    private String getUsbPath() {
//        String path = mStorage.getSavePath();
//        if (TextUtils.isEmpty(path)) {
//            //弹框提示，没有设置存储路径
//            showNoticeDialog(1, "您没有设置默认导出路径，点击设置将到设置界面设置！");
//        }
        String path = getStorageManage().getDefaultUsbPath();
        if (TextUtils.isEmpty(path)) {
            //弹框提示，没有设置存储路径
            handler.sendEmptyMessage(PDF_NOT_USB_STORAGE);
        }
        return path;
    }

    public static Uri getImageContentUri(Context context, String filePath) {
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID}, MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATA, filePath);
            return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        }
    }

    private void printPatientPdf() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                filePath = SaveCurrentImg.getScreenHot(screenLayout, cureID);
                if (TextUtils.isEmpty(filePath))
                    return;

                getPrintManager().PrintDocument(filePath, new INormalEventListener() {
                    @Override
                    public void onValueEvent(int key, Object value) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.s("打印任务发送成功！");
                            }
                        });
                    }

                    @Override
                    public void onErrEvent(final int errCode) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                switch (errCode) {
                                    case 4000: {
                                        ToastUtil.s("打印失败，文件不存在！");
                                        break;
                                    }
                                    case 4001: {
                                        ToastUtil.s("打印失败，文件路径不存在！");
                                        break;
                                    }
                                    case 4002: {
                                        ToastUtil.s("打印失败，请重新打印!");
                                        break;
                                    }
                                }
                            }
                        });
                    }
                });
            }
        }).start();
    }
}
