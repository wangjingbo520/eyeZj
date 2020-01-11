package com.l.eyescure.activity.fragement;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.apkfuns.logutils.LogUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.l.eyescure.R;
import com.l.eyescure.activity.CureStepActivity;
import com.l.eyescure.activity.LookPrintActivity;
import com.l.eyescure.activity.chartviews.DataBean;
import com.l.eyescure.activity.chartviews.DataUtils;
import com.l.eyescure.server.ServerHelper;
import com.l.eyescure.server.SqlManage.localsql.DbCureDetailEntity;
import com.l.eyescure.server.SqlManage.localsql.DbPatientEntity;
import com.l.eyescure.server.StorageManager.StorageManager;
import com.l.eyescure.server.callBack.INormalEventListener;
import com.l.eyescure.server.cureManage.CureInfoManage;
import com.l.eyescure.server.printManager.PrintManager;
import com.l.eyescure.util.ChartService;
import com.l.eyescure.util.Constant;
import com.l.eyescure.util.DateUtil;
import com.l.eyescure.util.FileUtils;
import com.l.eyescure.util.SaveCurrentImg;
import com.l.eyescure.util.ToastUtil;

import org.achartengine.GraphicalView;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2017/8/22.
 */
public class PrintfViewFragemrnt extends Fragment {
    TextView lookLeftTime, printf_pageIndex;
    TextView lookRightTime;
    LinearLayout leftEyeStressControl;
    LinearLayout rightEyeStressControl;
    Button exportFile;
    Button saveFile;
    Button openFile;
    Button printFile;
    Button print_file_step;
    LinearLayout screenLayout;
    TextView lookPrintNumber;
    TextView lookPrintName;
    LinearLayout llLeftEyeStress;
    LinearLayout llLeftTemp;
    LinearLayout llRightEyeStress;
    LinearLayout llRightTemp;
    TextView lookPrintCureName;
    TextView lookPrintCureDate;
    TextView leftTempTv;
    TextView rightTempTv;
    TextView leftCounttimeTv;
    TextView rightCounttimeTv;
    LinearLayout llLeftStress;
    LinearLayout llRightStress;
    RelativeLayout warmingLefTopTipsRe;
    RelativeLayout warmingLeftMidRe;
    RelativeLayout warmingRightTopTipsRe;
    RelativeLayout warmingRightMidRe;
    TextView lookPrintHowOld;
    TextView lookPrintSex;

    private static final int PDF_SAVE_START = 1;// 保存PDF文件的开始意图
    private static final int PDF_SAVE_RESULT = 2;// 保存PDF文件的结果开始意图
    private static final int PDF_NOT_SD_STORAGE = 3;// 保存PDF文件没有SD存储设备
    private static final int PDF_NOT_USB_STORAGE = 4;// 保存PDF文件没有USB存储设备
    private ProgressDialog myDialog; // 保存进度框

    private int index = -1;//治疗编号
    private String patientID;//病人编号
    private String cureID; //病例数据ID
    private String filePath = null;//治疗文件路径
    private DbCureDetailEntity dbCureDetailEntity = null;
    private DbPatientEntity dbPatientEntity = null;
    private Context context;
    //    private NetPrinter netPrinter;
    private Bitmap bm = null;
    private Dialog mDialog = null;
    private Gson gson = null;
    private View mView;
    private int curNum = 0;
    private int TotalNum = 0;

    private GraphicalView mOneLeftView, mOneRightView;//左右第一行图表
    private ChartService mOneLeftService, mOneRightService;//左右第一行图表控制
    private GraphicalView mTwoLeftView, mTwoRightView;//左右第二行图表
    private ChartService mTwoLeftService, mTwoRightService;//左右第二行图表控制
    private GraphicalView mThreeLeftView, mThreeRightView;//左右第三行图表
    private ChartService mThreeLeftService, mThreeRightService;//左右第三行图表控制

