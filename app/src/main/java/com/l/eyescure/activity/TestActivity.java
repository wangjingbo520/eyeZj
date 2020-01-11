package com.l.eyescure.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.LinearLayout;

import com.l.eyescure.R;
import com.l.eyescure.util.CustomCountDownTimer;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author:Look
 * Version: V1.0
 * Description:
 * Date: 2017/4/23
 */

public class TestActivity extends AppCompatActivity {
    @BindView(R.id.linearLayout1)
    LinearLayout mLayout;


//    private GraphicalView mView;//左右图表
//    private ChartService mService;
//    private Timer timer;
//    private String[] xText = new String[180];
//    private ArrayList<Integer> xList = new ArrayList<>();
//    private ArrayList<Double> yList = new ArrayList<>();

//    private LCountdownTimer countdownTimer = null;
    private CustomCountDownTimer countdownTimer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);
        countdownTimer = new CustomCountDownTimer(10*60*1000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.i("111","倒计时:"+millisUntilFinished);
                String minute = (millisUntilFinished / 1000 / 60) > 9 ? (millisUntilFinished / 1000 / 60)+"":"0"+(millisUntilFinished / 1000 / 60);
                String millisecond = (millisUntilFinished / 1000 % 60) > 9 ? (millisUntilFinished / 1000 % 60) +"":"0"+(millisUntilFinished / 1000 % 60);
                Log.i("111",minute+":"+millisecond);
            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onMyInterval() {

            }
        };
        countdownTimer.start();
//        mService = new ChartService(this);
//        mService.setXYMultipleSeriesDataset("左温度曲线");

//        for (int i = 0;i<180;i++){
//            xText[i] = "";
//            xList.add(i);
//            if (i<30){
//                if (i == 15){
//                    yList.add(0.0);
//                }else{
//                    yList.add(3.3);
//                }
//
//            }
//            if (i>=30 && i< 33){
//                yList.add(0.0);
//            }
//            if (i>=33 && i< 36){
//                yList.add(0.4);
//            }
//            if (i>=36 && i< 39){
//                yList.add(0.8);
//            }
//            if (i>=39 && i< 42){
//                yList.add(1.2);
//            }
//            if (i>=42 && i< 45){
//                yList.add(1.6);
//            }
//            if (i>=45 && i< 48){
//                yList.add(2.0);
//            }
//            if (i>=48 && i< 51){
//                yList.add(2.4);
//            }
//            if (i>=51 && i< 54){
//                yList.add(2.8);
//            }
//            if (i>=54 && i< 57){
//                yList.add(3.2);
//            }
//            if (i>=57 && i< 60){
//                yList.add(3.5);
//            }
//            if (i>=60 && i< 90){
//                yList.add(5.2);
//            }
//            if (i>=90 && i< 120){
//                if (i==115){
//                    yList.add(0.0);
//                }else{
//                    yList.add(3.3);
//                }
//
//            }
//            if (i>=120 && i< 123){
//                yList.add(0.0);
//            }
//            if (i>=123 && i< 126){
//                yList.add(0.4);
//            }
//            if (i>=126 && i< 129){
//                yList.add(0.8);
//            }
//            if (i>=129 && i< 132){
//                yList.add(1.2);
//            }
//            if (i>=132 && i< 135){
//                yList.add(1.6);
//            }
//            if (i>=135 && i< 138){
//                yList.add(2.0);
//            }
//            if (i>=138 && i< 141){
//                yList.add(2.4);
//            }
//            if (i>=141 && i< 144){
//                yList.add(2.8);
//            }
//            if (i>=144 && i< 147){
//                yList.add(3.2);
//            }
//            if (i>=147 && i< 150){
//                yList.add(3.5);
//            }
//            if (i>=150 && i< 180){
//                yList.add(5.2);
//            }
//
//        }
//        LogUtils.i(yList.toString());
//        mService.setXYMultipleSeriesRenderer(180, 6.5, "111111", "时间", "温度",
//                Color.RED, Color.RED, Color.RED, Color.BLACK);
//        mView = mService.getGraphicalView();
//
//        //将左右图表添加到布局容器中
//        mLayout.addView(mView, new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
//        mService.updateChart(xList,yList);
//        timer = new Timer();
//        timer.schedule(new TimerTask() {
//            public void run() {
//                handler.sendMessage(handler.obtainMessage());
//            }
//        }, 10, 3000);

    }

//    private int t = 0;
//    private Handler handler = new Handler() {
//        //定时更新图表
//        public void handleMessage(Message msg) {
//            mService.updateChart(t, Math.random() * 100);
//            t += 1;
//        }
//    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (timer != null) {
//            timer.cancel();
//        }
    }
}
