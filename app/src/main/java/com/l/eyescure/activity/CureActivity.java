package com.l.eyescure.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apkfuns.logutils.LogUtils;
import com.google.gson.Gson;
import com.l.eyescure.R;
import com.l.eyescure.activity.chartviews.DataBean;
import com.l.eyescure.activity.chartviews.DataUtils;
import com.l.eyescure.application.MyApplication;
import com.l.eyescure.server.SqlManage.localsql.DbCureDetailEntity;
import com.l.eyescure.server.SqlManage.localsql.DbCureStepEntity;
import com.l.eyescure.server.SqlManage.localsql.DbPatientEntity;
import com.l.eyescure.server.callBack.INormalEventListener;
import com.l.eyescure.server.callBack.IRecvMsgByServiceCallback;
import com.l.eyescure.util.ChartService;
import com.l.eyescure.util.Constant;
import com.l.eyescure.util.CustomCountDownTimer;
import com.l.eyescure.util.DateUtil;
import com.l.eyescure.util.SPUtils;
import com.l.eyescure.util.SendDataArray;
import com.l.eyescure.util.SerialPortUtil;
import com.l.eyescure.util.ToastUtil;
import com.l.eyescure.view.BottomBtnView;
import com.l.eyescure.view.CTextView;
import com.l.eyescure.view.TopBarView;

import org.achartengine.GraphicalView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Look on 2017/1/6.
 * 治疗界面
 */
public class CureActivity extends BaseActivity {
    @BindView(R.id.ll_left_stress)
    LinearLayout llLeftStress;//左侧第一行标准图表
    @BindView(R.id.ll_right_stress)
    LinearLayout llRightStress;//右侧第一行标准图表
    @BindView(R.id.ll_left_eye_stress)
    LinearLayout llLeftEyeStress;//左侧第二行压力图
    @BindView(R.id.ll_right_eye_stress)
    LinearLayout llRightEyeStress;//右侧第二行压力图
    @BindView(R.id.ll_left_temp)
    LinearLayout llLeftTemp;//左侧温度表
    @BindView(R.id.ll_right_temp)
    LinearLayout llRightTemp;//右侧温度表

    @BindView(R.id.left_eye_stress_minus)
    TextView leftEyeStressMinus;//左侧压力减
    @BindView(R.id.left_eye_stress_tv)
    TextView leftEyeStressTv;//左侧压力数值
    @BindView(R.id.left_eye_stress_plus)
    TextView leftEyeStressPlus;//左侧压力加
    @BindView(R.id.left_eye_stress_control)
    LinearLayout leftEyeStressControl;//左侧压力控制块

    @BindView(R.id.right_eye_stress_minus)
    TextView rightEyeStressMinus;//右侧压力减
    @BindView(R.id.right_eye_stress_tv)
    TextView rightEyeStressTv;//右侧压力数值
    @BindView(R.id.right_eye_stress_plus)
    TextView rightEyeStressPlus;//右侧压力加
    @BindView(R.id.right_eye_stress_control)
    LinearLayout rightEyeStressControl;//右侧压力控制块

    @BindView(R.id.left_counttime_tv)
    TextView leftCounttimeTv;
    @BindView(R.id.right_counttime_tv)
    TextView rightCounttimeTv;
    @BindView(R.id.warming_left_tips_tv)
    CTextView warmingLeftTipsTv;
    @BindView(R.id.warming_left_reason_tv)
    TextView warmingLeftReasonTv;
    @BindView(R.id.warming_right_tips_tv)
    CTextView warmingRightTipsTv;
    @BindView(R.id.warming_right_reason_tv)
    TextView warmingRightReasonTv;
    @BindView(R.id.warming_right_top_tips_re)
    RelativeLayout warmingRightTopTipsRe;
    @BindView(R.id.left_temp_tv)
    TextView leftTempTv;
    @BindView(R.id.right_temp_tv)
    TextView rightTempTv;

    private Context context;
    private static String TAG = CureActivity.class.getSimpleName();
    @BindView(R.id.view_top)
    TopBarView viewTop;
    @BindView(R.id.bottom)
    BottomBtnView bottomBtnView;

    @BindView(R.id.ready)
    LinearLayout readyLin;
    @BindView(R.id.warming)
    LinearLayout warmingLin;
    @BindView(R.id.bottom_lump)
    LinearLayout bottomLumpLin;
    @BindView(R.id.left_cure_tips_tv)
    CTextView leftCureTipsTv;
    @BindView(R.id.cure_left_connect)
    TextView cureLeftConnect;
    @BindView(R.id.right_cure_tips_tv)
    CTextView rightCureTipsTv;
    @BindView(R.id.cure_right_connect)
    TextView cureRightConnect;

    private enum EN_CURE_WORK_STATE {
        EN_CURE_WORK_STATE_INVALID,                 //空闲
        EN_CURE_WORK_STATE_STARTED,                 //开始
        EN_CURE_WORK_STATE_WORK,                    //工作
        EN_CURE_WORK_STATE_PAUSE,                   //暂停
        EN_CURE_WORK_STATE_CONTINUE,                //继续
        EN_CURE_WORK_STATE_STOP,                    //停止
    }

    private EN_CURE_WORK_STATE leftCureStatus = EN_CURE_WORK_STATE.EN_CURE_WORK_STATE_INVALID;
    private EN_CURE_WORK_STATE rightCureStatus = EN_CURE_WORK_STATE.EN_CURE_WORK_STATE_INVALID;
    private EN_CURE_WORK_STATE midCureStatus = EN_CURE_WORK_STATE.EN_CURE_WORK_STATE_INVALID;
    private boolean isBack = false;//是否是按返回键

    private CustomCountDownTimer countLeftCureTimer = null;
    private CustomCountDownTimer countRightCureTimer = null;
    private long LeftTimerSYSJ=0;
    private long RightTimerSYSJ=0;

    private double leftStress = 5.3;//左眼默认压力
    private double rightStress = 5.3;//右眼默认压力
    private int left_stress_x = 0;//压力默认x轴
    private int right_stress_x = 0;//压力默认x轴
    private List left_stress_xs = new ArrayList();//压力默认x轴数组
    private List right_stress_xs = new ArrayList();//压力默认x轴数组
    private int left_temp_x = 0;//温度x轴
    private int right_temp_x = 0;//温度x轴
    private int text_query_l = 0;
    private int text_into_l = 0;
    private int text_deal_l = 0;
    private int text_query_r = 0;
    private int text_into_r = 0;
    private int text_deal_r = 0;
    private String patientid;//当前治疗病人的编号
    private int cureType = -1;//当前治疗病人的编号
    //治疗类型
    private int cure_model = 1;
    private String cureid;//当前治疗记录的编号
    private AlertDialog loadDialog = null;
    private Gson gson = null;//用于解析存储arraylist到数据库
    private ExecutorService mSingleThreadExecutor = null;//线程池

    private GraphicalView mOneLeftView, mOneRightView;//左右第一行图表
    private ChartService mOneLeftService, mOneRightService;//左右第一行图表控制
    private GraphicalView mTwoLeftView, mTwoRightView;//左右第二行图表
    private ChartService mTwoLeftService, mTwoRightService;//左右第二行图表控制
    private GraphicalView mThreeLeftView, mThreeRightView;//左右第三行图表
    private ChartService mThreeLeftService, mThreeRightService;//左右第三行图表控制


    private ArrayList<Integer> leftTwoDataX = new ArrayList<>();//左边第二行图表数据X
    private ArrayList<Double> leftTwoDataY = new ArrayList<>();//左边第二行图表数据Y
    private ArrayList<Integer> rightTwoDataX = new ArrayList<>();//右边第二行图表数据X
    private ArrayList<Double> rightTwoDataY = new ArrayList<>();//右边第二行图表数据Y
    private ArrayList<Integer> leftThreeDataX = new ArrayList<>();//左边第三行图表数据X
    private ArrayList<Double> leftThreeDataY = new ArrayList<>();//左边第三行图表数据Y
    private ArrayList<Integer> rightThreeDataX = new ArrayList<>();//右边第三行图表数据X
    private ArrayList<Double> rightThreeDataY = new ArrayList<>();//右边第三行图表数据Y

    private byte[] qy_result, temp_result;//查询后收到的气压与温度
    private byte[] qy_left_result, qy_right_result;//查询后收到的气压与温度
    private float left_f_qy, right_f_qy;//s查询后解析的气压值
    private float left_f_temp, right_f_temp;//查询后解析的温度值
    private String leftMinute, leftMillisecond;//倒计时的分秒
    private String rightMinute, rightMillisecond;//倒计时的分秒
    private int leftTimeCnt = 0, rightTimeCnt = 0;//左眼 右眼
    private double s_y, t_y;//测试值
    private int hotEyesMode = 0;

    //治疗步骤，格式：步骤序号 + 左/右眼 + 开始/暂停/继续/停止 + 时间
    //         存储格式：步骤1;步骤2;步骤3.....
    //         步骤序号: 3个字符  000 - 999
    //         左右眼： 左眼：L  右眼：R   1个字符
    //         开始/暂停/继续/停止:开始:K  暂停: Z 继续:J  停止:T   1个字符
    //         时间: 1002     前面两位表示分钟，后面两位表示时间    4个字符
    private List<String> cureSteplist = new ArrayList<>();
    private int cureStepNum = 0;
    private long leftTime = 0;//左眼治疗时间
    private long rightTime = 0;//右眼治疗时间
    private long leftStartTime = 0;//左眼开始治疗时间
    private long rightStartTime = 0;//右眼开始治疗时间
    private long cureStartTime = 0;     //开始治疗时间


    //第一行x轴的bottomView
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


    //标准14分钟，其他都是16分钟
    private int time = 14;

    //默认标准模式
    private int twoSize = 210;
    private int threeSize = 280;


    private boolean cureLeftIsWorking() {
        return leftCureStatus == EN_CURE_WORK_STATE.EN_CURE_WORK_STATE_STARTED || leftCureStatus == EN_CURE_WORK_STATE.EN_CURE_WORK_STATE_CONTINUE
                || leftCureStatus == EN_CURE_WORK_STATE.EN_CURE_WORK_STATE_WORK;
    }

    private boolean cureRightIsWorking() {
        return rightCureStatus == EN_CURE_WORK_STATE.EN_CURE_WORK_STATE_STARTED || rightCureStatus == EN_CURE_WORK_STATE.EN_CURE_WORK_STATE_CONTINUE
                || rightCureStatus == EN_CURE_WORK_STATE.EN_CURE_WORK_STATE_WORK;
    }

    private boolean cureLeftIsStarted() {
        return leftCureStatus != EN_CURE_WORK_STATE.EN_CURE_WORK_STATE_INVALID && leftCureStatus != EN_CURE_WORK_STATE.EN_CURE_WORK_STATE_STOP;
    }

    private boolean cureRighttIsStarted() {
        return rightCureStatus != EN_CURE_WORK_STATE.EN_CURE_WORK_STATE_INVALID && rightCureStatus != EN_CURE_WORK_STATE.EN_CURE_WORK_STATE_STOP;
    }

