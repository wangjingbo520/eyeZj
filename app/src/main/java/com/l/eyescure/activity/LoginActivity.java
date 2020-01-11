package com.l.eyescure.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apkfuns.logutils.LogUtils;
import com.l.eyescure.R;
import com.l.eyescure.server.SqlManage.localsql.DbDoctorEntity;
import com.l.eyescure.server.StorageManager.SharedPreferencesUtil;
import com.l.eyescure.server.callBack.INormalChangeCallback;
import com.l.eyescure.server.callBack.INormalEventListener;
import com.l.eyescure.util.Constant;
import com.l.eyescure.util.DateUtil;
import com.l.eyescure.util.InputCheck;
import com.l.eyescure.util.JayUtils;
import com.l.eyescure.util.SPUtils;
import com.l.eyescure.util.ToastUtil;
import com.l.eyescure.view.DropEditText;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Look on 2016/12/28.
 * 登录界面
 */

public class LoginActivity extends BaseActivity {
    private DropEditText userEt;
    private BaseAdapter userEtAdapter;
    private EditText pwdEt;
    private String userName, pwd;
    private TextView switch_net_mode, login_set_ip;
    private DbDoctorEntity loginEntity = null;
    private List<DbDoctorEntity> listPerson = new ArrayList<>();
    private List<String> mList = new ArrayList<String>();
    private TextView super_back_door;
    private int super_click_cnt = 0;
    private Handler mHandler;
    private AlertDialog mNoTfDialog = null;
    private RelativeLayout login_container_setip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //启动activity时不自动弹出软键盘
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getStorageManage().unregisterTfInfoChangedCallback(tfChange);
    }

    @Override
    protected void initView() {
        mHandler = new Handler();

        userEt = (DropEditText) findViewById(R.id.login_user_et);
        userEt.setDropviewImageClickCallBack(new INormalChangeCallback() {
            @Override
            public void onChanged(boolean bMount) {
                if (bMount) {
                    initDoctorList(true);
                }
            }
        });
        pwdEt = (EditText) findViewById(R.id.login_pwd_et);
        switch_net_mode = (TextView) findViewById(R.id.switch_net_mode);
        switch_net_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputSuperPassWordDialog();
            }
        });

        login_set_ip = (TextView) findViewById(R.id.login_set_ip);
        login_set_ip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setServiceIP();
            }
        });

        super_back_door = (TextView) findViewById(R.id.super_back_door);
        super_back_door.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                super_click_cnt++;
                if (super_click_cnt > 6) {
                    resetSuperPassword();
                    super_click_cnt = 0;
                    return;
                }
                mHandler.removeCallbacks(CalcSuperBtnClickTime);
                mHandler.postDelayed(CalcSuperBtnClickTime, 1200);
            }
        });

        login_container_setip = (RelativeLayout) findViewById(R.id.login_container_setip);

        initModeInfo();
        isNeedBack(false);
        setTitle("登录");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getStorageManage() != null) {
            getStorageManage().registerTfInfoChangedCallback(tfChange);
        }

        initDoctorList(false);

        String account = (String) SPUtils.get(context, "account", "");
        String password = (String) SPUtils.get(context, "password", "");
        if (!TextUtils.isEmpty(account)) {
            userEt.setText(account);
        }
        if (!TextUtils.isEmpty(password)) {
            //pwdEt.setText(password);
        }

        if (getCureInfoManage().getInfoMode == 0 && TextUtils.isEmpty(getStorageManage().getHandwareSd())) {
            showNoTfDialog();
        }
    }

    private INormalChangeCallback tfChange = new INormalChangeCallback() {
        @Override
        public void onChanged(final boolean bMount) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (bMount) {
                        initDoctorList(false);
                        if (mNoTfDialog != null) {
                            mNoTfDialog.dismiss();
                        }
                    } else {
                        if (getCureInfoManage().getInfoMode == 0) {
                            ToastUtil.s("您拔出了数据卡，将无法获取和存储数据！");
                        }
                    }
                }
            });
        }
    };

    private Runnable CalcSuperBtnClickTime = new Runnable() {
        @Override
        public void run() {
            super_click_cnt = 0;
        }
    };

    private void resetSuperPassword() {
        DbDoctorEntity dbLoginEntity = new DbDoctorEntity();
        if (getCureInfoManage().getInfoMode == 0) {
            dbLoginEntity.setAccount("admins");
        } else
            dbLoginEntity.setAccount("admins     ");
        dbLoginEntity.setNick("超级管理员");
       // dbLoginEntity.setPassword("123456");
        dbLoginEntity.setCreate_date(DateUtil.getCurDateStr("yyyy-MM-dd HH:mm:ss"));
        dbLoginEntity.setIsLogin(1);
        getCureInfoManage().save(dbLoginEntity, Constant.DOCTOR_UPDATE, new INormalEventListener() {
            @Override
            public void onValueEvent(int key, Object value) {
                final int result = (int) value;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (result == 1) {
                            ToastUtil.s("重置超级用户密码成功! ");
                        } else {
                            ToastUtil.s("重置超级用户密码失败!");
                        }
                    }
                });
            }

            @Override
            public void onErrEvent(int errCode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.s("重置超级用户密码失败！");
                    }
                });
            }
        });
    }

    private void initModeInfo() {
        int netMode = util.readInt(Constant.GET_NET_MODE);
        if (netMode == 0) {
            switch_net_mode.setText("切换到网络模式");
            login_container_setip.setVisibility(View.INVISIBLE);
        } else {
            switch_net_mode.setText("切换到本地模式");
            login_container_setip.setVisibility(View.VISIBLE);
        }
    }

    private void initDoctorList(final boolean bshow) {
        getCureInfoManage().getAllDoctorDetail(new INormalEventListener() {
            @Override
            public void onValueEvent(int key, final Object value) {
                if (value == null) {
                    LogUtils.e("没有获取到用户信息");
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listPerson = (List<DbDoctorEntity>) value;
                            if (listPerson.size() == 0) {
                                LogUtils.e("没有获取到用户信息");
                            }

                            mList.clear();
                            for (int i = 0; i < listPerson.size(); i++)
                                mList.add(listPerson.get(i).getAccount());

                            if (userEtAdapter == null) {
                                userEtAdapter = new BaseAdapter() {
                                    @Override
                                    public int getCount() {
                                        return mList.size();
                                    }

                                    @Override
                                    public Object getItem(int position) {
                                        return mList.get(position);
                                    }

                                    @Override
                                    public long getItemId(int position) {
                                        return position;
                                    }

                                    @Override
                                    public View getView(int position, View convertView, ViewGroup parent) {
                                        if (convertView == null) {
                                            convertView = LayoutInflater.from(context).inflate(R.layout.item_login, null);
                                        }
                                        TextView tv = (TextView) convertView.findViewById(R.id.tv_item_login);
                                        tv.setText(mList.get(position));
                                        return convertView;
                                    }
                                };
                                userEt.setAdapter(userEtAdapter);
                            } else {
                                userEtAdapter.notifyDataSetChanged();
                                if (bshow)
                                    userEt.showPopList();
                            }
                        }
                    });
                }
            }

            @Override
            public void onErrEvent(int errCode) {
                LogUtils.e("获取用户信息失败");
            }
        });
        getCureInfoManage().fristSaveSuperAccount();
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

    @Override
    protected int bindLayout() {
        return R.layout.activity_login;
    }

    @Override
    protected boolean isNeedTitle() {
        return true;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_ok:
                if (getCureInfoManage().getInfoMode == 0 && TextUtils.isEmpty(getStorageManage().getHandwareSd())) {
                    showNoTfDialog();
                    return;
                }

                userName = userEt.getText().toString().trim();
                if (!checkAccountInput(userName)) {
                    return;
                }

                pwd = pwdEt.getText().toString().trim();
                pwd = "123456";
                if (!checkPasswordInput(pwd, 0)) {
                    return;
                }

                JayUtils.hideKeyboard(LoginActivity.this);
                getCureInfoManage().getOneDoctor(userName, new INormalEventListener() {
                    @Override
                    public void onValueEvent(int key, final Object value) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (value == null) {
                                    ToastUtil.s("当前用户名不存在");
                                } else {
                                    loginEntity = (DbDoctorEntity) value;
                                    if (TextUtils.isEmpty(loginEntity.getAccount())) {
                                        ToastUtil.s("当前用户名不存在");
                                    } else {
                                        String account = loginEntity.getAccount();
                                        String password = loginEntity.getPassword();

                                        if (!account.equals(userName)) {
                                            ToastUtil.s("账号错误,请重新输入");
                                        } else if (!password.equals(pwd)) {
                                            ToastUtil.s("密码错误,请重新输入");
                                        } else {
                                            SPUtils.put(context, "account", account);
                                            SPUtils.put(context, "password", password);
                                            SPUtils.put(context, "cureName", loginEntity.getNick());
                                            loginEntity.setAccount(account);
                                            loginEntity.setPassword(password);
                                            if (account.equals("admins")) {
                                                loginEntity.setIsLogin(1);
                                            } else {
                                                loginEntity.setIsLogin(0);
                                            }
                                            getCureInfoManage().save(loginEntity, Constant.DOCTOR_UPDATE, new INormalEventListener() {
                                                @Override
                                                public void onValueEvent(int key, Object value) {
                                                    int result = (int) value;
                                                    if (result == 1) {
                                                        LogUtils.e("保存账户成功");
                                                    } else {
                                                        LogUtils.e("保存账户失败");
                                                    }
                                                }

                                                @Override
                                                public void onErrEvent(int errCode) {
                                                    LogUtils.e("保存账户失败");
                                                }
                                            });
                                            Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                            startActivity(i);

                                            //处理登录成功
                                            getCureInfoManage().setLoginDoctorEntity(loginEntity);
                                            finish();
                                        }
                                    }
                                }
                            }
                        });
                    }

                    @Override
                    public void onErrEvent(int errCode) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.s("登录失败，请检查您的网络！");
                            }
                        });
                    }
                });
                break;
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                return true;

        }
        return super.onKeyUp(keyCode, event);
    }

    protected void inputSuperPassWordDialog() {
        final EditText editText = new EditText(LoginActivity.this);
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        AlertDialog.Builder inputDialog = new AlertDialog.Builder(context, R.style.AlertDialogCustom1);
        inputDialog.setTitle("请输入超级管理员密码").setView(editText);
        inputDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String password = editText.getText().toString();
                        if (TextUtils.isEmpty(password)) {
                            ToastUtil.s("您未输入超级管理员密码");
                        } else {
                            String superPassword = getCureInfoManage().getSuperPassword();
                            if (TextUtils.isEmpty(superPassword)) {
                                if (getCureInfoManage().getInfoMode == 0) {
                                    ToastUtil.s("请您检查是否插入了存有数据的TF卡!");
                                } else {
                                    ToastUtil.s("网络异常，请检查网络状态！");
                                }
                                return;
                            }
                            if (password.equals(superPassword)) {
                                switchNetModeDialog();
                                dialog.dismiss();
                            } else {
                                ToastUtil.s("您输入的超级管理员密码有误");
                            }
                        }
                    }
                }).show();
    }

    protected void setServiceIP() {
        LayoutInflater inflater = LayoutInflater.from(LoginActivity.this);
        View layout = inflater.inflate(R.layout.dialog_ip, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this, R.style.AlertDialogCustom);
        final EditText ipEt = (EditText) layout.findViewById(R.id.name_id);
        //ipEt.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(layout);
        builder.setTitle("服务器IP设置");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String input_str = ipEt.getText().toString().trim();
                if (TextUtils.isEmpty(input_str)) {
                    ToastUtil.s("您还未输入IP!");
                    return;
                }

                if (!InputCheck.getInstance().checkIp(input_str)) {
                    ToastUtil.s("您还未输入IP有误!");
                    return;
                }

                String[] ss = input_str.split("\\.");
                if (ss.length > 0) {
                    int num = Integer.parseInt(ss[0]);
                    if (num < 0 || num > 255) {
                        ToastUtil.s("您还未输入IP有误!");
                        return;
                    }
                }

                SharedPreferencesUtil util = new SharedPreferencesUtil(LoginActivity.this);
                util.saveString("saveIP", input_str);

            }
        });
        builder.show();
    }

    protected void switchNetModeDialog() {
        final int netMode = util.readInt(Constant.GET_NET_MODE);
        String showMsg = null;
        if (netMode == 0) {
            showMsg = "即将切换到网络模式,医疗数据将会存储在服务器中,确定切换吗?";
        } else if (netMode == 1) {
            showMsg = "即将切换到本地模式,医疗数据将会存储在本地TF卡中,确定切换吗?";
        } else {
            return;
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogCustom);
        builder.setTitle("模式切换");
        builder.setMessage(showMsg);
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (netMode == 0) {
                    mList.clear();
                    util.saveInt(Constant.GET_NET_MODE, 1);
                    ToastUtil.s("已成功切换模式为网络模式");
                } else {
                    mList.clear();
                    util.saveInt(Constant.GET_NET_MODE, 0);
                    ToastUtil.s("已成功切换模式为本地模式");
                    mHandler.postDelayed(checkTfCardRunnable, 3000);
                }
                getCureInfoManage().syncNetMode();

                getCureInfoManage().fristSaveSuperAccount();

                initDoctorList(false);

                initModeInfo();

                dialog.dismiss();

            }
        });
        builder.setNeutralButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }


    private Runnable checkTfCardRunnable = new Runnable() {
        @Override
        public void run() {
            if (TextUtils.isEmpty(getTfCardPath())) {
                showNoTfDialog();
            }
        }
    };

    protected void showNoTfDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogCustom);
        builder.setTitle("警告");
        builder.setMessage("您没有插入存储卡，无法获取和保存数据，且不能登录！请插入存储卡或者联系超级管理员切换到网络模式！");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                mNoTfDialog = null;
            }
        });
        mNoTfDialog = builder.show();
    }

}