    private ArrayList<Integer> xList = new ArrayList<>();
    private ArrayList<Double> yList = new ArrayList<>();//第一个标准图表数据
    private ArrayList<Integer> leftTwoDataX = new ArrayList<>();//左边第二行图表数据
    private ArrayList<Double> leftTwoDataY = new ArrayList<>();//左边第二行图表数据
    private ArrayList<Integer> rightTwoDataX = new ArrayList<>();//右边第二行图表数据
    private ArrayList<Double> rightTwoDataY = new ArrayList<>();//右边第二行图表数据
    private ArrayList<Integer> leftThreeDataX = new ArrayList<>();//左边第三行图表数据
    private ArrayList<Double> leftThreeDataY = new ArrayList<>();//左边第三行图表数据
    private ArrayList<Integer> rightThreeDataX = new ArrayList<>();//右边第二行图表数据
    private ArrayList<Double> rightThreeDataY = new ArrayList<>();//右边第二行图表数据

    private StorageManager mStorage;
    private PrintManager printManager;
    private Handler mHandler;
    private Context mContext;
    private CureInfoManage cureInfoManage;

    private boolean isViewCreated;

    public AlertDialog loadDialog = null;
    private int cure_model = 1;

    private int twoSize = 210;
    private int threeSize = 280;

    private LinearLayout llBottom1;
    private LinearLayout llBottom2;

    //第二行
    private LinearLayout llBottom3;
    private LinearLayout llBottom4;

    //第三行
    private LinearLayout llBottom5;
    private LinearLayout llBottom6;

    private LinearLayout llBottom_r1;
    private LinearLayout llBottom_r2;

    private LinearLayout llBottom_rb1;
    private LinearLayout llBottom_rb2;