    private void cureLeftStop() {
        cureLeftStopSync();


        if (left_stress_x>=twoSize-3) {

            if (leftTwoDataY.get(left_stress_x-1)>=3.5f) {
                leftTwoDataX.add(left_stress_x);
                leftTwoDataY.add(0.3);
                mTwoLeftService.updateChart(left_stress_x, 0.3);
            }
        }

        countLeftCureTimer.stop();
        leftTimeCnt = 0;
        leftCounttimeTv.setText("00:00");
        mSingleThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                stopWarm(1, 0);
            }
        });
    }

    private void cureLeftStopSync() {
        leftCureStatus = EN_CURE_WORK_STATE.EN_CURE_WORK_STATE_STOP;
        bottomBtnView.setLeftEnabled(false);
        bottomBtnView.setLeftStopEnabled(false);
        bottomBtnView.setMidEnabled(false);
        bottomBtnView.setMidStopEnabled(false);
    }

    private void cureRightStop() {
        cureRightStopSync();

        if (right_stress_x>=twoSize-3) {

            if (rightTwoDataY.get(right_stress_x-1)>=3.5f) {

                rightTwoDataX.add(right_stress_x);
                rightTwoDataY.add(0.3);
                mTwoRightService.updateChart(right_stress_x, 0.3);
            }
        }
        countRightCureTimer.stop();
        rightCounttimeTv.setText("00:00");
        rightTimeCnt = 0;
        mSingleThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                stopWarm(0, 1);
            }
        });
    }

    private void cureRightStopSync() {
        rightCureStatus = EN_CURE_WORK_STATE.EN_CURE_WORK_STATE_STOP;
        bottomBtnView.setRightEnabled(false);
        bottomBtnView.setRightStopEnabled(false);

        bottomBtnView.setMidEnabled(false);
        bottomBtnView.setMidStopEnabled(false);
    }

    private void cureStop() {
        bottomBtnView.setAllEnabled(false);
        bottomBtnView.resetAllBg();
        mSingleThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                cureFinish();
            }
        });
        showCureFinish();
    }

    @Override
    protected int bindLayout() {
        return R.layout.activity_cure;
    }

    @Override
    protected boolean isNeedTitle() {
        return false;
    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);
        context = this;
        gson = new Gson();

        patientid = this.getIntent().getExtras().getString("patientid");
        cureType = this.getIntent().getExtras().getInt("checkType", -1);
        cure_model = getIntent().getExtras().getInt("cure_model", -1);

        LogUtils.e("oncreate===id===" + patientid);
        initButton();
        setListener();

        llBottom1 = findViewById(R.id.llBottom1);
        llBottom2 = findViewById(R.id.llBottom2);

        llBottom3 = findViewById(R.id.llBottom3);
        llBottom4 = findViewById(R.id.llBottom4);
        llBottom5 = findViewById(R.id.llBottom5);
        llBottom6 = findViewById(R.id.llBottom6);


        llBottom_r1 = findViewById(R.id.llBottom_r1);
        llBottom_r2 = findViewById(R.id.llBottom_r2);
        llBottom_rb1 = findViewById(R.id.llBottom_rb1);
        llBottom_rb2 = findViewById(R.id.llBottom_rb2);
        llBottom_rc1 = findViewById(R.id.llBottom_rc1);
        llBottom_rc2 = findViewById(R.id.llBottom_rc2);


        mOneLeftService = new ChartService(this);
        mOneRightService = new ChartService(this);
        mOneLeftService.setXYMultipleSeriesDataset("");
        mOneRightService.setXYMultipleSeriesDataset("");
        // 标准，14分钟  长按摩模式治疗16分钟  长保压按摩模式16分钟
        if (cure_model == 1) {
            time = 14;
            twoSize = 210;
            threeSize = 280;
            //第一行，显示14分钟，隐藏16分钟
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
            time = 16;
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
            time = 16;
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

        mOneLeftService.setXYMultipleSeriesRenderer2(twoSize+1, 100, "", "", "", Color.GREEN, Color.GREEN, Color.GREEN, Color.GREEN, 1);
        mOneRightService.setXYMultipleSeriesRenderer2(twoSize+1, 100, "", "", "", Color.GREEN, Color.GREEN, Color.GREEN, Color.GREEN, 1);

        mOneLeftView = mOneLeftService.getGraphicalView();
        llLeftStress.addView(mOneLeftView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        mOneRightView = mOneRightService.getGraphicalView();
        llRightStress.addView(mOneRightView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        mOneLeftService.setCustomX(true);
        mOneRightService.setCustomX(true);

        DataBean datas = DataUtils.getDatas(cure_model);
        mOneRightService.updateChart(datas.getxList(), datas.getyList());
        mOneLeftService.updateChart(datas.getxList(), datas.getyList());

        //显示视图
        initThisChart();

        SPUtils.put(context, Constant.LEFT, 0);
        SPUtils.put(context, Constant.RIGHT, 0);
        countLeftCureTimer = new CustomCountDownTimer(time * 60 * 1000+4000, 1000) {
            @Override
            public void onTick(long millis) {
                if (cureLeftIsWorking()) {
                    LeftTimerSYSJ=millis;
                    leftMinute = (millis / 1000 / 60) > 9 ? (millis / 1000 / 60) + "" : "0" + (millis / 1000 / 60);
                    leftMillisecond = (millis / 1000 % 60) > 9 ? (millis / 1000 % 60) + "" : "0" + (millis / 1000 % 60);
                    leftTime = (time * 60 * 1000) - millis + 1000;//加上倒计时开始时候减去的一秒
                    leftCounttimeTv.setText(leftMinute + ":" + leftMillisecond);
                }
            }

            @Override
            public void onFinish() {
                saveStepString("L", "T");
                leftStartTime = 0;
                cureLeftStop();
                if (!cureRighttIsStarted()) {
                    cureStop();
                }
            }

            @Override
            public void onMyInterval() {
                leftTimeCnt++;

                //1s查询一次，4S去处理一次

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSingleThreadExecutor.execute(new Runnable() {
                            @Override
                            public void run() {
                                queryLeftTemp();
                            }
                        });
                    }
                }, 100);

                if (leftTimeCnt % 3 == 0) {
                    mSingleThreadExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            queryTemp();
                        }
                    });
                }
            }
        };

        countRightCureTimer = new CustomCountDownTimer(time * 60 * 1000+4000, 1000) {
            @Override
            public void onTick(long millis) {
                if (cureRightIsWorking()) {
                    RightTimerSYSJ=millis;
                    rightMinute = (millis / 1000 / 60) > 9 ? (millis / 1000 / 60) + "" : "0" + (millis / 1000 / 60);
                    rightMillisecond = (millis / 1000 % 60) > 9 ? (millis / 1000 % 60) + "" : "0" + (millis / 1000 % 60);
                    rightTime = (time * 60 * 1000) - millis + 1000;
                    rightCounttimeTv.setText(rightMinute + ":" + rightMillisecond);
                }
            }

            @Override
            public void onFinish() {
                saveStepString("R", "T");
                rightStartTime = 0;
                cureRightStop();
                if (!cureLeftIsStarted()) {
                    cureStop();
                }
            }

            @Override
            public void onMyInterval() {
                rightTimeCnt++;

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSingleThreadExecutor.execute(new Runnable() {
                            @Override
                            public void run() {
                                queryRightTemp();
                            }
                        });
                    }
                }, 200);

                if (leftTimeCnt == 0 && rightTimeCnt % 3 == 0) {
                    mSingleThreadExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            queryTemp();
                        }
                    });
                }
//                if (rightTimeCnt % 4 == 0) {
//                    right_stress_x++;
//                    Log.e("chakan1","deal one data R "+(text_deal_r++));
//                    getRightStressXValue();
//                }
            }
        };

        //查询热敷眼罩工作模式
        mHandler.sendEmptyMessageDelayed(120, 500);

        initBottomCureType();
    }

    private void initBottomCureType() {
        switch (cureType) {
            case 1: {
                cureRightStopSync();
                break;
            }
            case 2: {
                cureLeftStopSync();
                break;
            }
        }
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

    @Override
    protected boolean topExit() {
        if (!cureLeftIsStarted() && !cureRighttIsStarted()) {
            mHandler.removeMessages(60);
            finish();
        }
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

    protected void showHelpDialog() {
        String showStr = "治疗界面可以点击开始按钮开始治疗，可以左右眼分别治疗或一起治疗。\n" +
                "治疗过程中可以随时暂停，可左右眼分别暂停或者一起暂停。\n" +
                "治疗过程中可以增加或减少最高气压值，增减脉动的力度。\n" +
                "当左右眼都结束治疗时，才是治疗结束，20s后会自动跳转到查看/打印界面。";
        showHelpDialog(showStr);
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
        closeLight();
        getMessageManage().unregisterMsgRecvByServiceCallback(recvMsg);
    }

    private void initButton() {
        eyeStressEnable();
        //    viewTop.disableBtn(1);
        viewTop.disableBtn(2);
        viewTop.disableBtn(3);
        viewTop.disableBtn(4);
        viewTop.disableBtn(5);
          mSingleThreadExecutor = Executors.newSingleThreadExecutor();//初始化每次只执行一个任务的线程池
        //   mSingleThreadExecutor =Executors.newFixedThreadPool(5);//初始化每次只执行一个任务的线程池
       // mSingleThreadExecutor = new ThreadPoolExecutor(3, Integer.MAX_VALUE, 0,
        //        TimeUnit.MILLISECONDS, new SynchronousQueue<Runnable>());//初始化每次只执行一个任务的线程池
    }

    private void setListener() {
        setTopBarView(viewTop);
        bottomBtnView.setListeners(new NoDoubleClickListener());

        leftEyeStressMinus.setOnClickListener(new NoDoubleClickListener());
        leftEyeStressPlus.setOnClickListener(new NoDoubleClickListener());
        rightEyeStressMinus.setOnClickListener(new NoDoubleClickListener());
        rightEyeStressPlus.setOnClickListener(new NoDoubleClickListener());

        bottomBtnView.setAllEnabled(true);
    }


    private void showLoadDialog() {
        if (loadDialog == null) {
            loadDialog = new AlertDialog.Builder(context, R.style.AlertDialogCustom).create();
        }
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.dialog_loading, null);
        loadDialog.setView(layout);
        loadDialog.setCancelable(false);
        loadDialog.show();
    }

    private void dismissLoadDialog() {
        if (loadDialog != null) {
            loadDialog.dismiss();
            loadDialog = null;
        }
    }

    private String startTime = "";

    private void showCureFinish() {
        //治疗完成发送提示音
        for (int i = 0; i < 3; i++) {
            mSingleThreadExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    ring();
                    try {
                        Thread.sleep(1800);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        if (!isBack) {
            bottomBtnView.setAllEnabled(false);//禁止所有点击
            readyLin.setVisibility(View.VISIBLE);
            warmingLin.setVisibility(View.GONE);
            refreshTitleMsg("治疗完成", "治疗完成");
            cureLeftConnect.setText("治疗完成，从机器和眼上移除治疗头，一次性治疗头不可以多次使用。");
            cureRightConnect.setText("治疗完成，从机器和眼上移除治疗头，一次性治疗头不可以多次使用。");
            leftEyeStressControl.setVisibility(View.GONE);
            rightEyeStressControl.setVisibility(View.GONE);

            DbCureDetailEntity userCureDetailEntity = new DbCureDetailEntity();
            cureid = patientid + System.currentTimeMillis();
            userCureDetailEntity.setCureId(cureid);
            LogUtils.e("id====" + patientid);
            userCureDetailEntity.setLeftTime(leftTime);
            userCureDetailEntity.setRightTime(rightTime);
            userCureDetailEntity.setNumber(patientid);
            if (!TextUtils.isEmpty((String) SPUtils.get(context, "cureName", ""))) {
                userCureDetailEntity.setDoctorName((String) SPUtils.get(context, "cureName", ""));
            }

            if (cureStartTime == 0) {
                startTime = DateUtil.getCurDateStr();
            } else {
                startTime = DateUtil.getCurStartTime(cureStartTime);
            }
            userCureDetailEntity.setCureDate(startTime);

            String leftPressDataX = null,
                    leftPressDataY = null,
                    rightPressDataX = null,
                    rightPressDataY = null,
                    leftTempDataX = null,
                    leftTempDataY = null,
                    rightTempDataX = null,
                    rightTempDataY = null;
            if (leftTwoDataX.size() > 0)
                leftPressDataX = gson.toJson(leftTwoDataX);
            if (leftTwoDataY.size() > 0)
                leftPressDataY = gson.toJson(leftTwoDataY);
            if (rightTwoDataX.size() > 0)
                rightPressDataX = gson.toJson(rightTwoDataX);
            if (rightTwoDataY.size() > 0)
                rightPressDataY = gson.toJson(rightTwoDataY);
            if (leftThreeDataX.size() > 0)
                leftTempDataX = gson.toJson(leftThreeDataX);
            if (leftThreeDataY.size() > 0)
                leftTempDataY = gson.toJson(leftThreeDataY);
            if (rightThreeDataX.size() > 0)
                rightTempDataX = gson.toJson(rightThreeDataX);
            if (rightThreeDataY.size() > 0)
                rightTempDataY = gson.toJson(rightThreeDataY);
            userCureDetailEntity.setLeftPressDataX(leftPressDataX);//左眼压力图表数据
            userCureDetailEntity.setLeftPressDataY(leftPressDataY);//左眼压力图表数据
            userCureDetailEntity.setRightPressDataX(rightPressDataX);//右眼压力图表数据
            userCureDetailEntity.setRightPressDataY(rightPressDataY);//右眼压力图表数据
            userCureDetailEntity.setLeftTempDataX(leftTempDataX);//左眼温度图表数据
            userCureDetailEntity.setLeftTempDataY(leftTempDataY);//左眼温度图表数据
            userCureDetailEntity.setRightTempDataX(rightTempDataX);//右眼温度图表数据
            userCureDetailEntity.setRightTempDataY(rightTempDataY);//右眼温度图表数据
            // 这里需要保存一个cure_model字段
            userCureDetailEntity.setCure_model(cure_model);


            getCureInfoManage().save(userCureDetailEntity, Constant.CUREINFO_ADD, new INormalEventListener() {
                @Override
                public void onValueEvent(int key, Object value) {
                    int result = (int) value;
                    if (result == 1) {
                        getCureInfoManage().getOnePatientDetail(patientid, new INormalEventListener() {
                            @Override
                            public void onValueEvent(int key, Object value) {
                                if (value == null) {
                                    LogUtils.e("未获取到病人数据");
                                } else {
                                    final DbPatientEntity userEntity = (DbPatientEntity) value;
                                    if (TextUtils.isEmpty(userEntity.getName())) {
                                        LogUtils.e("未获取到病人数据");
                                    } else {
                                        getCureInfoManage().getPatientAllCureDetail(patientid, new INormalEventListener() {
                                            @Override
                                            public void onValueEvent(int key, Object value) {
                                                if (value == null) {
                                                    userEntity.setCureNumber(0);
                                                } else {
                                                    List<DbCureDetailEntity> userDetailEntities = (List<DbCureDetailEntity>) value;
                                                    userEntity.setCureNumber(userDetailEntities.size());
                                                }

                                                getCureInfoManage().save(userEntity, Constant.PATIENT_UPDATE, new INormalEventListener() {
                                                    @Override
                                                    public void onValueEvent(int key, Object value) {
                                                        int result = (int) value;
                                                        if (result == 1) {
                                                            LogUtils.e("更新病人治疗次数成功");
                                                        } else {
                                                            LogUtils.e("更新病人治疗次数失败");
                                                        }
                                                    }

                                                    @Override
                                                    public void onErrEvent(int errCode) {
                                                        LogUtils.e("更新病人治疗次数失败");
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onErrEvent(int errCode) {
                                                LogUtils.e("获取病人所有病例数据失败");
                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onErrEvent(int errCode) {
                                LogUtils.e("获取病人数据失败");
                            }
                        });
                        mHandler.sendEmptyMessageDelayed(60, 20000);
                    } else {
                        LogUtils.e("存储病例数据失败");
                    }
                }

                @Override
                public void onErrEvent(int errCode) {
                    LogUtils.e("存储病例数据失败");
                }
            });

            DbCureStepEntity dbCureStepEntity = new DbCureStepEntity();
            dbCureStepEntity.setAccount(cureid);
            String step_str = getStepStr();
            dbCureStepEntity.setStep_str(step_str);
            getCureInfoManage().save(dbCureStepEntity, Constant.CURE_STEP_ADD, new INormalEventListener() {
                @Override
                public void onValueEvent(int key, Object value) {

                }

                @Override
                public void onErrEvent(int errCode) {

                }
            });
        } else {
            mHandler.sendEmptyMessage(61);
        }
    }

    private void refreshTitleMsg(String leftStr, String rightStr) {
        if (TextUtils.isEmpty(leftStr) || TextUtils.isEmpty(rightStr)) {
            return;
        }
        if (("0").equals(leftStr)) {
            leftCureTipsTv.setVisibility(View.INVISIBLE);
        } else if (("1").equals(leftStr)) {

        } else {
            leftCureTipsTv.setVisibility(View.VISIBLE);
            leftCureTipsTv.setText(leftStr, AnimationUtils.loadAnimation(context, R.anim.text_anim), 300);
        }

        if (("0").equals(rightStr)) {
            rightCureTipsTv.setVisibility(View.INVISIBLE);
        } else if (("1").equals(leftStr)) {

        } else {
            rightCureTipsTv.setVisibility(View.VISIBLE);
            rightCureTipsTv.setText(rightStr, AnimationUtils.loadAnimation(context, R.anim.text_anim), 300);
        }
    }

    protected void showModeDialog(final int left, final int right) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogCustom);
        builder.setTitle("选择模式");
        builder.setSingleChoiceItems(new String[]{"模式一", "模式二", "模式三", "自主编辑"}, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Message msg = new Message();
                warm((byte) 0x2A, (byte) 0x05, left, right);
                SPUtils.put(context, Constant.LEFT, left);
                SPUtils.put(context, Constant.RIGHT, right);
                switch (which) {
                    case 0://固定压力
                        SPUtils.put(context, Constant.QY_MODE, 1);
                        msg.what = 50;
                        break;
                    case 1://压力线性增加
                        SPUtils.put(context, Constant.QY_MODE, 2);
                        msg.what = 51;
                        break;
                    case 2://震荡压力
                        SPUtils.put(context, Constant.QY_MODE, 3);
                        msg.what = 52;
                        break;
                    case 3://自主编辑
                        msg.what = 53;
                        break;
                }
                mHandler.sendMessageDelayed(msg, 1000);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 4://气压阀工作
                    if (cureLeftIsWorking()) {
                        warmingLeftTipsTv.setText("正在治疗", AnimationUtils.loadAnimation(context, R.anim.text_anim), 300);
                        warmingLeftReasonTv.setText("使用下方“增加”“减小”按键可以控制治疗过程");
                        leftCureStatus = EN_CURE_WORK_STATE.EN_CURE_WORK_STATE_WORK;
                    }
                    if (cureRightIsWorking()) {
                        warmingRightTipsTv.setText("正在治疗", AnimationUtils.loadAnimation(context, R.anim.text_anim), 300);
                        warmingRightReasonTv.setText("使用下方“增加”“减小”按键可以控制治疗过程");
                        rightCureStatus = EN_CURE_WORK_STATE.EN_CURE_WORK_STATE_WORK;
                    }
                    break;
                case 6://停止气压阀工作
                    if (!cureLeftIsStarted()) {
                        warmingLeftTipsTv.setText("气压治疗未开始", AnimationUtils.loadAnimation(context, R.anim.text_anim), 300);
                        warmingLeftReasonTv.setText("气压阀未工作");
                    }
                    if (!cureRighttIsStarted()) {
                        warmingRightTipsTv.setText("气压治疗未开始", AnimationUtils.loadAnimation(context, R.anim.text_anim), 300);
                        warmingRightReasonTv.setText("气压阀未工作");
                    }
                    break;
                case 8: {//加热片加热
                    switch (leftCureStatus) {
                        case EN_CURE_WORK_STATE_STARTED: {
                            mSingleThreadExecutor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    qyfWork(2 + cure_model - 1, 1, 1, 0);          //气压阀工作，左眼
                                }
                            });
                            break;
                        }
                        case EN_CURE_WORK_STATE_WORK:
                        case EN_CURE_WORK_STATE_CONTINUE: {    //双眼
                            mSingleThreadExecutor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    stopQyf(1, 1, 0);       //停止气压阀，左眼
                                }
                            });
                            break;
                        }
                        case EN_CURE_WORK_STATE_STOP: {
                            mSingleThreadExecutor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    stopQyf(2, 1, 0);       //停止气压阀，左眼


                                }
                            });
                            break;
                        }
                        case EN_CURE_WORK_STATE_INVALID:
                        case EN_CURE_WORK_STATE_PAUSE: {
                            mSingleThreadExecutor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    stopQyf(0, 1, 0);       //停止气压阀，左眼
                                }
                            });
                            break;
                        }
                    }

                    switch (rightCureStatus) {
                        case EN_CURE_WORK_STATE_STARTED: {
                            mSingleThreadExecutor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    qyfWork(2 + cure_model - 1, 1, 0, 1);          //气压阀工作，右眼
                                }
                            });
                            break;
                        }
                        case EN_CURE_WORK_STATE_WORK:
                        case EN_CURE_WORK_STATE_CONTINUE: {
                            mSingleThreadExecutor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    stopQyf(1, 0, 1);       //继续气压阀，右眼
                                }
                            });
                            break;
                        }
                        case EN_CURE_WORK_STATE_STOP: {
                            mSingleThreadExecutor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    stopQyf(2, 0, 1);       //停止气压阀，右眼

                                                                   }
                            });
                            break;
                        }
                        case EN_CURE_WORK_STATE_INVALID:
                        case EN_CURE_WORK_STATE_PAUSE: {
                            mSingleThreadExecutor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    stopQyf(0, 0, 1);       //停止气压阀，右眼
                                }
                            });
                            break;
                        }
                    }
                    break;
                }
                case 10://停止加热
                    switch (leftCureStatus) {
                        case EN_CURE_WORK_STATE_STARTED:
                        case EN_CURE_WORK_STATE_WORK:
                        case EN_CURE_WORK_STATE_CONTINUE: {    //双眼
                            mSingleThreadExecutor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    warm((byte) 0x2A, (byte) 0x05, 1, 0);
                                }
                            });
                            break;
                        }
                        case EN_CURE_WORK_STATE_STOP: {
                            mSingleThreadExecutor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    stopQyf(2, 1, 0);       //停止气压阀，左眼

                                }
                            });
                            break;
                        }
                        case EN_CURE_WORK_STATE_INVALID:
                        case EN_CURE_WORK_STATE_PAUSE: {
                            mSingleThreadExecutor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    stopQyf(0, 1, 0);       //暂停，左眼

                                }
                            });
                            break;
                        }
                    }

                    switch (rightCureStatus) {
                        case EN_CURE_WORK_STATE_STARTED:
                        case EN_CURE_WORK_STATE_WORK:
                        case EN_CURE_WORK_STATE_CONTINUE: {    //双眼
                            mSingleThreadExecutor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    warm((byte) 0x2A, (byte) 0x05, 0, 1);
                                }
                            });
                            break;
                        }
                        case EN_CURE_WORK_STATE_STOP: {
                            mSingleThreadExecutor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    stopQyf(2, 0, 1);       //停止气压阀，右眼


                                }
                            });
                            break;
                        }
                        case EN_CURE_WORK_STATE_INVALID:
                        case EN_CURE_WORK_STATE_PAUSE: {
                            mSingleThreadExecutor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    stopQyf(0, 0, 1);       //暂停，左眼
                                }
                            });
                            break;
                        }
                    }
                    break;
                case 24: {//回复左气压
                    //Log.e("命令返回====>", "串口返回左气压数据 ");
                    qy_left_result = (byte[]) msg.obj;
                 //  Log.i(TAG, "气压" + SerialPortUtil.byte2hex(qy_left_result));
                    if (qy_left_result.length != 0) {
                        if (cureLeftIsStarted()) {
                            left_f_qy = (qy_left_result[0] & 0xFF) / 10f;
                            left_stress_xs.add(left_f_qy);
                     //       Log.e("chakan", "input one data L " + (left_f_qy));

                            int cureSize = left_stress_xs.size();
                            if (cureSize % 4 == 0) {
                                getLeftStressXValue();
                                left_stress_x++;

                                long millisTmp=(twoSize-left_stress_x+1)*4*1000;

                                if (LeftTimerSYSJ<(millisTmp))
                                {
                                    countLeftCureTimer.setmMillisInFuture(millisTmp);
                                }
                            }
                        }
                    }
                    break;
                }
                case 26: {//回复右气压
               //     Log.e("命令返回====>", "串口返回复右气压数据 ");
               //     Log.e(TAG, "handleMessage:=======================》 ");
                    qy_right_result = (byte[]) msg.obj;
               //     Log.i(TAG, "气压" + SerialPortUtil.byte2hex(qy_right_result));
                    if (qy_right_result.length != 0) {
                        if (cureRighttIsStarted()) {
                            right_f_qy = (qy_right_result[0] & 0xFF) / 10f;
                            right_stress_xs.add(right_f_qy);
                 //           Log.e("chakan1", "input one data R " + (right_f_qy));

                            int cureSize = right_stress_xs.size();
                            if (cureSize % 4 == 0) {
                                getRightStressXValue();
                                right_stress_x++;

                                long millisTmp=(twoSize-right_stress_x+1)*4*1000;

                                if (RightTimerSYSJ<(millisTmp))
                                {
                                    countRightCureTimer.setmMillisInFuture(millisTmp);
                                }
                            }

                        }
                    }
                    break;
                }
                case 14://回复温度
                 //   Log.e("命令返回====>", "串口返回温度数据 ");
                    temp_result = (byte[]) msg.obj;
                 //   Log.i(TAG, "温度" + SerialPortUtil.byte2hex(temp_result));
                    if (temp_result.length != 0 && (cureLeftIsWorking() || cureRightIsWorking())) {
                        if (cureLeftIsWorking()) {
                            left_f_temp = (int) temp_result[0] + ((int) temp_result[1] / 10);
                            left_temp_x += 1;
                        }

                        if (cureRightIsWorking()) {
                            right_f_temp = (int) temp_result[2] + ((int) temp_result[3] / 10);
                            right_temp_x += 1;
                        }

                   //     Log.e("chakan2", "chack temp L =  " + (left_f_temp) + " temp R =  " + right_f_temp);
                        setTempData(left_temp_x, right_temp_x, left_f_temp, right_f_temp);
                    }
                    break;
                case 16://治疗完成并自毁
                    if (!isBack) {//正常完成
                        //    showCureFinish();
                    } else {//按返回键
                        rightCureStatus = EN_CURE_WORK_STATE.EN_CURE_WORK_STATE_STOP;
                        leftCureStatus = EN_CURE_WORK_STATE.EN_CURE_WORK_STATE_STOP;
                    }
                    break;
                case 20: {
                    byte[] content = (byte[]) msg.obj;
                    if (content.length < 1)
                        break;
                    if ((content[0] & 0xFF) == 0x01) {
                        if (hotEyesMode == 0) {
                            saveStepString("H", "L");
                        } else if (hotEyesMode == 2) {
                            saveStepString("H", "T");
                        }
                        hotEyesMode = 1;
                    } else if ((content[0] & 0xFF) == 0x02) {
                        saveStepString("H", "G");
                        hotEyesMode = 2;
                    } else {
                        saveStepString("H", "D");
                        hotEyesMode = 0;
                    }
                    break;
                }

                case 66://热敷眼罩已连接

                    break;
