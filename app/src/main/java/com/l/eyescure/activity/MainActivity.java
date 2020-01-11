package com.l.eyescure.activity;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.apkfuns.logutils.LogUtils;
import com.l.eyescure.R;
import com.l.eyescure.activity.adapter.CureListAdapter;
import com.l.eyescure.application.MyApplication;
import com.l.eyescure.server.SqlManage.localsql.DbCureDetailEntity;
import com.l.eyescure.server.SqlManage.localsql.DbDoctorEntity;
import com.l.eyescure.server.SqlManage.localsql.DbPatientEntity;
import com.l.eyescure.server.callBack.INormalChangeCallback;
import com.l.eyescure.server.callBack.INormalEventListener;
import com.l.eyescure.util.Constant;
import com.l.eyescure.util.SPUtils;
import com.l.eyescure.util.ToastUtil;
import com.l.eyescure.view.TopBarView;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class MainActivity extends BaseActivity {
    private static String TAG = MainActivity.class.getSimpleName();
    @BindView(R.id.main_view_top)
    TopBarView viewTop;
    @BindView(R.id.main_listView)
    ListView mainListView;

    private CureListAdapter adapter;
    private List<DbPatientEntity> userList = new ArrayList<>();
    private Context context;
    private List<DbCureDetailEntity> detailEntities = new ArrayList<>();

    private boolean isCure = false;//是否开始治疗
    private String cureNumber = null;
    private Handler mHandler = null;

    @Override
    public int bindLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected boolean isNeedTitle() {
        return false;
    }

    @Override
    protected void initView() {
        context = this;
        mHandler = new Handler();

        setListener();
        Logger.init(TAG);
        setTopBarView(viewTop);

        util.saveString(Constant.CUR_DETAIL_SAVE_NUM, "");
        util.saveString(Constant.CUR_DETAIL_SAVE_ID, "");
    }

    private INormalChangeCallback tfChange = new INormalChangeCallback() {
        @Override
        public void onChanged(final boolean bMount) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (bMount) {
                        refreshSdInfo();
                    } else {
                        if (getCureInfoManage().getInfoMode == 0) {
                            ToastUtil.s("您拔出了数据卡，将无法获取和存储数据！");
                            userList.clear();
                            if (adapter != null)
                                adapter.notifyDataSetChanged();
                        }
                    }
                }
            });
        }
    };

    private void refreshSdInfo() {
        final String curName = (String) SPUtils.get(context, "account", "");//当前登录账号
        getCureInfoManage().getOneDoctor(curName, new INormalEventListener() {
            @Override
            public void onValueEvent(int key, Object value) {
                if (value == null) {
                    LogUtils.e("未获取到医生数据");
                } else {
                    final DbDoctorEntity entity = (DbDoctorEntity) value;
                    if (TextUtils.isEmpty(entity.getAccount())) {
                        LogUtils.e("未获取到医生数据");
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                boolean bSuper = false;
                                if (!TextUtils.isEmpty(curName)) {
                                    if (curName.equals("admins"))
                                        bSuper = true;
                                }
                                String nick = entity.getNick();
                                viewTop.setUserTv(nick, bSuper);
                                viewTop.setEnabled(false);

                                MyApplication.getInstance().loginerNick = nick;
                                MyApplication.getInstance().loginerIsSuper = bSuper;
                            }
                        });
                    }
                }
            }

            @Override
            public void onErrEvent(int errCode) {
                LogUtils.e("未获取到医生数据");
            }
        });

        setData();
    }

    @Override
    protected void onResume() {
        super.onResume();

        showLoadDialog("正在加载病例数据...");
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dismissLoad();
            }
        }, 5000);
        setData();
        if (getStorageManage() != null) {
            getStorageManage().registerTfInfoChangedCallback(tfChange);
            refreshSdInfo();
        }
    }

    @Override
    protected int topHelp() {
        showHelpDialog();
        return 0;
    }

    @Override
    protected boolean topExit() {
        showAlertDialog();
        return false;
    }

    @Override
    protected void topSet() {
        setBtnClick();
    }

    @Override
    protected void topCare() {
        ToastUtil.s("你需要先选定一个病人，才能开始治疗！");
    }

    @Override
    protected void topFile() {
        fileBtnClick();
    }

    @Override
    protected void topPrintf() {
        String userNum = util.readString(Constant.CUR_DETAIL_SAVE_NUM);
        if (!TextUtils.isEmpty(userNum)) {
            clickPritfBtn();
        } else {
            ToastUtil.s("你需要先选定一个病人，才能查看打印病例！");
        }
    }

    @Override
    protected void topUser() {
        if (getCureInfoManage().isSuperLogin()) {
            readyGo(AccountManagerActivity.class);
        }
    }

    protected void showHelpDialog() {
        String showStr = "退出按钮可以退出当前用户登录，切换其他用户登录。\n" +
                "设置按钮可以进行密码，打印机，时间，wifi等选项的设置。\n" +
                "档案按钮可以进入病人档案管理界面，可以增加或者查询病人档案。\n" +
                "点击治疗按钮和查看/打印前，您需要先选定一个病人。\n" +
                "点击病例列表中的选项，可以进入病例详情界面，可以在详情界面进行预览治疗记录，或者再次治疗。\n" +
                "如果你是超级管理员(admin账号)，你可以对设备用户进行管理，方式是点击最右侧的名称按钮。";
        showHelpDialog(showStr);
    }

    private void setData() {
        if (getCureInfoManage() == null) {
            return;
        }
        String doctorAccount = getCureInfoManage().getLoginerAccount();
        getCureInfoManage().getDoctorAllPatientDetail(doctorAccount, new INormalEventListener() {
            @Override
            public void onValueEvent(int key, Object value) {
                if (value == null) {
                    LogUtils.e("获取到医生的病人列表为空");
                } else {
                    userList = (List<DbPatientEntity>) value;
                    if (userList.size() == 0) {
                        LogUtils.e("获取到医生的病人列表为空");
                    }
                    sortListByActive(userList);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter = new CureListAdapter(context, userList);
                            mainListView.setAdapter(adapter);
                        }
                    });
                }
                dismissLoad();
            }

            @Override
            public void onErrEvent(int errCode) {
                LogUtils.e("获取到医生的病人列表为空");
                dismissLoad();
            }
        });
    }

    private void sortListByActive(List<DbPatientEntity> patients) {
        String activeID = MyApplication.getInstance().activePatientId;
        if (TextUtils.isEmpty(activeID)) {
            return;
        }

        for (int i = 0; i < patients.size(); i++) {
            DbPatientEntity ent = patients.get(i);
            if (ent != null && !TextUtils.isEmpty(ent.getNumber()) && (ent.getNumber()).equals(activeID)) {
                patients.add(0, patients.remove(i));
            }
        }
    }

    private void setListener() {
        mainListView.setOnItemClickListener(onItemListener);
//        mainListView.setOnItemLongClickListener(onItemLongClickListener);
    }

    private AdapterView.OnItemClickListener onItemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            adapter.setSelectItem(position);
//            adapter.notifyDataSetInvalidated();
//            isCure = true;
            cureNumber = userList.get(position).getNumber();
            Bundle bundle = new Bundle();
            bundle.putString("cureNumber", cureNumber);
            readyGo(PatientDetailActivity.class, bundle);
        }
    };

    private AdapterView.OnItemLongClickListener onItemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            showDeleteDialog();
            return true;
        }
    };

    protected void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogCustom);
        builder.setTitle("警告");
        builder.setMessage("确定删除当前病例吗？");
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
//                if (id != -1) {
//                    MyDbUtils.getInstance().deleteUserDetail(id);
//                    ToastUtil.s("删除病例完成");
//                    finish();
//                }
            }
        });
        builder.show();
    }


    @Override
    protected void onDestroy() {
        getStorageManage().unregisterTfInfoChangedCallback(tfChange);
        super.onDestroy();
    }
}