    private LinearLayout llBottom_rc1;
    private LinearLayout llBottom_rc2;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mHandler = new Handler();
        mContext = getActivity();
        mView = inflater.inflate(R.layout.view_paintf, container, false);
        isViewCreated = true;
        Log.e("=======>", "onCreateView: fragment创建了" );
        return mView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isViewCreated) {
            loadData();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getUserVisibleHint()) {
            loadData();
        }
    }


    /*********************************************心电图展示UI************************************************************/
    private void loadData() {
        //找到view控件
        findViews();
        //对一些控件进行展示和隐藏，注册监听事件
        init();
        //画心电图
               initBiaozhunChart();
        //初始化第二行，第三行展示
//        initLineChart();
//        //获取数据
//        initData();
//
//        if (TotalNum == 0 || curNum > TotalNum) {
//            printf_pageIndex.setVisibility(View.INVISIBLE);
//        } else {
//            printf_pageIndex.setVisibility(View.VISIBLE);
//            String page = curNum + " / " + TotalNum;
//            printf_pageIndex.setText(page);
//        }
    }


    private void initBiaozhunChart() {
        mOneLeftService = new ChartService(getActivity());
        mOneRightService = new ChartService(getActivity());
        mOneLeftService.setXYMultipleSeriesDataset("");
        mOneRightService.setXYMultipleSeriesDataset("");
        // 标准，14分钟  长按摩模式治疗16分钟  长保压按摩模式16分钟
        if (cure_model == 1) {
            twoSize = 210;
            threeSize = 280;
            llBottom1.setVisibility(View.VISIBLE);
            llBottom2.setVisibility(View.GONE);

            //第二行，显示14分钟，隐藏16分钟
            llBottom3.setVisibility(View.VISIBLE);
            llBottom4.setVisibility(View.GONE);

            //第三行，显示14分钟，隐藏16分钟
            llBottom5.setVisibility(View.VISIBLE);
            llBottom6.setVisibility(View.GONE);


            llBottom_r1.setVisibility(View.VISIBLE);
            llBottom_r2.setVisibility(View.GONE);

            llBottom_rb1.setVisibility(View.VISIBLE);
            llBottom_rb2.setVisibility(View.GONE);

            llBottom_rc1.setVisibility(View.VISIBLE);
            llBottom_rc2.setVisibility(View.GONE);

//            mOneLeftService.setXYMultipleSeriesRenderer2(210, 100, "", "", "", Color.GREEN, Color.GREEN, Color.GREEN, Color.GREEN, 1);
//            mOneRightService.setXYMultipleSeriesRenderer2(210, 100, "", "", "", Color.GREEN, Color.GREEN, Color.GREEN, Color.GREEN, 1);
        } else if (cure_model == 2) {
            twoSize = 240;
            threeSize = 320;
            llBottom1.setVisibility(View.GONE);
            llBottom2.setVisibility(View.VISIBLE);

            //第二行，显示16分钟，隐藏14分钟
            llBottom3.setVisibility(View.GONE);
            llBottom4.setVisibility(View.VISIBLE);

            //第三行，显示16分钟，隐藏14分钟
            llBottom5.setVisibility(View.GONE);
            llBottom6.setVisibility(View.VISIBLE);

            llBottom_r1.setVisibility(View.GONE);
            llBottom_r2.setVisibility(View.VISIBLE);

            llBottom_rb1.setVisibility(View.GONE);
            llBottom_rb2.setVisibility(View.VISIBLE);

            llBottom_rc1.setVisibility(View.GONE);
            llBottom_rc2.setVisibility(View.VISIBLE);

//            mOneLeftService.setXYMultipleSeriesRenderer2(240, 100, "", "", "", Color.GREEN, Color.GREEN, Color.GREEN, Color.GREEN, 1);
//            mOneRightService.setXYMultipleSeriesRenderer2(240, 100, "", "", "", Color.GREEN, Color.GREEN, Color.GREEN, Color.GREEN, 1);
        } else if (cure_model == 3) {
            twoSize = 240;
            threeSize = 320;
            llBottom1.setVisibility(View.GONE);
            llBottom2.setVisibility(View.VISIBLE);

            //第二行，显示16分钟，隐藏14分钟
            llBottom3.setVisibility(View.GONE);
            llBottom4.setVisibility(View.VISIBLE);

            //第三行，显示16分钟，隐藏14分钟
            llBottom5.setVisibility(View.GONE);
            llBottom6.setVisibility(View.VISIBLE);


            llBottom_r1.setVisibility(View.GONE);
            llBottom_r2.setVisibility(View.VISIBLE);

            llBottom_rb1.setVisibility(View.GONE);
            llBottom_rb2.setVisibility(View.VISIBLE);

            llBottom_rc1.setVisibility(View.GONE);
            llBottom_rc2.setVisibility(View.VISIBLE);

//            mOneLeftService.setXYMultipleSeriesRenderer2(240, 100, "", "", "", Color.GREEN, Color.GREEN, Color.GREEN, Color.GREEN, 1);
//            mOneRightService.setXYMultipleSeriesRenderer2(240, 100, "", "", "", Color.GREEN, Color.GREEN, Color.GREEN, Color.GREEN, 1);
        }

        mOneLeftService.setXYMultipleSeriesRenderer2(twoSize + 1, 100, "", "", "", Color.GREEN, Color.GREEN, Color.GREEN, Color.GREEN, 1);
        mOneRightService.setXYMultipleSeriesRenderer2(twoSize + 1, 100, "", "", "", Color.GREEN, Color.GREEN, Color.GREEN, Color.GREEN, 1);

        mOneLeftView = mOneLeftService.getGraphicalView();
        llLeftStress.addView(mOneLeftView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        mOneRightView = mOneRightService.getGraphicalView();
        llRightStress.addView(mOneRightView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        mOneLeftService.setCustomX(true);
        mOneRightService.setCustomX(true);

        DataBean datas = DataUtils.getDatas(cure_model);
        mOneRightService.updateChart(datas.getxList(), datas.getyList());
        mOneLeftService.updateChart(datas.getxList(), datas.getyList());
    }


    //这里是传过来的数据源
    public void FrageMentInit(String patientID, String cureID, Context context, int TotalNum, int curNum, int cure_model) {
        this.context = context;
        gson = new Gson();
        this.patientID = patientID;
        this.cureID = cureID;
        this.TotalNum = TotalNum;
        this.curNum = curNum;
        this.cure_model = cure_model;
    }


    /********************************************************数据展示**************************************************************/
    private void initLineChart() {
        /*--------第二行左侧压力表--------------*/
        mTwoLeftService = new ChartService(getActivity());
        mTwoLeftService.setXYMultipleSeriesDataset("");
        mTwoLeftService.setXYMultipleSeriesRenderer(twoSize + 1, 7, "", "", "",
                Color.GREEN, Color.GREEN, Color.GREEN, Color.GREEN, 2);
        mTwoLeftView = mTwoLeftService.getGraphicalView();
        mTwoLeftService.setCustomX(false);
        //将左右图表添加到布局容器中
        llLeftEyeStress.addView(mTwoLeftView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        /*--------第二行右侧压力表--------------*/
        mTwoRightService = new ChartService(getActivity());
        mTwoRightService.setXYMultipleSeriesDataset("");
        mTwoRightService.setXYMultipleSeriesRenderer(twoSize + 1, 7, "", "", "",
                Color.GREEN, Color.GREEN, Color.GREEN, Color.GREEN, 2);
        mTwoRightView = mTwoRightService.getGraphicalView();
        mTwoRightService.setCustomX(false);
        //将左右图表添加到布局容器中
        llRightEyeStress.addView(mTwoRightView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        /*--------第三行左侧温度表--------------*/
        mThreeLeftService = new ChartService(getActivity());
        mThreeLeftService.setXYMultipleSeriesDataset("");
        mThreeLeftService.setXYMultipleSeriesRenderer(threeSize + 1, 50, "", "", "",
                Color.GREEN, Color.GREEN, Color.GREEN, Color.GREEN, 3);
        mThreeLeftView = mThreeLeftService.getGraphicalView();
        mThreeLeftService.setCustomX(false);
        //将左右图表添加到布局容器中
        llLeftTemp.addView(mThreeLeftView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        /*--------第三行右侧温度表--------------*/
        mThreeRightService = new ChartService(getActivity());
        mThreeRightService.setXYMultipleSeriesDataset("");
        mThreeRightService.setXYMultipleSeriesRenderer(threeSize + 1, 50, "", "", "",
                Color.GREEN, Color.GREEN, Color.GREEN, Color.GREEN, 3);
        mThreeRightView = mThreeRightService.getGraphicalView();
        mThreeRightService.setCustomX(false);
        //将左右图表添加到布局容器中
        llRightTemp.addView(mThreeRightView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

    }


    //通过病人编号去获取数据
    private void initData() {
        getCureInfoManage().getCurrCureDetailIndex(patientID, cureID, new INormalEventListener() {
            @Override
            public void onValueEvent(int key, Object value) {
                int result = (int) value;
                if (result > 0) {
                    index = result;
                } else {
                    LogUtils.e("未获取到页面索引");
                }
            }

            @Override
            public void onErrEvent(int errCode) {
                LogUtils.e("获取页面索引失败");
            }
        });

        //病例数据ID
        getCureInfoManage().getOneCureDetail(cureID, new INormalEventListener() {
            @Override
            public void onValueEvent(int key, Object value) {
                if (value == null) {
                    LogUtils.e("未获取到该id的病例数据");
                } else {
                    dbCureDetailEntity = (DbCureDetailEntity) value;
                    if (TextUtils.isEmpty(dbCureDetailEntity.getCureId())) {
                        LogUtils.e("未获取到该id的病例数据");
                    } else {
                        filePath = dbCureDetailEntity.getFilePath();
                    }

                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                lookLeftTime.setText(DateUtil.parseSencond(dbCureDetailEntity.getLeftTime()));
                                lookRightTime.setText(DateUtil.parseSencond(dbCureDetailEntity.getRightTime()));
                                lookPrintNumber.setText("编号：" + dbCureDetailEntity.getNumber());
                                lookPrintCureName.setText("医师：" + dbCureDetailEntity.getDoctorName());
                                lookPrintCureDate.setText("日期：" + dbCureDetailEntity.getCureDate());

                                Type type1 = new TypeToken<ArrayList<Integer>>() {
                                }.getType();
                                Type type2 = new TypeToken<ArrayList<Double>>() {
                                }.getType();

                                //第二行设置数据
                                leftTwoDataX = gson.fromJson(dbCureDetailEntity.getLeftPressDataX(), type1);
                                leftTwoDataY = gson.fromJson(dbCureDetailEntity.getLeftPressDataY(), type2);
                                rightTwoDataX = gson.fromJson(dbCureDetailEntity.getRightPressDataX(), type1);
                                rightTwoDataY = gson.fromJson(dbCureDetailEntity.getRightPressDataY(), type2);

                                //第三行设置数据
                                leftThreeDataX = gson.fromJson(dbCureDetailEntity.getLeftTempDataX(), type1);
                                leftThreeDataY = gson.fromJson(dbCureDetailEntity.getLeftTempDataY(), type2);
                                rightThreeDataX = gson.fromJson(dbCureDetailEntity.getRightTempDataX(), type1);
                                rightThreeDataY = gson.fromJson(dbCureDetailEntity.getRightTempDataY(), type2);
//                                LogUtils.i(dbCureDetailEntity.getLeftPressDataX());
//                                LogUtils.i(dbCureDetailEntity.getLeftPressDataY());
//                                LogUtils.i(dbCureDetailEntity.getRightPressDataX());
//                                LogUtils.i(dbCureDetailEntity.getRightPressDataY());
//
//                                LogUtils.i(dbCureDetailEntity.getLeftTempDataX());
//                                LogUtils.i(dbCureDetailEntity.getLeftTempDataY());
//                                LogUtils.i(dbCureDetailEntity.getRightTempDataX());
//                                LogUtils.i(dbCureDetailEntity.getRightTempDataY());


                                if (leftTwoDataX != null && leftTwoDataY != null && leftTwoDataX.size() > 0 && leftTwoDataY.size() > 0) {
                                    //设置数据源，更新UI
                                    mTwoLeftService.updateChart(leftTwoDataX, leftTwoDataY);
                                }
                                if (rightTwoDataX != null && rightTwoDataY != null && rightTwoDataX.size() > 0 && rightTwoDataY.size() > 0) {
                                    mTwoRightService.updateChart(rightTwoDataX, rightTwoDataY);
                                }


                                if (leftThreeDataX != null && leftThreeDataY != null && leftThreeDataX.size() > 0 && leftThreeDataY.size() > 0) {
                                    mThreeLeftService.updateChart(leftThreeDataX, leftThreeDataY);
                                }
                                if (rightThreeDataX != null && rightThreeDataY != null && rightThreeDataX.size() > 0 && rightThreeDataY.size() > 0) {
                                    mThreeRightService.updateChart(rightThreeDataX, rightThreeDataY);
                                }
                            }
                        });
                    }
                }
            }

            @Override
            public void onErrEvent(int errCode) {
                LogUtils.e("未获取到该id的病例数据");
            }
        });

        //病人编号
        getCureInfoManage().getOnePatientDetail(patientID, new INormalEventListener() {
            @Override
            public void onValueEvent(int key, Object value) {
                if (value == null) {
                    LogUtils.e("未获取到病人数据");
                } else {
                    dbPatientEntity = (DbPatientEntity) value;
                    if (TextUtils.isEmpty(dbPatientEntity.getName())) {
                        LogUtils.e("未获取到病人数据");
                    }
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                lookPrintName.setText("姓名：" + dbPatientEntity.getName());
                                lookPrintHowOld.setText("年龄：" + (DateUtil.getCurrentYear() - Integer.valueOf(dbPatientEntity.getBirthday().substring(0, 4))) + "");
                                lookPrintSex.setText("性别：" + dbPatientEntity.getSex());
                            }
                        });
                    }
                }
            }

            @Override
            public void onErrEvent(int errCode) {
                LogUtils.e("未获取到病人数据");
            }
        });
    }


    /************************************************************************************************************************************************************************************************************/

    private void init() {
        leftEyeStressControl.setVisibility(View.GONE);
        rightEyeStressControl.setVisibility(View.GONE);
        leftTempTv.setVisibility(View.GONE);
        rightTempTv.setVisibility(View.GONE);
        leftCounttimeTv.setVisibility(View.GONE);
        rightCounttimeTv.setVisibility(View.GONE);
        warmingLefTopTipsRe.setVisibility(View.GONE);
        warmingLeftMidRe.setVisibility(View.GONE);
        warmingRightTopTipsRe.setVisibility(View.GONE);
        warmingRightMidRe.setVisibility(View.GONE);

        exportFile.setOnClickListener(new NoDoubleClickListener());
        saveFile.setOnClickListener(new NoDoubleClickListener());
        openFile.setOnClickListener(new NoDoubleClickListener());
        printFile.setOnClickListener(new NoDoubleClickListener());
        print_file_step.setOnClickListener(new NoDoubleClickListener());

        initProgress();
    }

    private String getSavedFileName() {
        if (dbCureDetailEntity == null)
            return "unknow";
        String name = dbCureDetailEntity.getDoctorName();
        if (!TextUtils.isEmpty(name))
            return name;
        else {
            return "unknow";
        }
    }

    private String getSavedPatientName() {
        if (dbPatientEntity == null)
            return "unknow";
        String name = dbPatientEntity.getName();
        if (!TextUtils.isEmpty(name))
            return name;
        else {
            name = dbCureDetailEntity.getNumber();
            if (!TextUtils.isEmpty(name))
                return name;
        }
        return "unknow";
    }


    public class NoDoubleClickListener implements View.OnClickListener {

        public static final int MIN_CLICK_DELAY_TIME = 1000;
        private long lastClickTime = 0;

        @Override
        public void onClick(View v) {
            long currentTime = Calendar.getInstance().getTimeInMillis();
            if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
                lastClickTime = currentTime;
                switch (v.getId()) {
                    case R.id.export_file://导出文档
                        // SCreenUtils.saveImage(getActivity(),llLeftStress,"左边图");

                        savePatientPdf(1);
//                        filePath = SaveCurrentImg.getScreenHot(screenLayout, cureNum + "_" + id);
//                        if (!TextUtils.isEmpty(filePath)) {
//                            ToastUtil.s("保存文档成功");
//                            dbUserEntity.setFilePath(filePath);
//                            MyDbUtils.getInstance().save(dbUserEntity);
//                        } else {
//                            ToastUtil.s("保存文档失败");
//                        }
//                        if (!TextUtils.isEmpty(filePath)) {
//                            ToastUtil.s("当前文档位置" + filePath);
//                            new LFilePicker()
//                                    .withActivity(LookPrintActivity.this)
//                                    .withRequestCode(Constant.FILE_CHOOSER_REQUEST_CODE)
//                                    .withTitle("选择存储路径")//标题文字
//                                    .withTitleColor("#FFFFFF")//文字颜色
//                                    .withMutilyMode(false)//单选
//                                    .withFileFilter(new String[]{".xxxx"})
//                                    .start();
//                        } else {
//                            ToastUtil.s("当前文档暂未保存");
//                        }
                        break;
                    case R.id.save_file://保存文档

                        //   SCreenUtils.saveImage(getActivity(),llRightStress,"右边图");
                        if (getCureInfoManage().getInfoMode == 1) {
                            handler.sendEmptyMessage(PDF_SAVE_START);
                            handler.sendEmptyMessageDelayed(PDF_SAVE_RESULT, 1500);
                        } else {
                            savePatientPdf(0);
                        }
                        break;
                    case R.id.open_file://打开病例
                        if (!TextUtils.isEmpty(filePath)) {
                            bm = BitmapFactory.decodeFile(filePath);
                            mDialog = new Dialog(getActivity());
                            mDialog.setContentView(R.layout.dialog_showimg);
                            mDialog.setCanceledOnTouchOutside(true);
                            mDialog.getWindow().setGravity(Gravity.CENTER);
                            mDialog.show();
                            View view = mDialog.getWindow().getDecorView();
                            final ImageView lookImg = (ImageView) view.findViewById(R.id.look_img);
                            lookImg.setImageBitmap(bm);
                            mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    lookImg.setImageBitmap(null);
                                    bm.recycle();
                                    bm = null;
                                }
                            });
                        }
                        break;
                    case R.id.print_file://打印病例
                    {
                        int mPrinterType = getPrintManager().getPrinterType();
                        if (mPrinterType == 0) {
                            printPatientPdf();
                        } else if (mPrinterType == 1) {
                            //wifi 打印
                            filePath = SaveCurrentImg.getScreenHot(screenLayout, getSavedPatientName());
                            if (TextUtils.isEmpty(filePath))
                                return;
                            dbCureDetailEntity.setFilePath(filePath);
                            getCureInfoManage().save(dbCureDetailEntity, Constant.CUREINFO_UPDATE, null);

                            Uri uri = getImageContentUri(getActivity(), filePath);

                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.setType("image/*");
                            intent.putExtra(Intent.EXTRA_STREAM, uri);
                            intent.putExtra(Intent.EXTRA_SUBJECT, "Share");
                            intent.putExtra(Intent.EXTRA_TEXT, "eyescure");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(Intent.createChooser(intent, "请选择HP打印服务插件"));
                        }
                        break;
                    }
                    case R.id.print_file_step: {
                        Intent intent = new Intent(getActivity(), CureStepActivity.class);
                        intent.putExtra("curid", cureID);
                        intent.putExtra("patientID", patientID);
                        intent.putExtra("SavedFileName", getSavedFileName());
                        intent.putExtra("SavedPatientName", getSavedPatientName());
                        intent.putExtra("prinftExe", 0);
                        startActivity(intent);
                        break;
                    }
                }
            }
        }
    }

    /**
     * 初始化识别进度框
     */
    private void initProgress() {
        myDialog = new ProgressDialog(getActivity(), ProgressDialog.THEME_HOLO_LIGHT);
        myDialog.setIndeterminateDrawable(getResources().getDrawable(
                R.drawable.progress_ocr));
        myDialog.setMessage("正在保存PDF文件...");
        myDialog.setCanceledOnTouchOutside(false);
        myDialog.setCancelable(false);
    }

    private void savePatientPdf(final int saveType) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                filePath = SaveCurrentImg.getScreenHot(screenLayout, patientID + "_" + index);
                if (TextUtils.isEmpty(filePath))
                    return;
                dbCureDetailEntity.setFilePath(filePath);
                getCureInfoManage().save(dbCureDetailEntity, Constant.CUREINFO_UPDATE, null);

                SaveToPdf(filePath, saveType);
            }
        }).start();
    }

    private void printPatientPdf() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                filePath = SaveCurrentImg.getScreenHot(screenLayout, getSavedPatientName());
                if (TextUtils.isEmpty(filePath))
                    return;
                dbCureDetailEntity.setFilePath(filePath);
                getCureInfoManage().save(dbCureDetailEntity, Constant.CUREINFO_UPDATE, null);

                getPrintManager().PrintDocument(filePath, new INormalEventListener() {
                    @Override
                    public void onValueEvent(int key, Object value) {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil.s("打印任务发送成功！");
                                    showNoticeDialog(2, "你需要把治疗明细也一起打印吗？若需要，请点击“确定”；否则请点击“取消”！");
                                }
                            });
                        }
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
                    Toast.makeText(getActivity(), "保存成功", Toast.LENGTH_SHORT)
                            .show();
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

    @Override
    public void onResume() {
        super.onResume();
        ((LookPrintActivity) mContext).loadPageOK();
    }

    protected void showNoticeDialog(final int type, String showMsg) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogCustom);
        builder.setTitle("系统通知");
        builder.setMessage(showMsg);
        builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                switch (type) {
                    case 2: {
                        Intent intent = new Intent(getActivity(), CureStepActivity.class);
                        intent.putExtra("curid", cureID);
                        intent.putExtra("patientID", patientID);
                        intent.putExtra("SavedFileName", getSavedFileName());
                        intent.putExtra("SavedPatientName", getSavedPatientName());
                        intent.putExtra("prinftExe", 1);
                        startActivity(intent);
                        break;
                    }
                }
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

        File saveFile = new File(dir, getSavedFileName());
        switch (sType) {
            case 0: {
                saveFile = new File(dir, getSavedFileName());
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
        final String pdf_address = saveFile.getPath() + File.separator + getSavedPatientName() + "_" + sdf.format(date) + ".pdf";
        return pdf_address;
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


    public PrintfViewFragemrnt() {
        mStorage = (StorageManager) ServerHelper.getInstance().getModule(ServerHelper.MODULE_ID_STORAGE);
        assert mStorage != null;

        printManager = (PrintManager) ServerHelper.getInstance().getModule(ServerHelper.MODULE_ID_PRINT);
        assert (printManager != null);

        cureInfoManage = (CureInfoManage) ServerHelper.getInstance().getModule(ServerHelper.MODULE_ID_CUREINFO);
        assert (cureInfoManage != null);
    }

    public StorageManager getStorageManage() {
        if (mStorage != null)
            return mStorage;
        else {
            mStorage = (StorageManager) ServerHelper.getInstance().getModule(ServerHelper.MODULE_ID_STORAGE);
            return mStorage;
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


    private void findViews() {
        lookLeftTime = (TextView) mView.findViewById(R.id.look_left_time);
        lookRightTime = (TextView) mView.findViewById(R.id.look_right_time);
        leftEyeStressControl = (LinearLayout) mView.findViewById(R.id.left_eye_stress_control);
        rightEyeStressControl = (LinearLayout) mView.findViewById(R.id.right_eye_stress_control);
        exportFile = (Button) mView.findViewById(R.id.export_file);
        saveFile = (Button) mView.findViewById(R.id.save_file);
        openFile = (Button) mView.findViewById(R.id.open_file);
        printFile = (Button) mView.findViewById(R.id.print_file);
        print_file_step = (Button) mView.findViewById(R.id.print_file_step);
        screenLayout = (LinearLayout) mView.findViewById(R.id.screen_layout);
        lookPrintNumber = (TextView) mView.findViewById(R.id.look_print_number);
        lookPrintName = (TextView) mView.findViewById(R.id.look_print_name);
        llLeftEyeStress = (LinearLayout) mView.findViewById(R.id.ll_left_eye_stress);
        llLeftTemp = (LinearLayout) mView.findViewById(R.id.ll_left_temp);
        llRightEyeStress = (LinearLayout) mView.findViewById(R.id.ll_right_eye_stress);
        llRightTemp = (LinearLayout) mView.findViewById(R.id.ll_right_temp);
        lookPrintCureName = (TextView) mView.findViewById(R.id.look_print_cure_name);
        lookPrintCureDate = (TextView) mView.findViewById(R.id.look_print_cure_date);
        leftTempTv = (TextView) mView.findViewById(R.id.left_temp_tv);
        rightTempTv = (TextView) mView.findViewById(R.id.right_temp_tv);
        leftCounttimeTv = (TextView) mView.findViewById(R.id.left_counttime_tv);
        rightCounttimeTv = (TextView) mView.findViewById(R.id.right_counttime_tv);
        llLeftStress = (LinearLayout) mView.findViewById(R.id.ll_left_stress);
        llRightStress = (LinearLayout) mView.findViewById(R.id.ll_right_stress);
        warmingLefTopTipsRe = (RelativeLayout) mView.findViewById(R.id.warming_lef_top_tips_re);
        warmingLeftMidRe = (RelativeLayout) mView.findViewById(R.id.warming_left_mid_re);
        warmingRightTopTipsRe = (RelativeLayout) mView.findViewById(R.id.warming_right_top_tips_re);
        warmingRightMidRe = (RelativeLayout) mView.findViewById(R.id.warming_right_mid_re);
        lookPrintHowOld = (TextView) mView.findViewById(R.id.look_print_how_old);
        lookPrintSex = (TextView) mView.findViewById(R.id.look_print_sex);
        printf_pageIndex = (TextView) mView.findViewById(R.id.printf_pageIndex);


        llBottom1 = mView.findViewById(R.id.llBottom1);
        llBottom2 = mView.findViewById(R.id.llBottom2);

        llBottom3 = mView.findViewById(R.id.llBottom3);
        llBottom4 = mView.findViewById(R.id.llBottom4);
        llBottom5 = mView.findViewById(R.id.llBottom5);
        llBottom6 = mView.findViewById(R.id.llBottom6);

        llBottom_r1 = mView.findViewById(R.id.llBottom_r1);
        llBottom_r2 = mView.findViewById(R.id.llBottom_r2);
        llBottom_rb1 = mView.findViewById(R.id.llBottom_rb1);
        llBottom_rb2 = mView.findViewById(R.id.llBottom_rb2);
        llBottom_rc1 = mView.findViewById(R.id.llBottom_rc1);
        llBottom_rc2 = mView.findViewById(R.id.llBottom_rc2);

        if (cure_model == 1) {
            twoSize = 210;
            threeSize = 280;
        } else if (cure_model == 2) {

            twoSize = 240;
            threeSize = 320;
        } else if (cure_model == 3) {

            twoSize = 240;
            threeSize = 320;
        }

    }

}
