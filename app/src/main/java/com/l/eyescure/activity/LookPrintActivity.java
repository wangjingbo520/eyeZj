package com.l.eyescure.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.apkfuns.logutils.LogUtils;
import com.l.eyescure.R;
import com.l.eyescure.activity.fragement.PrintFragment;
import com.l.eyescure.activity.fragement.PrintfViewFragemrnt;
import com.l.eyescure.activity.fragement.ViewPageFragment;
import com.l.eyescure.activity.fragement.ViewPageFragment2;
import com.l.eyescure.server.ServerHelper;
import com.l.eyescure.server.SqlManage.localsql.DbCureDetailEntity;
import com.l.eyescure.server.callBack.INormalEventListener;
import com.l.eyescure.server.cureManage.CureInfoManage;

import org.xutils.common.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class LookPrintActivity extends FragmentActivity {
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    private List<PrintfViewFragemrnt> mindicators = new ArrayList<>();
    private List<PrintFragment> mindicators2 = new ArrayList<>();
    private ViewPageFragment ViewFragment;
    private ViewPageFragment2 ViewFragment2;
    private CureStepActivity cureStepFragment;
    private List<DbCureDetailEntity> list;
    private String cureNumber;
    private String cureId;
    private ImageView backView;
    private TextView titleView;
    private TextView rightView;
    private LinearLayout backLayout;
    public AlertDialog loadDialog = null;
    private int pageInt = 0;
    public CureInfoManage cureInfoManage;

    private int cure_model = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lookprint);

        cureInfoManage = (CureInfoManage) ServerHelper.getInstance().getModule(ServerHelper.MODULE_ID_CUREINFO);
        assert cureInfoManage != null;

        backLayout = (LinearLayout) findViewById(R.id.view_base_back_ll);
        backView = (ImageView) findViewById(R.id.view_base_back);
        titleView = (TextView) findViewById(R.id.view_base_title);
        rightView = (TextView) findViewById(R.id.view_right_title);
        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        titleView.setText("查看打印");

        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();

        //
        initTabs();
        //  mHandler.sendEmptyMessage(10);
    }

    public CureInfoManage getCureInfoManage() {
        if (cureInfoManage != null)
            return cureInfoManage;
        else {
            cureInfoManage = (CureInfoManage) ServerHelper.getInstance().getModule(ServerHelper.MODULE_ID_CUREINFO);
            return cureInfoManage;
        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 10: {
                    initTabs();
                    break;
                }
                case 20: {
                    dismissLoad();
                    break;
                }
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
//        showLoadDialog("正在加载病例");
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                dismissLoad();
//            }
//        }, 5000);
    }

    public void showLoadDialog(String title) {
        if (loadDialog == null) {
            loadDialog = new AlertDialog.Builder(this, R.style.AlertDialogCustom).create();
        }
        LayoutInflater inflater = LayoutInflater.from(this);
        View layout = inflater.inflate(R.layout.dialog_loading, null);
        TextView tv = (TextView) layout.findViewById(R.id.loading_tv);
        tv.setText(title);
        loadDialog.setView(layout);
        loadDialog.setCancelable(false);
        loadDialog.show();
    }

    public void dismissLoad() {
        if (loadDialog != null) {
            loadDialog.dismiss();
            loadDialog = null;
        }
    }

    private void initTabs() {
        mindicators = new ArrayList<PrintfViewFragemrnt>();

        cureNumber = getIntent().getExtras().getString("patientid");
        cureId = getIntent().getExtras().getString("curid");
        //默认标准模式
        cure_model = getIntent().getIntExtra("cure_model", 1);

        getCureInfoManage().getPatientAllCureDetail(cureNumber, new INormalEventListener() {
            @Override
            public void onValueEvent(int key, Object value) {
                if (value == null) {
                    LogUtils.e("获取所有病人病例数据为空");
                } else {
                    list = (List<DbCureDetailEntity>) value;
                    if (list.size() == 0) {
                        LogUtils.e("获取所有病人病例数据为空");
                    } else {
                        LogUtils.e("获取所有病人病例数据成功");
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initListData();
                            createFragment();
                        }
                    });
                }
            }

            @Override
            public void onErrEvent(int errCode) {
                LogUtils.e("获取所有病人病例数据失败");
            }
        });

    }

    private void initListData() {
        pageInt = list.size();

        if (!TextUtils.isEmpty(cureId)) {
            DbCureDetailEntity mEntity = null;
            for (int i = 0; i < list.size(); i++) {
                DbCureDetailEntity mEntity1 = list.get(i);
                if (mEntity1.getCureId().equals(cureId)) {
                    mEntity = mEntity1;
                    break;
                }
            }

            if (mEntity != null) {
                PrintfViewFragemrnt frm = new PrintfViewFragemrnt();
                frm.FrageMentInit(cureNumber, cureId, this, 0, 0, mEntity.getCure_model());
                pageInt = 1;
                mindicators.add(frm);
            }
        } else {
            if (list != null) {
                for (int i = 0; i < list.size(); i++) {
                    DbCureDetailEntity mEntity = list.get(i);
                    if (mEntity != null) {
                        PrintfViewFragemrnt frm = new PrintfViewFragemrnt();
                        frm.FrageMentInit(cureNumber, mEntity.getCureId(), this, pageInt, i + 1, mEntity.getCure_model());

                        mindicators.add(frm);
                    }
                }
            }
        }
    }

    public void loadPageOK() {
        mHandler.sendEmptyMessage(20);
    }

    private void createFragment() {
//        ViewFragment = new ViewPageFragment(mindicators, this);
//        Log.e("=======>数据", "====>总共有" + mindicators.size() + "个页面");
//        mFragmentTransaction.add(R.id.print_page_fragment, ViewFragment);
//        mFragmentTransaction.commit();

        mindicators2.add(new PrintFragment());
        ViewFragment2 = new ViewPageFragment2(mindicators2, this);
        Log.e("=======>数据", "====>总共有" + mindicators.size() + "个页面");
        mFragmentTransaction.add(R.id.print_page_fragment, ViewFragment);
        mFragmentTransaction.commit();
    }

}
