package com.l.eyescure.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.l.eyescure.R;
import com.l.eyescure.server.callBack.IRecvMsgByServiceCallback;
import com.l.eyescure.util.AnalyzeDataUtils;
import com.l.eyescure.util.ChartService;
import com.l.eyescure.util.SendDataArray;
import com.l.eyescure.util.SerialPortUtil;
import com.l.eyescure.util.ToastUtil;
import com.l.eyescure.view.TopBarView;
import com.orhanobut.logger.Logger;

import org.achartengine.GraphicalView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Look on 2017/1/6.
 * 自检
 */

public class SelfInspectionActivity extends BaseActivity {
    @BindView(R.id.ll_left_stress)
    LinearLayout llLeftStress;
    @BindView(R.id.ll_right_stress)
    LinearLayout llRightStress;
    private Context context;
    private static String TAG = SelfInspectionActivity.class.getSimpleName();
    @BindView(R.id.left_tips_tv)
    TextView leftTipsTv;
    @BindView(R.id.left_reason_tv)
    TextView leftReasonTv;
    @BindView(R.id.right_tips_tv)
    TextView rightTipsTv;
    @BindView(R.id.right_reason_tv)
    TextView rightReasonTv;
    @BindView(R.id.self_view_top)
    TopBarView viewTop;
    @BindView(R.id.btn_recheck)
    Button btn_recheck;

    private boolean isReady = false;//自检是否完成
    private AnalyzeDataUtils dataUtils = null;
    private Thread mThread;
    private int x = 0;
    private String patientId;
    private GraphicalView mLeftView, mRightView;//左右图表
    private ChartService mLeftService, mRightService;
    private EN_WORK_STATE mEn_Work_Status = EN_WORK_STATE.EN_WORK_STATE_INVALID;
    private static final int WAIT_MCU_REPALY_TIEM = 6000;
    private int checkSelfAckCnt = 0;
    private int checkEyesHandwareAckCnt = 0;
    private int iCheckStep1Ok = -1;  //0:双眼都通过 1：只有左眼通过 2：只有右眼通过
    private boolean bCheckOk = false;

    //治疗模式
    private int cure_model = -1;

    private enum EN_WORK_STATE {
        EN_WORK_STATE_INVALID,                 //空闲
        EN_WORK_STATE_CHECK_CURE_TOOL,         //检查治疗头
        EN_WORK_STATE_CHECK_EYES_HOT,          //检查热敷眼罩
    }

    @Override
    protected int bindLayout() {
        return R.layout.activity_selfinspection;
    }

    @Override
    protected boolean isNeedTitle() {
        return false;
    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);