//                case 50://固定压力
//                    readyLin.setVisibility(View.GONE);
//                    warmingLin.setVisibility(View.VISIBLE);
//                    bottomLumpLin.setVisibility(View.VISIBLE);
//                    leftEyeStressControl.setVisibility(View.GONE);
//                    rightEyeStressControl.setVisibility(View.GONE);
//                    break;
//                case 51://压力线性增加
//                    readyLin.setVisibility(View.GONE);
//                    warmingLin.setVisibility(View.VISIBLE);
//                    bottomLumpLin.setVisibility(View.VISIBLE);
//                    leftEyeStressControl.setVisibility(View.GONE);
//                    rightEyeStressControl.setVisibility(View.GONE);
//                    break;
//                case 52://震荡压力
//                    readyLin.setVisibility(View.GONE);
//                    warmingLin.setVisibility(View.VISIBLE);
//                    bottomLumpLin.setVisibility(View.VISIBLE);
//                    leftEyeStressControl.setVisibility(View.GONE);
//                    rightEyeStressControl.setVisibility(View.GONE);
//                    break;
                case 53://自主编辑
                    readyLin.setVisibility(View.GONE);
                    warmingLin.setVisibility(View.VISIBLE);
                    bottomLumpLin.setVisibility(View.VISIBLE);
                    leftEyeStressControl.setVisibility(View.VISIBLE);
                    rightEyeStressControl.setVisibility(View.VISIBLE);
                    if (!cureLeftIsStarted()) {
                        leftEyeStressMinus.setEnabled(false);
                        leftEyeStressPlus.setEnabled(false);
                    }
                    if (!cureRighttIsStarted()) {
                        rightEyeStressMinus.setEnabled(false);
                        rightEyeStressPlus.setEnabled(false);
                    }
                    break;
                case 60:
                    if (!isBack) {
                        //跳转到正式的fragmentActivity页面
                        Intent intent = new Intent(context, LookPrintActivity.class);
                        intent.putExtra("patientid", patientid);
                        intent.putExtra("curid", cureid);
                        intent.putExtra("cure_model", cure_model);
                        startActivity(intent);
                        finish();
                    }
                    break;
                case 61://返回键
                    finish();
                    break;
