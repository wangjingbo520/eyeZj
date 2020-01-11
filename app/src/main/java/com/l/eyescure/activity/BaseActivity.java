package com.l.eyescure.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.l.eyescure.R;
import com.l.eyescure.application.MyApplication;
import com.l.eyescure.server.ServerHelper;
import com.l.eyescure.server.SqlManage.localsql.DbCureDetailEntity;
import com.l.eyescure.server.StorageManager.SharedPreferencesUtil;
import com.l.eyescure.server.StorageManager.StorageManager;
import com.l.eyescure.server.callBack.INormalEventListener;
import com.l.eyescure.server.cureManage.CureInfoManage;
import com.l.eyescure.server.portManage.MessageManage;
import com.l.eyescure.server.printManager.PrintManager;
import com.l.eyescure.util.Constant;
import com.l.eyescure.util.InputCheck;
import com.l.eyescure.util.SPUtils;
import com.l.eyescure.util.ToastUtil;
import com.l.eyescure.view.TopBarView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;

/**
 * Created by Look on 2017/3/5.
 */

public abstract class BaseActivity extends Activity {
    public Context context;
    private ImageView backView;
    private TextView titleView;
    private TextView rightView;
    private LinearLayout backLayout;
    public AlertDialog loadDialog = null;
    protected MyApplication mApplication;
    public TopBarView viewTop;
    public SharedPreferencesUtil util;
    public boolean bLoadingShow = false;
    private CureInfoManage cureInfoManage;
    private StorageManager mStorage;
    private MessageManage messageManage;
    private PrintManager printManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(bindLayout());
        ButterKnife.bind(this);

        cureInfoManage = (CureInfoManage) ServerHelper.getInstance().getModule(ServerHelper.MODULE_ID_CUREINFO);
        assert cureInfoManage != null;

        mStorage = (StorageManager) ServerHelper.getInstance().getModule(ServerHelper.MODULE_ID_STORAGE);
        assert mStorage != null;

        messageManage = (MessageManage) ServerHelper.getInstance().getModule(ServerHelper.MODULE_ID_INITPORT);
        assert messageManage != null;

        printManager = (PrintManager) ServerHelper.getInstance().getModule(ServerHelper.MODULE_ID_PRINT);
        assert (printManager != null);

        context = this;
        mApplication = MyApplication.getInstance();
        util = new SharedPreferencesUtil(this);

        if (isNeedTitle()) {
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
        }

