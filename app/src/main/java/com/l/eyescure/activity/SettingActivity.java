package com.l.eyescure.activity;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.apkfuns.logutils.LogUtils;
import com.l.eyescure.R;
import com.l.eyescure.server.SqlManage.localsql.DbDoctorEntity;
import com.l.eyescure.server.StorageManager.SharedPreferencesUtil;
import com.l.eyescure.server.callBack.INormalEventListener;
import com.l.eyescure.util.Constant;
import com.l.eyescure.util.FileUtils;
import com.l.eyescure.util.SPUtils;
import com.l.eyescure.util.ToastUtil;
import com.l.eyescure.view.TopBarView;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by luka on 2017-03-13.
 */
public class SettingActivity extends BaseActivity {
    @BindView(R.id.setting_user_manage)
    TextView settingUserManage;
    @BindView(R.id.setting_pwd_manage)
    TextView settingPwdManage;
    @BindView(R.id.setting_printer_info)
    TextView setting_printer_info;
    @BindView(R.id.setting_printer_type)
    TextView setting_printer_type;

    @BindView(R.id.setting_save1_manage)
    TextView setting_save1;
    @BindView(R.id.setting_save2_manage)
    TextView setting_save2;

    @BindView(R.id.setting_wifi)
    TextView setting_wifi;
    @BindView(R.id.setting_time_data)
    TextView setting_time_data;

    @BindView(R.id.set_view_top)
    TopBarView viewTop;

    private DbDoctorEntity dbLoginEntity = null;
    private int printerType = 0;

    @Override
    protected boolean isNeedTitle() {
        return false;
    }

    @Override
    protected void initView() {
        setTitle("设置");
        setTopBarView(viewTop);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getPrintManager() != null)
            printerType = getPrintManager().getPrinterType();

        if (printerType == 1) {
            setting_printer_info.setVisibility(View.GONE);
        } else if (printerType == 0) {
            setting_printer_info.setVisibility(View.VISIBLE);
        }

        String account = SPUtils.get(this, "account", "").toString();
		if (getCureInfoManage() != null){
        getCureInfoManage().getOneDoctor(account, new INormalEventListener() {
            @Override
            public void onValueEvent(int key, Object value) {
                if (value == null) {
                    LogUtils.e("获取到用户数据为空");
                } else {
                    dbLoginEntity = (DbDoctorEntity) value;
                    if (TextUtils.isEmpty(dbLoginEntity.getAccount())) {
                        LogUtils.e("获取到用户数据为空");
                    }
                }
            }

            @Override
            public void onErrEvent(int errCode) {
                LogUtils.e("获取用户数据失败");
            }
        });
		}
    }

    @Override
    protected int topHelp() {
        showHelpDialog();
        return 0;
    }

    @Override
    protected boolean topExit() {
        finish();
        return false;
    }

    @Override
    protected void topSet() {

    }

    @Override
    protected void topCare() {
        ToastUtil.s("在进行系统设置的时候，不能进行治疗！");
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
        String showStr = "密码管理会重设用户的登陆密码，需要旧密码。\n" +
                "如果你忘记了登陆密码，请联系管理员，重置你的密码。\n" +
                "打印机选择，当有多个打印机可设置时，你需要选定一个。\n" +
                "打印机信息，当你选择爱普生打印机后，点开可看到打印机的相关信息。\n" +
                "时间设置，设置系统时间。\n" +
                "wifi设置，设置连接可用wifi。";
        showHelpDialog(showStr);
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_setting;
    }

    @OnClick({R.id.setting_user_manage, R.id.setting_pwd_manage, R.id.setting_printer_info, R.id.setting_printer_type, R.id.setting_save2_manage, R.id.setting_save1_manage, R.id.setting_wifi, R.id.setting_time_data})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.setting_user_manage://用户管理
                if (dbLoginEntity.getIsLogin() == 1) {
                    Intent ii = new Intent(SettingActivity.this, AccountManagerActivity.class);
                    startActivity(ii);
                } else {
                    ToastUtil.s("当前用户不是管理员");
                }
                break;
            case R.id.setting_pwd_manage: {//密码管理
                Intent i = new Intent(SettingActivity.this, ChangePwdActivity.class);
                startActivity(i);
                break;
            }
            case R.id.setting_save1_manage: {//导出文档设定
                Intent i = new Intent(SettingActivity.this, SetSavePdfPathActivity.class);
                i.putExtra("savetype", 1);
                startActivity(i);
                break;
            }
            case R.id.setting_save2_manage: {//保存文档设定
                Intent i = new Intent(SettingActivity.this, SetSavePdfPathActivity.class);
                i.putExtra("savetype", 0);
                startActivity(i);
                break;
            }
            case R.id.setting_printer_info://打印机信息
                Intent i = new Intent(SettingActivity.this, PrintInfoActivity.class);
                startActivity(i);