        Logger.init(TAG);
        context = this;
        dataUtils = new AnalyzeDataUtils();
        //   viewTop.disableBtn(-1);
        initLineChart();

//        mThread = new Thread(runnable);
//        mThread.start();
        Thread lineThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isReady) {
                    mHandler.sendEmptyMessage(77);
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        lineThread.start();

        patientId = getIntent().getStringExtra("patientid");
        //治疗模式
        cure_model = getIntent().getIntExtra("cure_model", -1);

        sendCheckCureToolCommand();

        setTopBarView(viewTop);

        btn_recheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEn_Work_Status = EN_WORK_STATE.EN_WORK_STATE_INVALID;
                setStatus(9);
                mLeftService.clearChart();
                mRightService.clearChart();
                x = 0;
                sendCheckCureToolCommand();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getMessageManage() != null) {
            getMessageManage().registerMsgRecvByServiceCallback(recvMsg);
        }
    }

    @Override
    protected int topHelp() {
        showHelpDialog();
        return 0;
    }

    protected void showHelpDialog() {
        String showStr = "治疗前会先进行自检，检测设备是否就绪和治疗头是否就绪。\n" +
                "如果自检未通过，请按照屏幕中的提示检查硬件！";
        showHelpDialog(showStr);
    }

    @Override
    protected boolean topExit() {
        finish();
        return false;
    }

    @Override
    protected void topSet() {
        ToastUtil.s("正在治疗的时候，不允许进行系统设置！");
    }

    @Override
    protected void topCare() {

    }

    @Override
    protected void topFile() {
        ToastUtil.s("正在治疗的时候，不允许进行病例资料编辑!");
    }

    @Override
    protected void topPrintf() {
        ToastUtil.s("正在治疗的时候，不允许进行查看打印！");
    }

    @Override
    protected void topUser() {
        ToastUtil.s("正在治疗的时候，不允许进行系统设置！");
    }

    private void initLineChart() {
        mLeftService = new ChartService(this);
        mLeftService.setXYMultipleSeriesDataset("");
        mLeftService.setXYMultipleSeriesRenderer(20, 7, "", "", "",
                Color.GREEN, Color.GREEN, Color.GREEN, Color.BLACK, 2);
        mLeftView = mLeftService.getGraphicalView();
        mLeftService.setCustomX(false);
        //将左右图表添加到布局容器中
        llLeftStress.addView(mLeftView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        mRightService = new ChartService(this);
        mRightService.setXYMultipleSeriesDataset("");
        mRightService.setXYMultipleSeriesRenderer(20, 7, "", "", "",
                Color.GREEN, Color.GREEN, Color.GREEN, Color.BLACK, 2);
        mRightView = mRightService.getGraphicalView();
        mRightService.setCustomX(false);
        //将左右图表添加到布局容器中
        llRightStress.addView(mRightView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
    }

    private IRecvMsgByServiceCallback recvMsg = new IRecvMsgByServiceCallback() {
        @Override
        public void onReceive(int msgKey, byte[] data) {
            if (data == null || data.length == 0)
                return;
            Message msg = new Message();
            msg.what = msgKey;
            msg.obj = data;
            mHandler.sendMessage(msg);
        }

        @Override
        public void onReceiveErr(String msgKey, String info, int err_ID) {

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!bCheckOk)
            getMessageManage().send(SendDataArray.CloseLight());
        getMessageManage().unregisterMsgRecvByServiceCallback(recvMsg);
    }

    private void sendCheckCureToolCommand() {
        if (mEn_Work_Status != EN_WORK_STATE.EN_WORK_STATE_INVALID)
            return;
        mEn_Work_Status = EN_WORK_STATE.EN_WORK_STATE_CHECK_CURE_TOOL;

        mHandler.sendEmptyMessage(100);
    }

    private void sendCheckEyesHotCommand() {
        if (mEn_Work_Status != EN_WORK_STATE.EN_WORK_STATE_CHECK_CURE_TOOL)
            return;
        mEn_Work_Status = EN_WORK_STATE.EN_WORK_STATE_CHECK_EYES_HOT;

        mHandler.sendEmptyMessage(101);
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            byte[] content = (byte[]) msg.obj;
            switch (msg.what) {
                case 2://自检返回
                    if (content.length < 4)
                        break;
                    Log.i(TAG, "元数据" + SerialPortUtil.byte2hex(content));
                    Log.i(TAG, "处理数据" + ((int) content[1] + (int) content[2] + (int) content[3] + (int) content[0]));
                    if (((int) content[1] + (int) content[2] + (int) content[3] + (int) content[0]) == 4) {
                        setStatus(0);
                    } else {
                        setStatus1((content[0] & 0xFF), (content[1] & 0xFF), (content[2] & 0xFF), (content[3] & 0xFF));
                    }
                    checkSelfAckCnt = 0;
                    mHandler.removeMessages(100);
                    mHandler.removeMessages(201);
                    break;
                case 4://气压阀工作
                    break;
                case 6://停止气压阀工作
                    break;
                case 8://加热片加热
                    break;
                case 10://停止加热
                    break;
                case 12://查询气压
                    break;
                case 14://查询温度
                    break;
                case 16://治疗完成并自毁
                    break;
                case 20:
                    if (content.length < 1)
                        break;
                    if ((content[0] & 0xFF) == 0x01 || (content[0] & 0xFF) == 0x02) {
                        setStatus(6);
                    } else {
                        setStatus(5);
                    }

                    checkEyesHandwareAckCnt = 0;
                    mHandler.removeMessages(101);
                    break;
                case 66://自检通过开始治疗
//                    SerialPortStop();
                    bCheckOk = true;
                    Intent intent = new Intent(context, CureActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("patientid", patientId);
                    bundle.putInt("checkType", iCheckStep1Ok);
                    bundle.putInt("cure_model", cure_model);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                    break;
                case 77:
                    // 生成随机测试数
                    int y = (int) ((Math.random() * 4 + 3));
                    mLeftService.updateChart(x, y);
                    mRightService.updateChart(x, y);
                    x += 1;
                    break;
                case 99://错误
                    ToastUtil.s("数据错误，请重新开启治疗");
//                    SerialPortStop();
                    finish();
                    break;
                case 100: {
                    if (mEn_Work_Status != EN_WORK_STATE.EN_WORK_STATE_CHECK_CURE_TOOL)
                        return;
                    // 重新发送自检操作，并在此提示用户
                    if (checkSelfAckCnt > 3) {
                        setStatus(7);

                        checkSelfAckCnt = 0;
                        mHandler.removeMessages(100);
                        mHandler.removeMessages(201);
                        return;
                    }
                    checkSelfAckCnt++;
                    getMessageManage().send(SendDataArray.selfCheck());
                    mHandler.removeMessages(100);
                    mHandler.removeMessages(201);
                    mHandler.sendEmptyMessageDelayed(100, WAIT_MCU_REPALY_TIEM);
                    mHandler.sendEmptyMessageDelayed(201, 2000);
                    break;
                }
                case 201: {
                    getMessageManage().resetPort();
                    break;
                }
                case 101: {
                    if (mEn_Work_Status != EN_WORK_STATE.EN_WORK_STATE_CHECK_EYES_HOT)
                        return;

                    // 重新发送自检操作，并在此提示用户
                    if (checkEyesHandwareAckCnt > 3) {
                        setStatus(5);
                        checkEyesHandwareAckCnt = 0;
                        mHandler.removeMessages(101);
                        return;
                    }
                    checkEyesHandwareAckCnt++;
                    getMessageManage().send(SendDataArray.checkGoggles());
                    mHandler.removeMessages(101);
                    mHandler.sendEmptyMessageDelayed(101, WAIT_MCU_REPALY_TIEM);
                    break;
                }
                default:
                    Log.i(TAG, "===" + msg.what);
                    Log.i(TAG, "===" + msg.obj);
                    break;
            }
        }
    };

    private void setStatus1(int leftTemp, int leftPress, int rightTemp, int rightPress) {
        boolean bLeftReady = false;
        boolean bRightReady = false;

        if (leftTemp == 1) {
            if (leftPress == 1) {
                leftTipsTv.setText("请连接热敷眼罩");
                rightReasonTv.setText("设备自检完成");

                bLeftReady = true;
            } else {
                leftTipsTv.setText("左眼压力未检测到");
                leftReasonTv.setText("自检失败，请重新连接治疗头");
            }
        } else {
            leftTipsTv.setText("左眼温度未检测到");
            leftReasonTv.setText("自检失败，请重新连接治疗头");
        }

        if (rightTemp == 1) {
            if (rightPress == 1) {
                rightTipsTv.setText("请连接热敷眼罩");
                rightReasonTv.setText("设备自检完成");
                bRightReady = true;
            } else {
                rightTipsTv.setText("右眼压力未检测到");
                rightReasonTv.setText("自检失败，请重新连接治疗头");
            }
        } else {
            rightTipsTv.setText("右眼温度未检测到");
            rightReasonTv.setText("自检失败，请重新连接治疗头");
        }

        if ((bLeftReady && bRightReady) || (!bLeftReady && !bRightReady)) {
            return;
        }

        String show_title = "单眼检测通过";
        String show_notice = "";
        if (bLeftReady && !bRightReady) {
            show_notice = "左眼检测通过，但右眼未能检测通过。点击“确定”，您将只开始左眼治疗。点击“取消”将停止当前自检，您可以排除治疗头故障后再次自检！";
            showContinueDialog(show_title, show_notice, 2);
        } else if (!bLeftReady && bRightReady) {
            show_notice = "右眼检测通过，但左眼未能检测通过。点击“确定”，您将只开始右眼治疗。点击“取消”将停止当前自检，您可以排除治疗头故障后再次自检！";
            showContinueDialog(show_title, show_notice, 3);
        }
    }

    /**
     * 设置状态  0自检通过
     *
     * @param type
     */
    private void setStatus(int type) {
        switch (type) {
            case 0://自检通过
                Log.i(TAG, "自检通过");
                isReady = false;
                leftTipsTv.setText("请连接热敷眼罩");
                rightTipsTv.setText("请连接热敷眼罩");
                leftReasonTv.setText("设备自检完成");
                rightReasonTv.setText("设备自检完成");

                iCheckStep1Ok = 0;
                sendCheckEyesHotCommand();
                break;
            case 1://左眼温度未检测到
                isReady = false;
                leftTipsTv.setText("左眼温度未检测到");
                rightTipsTv.setText("检测到右眼温度");
                leftReasonTv.setText("自检失败，请重新连接治疗头");
                rightReasonTv.setText("设备自检完成");
                break;
            case 2://左眼压力未检测到
                isReady = false;
                leftTipsTv.setText("左眼压力未检测到");
                rightTipsTv.setText("检测到右眼压力");
                leftReasonTv.setText("自检失败，请重新连接治疗头");
                rightReasonTv.setText("设备自检完成");
                break;
            case 3://右眼温度未检测到
                isReady = false;
                leftTipsTv.setText("检测到左眼温度");
                rightTipsTv.setText("右眼温度未检测到");
                leftReasonTv.setText("设备自检完成");
                rightReasonTv.setText("自检失败，请重新连接治疗头");
                break;
            case 4://右眼压力未检测到
                isReady = false;
                leftTipsTv.setText("检测到左眼压力");
                rightTipsTv.setText("右眼压力未检测到");
                leftReasonTv.setText("设备自检完成");
                rightReasonTv.setText("自检失败，请重新连接治疗头");
                break;
            case 5:
                isReady = false;
                leftTipsTv.setText("未检测到热敷眼罩");
                rightTipsTv.setText("未检测到热敷眼罩");
                leftReasonTv.setText("请重新连接热敷眼罩");
                rightReasonTv.setText("请重新连接热敷眼罩");

                showContinueDialog("热敷眼罩自检未通过", "未检测到热敷眼罩。按“确定”可继续治疗。按“取消”后，请连接热敷眼罩，并点击“重新检测”自检！", 1);
                break;
            case 6://右眼压力未检测到
                isReady = true;
                leftTipsTv.setText("检测到热敷眼罩");
                rightTipsTv.setText("检测到热敷眼罩");
                leftReasonTv.setText("检测完成，开始治疗");
                rightReasonTv.setText("检测完成，开始治疗");

                if (iCheckStep1Ok >= 0) {
                    mHandler.sendEmptyMessage(66);
                }
                break;
            case 7:
                isReady = false;
                leftTipsTv.setText("左眼温度未检测到");
                rightTipsTv.setText("右眼温度未检测到");
                leftReasonTv.setText("自检失败，请重新连接治疗头");
                rightReasonTv.setText("自检失败，请重新连接治疗头");
                break;
            case 8:
                isReady = false;
                leftTipsTv.setText("请连接热敷眼罩");
                rightTipsTv.setText("请连接热敷眼罩");
                leftReasonTv.setText("未检测到热敷眼罩");
                rightReasonTv.setText("未检测到热敷眼罩");
                break;
            case 9:
                isReady = false;
                leftTipsTv.setText("请稍后");
                rightTipsTv.setText("请稍后");
                leftReasonTv.setText("设备正在执行自检");
                rightReasonTv.setText("设备正在执行自检");
                break;
        }
    }

    protected void showContinueDialog(String title, String msg, final int type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogCustom);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                switch (type) {
                    case 1: {
                        bCheckOk = true;
                        Intent intent = new Intent(context, CureActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("patientid", patientId);
                        bundle.putInt("checkType", iCheckStep1Ok);
                        bundle.putInt("cure_model", cure_model);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                        break;
                    }
                    case 2: {
                        iCheckStep1Ok = 1;
                        sendCheckEyesHotCommand();
                        break;
                    }
                    case 3: {
                        iCheckStep1Ok = 2;
                        sendCheckEyesHotCommand();
                        break;
                    }
                }
            }
        });
        builder.show();
    }

}