//                case 99://错误
//                    ToastUtil.s("数据错误，请重新开启治疗");
//                    countdownTimer.stop();
////                    mSingleThreadExecutor.execute(new Runnable() {
////                        @Override
////                        public void run() {
////                            cureFinish();
////                        }
////                    });
//                    SerialPortStop();
//                    finish();
//                    break;
                case 120: {
                    mSingleThreadExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            selfCheck();
                        }
                    });
                }
                default:
                    Log.i(TAG, "===" + msg.what);
                    Log.i(TAG, "===" + msg.obj);
                    break;
            }
        }
    };

    private void ring() {//蜂鸣器
        getMessageManage().send(SendDataArray.ring());
    }

    private void closeLight() {//关灯
        getMessageManage().send(SendDataArray.CloseLight());
    }

    private void selfCheck() {//热敷眼罩
        getMessageManage().send(SendDataArray.checkGoggles());
    }

    private void qyfWork(int mode, int type, int left, int right) {//气压阀工作
        getMessageManage().send(SendDataArray.qyfWork(mode, type, left, right));
    }

    private void warm(byte warm1, byte warm2, int left, int right) {//加温
        getMessageManage().send(SendDataArray.warm(warm1, warm2, left, right));
    }

    private void stopWarm(int left, int right) {//停止加温
        getMessageManage().send(SendDataArray.stopWarm(left, right));
    }

    private void stopQyf(int mode, int left, int right) {//停止气压阀
        getMessageManage().send(SendDataArray.stopQyf(mode, left, right));
    }

    private void queryQy() {//查询气压
        getMessageManage().send(SendDataArray.queryQy());
    }

    private void queryTemp() {//查询温度
        getMessageManage().send(SendDataArray.queryTemp());
    }

    private void queryLeftTemp() {//查询左温度
        getMessageManage().send(SendDataArray.queryLeftQy());
    }

    private void queryRightTemp() {//查询右温度
        getMessageManage().send(SendDataArray.queryRightQy());
    }

    private void cureFinish() {//治疗完成
        getMessageManage().send(SendDataArray.finish());
    }

    private void initThisChart() {
        /*--------第二行左侧压力表--------------*/
        mTwoLeftService = new ChartService(this);
        mTwoLeftService.setXYMultipleSeriesDataset("");
        mTwoLeftService.setXYMultipleSeriesRenderer(twoSize+1, 7, "", "", "",
                Color.GREEN, Color.GREEN, Color.GREEN, Color.GREEN, 2);
        mTwoLeftView = mTwoLeftService.getGraphicalView();
        mTwoLeftService.setCustomX(false);
        //将左右图表添加到布局容器中
        llLeftEyeStress.addView(mTwoLeftView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        /*--------第二行右侧压力表--------------*/
        mTwoRightService = new ChartService(this);
        mTwoRightService.setXYMultipleSeriesDataset("");
        mTwoRightService.setXYMultipleSeriesRenderer(twoSize+1, 7, "", "", "",
                Color.GREEN, Color.GREEN, Color.GREEN, Color.GREEN, 2);
        mTwoRightView = mTwoRightService.getGraphicalView();
        mTwoRightService.setCustomX(false);
        //将左右图表添加到布局容器中
        llRightEyeStress.addView(mTwoRightView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        /*--------第三行左侧温度表--------------*/
        mThreeLeftService = new ChartService(this);
        mThreeLeftService.setXYMultipleSeriesDataset("");
        mThreeLeftService.setXYMultipleSeriesRenderer(threeSize+1, 50, "", "", "",
                Color.GREEN, Color.GREEN, Color.GREEN, Color.GREEN, 3);
        mThreeLeftView = mThreeLeftService.getGraphicalView();
        mThreeLeftService.setCustomX(false);
        //将左右图表添加到布局容器中
        llLeftTemp.addView(mThreeLeftView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        /*--------第三行右侧温度表--------------*/
        mThreeRightService = new ChartService(this);
        mThreeRightService.setXYMultipleSeriesDataset("");
        mThreeRightService.setXYMultipleSeriesRenderer(threeSize+1, 50, "", "", "",
                Color.GREEN, Color.GREEN, Color.GREEN, Color.GREEN, 3);
        mThreeRightView = mThreeRightService.getGraphicalView();
        mThreeRightService.setCustomX(false);
        //将左右图表添加到布局容器中
        llRightTemp.addView(mThreeRightView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
    }


    //第二行左边展示
    private void setLeftData(int lx, double left_y) {
        if (cureLeftIsWorking()) {
            //左眼在工作
            leftTwoDataX.add(lx);
            leftTwoDataY.add(left_y);
//            mTwoLeftService.updateChart(leftTwoDataX, leftTwoDataY);
            mTwoLeftService.updateChart(lx, left_y);
        }
    }

    private void setRightData(int rx, double right_y) {
        if (cureRightIsWorking()) {
            rightTwoDataX.add(rx);
            rightTwoDataY.add(right_y);
//            mTwoRightService.updateChart(rightTwoDataX, rightTwoDataY);
            mTwoRightService.updateChart(rx, right_y);
        }
    }

    private void setTempData(int lx, int rx, double left_y, double right_y) {
        if (left_y >= 47.0f) {
            if (left_y >= 47.0f && left_y < 51.0f) {
                left_y = 44.0f;
            }
        } else if (left_y <= 40.0f) {
            if (left_y >= 36.0f && left_y < 40.0f) {
                left_y = 41.0f;
            }
        } else {
            left_y = 42.5f;
        }

        if (right_y >= 47.0f) {
            if (right_y >= 47.0f && right_y < 51.0f) {
                right_y = 44.0f;
            }
        } else if (right_y <= 40.0f) {
            if (right_y >= 36.0f && right_y < 40.0f) {
                right_y = 41.0f;
            }
        } else {
            right_y = 42.5f;
        }

        if (cureLeftIsWorking()) {
            leftThreeDataX.add(lx);
            leftThreeDataY.add(left_y);
            leftTempTv.setText(left_y + "℃");
//            mThreeLeftService.updateChart(leftThreeDataX, leftThreeDataY);
            mThreeLeftService.updateChart(lx, left_y);
        }
        if (cureRightIsWorking()) {
            rightThreeDataX.add(rx);
            rightThreeDataY.add(right_y);
            rightTempTv.setText(right_y + "℃");
//            mThreeRightService.updateChart(rightThreeDataX, rightThreeDataY);
            mThreeRightService.updateChart(rx, right_y);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            isBack = true;
            if (cureRighttIsStarted() || cureLeftIsStarted()) {
                ToastUtil.s("正在处理数据，请稍后");
            } else {
                countLeftCureTimer.stop();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        countRightCureTimer.stop();
                    }
                }, 100);

                rightTimeCnt = 0;
                leftTimeCnt = 0;
                mSingleThreadExecutor.shutdown();
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void cureMidBtnClick() {
        switch (midCureStatus) {
            case EN_CURE_WORK_STATE_INVALID: {
                midCureStatus = EN_CURE_WORK_STATE.EN_CURE_WORK_STATE_STARTED;
                bottomBtnView.setMidBg(1);
                mHandler.sendEmptyMessage(53);
                if (leftCureStatus == EN_CURE_WORK_STATE.EN_CURE_WORK_STATE_INVALID)
                    cureLeftBtnClick();
                if (rightCureStatus == EN_CURE_WORK_STATE.EN_CURE_WORK_STATE_INVALID)
                    cureRightBtnClick();
                break;
            }
            case EN_CURE_WORK_STATE_CONTINUE:
            case EN_CURE_WORK_STATE_WORK:
            case EN_CURE_WORK_STATE_STARTED: {
                midCureStatus = EN_CURE_WORK_STATE.EN_CURE_WORK_STATE_PAUSE;
                bottomBtnView.setMidBg(2);
                if (cureLeftIsWorking())
                    cureLeftBtnClick();
                if (cureRightIsWorking())
                    cureRightBtnClick();
                break;
            }
            case EN_CURE_WORK_STATE_PAUSE: {
                midCureStatus = EN_CURE_WORK_STATE.EN_CURE_WORK_STATE_CONTINUE;
                bottomBtnView.setMidBg(1);
                if (leftCureStatus == EN_CURE_WORK_STATE.EN_CURE_WORK_STATE_PAUSE)
                    cureLeftBtnClick();
                if (rightCureStatus == EN_CURE_WORK_STATE.EN_CURE_WORK_STATE_PAUSE)
                    cureRightBtnClick();
                break;
            }
            case EN_CURE_WORK_STATE_STOP: {
                bottomBtnView.setMidBg(0);
                leftCureStatus = EN_CURE_WORK_STATE.EN_CURE_WORK_STATE_STOP;
                rightCureStatus = EN_CURE_WORK_STATE.EN_CURE_WORK_STATE_STOP;
                cureLeftBtnClick();
                cureRightBtnClick();
                break;
            }
        }
    }

    private void CheckMidStatus() {
        if ((leftCureStatus == EN_CURE_WORK_STATE.EN_CURE_WORK_STATE_STARTED || (leftCureStatus == EN_CURE_WORK_STATE.EN_CURE_WORK_STATE_WORK)) &&
                (rightCureStatus == EN_CURE_WORK_STATE.EN_CURE_WORK_STATE_STARTED || (rightCureStatus == EN_CURE_WORK_STATE.EN_CURE_WORK_STATE_WORK))) {
            midCureStatus = EN_CURE_WORK_STATE.EN_CURE_WORK_STATE_STARTED;
            bottomBtnView.setMidBg(1);
        } else if (leftCureStatus == EN_CURE_WORK_STATE.EN_CURE_WORK_STATE_PAUSE && rightCureStatus == EN_CURE_WORK_STATE.EN_CURE_WORK_STATE_PAUSE) {
            midCureStatus = EN_CURE_WORK_STATE.EN_CURE_WORK_STATE_PAUSE;
            bottomBtnView.setMidBg(2);
        } else if (leftCureStatus == EN_CURE_WORK_STATE.EN_CURE_WORK_STATE_CONTINUE && rightCureStatus == EN_CURE_WORK_STATE.EN_CURE_WORK_STATE_CONTINUE) {
            midCureStatus = EN_CURE_WORK_STATE.EN_CURE_WORK_STATE_CONTINUE;
            bottomBtnView.setMidBg(1);
        } else if (leftCureStatus != EN_CURE_WORK_STATE.EN_CURE_WORK_STATE_INVALID && rightCureStatus != EN_CURE_WORK_STATE.EN_CURE_WORK_STATE_INVALID) {
            midCureStatus = leftCureStatus;
            if ((midCureStatus == EN_CURE_WORK_STATE.EN_CURE_WORK_STATE_STARTED) || (leftCureStatus == EN_CURE_WORK_STATE.EN_CURE_WORK_STATE_WORK)) {
                bottomBtnView.setMidBg(1);
            } else if (midCureStatus == EN_CURE_WORK_STATE.EN_CURE_WORK_STATE_PAUSE) {
                bottomBtnView.setMidBg(2);
            } else if (midCureStatus == EN_CURE_WORK_STATE.EN_CURE_WORK_STATE_CONTINUE) {
                bottomBtnView.setMidBg(1);
            }
        }
    }

    private void cureLeftBtnClick() {
        switch (leftCureStatus) {
            case EN_CURE_WORK_STATE_INVALID: {
                mSingleThreadExecutor.execute(new Runnable() {
                    @Override
                    public void run() {    //发送07加温指令
                        warm((byte) 0x2A, (byte) 0x05, 1, 0);
                    }
                });

                if (!cureRighttIsStarted()) {
                    startTime = DateUtil.getCurDateStr();
                }

                mHandler.sendEmptyMessage(53);
                leftCureStatus = EN_CURE_WORK_STATE.EN_CURE_WORK_STATE_STARTED;
                bottomBtnView.setLeftEnabled(true);
                bottomBtnView.setLeftStopEnabled(true);
                bottomBtnView.setLeftBg(1);

                CheckMidStatus();
                leftStartTime = System.currentTimeMillis();
                if (rightStartTime == 0)
                    cureStartTime = leftStartTime;

                saveStepString("L", "K");
                if (countLeftCureTimer.isStop())
                    countLeftCureTimer.start();
                break;
            }
            case EN_CURE_WORK_STATE_CONTINUE:
            case EN_CURE_WORK_STATE_WORK:
            case EN_CURE_WORK_STATE_STARTED: {
                mSingleThreadExecutor.execute(new Runnable() {
                    @Override
                    public void run() {      //停温
                        //stopWarm(1, 0);
                        stopQyf(0, 1, 0);       //停止气压阀，左眼
                    }
                });

                leftCureStatus = EN_CURE_WORK_STATE.EN_CURE_WORK_STATE_PAUSE;
                bottomBtnView.setLeftEnabled(true);
                bottomBtnView.setLeftStopEnabled(true);
                bottomBtnView.setLeftBg(2);

                CheckMidStatus();
                saveStepString("L", "Z");
                countLeftCureTimer.pause();
                break;
            }
            case EN_CURE_WORK_STATE_PAUSE: {
                mSingleThreadExecutor.execute(new Runnable() {
                    @Override
                    public void run() {    //发送07加温指令
                        // warm((byte) 0x2A, (byte) 0x05, 1, 0);
                        stopQyf(1, 1, 0);       //继续气压阀，左眼
                    }
                });

                leftCureStatus = EN_CURE_WORK_STATE.EN_CURE_WORK_STATE_CONTINUE;
                bottomBtnView.setLeftEnabled(true);
                bottomBtnView.setLeftStopEnabled(true);
                bottomBtnView.setLeftBg(1);

                CheckMidStatus();
                saveStepString("L", "J");
                countLeftCureTimer.restart();
                break;
            }
            case EN_CURE_WORK_STATE_STOP: {
                bottomBtnView.setLeftEnabled(false);
                bottomBtnView.setLeftStopEnabled(false);
                bottomBtnView.setLeftBg(0);

                bottomBtnView.setMidEnabled(false);
                bottomBtnView.setMidStopEnabled(false);
                bottomBtnView.setMidBg(0);

                CheckMidStatus();
                mSingleThreadExecutor.execute(new Runnable() {
                    @Override
                    public void run() {       //发送停止指令
                        stopWarm(1, 0);
                    }
                });

                saveStepString("L", "T");
                countLeftCureTimer.stop();
                leftTimeCnt = 0;
                leftStartTime = 0;
                break;
            }
        }
    }

    private void cureRightBtnClick() {
        switch (rightCureStatus) {
            case EN_CURE_WORK_STATE_INVALID: {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSingleThreadExecutor.execute(new Runnable() {
                            @Override
                            public void run() {    //发送07加温指令
                                warm((byte) 0x2A, (byte) 0x05, 0, 1);
                            }
                        });
                    }
                }, 100);

                if (!cureLeftIsStarted()) {
                    startTime = DateUtil.getCurDateStr();
                }

                mHandler.sendEmptyMessage(53);
                rightCureStatus = EN_CURE_WORK_STATE.EN_CURE_WORK_STATE_STARTED;
                bottomBtnView.setRightEnabled(true);
                bottomBtnView.setRightStopEnabled(true);
                bottomBtnView.setRightBg(1);

                CheckMidStatus();
                rightStartTime = System.currentTimeMillis();
                if (leftStartTime == 0)
                    cureStartTime = rightStartTime;

                saveStepString("R", "K");
                if (countRightCureTimer.isStop())
                    countRightCureTimer.start();
                break;
            }
            case EN_CURE_WORK_STATE_CONTINUE:
            case EN_CURE_WORK_STATE_WORK:
            case EN_CURE_WORK_STATE_STARTED: {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSingleThreadExecutor.execute(new Runnable() {
                            @Override
                            public void run() {      //停温
                                //stopWarm(0, 1);
                                stopQyf(0, 0, 1);       //停止气压阀，右眼
                            }
                        });
                    }
                }, 100);

                rightCureStatus = EN_CURE_WORK_STATE.EN_CURE_WORK_STATE_PAUSE;
                bottomBtnView.setRightEnabled(true);
                bottomBtnView.setRightStopEnabled(true);
                bottomBtnView.setRightBg(2);
                saveStepString("R", "Z");

                CheckMidStatus();
                countRightCureTimer.pause();
                break;
            }
            case EN_CURE_WORK_STATE_PAUSE: {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSingleThreadExecutor.execute(new Runnable() {
                            @Override
                            public void run() {    //发送07加温指令
                                //warm((byte) 0x2A, (byte) 0x05, 0, 1);
                                stopQyf(1, 0, 1);       //继续气压阀，左眼
                            }
                        });
                    }
                }, 100);

                rightCureStatus = EN_CURE_WORK_STATE.EN_CURE_WORK_STATE_CONTINUE;
                bottomBtnView.setRightEnabled(true);
                bottomBtnView.setRightStopEnabled(true);
                bottomBtnView.setRightBg(1);
                saveStepString("R", "J");

                CheckMidStatus();
                countRightCureTimer.restart();
                break;
            }
            case EN_CURE_WORK_STATE_STOP: {
                bottomBtnView.setRightEnabled(false);
                bottomBtnView.setRightStopEnabled(false);
                bottomBtnView.setRightBg(0);

                bottomBtnView.setMidEnabled(false);
                bottomBtnView.setMidStopEnabled(false);
                bottomBtnView.setMidBg(0);

                CheckMidStatus();

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSingleThreadExecutor.execute(new Runnable() {
                            @Override
                            public void run() {       //发送停止指令
                                stopWarm(0, 1);
                            }
                        });
                    }
                }, 100);

                saveStepString("R", "T");

                countRightCureTimer.stop();
                rightTimeCnt = 0;
                rightStartTime = 0;
                break;
            }
        }
    }

    /**
     * 设置点击间隔时间，防止点击过快
     */
    public class NoDoubleClickListener implements View.OnClickListener {

        public static final int MIN_CLICK_DELAY_TIME = 1000;
        private long lastClickTime = 0;

        @Override
        public void onClick(View v) {
            long currentTime = Calendar.getInstance().getTimeInMillis();
            if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
                lastClickTime = currentTime;
                switch (v.getId()) {
                    case R.id.mid_btn://开始暂停继续
                        cureMidBtnClick();
                        SPUtils.put(context, Constant.LEFT, 1);
                        SPUtils.put(context, Constant.RIGHT, 1);

                        MyApplication.getInstance().activePatientId = patientid;

                        eyeStressEnable();
                        break;
                    case R.id.mid_stop_btn://停止od与os
                        if (!cureLeftIsStarted() && !cureRighttIsStarted())
                            break;

                        showStopDialog("终止左右眼治疗吗？", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                midCureStatus = EN_CURE_WORK_STATE.EN_CURE_WORK_STATE_STOP;
                                cureMidBtnClick();

                                mSingleThreadExecutor.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        cureFinish();
                                    }
                                });
                                showCureFinish();
                            }
                        });
                        break;
                    case R.id.left_btn://开始暂停继续
                        SPUtils.put(context, Constant.LEFT, 1);
                        cureLeftBtnClick();

                        MyApplication.getInstance().activePatientId = patientid;
                        eyeStressEnable();
                        break;
                    case R.id.left_stop_btn://停止od
                        if (!cureLeftIsStarted())
                            break;
                        showStopDialog("终止左眼治疗吗？", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                leftCureStatus = EN_CURE_WORK_STATE.EN_CURE_WORK_STATE_STOP;
                                cureLeftBtnClick();
                                leftCureStop();
                            }
                        });
                        break;
                    case R.id.right_btn://开始暂停继续
                        SPUtils.put(context, Constant.RIGHT, 1);
                        cureRightBtnClick();

                        MyApplication.getInstance().activePatientId = patientid;
                        eyeStressEnable();
                        break;
                    case R.id.right_stop_btn://停止os
                        if (!cureRighttIsStarted())
                            break;
                        showStopDialog("终止右眼治疗吗？", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                rightCureStatus = EN_CURE_WORK_STATE.EN_CURE_WORK_STATE_STOP;
                                cureRightBtnClick();
                                rightCureStop();
                            }
                        });
                        break;
                    case R.id.left_eye_stress_minus://左控制减
                        if (!cureLeftIsStarted())
                            break;
                        if (leftStress > 0.1) {
                            leftStress -= 0.1;
                            final int stress = (int) Math.round(leftStress * 10);
                            mSingleThreadExecutor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    qyfWork(1, stress, 1, 0);
                                }
                            });
                            leftEyeStressTv.setText(formatDouble(leftStress) + "PSI");
                        } else {
                            ToastUtil.s("压力数值不能小于0.1");
                        }
                        break;
                    case R.id.left_eye_stress_plus://左控制加
                        if (!cureLeftIsStarted())
                            break;
                        if (leftStress < 6.4) {
                            leftStress += 0.1;
                            final int stress = (int) Math.round(leftStress * 10);
                            mSingleThreadExecutor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    qyfWork(1, stress, 1, 0);
                                }
                            });
                            leftEyeStressTv.setText(formatDouble(leftStress) + "PSI");
                        } else {
                            ToastUtil.s("压力数值不能大于6.5");
                        }
                        break;
                    case R.id.right_eye_stress_minus://右控制减
                        if (!cureRighttIsStarted())
                            break;
                        if (rightStress > 0.1) {
                            rightStress -= 0.1;
                            final int stress = (int) Math.round(rightStress * 10);
                            mSingleThreadExecutor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    qyfWork(1, stress, 0, 1);
                                }
                            });
                            rightEyeStressTv.setText(formatDouble(rightStress) + "PSI");
                        } else {
                            ToastUtil.s("压力数值不能小于0.1");
                        }
                        break;
                    case R.id.right_eye_stress_plus://右控制加
                        if (!cureRighttIsStarted())
                            break;
                        if (rightStress < 6.4) {
                            rightStress += 0.1;
                            final int stress = (int) Math.round(rightStress * 10);
                            mSingleThreadExecutor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    qyfWork(1, stress, 0, 1);
                                }
                            });
                            rightEyeStressTv.setText(formatDouble(rightStress) + "PSI");
                        } else {
                            ToastUtil.s("压力数值不能大于6.5");
                        }
                        break;
                }
            }
        }
    }

    private void leftCureStop() {
        cureLeftStop();

        if (!cureRighttIsStarted()) {
            cureStop();
        }
    }

    private void eyeStressEnable() {
        if (cureLeftIsStarted()) {
            leftEyeStressMinus.setEnabled(true);
            leftEyeStressPlus.setEnabled(true);
        } else {
            leftEyeStressMinus.setEnabled(false);
            leftEyeStressPlus.setEnabled(false);
        }

        if (cureRighttIsStarted()) {
            rightEyeStressMinus.setEnabled(true);
            rightEyeStressPlus.setEnabled(true);
        } else {
            rightEyeStressMinus.setEnabled(false);
            rightEyeStressPlus.setEnabled(false);
        }
    }

    private void rightCureStop() {
        cureRightStop();

        if (!cureLeftIsStarted()) {
            cureStop();
        }
    }

    private void saveStepString(String lOrR, String mode) {
        if (TextUtils.isEmpty(lOrR) || TextUtils.isEmpty(mode))
            return;
        cureStepNum++;
        String sNum = getStepNumStr(cureStepNum, 3);
        String sTime = getStepTimeStr(lOrR);
        String sStep = sNum + lOrR + mode + sTime;
        cureSteplist.add(sStep);

        //Log.e("mdqmdq", "*******  add step " + sStep);
    }

    private String getStepStr() {
        String ss_s = "";
        if (cureSteplist.size() == 0) {
            return ss_s;
        }

        for (int i = 0; i < cureSteplist.size(); i++) {
            String m_ss = cureSteplist.get(i);
            if (!TextUtils.isEmpty(m_ss)) {
                if (TextUtils.isEmpty(ss_s)) {
                    ss_s = m_ss;
                } else {
                    ss_s = ss_s + ";" + m_ss;
                }
            }
        }
        return ss_s;
    }

    private String getStepTimeStr(String lOrR) {
        String ss_time = "0000";
        if (lOrR.equals("L")) {
            long l_time = System.currentTimeMillis();
            long l_offset = l_time - leftStartTime;
            if (l_offset < 0) {
                return ss_time;
            }
            int i_time = (int) (l_offset / 1000);
            int min_value = i_time / 60;
            String min_str = getStepNumStr(min_value, 2);
            int sec_value = i_time % 60;
            String sec_str = getStepNumStr(sec_value, 2);
            ss_time = min_str + sec_str;
        } else if (lOrR.equals("R")) {
            long l_time = System.currentTimeMillis();
            long l_offset = l_time - rightStartTime;
            if (l_offset < 0) {
                return ss_time;
            }
            int i_time = (int) (l_offset / 1000);
            int min_value = i_time / 60;
            String min_str = getStepNumStr(min_value, 2);
            int sec_value = i_time % 60;
            String sec_str = getStepNumStr(sec_value, 2);
            ss_time = min_str + sec_str;
        } else if (lOrR.equals("H")) {
            long l_time = System.currentTimeMillis();
            long l_start = getMessageManage().getHotEyesWorkTime();
            if (l_start == 0) {
                return ss_time;
            }
            long l_offset = l_time - l_start;
            if (l_offset < 0) {
                return ss_time;
            }
            int i_time = (int) (l_offset / 1000);
            int min_value = i_time / 60;
            String min_str = getStepNumStr(min_value, 2);
            int sec_value = i_time % 60;
            String sec_str = getStepNumStr(sec_value, 2);
            ss_time = min_str + sec_str;
        }
        return ss_time;
    }

    //暂只支持几种情况的填值
    private String getStepNumStr(int num, int cnt) {
        String ss_num = String.valueOf(num);
        int i_len = ss_num.length();
        if (i_len != cnt) {
            int offset = cnt - i_len;
            if (offset == 1) {
                ss_num = "0" + ss_num;
            } else if (offset == 2) {
                ss_num = "00" + ss_num;
            } else if (offset == 3) {
                ss_num = "000" + ss_num;
            } else {
                ss_num = ss_num.substring(0, cnt);
            }
        }
        return ss_num;
    }

    private float getGoodPrintPointY(float pointY, boolean bLeft) {
        float backValue = pointY;
        if (bLeft) {
            int LastleftTime = (int) (leftTime / 1000);
            if ((LastleftTime > 0 && LastleftTime < 120) || (LastleftTime < 480 && LastleftTime > 360)) {
                if ((pointY > 3 && pointY < 3.2) || (pointY < 3 && pointY > 2.8)) {
                    backValue = 3;
                } else if (pointY > 3.2) {
                    backValue = pointY - 0.2f;
                } else if (pointY < 2.8) {
                    backValue = pointY + 0.2f;
                }
            }
        } else {
            int LastrightTime = (int) (rightTime / 1000);
            if ((LastrightTime > 0 && LastrightTime < 120) || (LastrightTime < 480 && LastrightTime > 360)) {
                if ((pointY > 3 && pointY < 3.2) || (pointY < 3 && pointY > 2.8)) {
                    backValue = 3;
                } else if (pointY > 3.2) {
                    backValue = pointY - 0.2f;
                } else if (pointY < 2.8) {
                    backValue = pointY + 0.2f;
                }
            }
        }
        return backValue;
    }

    //4s钟内检测到目标压值即认为采集到合适的值
    private void getRightStressXValue() {
        float printValue = 0.0f;
        int arr_size = right_stress_xs.size();
        if (arr_size <= 0)
            return;
        if (right_stress_x == 0 || right_stress_x == 1) {
            setRightData(right_stress_x, 3.5f);
            return;
        }
        if (arr_size > right_stress_x * 4 - 8) {
            int haveSize = arr_size - (right_stress_x * 4 - 8);
            float[] values = new float[haveSize];
            String printf_str = "values[] ";
            for (int i = 0; i < haveSize; i++) {
                values[i] = (float) right_stress_xs.get(i + (right_stress_x * 4 - 8));
                printf_str += (", " + values[i]);
            }
         //   Log.e("chakan1", printf_str);
            printValue = getBestValue(values, right_stress_x, false);
         //   Log.e("chakan1", "getBestValue printValue" + printValue + ",right_stress_x = " + right_stress_x);
            printValue = getGoodValue(right_stress_x, printValue, false);
        //    Log.e("chakan1", "getGoodValue printValue 1 = " + printValue + ",right_stress_x = " + right_stress_x);
        } else {
            printValue = getRightValue(right_stress_x, false);
        //    Log.e("chakan1", "getRightValue printValue  = " + printValue + ",right_stress_x = " + right_stress_x);
        }
        if (printValue<=3.5f) printValue+=0;
        else printValue+=0.4;
        setRightData(right_stress_x, printValue);
    }

    private float getBestValue(float[] values, int step, boolean bleft) {
        float rightValue = getRightValue(step, bleft);
        float offset = 0.0f;
        float bestOffset = 100.0f;
        float backValue = rightValue;
        for (int i = 0; i < values.length; i++) {
            offset = Math.abs(values[i] - rightValue);
            if (offset < bestOffset) {
                bestOffset = offset;
                backValue = values[i];
            }
        }
        return backValue;
    }

    private void getLeftStressXValue() {
        float printValue = 0.0f;
        int arr_size = left_stress_xs.size();
        if (arr_size <= 0)
            return;
        if (left_stress_x == 0 || left_stress_x == 1) {
            //第二行左边
            setLeftData(left_stress_x, 3.5f);
            return;
        }
        if (arr_size > left_stress_x * 4 - 8) {
            int haveSize = arr_size - (left_stress_x * 4 - 8);
            float[] values = new float[haveSize];
            String printf_str = "values[] ";
            for (int i = 0; i < haveSize; i++) {
                values[i] = (float) left_stress_xs.get(i + (left_stress_x * 4 - 8));
                printf_str += (",  " + values[i]);
            }
        //    Log.e("chakan", printf_str);
            printValue = getBestValue(values, left_stress_x, true);
        //    Log.e("chakan", "getBestValue printValue" + printValue + ",left_stress_x = " + left_stress_x);
            printValue = getGoodValue(left_stress_x, printValue, true);
       //     Log.e("chakan", "getGoodValue printValue 1 = " + printValue + ",left_stress_x = " + left_stress_x);
        } else {
            printValue = getRightValue(left_stress_x, true);
       //     Log.e("chakan", "getRightValue printValue  = " + printValue + ",left_stress_x = " + left_stress_x);
        }
        if (printValue<=3.5f) printValue+=0;
          else printValue+=0.4;
        setLeftData(left_stress_x, printValue);
    }

    private float getRightValue(int i, boolean bleft) {
        float standrendValue;
        if (bleft) {
            standrendValue = (float) leftStress;
        } else {
            standrendValue = (float) rightStress;
        }

        float returnValue = 0.0f;

        if (cure_model == 1) {
            if (i < 30) {
                if (i == 15) {
                    return 0.0f;
                } else {
                    return 3.5f;
                }
            }
            if (i >= 30 && i < 33) {
                return 0.0f;
            }
            if (i >= 33 && i < 36) {
                return 0.4f;
            }
            if (i >= 36 && i < 39) {
                return 0.8f;
            }
            if (i >= 39 && i < 42) {
                return 1.2f;
            }
            if (i >= 42 && i < 45) {
                return 1.6f;
            }
            if (i >= 45 && i < 48) {
                return 2.0f;
            }
            if (i >= 48 && i < 51) {
                return 2.4f;
            }
            if (i >= 51 && i < 54) {
                return 2.8f;
            }
            if (i >= 54 && i < 57) {
                return 3.2f;
            }
            if (i >= 57 && i < 60) {
                return 3.5f;
            }
            if (i >= 60 && i < 75) {
                if (i % 2 != 0) {
                    return 0.0f;
                } else {
                    return standrendValue;
                }
            }

            if (i >= 75 && i < 90) {
                if (i == 75 )
                    return 0.0f;
                else
                    return 3.5f;
            }

            if (i >= 90 && i < 105) {
                if (i % 2 == 0) {
                    return 0.0f;
                } else {
                    return standrendValue;
                }
            }
            if (i >= 105 && i < 135) {
                if ( i == 120) {
                    return 0.0f;
                } else {
                    return 3.5f;
                }
            }


            if (i >= 135 && i < 138) {
                return 0.0f;
            }
            if (i >= 138 && i < 141) {
                return 0.4f;
            }
            if (i >= 141 && i < 144) {
                return 0.8f;
            }
            if (i >= 144 && i < 147) {
                return 1.2f;
            }
            if (i >= 147 && i < 150) {
                return 1.6f;
            }
            if (i >= 150 && i < 153) {
                return 2.0f;
            }
            if (i >= 153 && i < 156) {
                return 2.4f;
            }
            if (i >= 156 && i < 159) {
                return 2.8f;
            }
            if (i >= 159 && i < 162) {
                return 3.2f;
            }
            if (i >= 162 && i < 165) {
                return 3.5f;
            }
            if (i >= 165 && i < 180) {
                if (i % 2 == 0) {
                    return 0.0f;
                } else {
                    return standrendValue;
                }
            }
            if (i >= 180 && i < 195) {
                if (i == 180 ) {
                    return 0.0f;
                } else {
                    return 3.5f;
                }
            }
            if (i >= 195 && i < 210) {
                if (i % 2 != 0) {
                    return 0.0f;
                } else {
                    return standrendValue;
                }
            }
            return returnValue;
        } else if (cure_model == 2) {
            if (i < 30) {
                if (i == 15) {
                    return 0.0f;
                } else {
                    return 3.5f;
                }
            }
            if (i >= 30 && i < 33) {
                return 0.0f;
            }
            if (i >= 33 && i < 36) {
                return 0.4f;
            }
            if (i >= 36 && i < 39) {
                return 0.8f;
            }
            if (i >= 39 && i < 42) {
                return 1.2f;
            }
            if (i >= 42 && i < 45) {
                return 1.6f;
            }
            if (i >= 45 && i < 48) {
                return 2.0f;
            }
            if (i >= 48 && i < 51) {
                return 2.4f;
            }
            if (i >= 51 && i < 54) {
                return 2.8f;
            }
            if (i >= 54 && i < 57) {
                return 3.2f;
            }
            if (i >= 57 && i < 60) {
                return 3.5f;
            }
            if (i >= 60 && i < 90) {
                if (i % 2 != 0) {
                    return 0.0f;
                } else {
                    return standrendValue;
                }
            }

            if (i >= 90 && i < 105) {

                    return 3.5f;
            }

            if (i >= 105 && i < 120) {
                if (i % 2 != 0) {
                    return 0.0f;
                } else {
                    return standrendValue;
                }
            }
            if (i >= 120 && i < 150) {
                if ( i == 135) {
                    return 0.0f;
                } else {
                    return 3.5f;
                }
            }


            if (i >= 150 && i < 153) {
                return 0.0f;
            }
            if (i >= 153 && i < 156) {
                return 0.4f;
            }
            if (i >= 156 && i < 159) {
                return 0.8f;
            }
            if (i >= 159 && i < 162) {
                return 1.2f;
            }
            if (i >= 162 && i < 165) {
                return 1.6f;
            }
            if (i >= 165 && i < 168) {
                return 2.0f;
            }
            if (i >= 168 && i < 171) {
                return 2.4f;
            }
            if (i >= 171 && i < 174) {
                return 2.8f;
            }
            if (i >= 174 && i < 177) {
                return 3.2f;
            }
            if (i >= 177 && i < 180) {
                return 3.5f;
            }
            if (i >= 180 && i < 210) {
                if (i % 2 != 0) {
                    return 0.0f;
                } else {
                    return standrendValue;
                }
            }
            if (i >= 210 && i < 225) {
               {
                    return 3.5f;
                }
            }
            if (i >= 225 && i < 240) {
                if (i % 2 != 0) {
                    return 0.0f;
                } else {
                    return standrendValue;
                }
            }


            return returnValue;
        } else if (cure_model == 3) {
            if (i < 30) {
                if (i == 15) {
                    return 0.0f;
                } else {
                    return 3.5f;
                }
            }
            if (i >= 30 && i < 33) {
                return 0.0f;
            }
            if (i >= 33 && i < 36) {
                return 0.4f;
            }
            if (i >= 36 && i < 39) {
                return 0.8f;
            }
            if (i >= 39 && i < 42) {
                return 1.2f;
            }
            if (i >= 42 && i < 45) {
                return 1.6f;
            }
            if (i >= 45 && i < 48) {
                return 2.0f;
            }
            if (i >= 48 && i < 51) {
                return 2.4f;
            }
            if (i >= 51 && i < 54) {
                return 2.8f;
            }
            if (i >= 54 && i < 57) {
                return 3.2f;
            }
            if (i >= 57 && i < 60) {
                return 3.5f;
            }
            if (i >= 60 && i < 75) {
                if (i % 2 != 0) {
                    return 0.0f;
                } else {
                    return standrendValue;
                }
            }

            if (i >= 75 && i < 105) {
                if (i == 75 ||i==90)
                    return 0.0f;
                else
                    return 3.5f;
            }

            if (i >= 105 && i < 120) {
                if (i % 2 != 0) {
                    return 0.0f;
                } else {
                    return standrendValue;
                }
            }
            if (i >= 120 && i < 150) {
                if ( i == 135 ) {
                    return 0.0f;
                } else {
                    return 3.5f;
                }
            }


            if (i >= 150 && i < 153) {
                return 0.0f;
            }
            if (i >= 153 && i < 156) {
                return 0.4f;
            }
            if (i >= 156 && i < 159) {
                return 0.8f;
            }
            if (i >= 159 && i < 162) {
                return 1.2f;
            }
            if (i >= 162 && i < 165) {
                return 1.6f;
            }
            if (i >= 165 && i < 168) {
                return 2.0f;
            }
            if (i >= 168 && i < 171) {
                return 2.4f;
            }
            if (i >= 171 && i < 174) {
                return 2.8f;
            }
            if (i >= 174 && i < 177) {
                return 3.2f;
            }
            if (i >= 177 && i < 180) {
                return 3.5f;
            }

            if (i >= 180 && i < 195) {
                if (i % 2 != 0) {
                    return 0.0f;
                } else {
                    return standrendValue;
                }
            }
            if (i >= 195 && i < 225) {
                if (i == 195 || i == 210 ) {
                    return 0.0f;
                } else {
                    return 3.5f;
                }
            }
            if (i >= 225 && i < 240) {
                if (i % 2 != 0) {
                    return 0.0f;
                } else {
                    return standrendValue;
                }
            }


            return returnValue;
        }

        return returnValue;
    }


    private float getGoodValue(int i, float curValue, boolean bleft) {
        float standrendValue;
        if (bleft) {
            standrendValue = (float) leftStress;
        } else {
            standrendValue = (float) rightStress;
        }

        float offset = 0.7f;
        float offsetX = 2.0f;
        float returnValue = 0.0f;

        if (cure_model == 1) {
            if (i < 30) {
                if (i == 15) {
                    if (curValue > offset) {
                        if (curValue > offset * offsetX) {
                            returnValue = 0.0f;
                        } else
                            returnValue = curValue - offset;
                    } else
                        returnValue = 0.0f;
                    return returnValue;
                } else {
                    return calcValue(curValue, 3.5f, 0.4f, 0.4f, 2.0f, 4.0f);
                }
            }
            if (i >= 30 && i < 33) {
                if (curValue > offset) {
                    if (curValue > offset * offsetX) {
                        returnValue = 0.0f;
                    } else {
                        returnValue = curValue - offset;
                    }
                } else
                    returnValue = 0.0f;
                return returnValue;
            }
            if (i >= 33 && i < 36) {
                if (curValue < 0)
                    return 0.4f;

                if (curValue > (0.4f + offset)) {
                    if (curValue > 0.4f + offset * offsetX) {
                        returnValue = 0.4f;
                    } else {
                        returnValue = curValue - offset;
                    }
                } else if (curValue < (0.4f - offset)) {
                    if (curValue < 0.4f - offset * offsetX) {
                        returnValue = 0.4f;
                    } else {
                        returnValue = curValue + offset;
                    }
                } else
                    returnValue = 0.4f;
                return returnValue;
            }
            if (i >= 36 && i < 39) {
                return calcValue(curValue, 0.8f, 0.4f, 0.2f, 1.5f, 3.0f);
            }
            if (i >= 39 && i < 42) {
                return calcValue(curValue, 1.2f, 0.4f, 0.3f, 1.5f, 3.0f);
            }
            if (i >= 42 && i < 45) {
                return calcValue(curValue, 1.6f, 0.4f, 0.4f, 1.5f, 3.0f);
            }
            if (i >= 45 && i < 48) {
                return calcValue(curValue, 2.0f, 0.4f, 0.4f, 2.0f, 4.0f);
            }
            if (i >= 48 && i < 51) {
                return calcValue(curValue, 2.4f, 0.4f, 0.4f, 2.0f, 4.0f);
            }
            if (i >= 51 && i < 54) {
                return calcValue(curValue, 2.8f, 0.4f, 0.4f, 2.0f, 4.0f);
            }
            if (i >= 54 && i < 57) {
                return calcValue(curValue, 3.2f, 0.4f, 0.4f, 2.0f, 4.0f);
            }
            if (i >= 57 && i < 60) {
                return calcValue(curValue, 3.5f, 0.4f, 0.4f, 2.0f, 4.0f);
            }


            if (i >= 60 && i < 75) {
                if (i % 2 != 0) {
                    if (curValue > offset) {
                        if (curValue > offset * offsetX) {
                            return 0.0f;
                        } else {
                            returnValue = curValue - offset;
                        }
                    } else
                        returnValue = 0.0f;
                } else {
                    if (standrendValue > 0.0f && standrendValue < 0.8f) {
                        return calcValue(curValue, standrendValue, 0.4f, 0.2f, 1.5f, 3.0f);
                    } else if (standrendValue >= 0.8f && standrendValue < 2.0f) {
                        return calcValue(curValue, standrendValue, 0.4f, 0.4f, 1.5f, 3.0f);
                    } else if (standrendValue >= 2.0f && standrendValue < 5.5f) {
                        if (i == 60) {
                            return calcValue(curValue, standrendValue, 0.6f, 0.6f, 2.0f, 4.0f);
                        } else {
                            return calcValue(curValue, standrendValue, 0.4f, 0.4f, 2.0f, 4.0f);
                        }
                    } else {
                        if (i == 60) {
                            return calcValue(curValue, standrendValue, 0.6f, 0.6f, 2.0f, 4.0f);
                        } else {
                            return calcValue(curValue, standrendValue, 0.4f, 0.4f, 2.0f, 4.0f);
                        }
                    }
                }
                return returnValue;
            }

            if (i >= 75 && i < 90) {
                if (i == 75 ) {
                    if (curValue > offset) {
                        if (curValue > offset * offsetX) {
                            returnValue = 0.0f;
                        } else
                            returnValue = curValue - offset;
                    } else
                        returnValue = 0.0f;
                    return returnValue;
                } else {
                    return calcValue(curValue, 3.5f, 0.4f, 0.4f, 2.0f, 4.0f);
                }
            }


            if (i >= 90 && i < 105) {
                if (i % 2 == 0) {
                    if (curValue > offset) {
                        if (curValue > offset * offsetX) {
                            return 0.0f;
                        } else {
                            returnValue = curValue - offset;
                        }
                    } else
                        returnValue = 0.0f;
                } else {
                    if (standrendValue > 0.0f && standrendValue < 0.8f) {
                        return calcValue(curValue, standrendValue, 0.4f, 0.2f, 1.5f, 3.0f);
                    } else if (standrendValue >= 0.8f && standrendValue < 2.0f) {
                        return calcValue(curValue, standrendValue, 0.4f, 0.4f, 1.5f, 3.0f);
                    } else if (standrendValue >= 2.0f && standrendValue < 5.5f) {
//                        if (i == 90) {
//                            return calcValue(curValue, standrendValue, 0.6f, 0.6f, 2.0f, 4.0f);
//                        } else
                            {
                            return calcValue(curValue, standrendValue, 0.4f, 0.4f, 2.0f, 4.0f);
                        }
                    } else {
//                        if (i == 90) {
//                            return calcValue(curValue, standrendValue, 0.6f, 0.6f, 2.0f, 4.0f);
//                        } else
                            {
                            return calcValue(curValue, standrendValue, 0.4f, 0.4f, 2.0f, 4.0f);
                        }
                    }
                }
                return returnValue;
            }

            if (i >= 105 && i < 135) {
                if (i == 120) {
                    if (curValue > offset) {
                        if (curValue > offset * offsetX) {
                            returnValue = 0.0f;
                        } else
                            returnValue = curValue - offset;
                    } else
                        returnValue = 0.0f;
                    return returnValue;
                } else {
                    return calcValue(curValue, 3.5f, 0.4f, 0.4f, 2.0f, 4.0f);
                }
            }


            if (i >= 135 && i < 138) {
                if (curValue > offset) {
                    if (curValue > offset * offsetX) {
                        returnValue = 0.0f;
                    } else
                        returnValue = curValue - offset;
                } else
                    returnValue = 0.0f;
                return returnValue;
            }
            if (i >= 138 && i < 141) {
                if (curValue < 0)
                    return 0.4f;

                if (curValue > (0.4f + offset)) {
                    if (curValue > 0.4f + offset * offsetX) {
                        returnValue = 0.4f;
                    } else {
                        returnValue = curValue - offset;
                    }
                } else if (curValue < (0.4f - offset)) {
                    if (curValue < 0.4f - offset * offsetX) {
                        returnValue = 0.4f;
                    } else {
                        returnValue = curValue + offset;
                    }
                } else
                    returnValue = 0.4f;
                return returnValue;
            }
            if (i >= 141 && i < 144) {
                return calcValue(curValue, 0.8f, 0.4f, 0.2f, 1.5f, 3.0f);
            }
            if (i >= 144 && i < 147) {
                return calcValue(curValue, 1.2f, 0.4f, 0.3f, 1.5f, 3.0f);
            }
            if (i >= 147 && i < 150) {
                return calcValue(curValue, 1.6f, 0.4f, 0.4f, 1.5f, 3.0f);
            }
            if (i >= 150 && i < 153) {
                return calcValue(curValue, 2.0f, 0.4f, 0.4f, 2.0f, 4.0f);
            }
            if (i >= 153 && i < 156) {
                return calcValue(curValue, 2.4f, 0.4f, 0.4f, 2.0f, 4.0f);
            }
            if (i >= 156 && i < 159) {
                return calcValue(curValue, 2.8f, 0.4f, 0.4f, 2.0f, 4.0f);
            }
            if (i >= 159 && i < 162) {
                return calcValue(curValue, 3.2f, 0.4f, 0.4f, 2.0f, 4.0f);
            }
            if (i >= 162 && i < 165) {
                return calcValue(curValue, 3.5f, 0.4f, 0.4f, 2.0f, 4.0f);
            }
            if (i >= 165 && i < 180) {
                if (i % 2 == 0) {
                    if (curValue > offset) {
                        if (curValue > offset * offsetX) {
                            return 0.0f;
                        } else {
                            returnValue = curValue - offset;
                        }
                    } else
                        returnValue = 0.0f;
                } else {
                    if (standrendValue > 0.0f && standrendValue < 0.8f) {
                        return calcValue(curValue, standrendValue, 0.4f, 0.2f, 1.5f, 3.0f);
                    } else if (standrendValue >= 0.8f && standrendValue < 2.0f) {
                        return calcValue(curValue, standrendValue, 0.4f, 0.4f, 1.5f, 3.0f);
                    } else if (standrendValue >= 2.0f && standrendValue < 5.5f) {
                        if (i == 165) {
                            return calcValue(curValue, standrendValue, 0.6f, 0.6f, 2.0f, 4.0f);
                        } else {
                            return calcValue(curValue, standrendValue, 0.4f, 0.4f, 2.0f, 4.0f);
                        }
                    } else {
                        if (i == 165) {
                            return calcValue(curValue, standrendValue, 0.6f, 0.6f, 2.0f, 4.0f);
                        } else {
                            return calcValue(curValue, standrendValue, 0.4f, 0.4f, 2.0f, 4.0f);
                        }
                    }
                }
                return returnValue;
            }

            if (i >= 180 && i < 195) {
                if (i == 180 ) {
                    if (curValue > offset) {
                        if (curValue > offset * offsetX) {
                            returnValue = 0.0f;
                        } else
                            returnValue = curValue - offset;
                    } else
                        returnValue = 0.0f;
                    return returnValue;
                } else {
                    return calcValue(curValue, 3.5f, 0.4f, 0.4f, 2.0f, 4.0f);
                }
            }


            if (i >= 195 && i < 210) {
                if (i % 2 != 0) {
                    if (curValue > offset) {
                        if (curValue > offset * offsetX) {
                            return 0.0f;
                        } else {
                            returnValue = curValue - offset;
                        }
                    } else
                        returnValue = 0.0f;
                } else {
                    if (standrendValue > 0.0f && standrendValue < 0.8f) {
                        return calcValue(curValue, standrendValue, 0.4f, 0.2f, 1.5f, 3.0f);
                    } else if (standrendValue >= 0.8f && standrendValue < 2.0f) {
                        return calcValue(curValue, standrendValue, 0.4f, 0.4f, 1.5f, 3.0f);
                    } else if (standrendValue >= 2.0f && standrendValue < 5.5f) {
//                        if (i == 195) {
//                            return calcValue(curValue, standrendValue, 0.6f, 0.6f, 2.0f, 4.0f);
//                        } else
                            {
                            return calcValue(curValue, standrendValue, 0.4f, 0.4f, 2.0f, 4.0f);
                        }
                    } else {
//                        if (i == 195) {
//                            return calcValue(curValue, standrendValue, 0.6f, 0.6f, 2.0f, 4.0f);
//                        } else
                            {
                            return calcValue(curValue, standrendValue, 0.4f, 0.4f, 2.0f, 4.0f);
                        }
                    }
                }
                return returnValue;
            }

        } else if (cure_model == 2) {

            if (i < 30) {
                if (i == 15) {
                    if (curValue > offset) {
                        if (curValue > offset * offsetX) {
                            returnValue = 0.0f;
                        } else
                            returnValue = curValue - offset;
                    } else
                        returnValue = 0.0f;
                    return returnValue;
                } else {
                    return calcValue(curValue, 3.5f, 0.4f, 0.4f, 2.0f, 4.0f);
                }
            }
            if (i >= 30 && i < 33) {
                if (curValue > offset) {
                    if (curValue > offset * offsetX) {
                        returnValue = 0.0f;
                    } else {
                        returnValue = curValue - offset;
                    }
                } else
                    returnValue = 0.0f;
                return returnValue;
            }
            if (i >= 33 && i < 36) {
                if (curValue < 0)
                    return 0.4f;

                if (curValue > (0.4f + offset)) {
                    if (curValue > 0.4f + offset * offsetX) {
                        returnValue = 0.4f;
                    } else {
                        returnValue = curValue - offset;
                    }
                } else if (curValue < (0.4f - offset)) {
                    if (curValue < 0.4f - offset * offsetX) {
                        returnValue = 0.4f;
                    } else {
                        returnValue = curValue + offset;
                    }
                } else
                    returnValue = 0.4f;
                return returnValue;
            }
            if (i >= 36 && i < 39) {
                return calcValue(curValue, 0.8f, 0.4f, 0.2f, 1.5f, 3.0f);
            }
            if (i >= 39 && i < 42) {
                return calcValue(curValue, 1.2f, 0.4f, 0.3f, 1.5f, 3.0f);
            }
            if (i >= 42 && i < 45) {
                return calcValue(curValue, 1.6f, 0.4f, 0.4f, 1.5f, 3.0f);
            }
            if (i >= 45 && i < 48) {
                return calcValue(curValue, 2.0f, 0.4f, 0.4f, 2.0f, 4.0f);
            }
            if (i >= 48 && i < 51) {
                return calcValue(curValue, 2.4f, 0.4f, 0.4f, 2.0f, 4.0f);
            }
            if (i >= 51 && i < 54) {
                return calcValue(curValue, 2.8f, 0.4f, 0.4f, 2.0f, 4.0f);
            }
            if (i >= 54 && i < 57) {
                return calcValue(curValue, 3.2f, 0.4f, 0.4f, 2.0f, 4.0f);
            }
            if (i >= 57 && i < 60) {
                return calcValue(curValue, 3.5f, 0.4f, 0.4f, 2.0f, 4.0f);
            }


            if (i >= 60 && i < 90) {
                if (i % 2 != 0) {
                    if (curValue > offset) {
                        if (curValue > offset * offsetX) {
                            return 0.0f;
                        } else {
                            returnValue = curValue - offset;
                        }
                    } else
                        returnValue = 0.0f;
                } else {
                    if (standrendValue > 0.0f && standrendValue < 0.8f) {
                        return calcValue(curValue, standrendValue, 0.4f, 0.2f, 1.5f, 3.0f);
                    } else if (standrendValue >= 0.8f && standrendValue < 2.0f) {
                        return calcValue(curValue, standrendValue, 0.4f, 0.4f, 1.5f, 3.0f);
                    } else if (standrendValue >= 2.0f && standrendValue < 5.5f) {
                        if (i == 60) {
                            return calcValue(curValue, standrendValue, 0.6f, 0.6f, 2.0f, 4.0f);
                        } else {
                            return calcValue(curValue, standrendValue, 0.4f, 0.4f, 2.0f, 4.0f);
                        }
                    } else {
                        if (i == 60) {
                            return calcValue(curValue, standrendValue, 0.6f, 0.6f, 2.0f, 4.0f);
                        } else {
                            return calcValue(curValue, standrendValue, 0.4f, 0.4f, 2.0f, 4.0f);
                        }
                    }
                }
                return returnValue;
            }

            if (i >= 90 && i < 105) {
//                if (i == 104) {
//                    if (curValue > offset) {
//                        if (curValue > offset * offsetX) {
//                            returnValue = 0.0f;
//                        } else
//                            returnValue = curValue - offset;
//                    } else
//                        returnValue = 0.0f;
//                    return returnValue;
//                } else
                    {
                    return calcValue(curValue, 3.5f, 0.4f, 0.4f, 2.0f, 4.0f);
                }
            }


            if (i >= 105 && i < 120) {
                if (i % 2 != 0) {
                    if (curValue > offset) {
                        if (curValue > offset * offsetX) {
                            return 0.0f;
                        } else {
                            returnValue = curValue - offset;
                        }
                    } else
                        returnValue = 0.0f;
                } else {
                    if (standrendValue > 0.0f && standrendValue < 0.8f) {
                        return calcValue(curValue, standrendValue, 0.4f, 0.2f, 1.5f, 3.0f);
                    } else if (standrendValue >= 0.8f && standrendValue < 2.0f) {
                        return calcValue(curValue, standrendValue, 0.4f, 0.4f, 1.5f, 3.0f);
                    } else if (standrendValue >= 2.0f && standrendValue < 5.5f) {
//                        if (i == 105) {
//                            return calcValue(curValue, standrendValue, 0.6f, 0.6f, 2.0f, 4.0f);
//                        } else
                            {
                            return calcValue(curValue, standrendValue, 0.4f, 0.4f, 2.0f, 4.0f);
                        }
                    } else {
//                        if (i == 105) {
//                            return calcValue(curValue, standrendValue, 0.6f, 0.6f, 2.0f, 4.0f);
//                        } else
                            {
                            return calcValue(curValue, standrendValue, 0.4f, 0.4f, 2.0f, 4.0f);
                        }
                    }
                }
                return returnValue;
            }

            if (i >= 120 && i < 150) {
                if ( i == 135) {
                    if (curValue > offset) {
                        if (curValue > offset * offsetX) {
                            returnValue = 0.0f;
                        } else
                            returnValue = curValue - offset;
                    } else
                        returnValue = 0.0f;
                    return returnValue;
                } else {
                    return calcValue(curValue, 3.5f, 0.4f, 0.4f, 2.0f, 4.0f);
                }
            }


            if (i >= 150 && i < 153) {
                if (curValue > offset) {
                    if (curValue > offset * offsetX) {
                        returnValue = 0.0f;
                    } else
                        returnValue = curValue - offset;
                } else
                    returnValue = 0.0f;
                return returnValue;
            }
            if (i >= 153 && i < 156) {
                if (curValue < 0)
                    return 0.4f;

                if (curValue > (0.4f + offset)) {
                    if (curValue > 0.4f + offset * offsetX) {
                        returnValue = 0.4f;
                    } else {
                        returnValue = curValue - offset;
                    }
                } else if (curValue < (0.4f - offset)) {
                    if (curValue < 0.4f - offset * offsetX) {
                        returnValue = 0.4f;
                    } else {
                        returnValue = curValue + offset;
                    }
                } else
                    returnValue = 0.4f;
                return returnValue;
            }
            if (i >= 156 && i < 159) {
                return calcValue(curValue, 0.8f, 0.4f, 0.2f, 1.5f, 3.0f);
            }
            if (i >= 159 && i < 162) {
                return calcValue(curValue, 1.2f, 0.4f, 0.3f, 1.5f, 3.0f);
            }
            if (i >= 162 && i < 165) {
                return calcValue(curValue, 1.6f, 0.4f, 0.4f, 1.5f, 3.0f);
            }
            if (i >= 165 && i < 168) {
                return calcValue(curValue, 2.0f, 0.4f, 0.4f, 2.0f, 4.0f);
            }
            if (i >= 168 && i < 171) {
                return calcValue(curValue, 2.4f, 0.4f, 0.4f, 2.0f, 4.0f);
            }
            if (i >= 171 && i < 174) {
                return calcValue(curValue, 2.8f, 0.4f, 0.4f, 2.0f, 4.0f);
            }
            if (i >= 174 && i < 177) {
                return calcValue(curValue, 3.2f, 0.4f, 0.4f, 2.0f, 4.0f);
            }
            if (i >= 177 && i < 180) {
                return calcValue(curValue, 3.5f, 0.4f, 0.4f, 2.0f, 4.0f);
            }
            if (i >= 180 && i < 210) {
                if (i % 2 != 0) {
                    if (curValue > offset) {
                        if (curValue > offset * offsetX) {
                            return 0.0f;
                        } else {
                            returnValue = curValue - offset;
                        }
                    } else
                        returnValue = 0.0f;
                } else {
                    if (standrendValue > 0.0f && standrendValue < 0.8f) {
                        return calcValue(curValue, standrendValue, 0.4f, 0.2f, 1.5f, 3.0f);
                    } else if (standrendValue >= 0.8f && standrendValue < 2.0f) {
                        return calcValue(curValue, standrendValue, 0.4f, 0.4f, 1.5f, 3.0f);
                    } else if (standrendValue >= 2.0f && standrendValue < 5.5f) {
                        if (i == 180) {
                            return calcValue(curValue, standrendValue, 0.6f, 0.6f, 2.0f, 4.0f);
                        } else {
                            return calcValue(curValue, standrendValue, 0.4f, 0.4f, 2.0f, 4.0f);
                        }
                    } else {
                        if (i == 180) {
                            return calcValue(curValue, standrendValue, 0.6f, 0.6f, 2.0f, 4.0f);
                        } else {
                            return calcValue(curValue, standrendValue, 0.4f, 0.4f, 2.0f, 4.0f);
                        }
                    }
                }
                return returnValue;
            }

            if (i >= 210 && i < 225) {
//                if (i == 224) {
//                    if (curValue > offset) {
//                        if (curValue > offset * offsetX) {
//                            returnValue = 0.0f;
//                        } else
//                            returnValue = curValue - offset;
//                    } else
//                        returnValue = 0.0f;
//                    return returnValue;
//                } else
               {
                    return calcValue(curValue, 3.5f, 0.4f, 0.4f, 2.0f, 4.0f);
                }
            }


            if (i >= 225 && i < 240) {
                if (i % 2 != 0) {
                    if (curValue > offset) {
                        if (curValue > offset * offsetX) {
                            return 0.0f;
                        } else {
                            returnValue = curValue - offset;
                        }
                    } else
                        returnValue = 0.0f;
                } else {
                    if (standrendValue > 0.0f && standrendValue < 0.8f) {
                        return calcValue(curValue, standrendValue, 0.4f, 0.2f, 1.5f, 3.0f);
                    } else if (standrendValue >= 0.8f && standrendValue < 2.0f) {
                        return calcValue(curValue, standrendValue, 0.4f, 0.4f, 1.5f, 3.0f);
                    } else if (standrendValue >= 2.0f && standrendValue < 5.5f) {
//                        if (i == 225) {
//                            return calcValue(curValue, standrendValue, 0.6f, 0.6f, 2.0f, 4.0f);
//                        } else
                            {
                            return calcValue(curValue, standrendValue, 0.4f, 0.4f, 2.0f, 4.0f);
                        }
                    } else {
//                        if (i == 225) {
//                            return calcValue(curValue, standrendValue, 0.6f, 0.6f, 2.0f, 4.0f);
//                        } else
                            {
                            return calcValue(curValue, standrendValue, 0.4f, 0.4f, 2.0f, 4.0f);
                        }
                    }
                }
                return returnValue;
            }

            return returnValue;

        } else if (cure_model == 3) {

            if (i < 30) {
                if (i == 15) {
                    if (curValue > offset) {
                        if (curValue > offset * offsetX) {
                            returnValue = 0.0f;
                        } else
                            returnValue = curValue - offset;
                    } else
                        returnValue = 0.0f;
                    return returnValue;
                } else {
                    return calcValue(curValue, 3.5f, 0.4f, 0.4f, 2.0f, 4.0f);
                }
            }
            if (i >= 30 && i < 33) {
                if (curValue > offset) {
                    if (curValue > offset * offsetX) {
                        returnValue = 0.0f;
                    } else {
                        returnValue = curValue - offset;
                    }
                } else
                    returnValue = 0.0f;
                return returnValue;
            }
            if (i >= 33 && i < 36) {
                if (curValue < 0)
                    return 0.4f;

                if (curValue > (0.4f + offset)) {
                    if (curValue > 0.4f + offset * offsetX) {
                        returnValue = 0.4f;
                    } else {
                        returnValue = curValue - offset;
                    }
                } else if (curValue < (0.4f - offset)) {
                    if (curValue < 0.4f - offset * offsetX) {
                        returnValue = 0.4f;
                    } else {
                        returnValue = curValue + offset;
                    }
                } else
                    returnValue = 0.4f;
                return returnValue;
            }
            if (i >= 36 && i < 39) {
                return calcValue(curValue, 0.8f, 0.4f, 0.2f, 1.5f, 3.0f);
            }
            if (i >= 39 && i < 42) {
                return calcValue(curValue, 1.2f, 0.4f, 0.3f, 1.5f, 3.0f);
            }
            if (i >= 42 && i < 45) {
                return calcValue(curValue, 1.6f, 0.4f, 0.4f, 1.5f, 3.0f);
            }
            if (i >= 45 && i < 48) {
                return calcValue(curValue, 2.0f, 0.4f, 0.4f, 2.0f, 4.0f);
            }
            if (i >= 48 && i < 51) {
                return calcValue(curValue, 2.4f, 0.4f, 0.4f, 2.0f, 4.0f);
            }
            if (i >= 51 && i < 54) {
                return calcValue(curValue, 2.8f, 0.4f, 0.4f, 2.0f, 4.0f);
            }
            if (i >= 54 && i < 57) {
                return calcValue(curValue, 3.2f, 0.4f, 0.4f, 2.0f, 4.0f);
            }
            if (i >= 57 && i < 60) {
                return calcValue(curValue, 3.5f, 0.4f, 0.4f, 2.0f, 4.0f);
            }


            if (i >= 60 && i < 75) {
                if (i % 2 != 0) {
                    if (curValue > offset) {
                        if (curValue > offset * offsetX) {
                            return 0.0f;
                        } else {
                            returnValue = curValue - offset;
                        }
                    } else
                        returnValue = 0.0f;
                } else {
                    if (standrendValue > 0.0f && standrendValue < 0.8f) {
                        return calcValue(curValue, standrendValue, 0.4f, 0.2f, 1.5f, 3.0f);
                    } else if (standrendValue >= 0.8f && standrendValue < 2.0f) {
                        return calcValue(curValue, standrendValue, 0.4f, 0.4f, 1.5f, 3.0f);
                    } else if (standrendValue >= 2.0f && standrendValue < 5.5f) {
                        if (i == 60) {
                            return calcValue(curValue, standrendValue, 0.6f, 0.6f, 2.0f, 4.0f);
                        } else {
                            return calcValue(curValue, standrendValue, 0.4f, 0.4f, 2.0f, 4.0f);
                        }
                    } else {
                        if (i == 60) {
                            return calcValue(curValue, standrendValue, 0.6f, 0.6f, 2.0f, 4.0f);
                        } else {
                            return calcValue(curValue, standrendValue, 0.4f, 0.4f, 2.0f, 4.0f);
                        }
                    }
                }
                return returnValue;
            }

            if (i >= 75 && i < 105) {
                if (i == 75 || i == 90 ) {
                    if (curValue > offset) {
                        if (curValue > offset * offsetX) {
                            returnValue = 0.0f;
                        } else
                            returnValue = curValue - offset;
                    } else
                        returnValue = 0.0f;
                    return returnValue;
                } else {
                    return calcValue(curValue, 3.5f, 0.4f, 0.4f, 2.0f, 4.0f);
                }
            }


            if (i >= 105 && i < 120) {
                if (i % 2 != 0) {
                    if (curValue > offset) {
                        if (curValue > offset * offsetX) {
                            return 0.0f;
                        } else {
                            returnValue = curValue - offset;
                        }
                    } else
                        returnValue = 0.0f;
                } else {
                    if (standrendValue > 0.0f && standrendValue < 0.8f) {
                        return calcValue(curValue, standrendValue, 0.4f, 0.2f, 1.5f, 3.0f);
                    } else if (standrendValue >= 0.8f && standrendValue < 2.0f) {
                        return calcValue(curValue, standrendValue, 0.4f, 0.4f, 1.5f, 3.0f);
                    } else if (standrendValue >= 2.0f && standrendValue < 5.5f) {
//                        if (i == 105) {
//                            return calcValue(curValue, standrendValue, 0.6f, 0.6f, 2.0f, 4.0f);
//                        } else
                            {
                            return calcValue(curValue, standrendValue, 0.4f, 0.4f, 2.0f, 4.0f);
                        }
                    } else {
//                        if (i == 105) {
//                            return calcValue(curValue, standrendValue, 0.6f, 0.6f, 2.0f, 4.0f);
//                        } else
                            {
                            return calcValue(curValue, standrendValue, 0.4f, 0.4f, 2.0f, 4.0f);
                        }
                    }
                }
                return returnValue;
            }

            if (i >= 120 && i < 150) {
                if ( i == 135) {
                    if (curValue > offset) {
                        if (curValue > offset * offsetX) {
                            returnValue = 0.0f;
                        } else
                            returnValue = curValue - offset;
                    } else
                        returnValue = 0.0f;
                    return returnValue;
                } else {
                    return calcValue(curValue, 3.5f, 0.4f, 0.4f, 2.0f, 4.0f);
                }
            }


            if (i >= 150 && i < 153) {
                if (curValue > offset) {
                    if (curValue > offset * offsetX) {
                        returnValue = 0.0f;
                    } else
                        returnValue = curValue - offset;
                } else
                    returnValue = 0.0f;
                return returnValue;
            }
            if (i >= 153 && i < 156) {
                if (curValue < 0)
                    return 0.4f;

                if (curValue > (0.4f + offset)) {
                    if (curValue > 0.4f + offset * offsetX) {
                        returnValue = 0.4f;
                    } else {
                        returnValue = curValue - offset;
                    }
                } else if (curValue < (0.4f - offset)) {
                    if (curValue < 0.4f - offset * offsetX) {
                        returnValue = 0.4f;
                    } else {
                        returnValue = curValue + offset;
                    }
                } else
                    returnValue = 0.4f;
                return returnValue;
            }
            if (i >= 156 && i < 159) {
                return calcValue(curValue, 0.8f, 0.4f, 0.2f, 1.5f, 3.0f);
            }
            if (i >= 159 && i < 162) {
                return calcValue(curValue, 1.2f, 0.4f, 0.3f, 1.5f, 3.0f);
            }
            if (i >= 162 && i < 165) {
                return calcValue(curValue, 1.6f, 0.4f, 0.4f, 1.5f, 3.0f);
            }
            if (i >= 165 && i < 168) {
                return calcValue(curValue, 2.0f, 0.4f, 0.4f, 2.0f, 4.0f);
            }
            if (i >= 168 && i < 171) {
                return calcValue(curValue, 2.4f, 0.4f, 0.4f, 2.0f, 4.0f);
            }
            if (i >= 171 && i < 174) {
                return calcValue(curValue, 2.8f, 0.4f, 0.4f, 2.0f, 4.0f);
            }
            if (i >= 174 && i < 177) {
                return calcValue(curValue, 3.2f, 0.4f, 0.4f, 2.0f, 4.0f);
            }
            if (i >= 177 && i < 180) {
                return calcValue(curValue, 3.5f, 0.4f, 0.4f, 2.0f, 4.0f);
            }
            if (i >= 180 && i < 195) {
                if (i % 2 != 0) {
                    if (curValue > offset) {
                        if (curValue > offset * offsetX) {
                            return 0.0f;
                        } else {
                            returnValue = curValue - offset;
                        }
                    } else
                        returnValue = 0.0f;
                } else {
                    if (standrendValue > 0.0f && standrendValue < 0.8f) {
                        return calcValue(curValue, standrendValue, 0.4f, 0.2f, 1.5f, 3.0f);
                    } else if (standrendValue >= 0.8f && standrendValue < 2.0f) {
                        return calcValue(curValue, standrendValue, 0.4f, 0.4f, 1.5f, 3.0f);
                    } else if (standrendValue >= 2.0f && standrendValue < 5.5f) {
                        if (i == 180) {
                            return calcValue(curValue, standrendValue, 0.6f, 0.6f, 2.0f, 4.0f);
                        } else {
                            return calcValue(curValue, standrendValue, 0.4f, 0.4f, 2.0f, 4.0f);
                        }
                    } else {
                        if (i == 180) {
                            return calcValue(curValue, standrendValue, 0.6f, 0.6f, 2.0f, 4.0f);
                        } else {
                            return calcValue(curValue, standrendValue, 0.4f, 0.4f, 2.0f, 4.0f);
                        }
                    }
                }
                return returnValue;
            }

            if (i >= 195 && i < 225) {
                if (i == 195 || i == 210 ) {
                    if (curValue > offset) {
                        if (curValue > offset * offsetX) {
                            returnValue = 0.0f;
                        } else
                            returnValue = curValue - offset;
                    } else
                        returnValue = 0.0f;
                    return returnValue;
                } else {
                    return calcValue(curValue, 3.5f, 0.4f, 0.4f, 2.0f, 4.0f);
                }
            }


            if (i >= 225 && i < 240) {
                if (i % 2 != 0) {
                    if (curValue > offset) {
                        if (curValue > offset * offsetX) {
                            return 0.0f;
                        } else {
                            returnValue = curValue - offset;
                        }
                    } else
                        returnValue = 0.0f;
                } else {
                    if (standrendValue > 0.0f && standrendValue < 0.8f) {
                        return calcValue(curValue, standrendValue, 0.4f, 0.2f, 1.5f, 3.0f);
                    } else if (standrendValue >= 0.8f && standrendValue < 2.0f) {
                        return calcValue(curValue, standrendValue, 0.4f, 0.4f, 1.5f, 3.0f);
                    } else if (standrendValue >= 2.0f && standrendValue < 5.5f) {
//                        if (i == 225) {
//                            return calcValue(curValue, standrendValue, 0.6f, 0.6f, 2.0f, 4.0f);
//                        } else
                            {
                            return calcValue(curValue, standrendValue, 0.4f, 0.4f, 2.0f, 4.0f);
                        }
                    } else {
//                        if (i == 225) {
//                            return calcValue(curValue, standrendValue, 0.6f, 0.6f, 2.0f, 4.0f);
//                        } else
                            {
                            return calcValue(curValue, standrendValue, 0.4f, 0.4f, 2.0f, 4.0f);
                        }
                    }
                }
                return returnValue;
            }


            return returnValue;
        }


        return returnValue;
    }

    /***
     * 优化算法
     *
     * @param curValue       当前值
     * @param standrendValue 期望标准值
     * @param offsetUp       允许上偏差值
     * @param offsetDown     允许下偏差值
     * @param offsetX        允许输入异常值的范围
     * @param offsetX2       不包容异常值的范围
     * @return
     */
    private float calcValue(float curValue, float standrendValue, float offsetUp, float offsetDown, float offsetX, float offsetX2) {
        float mValue = 0.0f;
        Log.e("chakan", "calcValue curValue = " + curValue + ";standrendValue = " + standrendValue + ";offsetUp = " + offsetUp + ";offsetDown = " + offsetDown
                + ";offsetX = " + offsetX + ";offsetX2 = " + offsetX2);
        //值异常，则返回0
        if (curValue < 0)
            return mValue;

        if (curValue > (standrendValue + offsetUp)) {
            float bounds1 = standrendValue + offsetUp * offsetX;
            float bounds2 = standrendValue + offsetUp * offsetX2;

            //发现超过极限值
            if (bounds1 > 6.5f) {
                bounds1 = 6.5f;
            }

            if (bounds2 > 6.5f) {
                bounds2 = 6.5f;
            }

            //实际值在3范围外，属于不包容异常，返回实际值
            //实际值在2和3范围内，返回标准值，属于包容值
            //实际值在1和2界面范围内，减去偏差，属于优化值
            //实际值在1范围内，就是标准值，属于优化值

            //存在一个界限的问题 1 2 3 4 都可能过了最低或者最高界限
            //如果1过了界限，直接就认为是标准值？但要考虑本来值就在界限上的问题
            //所以如果与界限值很接近，要考虑设小偏差值，或者直接发现采集到的4个值都为界限值的话，就强制设置成界限值
            //所以要设置上偏差和下偏差参数
            //举个例子：  如果在0.4的时候收到了0就认为他是0.4  但是在0.8的时候就要把下偏差设置为0.2 在小于0.8 - 0.2*3的时候就用实际值   这个可以有
            //同样的  如果在6.0的时候上部分降低偏差，7.0范围外就认为是异常值全给7.0    如果在5.3的时候就要把上偏差设置为0.4.在大于5.3+0.4*3的时候就采用实际值
            if (curValue > bounds2) {
                return curValue;
            } else if (curValue > bounds1) {
                mValue = standrendValue;
            } else {
                mValue = curValue - offsetUp;
            }
        } else if (curValue < (standrendValue - offsetDown)) {
            float bounds1 = standrendValue - offsetDown * offsetX;
            float bounds2 = standrendValue - offsetDown * offsetX2;

            //发现超过极限值
            if (bounds1 < 0.0f) {
                bounds1 = 0.0f;
            }

            if (bounds2 < 0.0f) {
                bounds2 = 0.0f;
            }

            if (curValue < bounds2) {
                return curValue;
            } else if (curValue < bounds1) {
                mValue = standrendValue;
            } else {
                mValue = curValue + offsetDown;
            }
        } else
            mValue = standrendValue;
        return mValue;
    }
}
