package com.l.eyescure.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.l.eyescure.R;
import com.l.eyescure.activity.adapter.LoginListAdapter;
import com.l.eyescure.server.SqlManage.localsql.DbDoctorEntity;
import com.l.eyescure.server.callBack.INormalEventListener;
import com.l.eyescure.util.Constant;
import com.l.eyescure.util.ToastUtil;
import com.l.eyescure.view.TopBarView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Author:admin
 * Version: V1.0
 * Description: 用户管理
 * Date: 2017/4/28
 */

public class AccountManagerActivity extends BaseActivity {

    @BindView(R.id.manager_listView)
    ListView manager_listView;

    @BindView(R.id.account_manager_view_top)
    TopBarView viewTop;

    @BindView(R.id.add_account_rl)
    RelativeLayout add_account_rl;

    private LoginListAdapter adapter;
    private List<DbDoctorEntity> list = new ArrayList<>();
    private String mCheckedAccount;
    private int mPosition;
    public AlertDialog dialog = null;

    @Override
    protected int bindLayout() {
        return R.layout.activity_manager;
    }

    @Override
    protected boolean isNeedTitle() {
        return false;
    }

    @Override
    protected void initView() {
        setTitle("用户管理");
        setRightText("新增");
        setRightOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ii = new Intent(context, AddAccount.class);
                startActivity(ii);
            }
        });
        manager_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String account = list.get(position).getAccount();
                showManagerDialog(position, account);
            }
        });

        setTopBarView(viewTop);
    }

    @Override
    protected int topHelp() {
        showHelpDialog();
        return 0;
    }

    protected void showHelpDialog() {
        String showStr = "点击“＋增加用户”项，可以进入增加新用户界面。\n" +
                "点击用户选项，可以删除用户或者重置用户密码。";
        showHelpDialog(showStr);
    }

    @Override
    protected boolean topExit() {
        finish();
        return false;
    }

    @Override
    protected void topSet() {
        setBtnClick();
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
        if (TextUtils.isEmpty(util.readString(Constant.CUR_DETAIL_SAVE_NUM))) {
            clickPritfBtn();
        } else {
            ToastUtil.s("你需要先选定一个病人，才能查看打印病例！");
        }
    }

    @Override
    protected void topUser() {

    }

    @OnClick({R.id.add_account_rl})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_account_rl: {
                Intent ii = new Intent(context, AddAccount.class);
                startActivity(ii);
                break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCureInfoManage().getAllDoctorDetail(new INormalEventListener() {
            @Override
            public void onValueEvent(int key, final Object value) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        list = (List<DbDoctorEntity>) value;
                        adapter = new LoginListAdapter(context, list);
                        manager_listView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onErrEvent(int errCode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.s("未能获取到用户信息");
                    }
                });
            }
        });
    }

    protected void showManagerDialog(int position, String account) {
        mCheckedAccount = account;
        mPosition = position;
        if (dialog == null) {
            dialog = new AlertDialog.Builder(context, R.style.AlertDialogCustom).create();
        }
        LayoutInflater inflater = LayoutInflater.from(AccountManagerActivity.this);
        View layout = inflater.inflate(R.layout.dialog_manager, null);
        final TextView deleteBtn = (TextView) layout.findViewById(R.id.item_detele);
        final TextView editBtn = (TextView) layout.findViewById(R.id.item_edit);
        dialog.setView(layout);
        dialog.setTitle("提示");
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCheckedAccount.equals("admins")) {
                    ToastUtil.s("系统管理员不允许删除");
                } else {
                    getCureInfoManage().delDoctorDetail(mCheckedAccount, new INormalEventListener() {
                        @Override
                        public void onValueEvent(int key, Object value) {
                            final int result = (int) value;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (result == 1) {
                                        list.remove(mPosition);
                                        adapter.notifyDataSetChanged();
                                        ToastUtil.s("删除成功");
                                    } else {
                                        ToastUtil.s("删除失败");
                                    }
                                }
                            });
                        }

                        @Override
                        public void onErrEvent(int errCode) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil.s("删除失败");
                                }
                            });
                        }
                    });
                }
                dialog.dismiss();
            }
        });
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (mCheckedAccount.equals("admins")) {
                    Intent i = new Intent(context, ChangePwdActivity.class);
                    startActivity(i);
                } else {
                    Intent i = new Intent();
                    i.setClass(context, AddAccount.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("account", mCheckedAccount);
                    bundle.putInt("key", 1);
                    i.putExtras(bundle);
                    startActivity(i);
                }
            }
        });
        dialog.show();
      /* 方法1：
      * 将对话框的大小按屏幕大小的百分比设置
      */
        WindowManager m = AccountManagerActivity.this.getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dialog.getWindow().getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (d.getHeight() * 0.3); // 高度设置为屏幕的0.5
        p.width = (int) (d.getWidth() * 0.4); // 宽度设置为屏幕的0.8
        dialog.getWindow().setAttributes(p);

    }
}