//                if (PrintUtils.isSend2PrinterInstalled(context) == false){
//                    AlertDialog dlg = new AlertDialog.Builder(context,R.style.AlertDialogCustom )
//                            .setTitle("提示")
//                            .setMessage("如果需要打印功能，则必须安装打印程序")
//                            .setPositiveButton( android.R.string.ok, new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick( DialogInterface dialog, int which )
//                                {
//                                    showLoadDialog("加载中....");
//                                    assetsDataToSD();
//                                }
//                            } )
//                            .show();
//
//                }else{
//                    ToastUtil.s("已安装打印程序，请直接打印");
//                }
//                showPrintDialog();
                break;
            case R.id.setting_printer_type: {
                showSingleChoiceDialog();
                break;
            }
            case R.id.setting_wifi: {
                startWifiSetPage();
                break;
            }
            case R.id.setting_time_data: {
                startTimeSetPage();
                break;
            }
        }
    }

    //参照源码实现弹框样式时间设置界面
    private void startTimeSetPage() {
        Intent it = new Intent();
        it.setAction("android.settings.DATE_SETTINGS");
        startActivity(it);
    }

    private void startWifiSetPage() {
        Intent it = new Intent();
        ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.wifi.WifiSettings");
        it.setComponent(cn);
        startActivity(it);
    }

    /**
     * 将assets中文件复制到SD卡中
     */
    public void assetsDataToSD() {
        FileUtils.getInstance(SettingActivity.this)
                .copyAssetsToSD("SendPrinter.apk", "/SendPrinter.apk")
                .setFileOperateCallback(new FileUtils.FileOperateCallback() {
                    @Override
                    public void onSuccess() {
                        dismissLoad();
                        String path = Environment.getExternalStorageDirectory() + "/SendPrinter.apk";
                        Log.i("1", path);
                        installUseAS(path);
                    }

                    @Override
                    public void onFailed(String error) {
                        dismissLoad();
                        ToastUtil.s("加载失败，请重试");
                    }
                });
    }


    /**
     * 正常方式安装APK
     */
    private void installUseAS(String filePath) {
        Uri uri = Uri.fromFile(new File(filePath));
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        startActivity(intent);
    }


    protected void showPrintDialog() {
        LayoutInflater inflater = LayoutInflater.from(SettingActivity.this);
        View layout = inflater.inflate(R.layout.dialog_print, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this, R.style.AlertDialogCustom);
        final EditText ipEt = (EditText) layout.findViewById(R.id.print_id);
        final EditText addressEt = (EditText) layout.findViewById(R.id.print_address);
        builder.setView(layout);
        builder.setTitle("设置");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (TextUtils.isEmpty(ipEt.getText().toString().trim())) {
                    ToastUtil.s("IP地址不能为空");
                } else if (TextUtils.isEmpty(addressEt.getText().toString().trim())) {
                    ToastUtil.s("端口不能为空");
                } else if (!isRightIp(ipEt.getText().toString().trim())) {
                    ToastUtil.s("请输入正确的ip地址");
                } else {
                    ToastUtil.s("保存成功");
                    SPUtils.put(SettingActivity.this, "ip", ipEt.getText().toString().trim());
                    SPUtils.put(SettingActivity.this, "port", addressEt.getText().toString().trim());
                    dialog.dismiss();
                }
            }
        });
        builder.show();

    }

    private boolean isRightIp(String ipAddress) {

        String ips[] = ipAddress.split("\\.");

        if (ips.length == 4) {
            for (String ip : ips) {
                System.out.println(ip);
                if (Integer.parseInt(ip) < 0 || Integer.parseInt(ip) > 255) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }

    }

    private void showSingleChoiceDialog() {
        if (printerType != 0 && printerType != 1) {
            printerType = -1;
        }

        final String[] items = {"爱普生打印机", "惠普打印机"};
        AlertDialog.Builder singleChoiceDialog =
                new AlertDialog.Builder(context, R.style.AlertDialogCustom);
        singleChoiceDialog.setTitle("选择打印机");
        // 第二个参数是默认选项，此处设置为0
        singleChoiceDialog.setSingleChoiceItems(items, printerType,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        printerType = which;
                    }
                });
        singleChoiceDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (printerType != -1) {
                            if (printerType == 1) {
                                setting_printer_info.setVisibility(View.GONE);
                            } else if (printerType == 0) {
                                setting_printer_info.setVisibility(View.VISIBLE);
                            } else {
                                ToastUtil.s("您还没有选择一款打印机!");
                                return;
                            }

                            getPrintManager().setPrinterType(printerType);
                            SharedPreferencesUtil util = new SharedPreferencesUtil(context);
                            util.saveInt(Constant.PRINTER_TYPE_STR, printerType);
                        } else {
                            ToastUtil.s("您还没有选择一款打印机!");
                        }
                    }
                });
        singleChoiceDialog.show();
    }

}
