package com.l.eyescure.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hanks.htextview.HTextView;
import com.hanks.htextview.HTextViewType;
import com.l.eyescure.R;
import com.l.eyescure.util.Constant;
import com.l.eyescure.util.SPUtils;
import com.l.eyescure.util.SendDataArray;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoadingActivity extends BaseActivity {
    @BindView(R.id.rl_loading)
    RelativeLayout rlLoading;
    @BindView(R.id.loading_tv)
    HTextView loadingTv;
    @BindView(R.id.loading_tv_name)
    TextView loadingTvName;
    @BindView(R.id.loading_tv_en)
    TextView loadingTvEn;
    @BindView(R.id.loading_tv_main_name)
    TextView loadingTvMainName;
    private LocalBroadcastManager broadcastManager;

    @Override
    protected int bindLayout() {
        return R.layout.activity_loading;
    }

    @Override
    protected boolean isNeedTitle() {
        return false;
    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);

        broadcastManager = LocalBroadcastManager.getInstance(this);
        // 注册登录的广播事件
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.ACTION_PORT_INIT_OK);
        intentFilter.addAction(Constant.ACTION_PORT_INIT_ERR);
        intentFilter.addAction(Constant.ACTION_CURE_MANAGE_INIT_OK);
        intentFilter.setPriority(800);

        BroadcastReceiver broadcastReceiver = new LocalBroadcastReceiver();
        broadcastManager.registerReceiver(broadcastReceiver, intentFilter);

        Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/arial.ttf");
        loadingTvName.setTypeface(typeFace);
        loadingTvEn.setTypeface(typeFace);
        loadingTvMainName.setTypeface(typeFace);

        loadingTv.setAnimateType(HTextViewType.TYPER);
        loadingTv.animateText("设备初始化中，请稍后");
        rlLoading.setEnabled(false);

        mHandler.sendEmptyMessageDelayed(100, 3000);
    }

    @Override
    protected int topHelp() {
        return 0;
    }

    @Override
    protected boolean topExit() {
        return false;
    }

    @Override
    protected void topSet() {

    }

    @Override
    protected void topCare() {

    }

    @Override
    protected void topFile() {

    }

    @Override
    protected void topPrintf() {

    }

    @Override
    protected void topUser() {

    }

    private class LocalBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constant.ACTION_PORT_INIT_OK)) {
                mHandler.removeMessages(100);
                mHandler.sendEmptyMessageDelayed(0, 800);
            } else if (action.equals(Constant.ACTION_PORT_INIT_ERR)) {
                mHandler.removeMessages(100);
                mHandler.sendEmptyMessage(99);
            }
        }
    }

    private void checkInitStatus() {
        boolean binit = getMessageManage().isbInit();
        if (binit) {
            mHandler.sendEmptyMessageDelayed(0, 800);
        } else if (binit) {
            mHandler.sendEmptyMessage(99);
        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    mHandler.sendEmptyMessageDelayed(18, 1000);
                    break;
                case 99:
                    rlLoading.setEnabled(false);
                    loadingTv.setAnimateType(HTextViewType.TYPER);
                    loadingTv.animateText("初始化失败，请重新开启程序");
                    Log.i("", "失败了");
                    break;
                case 100:
                    checkInitStatus();
                    break;
                case 18:
                    ring();
                    rlLoading.setEnabled(true);
                    loadingTv.setAnimateType(HTextViewType.TYPER);
                    loadingTv.animateText("初始化完成，请点击屏幕继续");
                    Log.i("", "响铃了");
                    break;
            }
        }
    };

    private void ring() {//蜂鸣器
        getMessageManage().send(SendDataArray.ring());
    }

    @OnClick(R.id.rl_loading)
    public void onClick() {
        String pwd = SPUtils.get(context, "password", "").toString();
        Intent i = new Intent();
        i.setClass(LoadingActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
    }
}