        initView();
    }

    public CureInfoManage getCureInfoManage() {
        if (cureInfoManage != null)
            return cureInfoManage;
        else {
            cureInfoManage = (CureInfoManage) ServerHelper.getInstance().getModule(ServerHelper.MODULE_ID_CUREINFO);
            return cureInfoManage;
        }
    }

    public StorageManager getStorageManage() {
        if (mStorage != null)
            return mStorage;
        else {
            mStorage = (StorageManager) ServerHelper.getInstance().getModule(ServerHelper.MODULE_ID_STORAGE);
            return mStorage;
        }
    }

    public MessageManage getMessageManage() {
        if (messageManage != null)
            return messageManage;
        else {
            messageManage = (MessageManage) ServerHelper.getInstance().getModule(ServerHelper.MODULE_ID_INITPORT);
            return messageManage;
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

    private void refreshLoginNick() {
        String nick = MyApplication.getInstance().loginerNick;
        boolean bSuper = MyApplication.getInstance().loginerIsSuper;
        if (!TextUtils.isEmpty(nick) && viewTop != null) {
            viewTop.setUserTv(nick, bSuper);
            viewTop.setEnabled(false);
        }
    }

    @Override
    protected void onStop() {
        if (bLoadingShow) {
            bLoadingShow = false;
            dismissLoad();
        }
        super.onStop();
    }

    public void setTopBarView(TopBarView mView) {
        viewTop = mView;
        if (viewTop != null) {
            viewTop.setListeners(new NoDoubleClickListener());
            refreshLoginNick();
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
                    case R.id.help_btn://帮助
                        topHelp();
                        break;
                    case R.id.exit_btn://退出
                        topExit();
                        break;
                    case R.id.set_btn://设置
                        topSet();
                        break;
                    case R.id.file_btn://档案
                        topFile();
                        break;
                    case R.id.cure_btn://治疗
                        topCare();
                        break;
                    case R.id.print_btn://查看打印
                        topPrintf();
                        break;
                    case R.id.user_btn://查看打印
                        topUser();
                        break;

                }
            }
        }
    }

    /**
     * [绑定布局]
     *
     * @return
     */
    protected abstract int bindLayout();

    protected abstract boolean isNeedTitle();

    protected abstract void initView();

    protected abstract int topHelp();

    protected abstract boolean topExit();

    protected abstract void topSet();

    protected abstract void topCare();

    protected abstract void topFile();

    protected abstract void topPrintf();

    protected abstract void topUser();

    protected void isNeedBack(boolean need) {
        if (backLayout != null) {
            if (need) {
                backLayout.setVisibility(View.VISIBLE);
            } else {
                backLayout.setVisibility(View.GONE);
            }
        }
    }

    public void setTitle(String text) {
        if (titleView != null) {
            titleView.setText(text);
        }
    }

    public void setRightText(String text) {
        if (rightView != null) {
            rightView.setText(text);
        }
    }

    protected void showHelpDialog(String showMsg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogCustom);
        builder.setTitle("使用帮助");
        builder.setMessage(showMsg);
        builder.setNegativeButton("关闭", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();

//        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                |View.SYSTEM_UI_FLAG_FULLSCREEN;
//        SettingActivity.this.getWindow().getDecorView().setSystemUiVisibility(uiOptions);
//        builder.create().getWindow().getDecorView().setSystemUiVisibility(uiOptions);
    }

    public void setBtnClick() {
        Intent ii = new Intent(context, SettingActivity.class);
        startActivity(ii);
    }

    public void fileBtnClick() {
        Intent iii = new Intent(context, FileActivity.class);
        startActivity(iii);
    }

    public void setRightOnClick(View.OnClickListener onClick) {
        if (rightView != null) {
            rightView.setOnClickListener(onClick);
        }
    }

    public void clickPritfBtn() {
        final String cureNum = util.readString(Constant.CUR_DETAIL_SAVE_NUM);
        if (TextUtils.isEmpty(cureNum)) {
            ToastUtil.s("你需要先选定一个病人，才能查看打印病例！");
            return;
        }

        getCureInfoManage().getPatientAllCureDetail(cureNum, new INormalEventListener() {
            @Override
            public void onValueEvent(final int key, final Object value) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (key == 1) {
                            List<DbCureDetailEntity> cureList = (List<DbCureDetailEntity>) value;
                            if (cureList != null && cureList.size() > 0) {
                                Intent intent = new Intent(context, LookPrintActivity.class);
                                intent.putExtra("patientid", cureNum);
                                intent.putExtra("curid", "");
                                startActivity(intent);
                            } else {
                                ToastUtil.s("当前病人没有治疗记录！");
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
                        ToastUtil.s("当前病人没有治疗记录！");
                    }
                });
            }
        });
    }

    protected void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogCustom);
        builder.setTitle("提示");
        builder.setMessage("确定退出吗？");
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
                SPUtils.remove(BaseActivity.this, "password");
                Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        builder.show();
    }

    protected void showStopDialog(String title, DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogCustom);
        builder.setTitle(title);
        builder.setMessage("警告，如果按停止键，治疗程序将不能重新开始，需要更换治疗头后，才可重新开始");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("确定", onClickListener);
        builder.show();
    }

    protected void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogCustom);
        builder.setTitle("档案");
        builder.setMessage("是否新增病人档案？");
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
                Intent intent = new Intent(BaseActivity.this, AddPatientActivity.class);
                startActivity(intent);
            }
        });
        builder.show();
    }

    public void showLoadDialog(String title) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.dialog_loading, null);
        TextView tv = (TextView) layout.findViewById(R.id.loading_tv);
        tv.setText(title);
        if (loadDialog == null) {
            loadDialog = new AlertDialog.Builder(context, R.style.AlertDialogCustom).create();

            loadDialog.setView(layout);
            loadDialog.setCancelable(false);
            loadDialog.show();
        }
    }

    public void dismissLoad() {
        if (loadDialog != null) {
            loadDialog.dismiss();
            loadDialog = null;
        }
    }

    public void toggleHideyBar() {
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // BEGIN_INCLUDE (get_current_ui_flags)
        // The UI options currently enabled are represented by a bitfield.
        // getSystemUiVisibility() gives us that bitfield.
//        int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
//        int newUiOptions = uiOptions;
//        // END_INCLUDE (get_current_ui_flags)
//        // BEGIN_INCLUDE (toggle_ui_flags)
//        boolean isImmersiveModeEnabled =
//                ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
//        if (isImmersiveModeEnabled) {
//            Logger.i("Turning immersive mode mode off. ");
//        } else {
////            Logger.i("123", "Turning immersive mode mode on.");
//        }
//
//        // Navigation bar hiding:  Backwards compatible to ICS.
//        if (Build.VERSION.SDK_INT >= 14) {
//            newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
//        }
//
//        // Status bar hiding: Backwards compatible to Jellybean
//        if (Build.VERSION.SDK_INT >= 16) {
//            newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
//        }
//
//        // Immersive mode: Backward compatible to KitKat.
//        // Note that this flag doesn't do anything by itself, it only augments the behavior
//        // of HIDE_NAVIGATION and FLAG_FULLSCREEN.  For the purposes of this sample
//        // all three flags are being toggled together.
//        // Note that there are two immersive mode UI flags, one of which is referred to as "sticky".
//        // Sticky immersive mode differs in that it makes the navigation and status bars
//        // semi-transparent, and the UI flag does not get cleared when the user interacts with
//        // the screen.
//        if (Build.VERSION.SDK_INT >= 18) {
//            newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
//        }

//         getWindow().getDecorView().setSystemUiVisibility(newUiOptions);//上边状态栏和底部状态栏滑动都可以调出状态栏
        getWindow().getDecorView().setSystemUiVisibility(4108);//这里的4108可防止从底部滑动调出底部导航栏
        //END_INCLUDE (set_ui_flags)
    }

    /**
     * 关闭Android导航栏，实现全屏
     */
    private void closeBar() {
        try {
            String command;
            command = "LD_LIBRARY_PATH=/vendor/lib:/system/lib service call activity 42 s16 com.android.systemui";
            ArrayList<String> envlist = new ArrayList<String>();
            Map<String, String> env = System.getenv();
            for (String envName : env.keySet()) {
                envlist.add(envName + "=" + env.get(envName));
            }
            String[] envp = envlist.toArray(new String[0]);
            Process proc = Runtime.getRuntime().exec(
                    new String[]{"su", "-c", command}, envp);
            proc.waitFor();
        } catch (Exception ex) {
            // Toast.makeText(getApplicationContext(), ex.getMessage(),
            // Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 显示导航栏
     */
    public static void showBar() {
        try {
            String command;
            command = "LD_LIBRARY_PATH=/vendor/lib:/system/lib am startservice -n com.android.systemui/.SystemUIService";
            ArrayList<String> envlist = new ArrayList<String>();
            Map<String, String> env = System.getenv();
            for (String envName : env.keySet()) {
                envlist.add(envName + "=" + env.get(envName));
            }
            String[] envp = envlist.toArray(new String[0]);
            Process proc = Runtime.getRuntime().exec(
                    new String[]{"su", "-c", command}, envp);
            proc.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 界面跳转
     *
     * @param clazz 目标Activity
     */
    protected void readyGo(Class<?> clazz) {
        readyGo(clazz, null);
    }

    /**
     * 跳转界面，  传参
     *
     * @param clazz  目标Activity
     * @param bundle 数据
     */
    protected void readyGo(Class<?> clazz, Bundle bundle) {
        Intent intent = new Intent(this, clazz);
        if (null != bundle)
            intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * 跳转界面并关闭当前界面
     *
     * @param clazz 目标Activity
     */
    protected void readyGoThenKill(Class<?> clazz) {
        readyGoThenKill(clazz, null);
    }

    /**
     * @param clazz  目标Activity
     * @param bundle 数据
     */
    protected void readyGoThenKill(Class<?> clazz, Bundle bundle) {
        readyGo(clazz, bundle);
        finish();
    }

    /**
     * startActivityForResult
     *
     * @param clazz       目标Activity
     * @param requestCode 发送判断值
     */
    protected void readyGoForResult(Class<?> clazz, int requestCode) {
        Intent intent = new Intent(this, clazz);
        startActivityForResult(intent, requestCode);
    }

    /**
     * startActivityForResult with bundle
     *
     * @param clazz       目标Activity
     * @param requestCode 发送判断值
     * @param bundle      数据
     */
    protected void readyGoForResult(Class<?> clazz, int requestCode, Bundle bundle) {
        Intent intent = new Intent(this, clazz);
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }

    /**
     * 保留1位小数，四舍五入的一个老土的方法
     *
     * @param d
     * @return
     */
    protected double formatDouble(double d) {
        return (double) Math.round(d * 10) / 10;
    }

    public boolean checkAccountInput(String userName) {
        if (userName.equals("admins")) {
            return true;
        }
        if (TextUtils.isEmpty(userName)) {
            ToastUtil.s("您需要输入帐号！");
            return false;
        } else {
            boolean bRightInput = InputCheck.getInstance().checkInputAccount(userName);
            if (!bRightInput) {
                ToastUtil.s("请输入正确的帐号！帐号由11位的数字组成。");
                return false;
            }
        }
        return true;
    }


    public boolean checkPasswordInput(String password, int type) {  //type: 0  登录密码  1 重复密码  2 新密码  3 旧密码
        if (TextUtils.isEmpty(password)) {
            switch (type) {
                case 0:
                    ToastUtil.s("您需要输入密码！");
                    break;
                case 1:
                    ToastUtil.s("您需要输入重复密码！");
                    break;
                case 2:
                    ToastUtil.s("您需要输入新的密码！");
                    break;
                case 3:
                    ToastUtil.s("您需要输入旧密码！");
                    break;
            }
            return false;
        } else {
            boolean bRightInput = InputCheck.getInstance().checkInputPassword(password);
            if (!bRightInput) {
                switch (type) {
                    case 0:
                        ToastUtil.s("请输入正确的密码！密码由6-18位的数字、字母组成。");
                        break;
                    case 1:
                        ToastUtil.s("请输入正确的重复密码！密码由6-18位的数字、字母组成。");
                        break;
                    case 2:
                        ToastUtil.s("请输入正确的新密码！密码由6-18位的数字、字母组成。");
                        break;
                    case 3:
                        ToastUtil.s("请输入正确的旧密码！密码由6-18位的数字、字母组成。");
                        break;
                }
                return false;
            }
        }
        return true;
    }

    public boolean checkNickInput(String nick) {
        if (TextUtils.isEmpty(nick)) {
            ToastUtil.s("您需要设置昵称！");
            return false;
        } else {
            boolean bRightInput = InputCheck.getInstance().checkInputNick(nick);
            if (!bRightInput) {
                ToastUtil.s("您设置的昵称需要由1-24位的数字、字母、汉字及下划线组成(一个汉字占3位)！");
                return false;
            }
        }
        return true;
    }

    public String getTfCardPath() {
        String sdPath = "";
        if (getStorageManage() != null) {
            sdPath = getStorageManage().getHandwareSd();
        }
        return sdPath;
    }

}
