package com.l.eyescure.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.l.eyescure.R;
import com.l.eyescure.server.SqlManage.localsql.DbDoctorEntity;
import com.l.eyescure.server.callBack.INormalEventListener;
import com.l.eyescure.util.Constant;
import com.l.eyescure.util.DateUtil;
import com.l.eyescure.util.InputCheck;
import com.l.eyescure.util.ToastUtil;
import com.l.eyescure.view.TopBarView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Author:Look
 * Version: V1.0
 * Description:添加 用户
 * Date: 2017/4/9
 */

public class AddAccount extends BaseActivity {


    @BindView(R.id.name_et)
    EditText nameEt;
    @BindView(R.id.new_pwd_et)
    EditText newPwdEt;
    @BindView(R.id.new_pwd1_et)
    EditText newPwd1Et;
    @BindView(R.id.tv_change_pwd)
    TextView tvChangePwd;
    @BindView(R.id.change_ok)
    Button changeOk;
    @BindView(R.id.et_user_nick)
    EditText etUserNick;
    @BindView(R.id.ll_add_name)
    LinearLayout llAddName;

    @BindView(R.id.add_account_view_top)
    TopBarView viewTop;

    @BindView(R.id.add_acc_new_pwd_cb)
    CheckBox add_acc_new_pwd_cb;
    @BindView(R.id.add_acc_new_pwd1_cb)
    CheckBox add_acc_new_pwd1_cb;

    private int type = 0;//1为修改用户密码 0 为增加账户
    private String account;

    @Override
    protected int bindLayout() {
        return R.layout.activity_addaccount;
    }

    @Override
    protected boolean isNeedTitle() {
        return false;
    }

    @Override
    protected void initView() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            type = bundle.getInt("key");
        }
        if (type == 1) {
            account = bundle.getString("account");
            llAddName.setVisibility(View.GONE);
            nameEt.setText(account);
            nameEt.setEnabled(false);
            tvChangePwd.setText("修改密码：");
            changeOk.setText("修改");
            setTitle("修改用户密码");
        } else {
            llAddName.setVisibility(View.VISIBLE);
            setTitle("添加用户");
            tvChangePwd.setText("设置密码：");
            changeOk.setText("确定");
        }

        setTopBarView(viewTop);

        newPwdEt.addTextChangedListener(new TextWatcher() {
            private int selectionStart;
            private int selectionEnd;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                /** 得到光标开始和结束位置 ,超过最大数后记录刚超出的数字索引进行控制 */
                String text = newPwdEt.getText().toString();
                if (!TextUtils.isEmpty(text)) {
                    if (add_acc_new_pwd_cb.getVisibility() != View.VISIBLE)
                        add_acc_new_pwd_cb.setVisibility(View.VISIBLE);
                } else {
                    if (add_acc_new_pwd_cb.getVisibility() != View.INVISIBLE)
                        add_acc_new_pwd_cb.setVisibility(View.INVISIBLE);
                    return;
                }

                selectionStart = newPwdEt.getSelectionStart();
                selectionEnd = newPwdEt.getSelectionEnd();
                if (text.length() <= InputCheck.getInstance().PASSWORD_MAX_LENGTH) {

                } else {
                    s.delete(selectionStart - 1, selectionEnd);
                    newPwdEt.setText(s);
                    newPwdEt.setSelection(s.length());
                }
            }
        });

        newPwd1Et.addTextChangedListener(new TextWatcher() {
            private int selectionStart;
            private int selectionEnd;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                /** 得到光标开始和结束位置 ,超过最大数后记录刚超出的数字索引进行控制 */
                String text = newPwd1Et.getText().toString();
                if (!TextUtils.isEmpty(text)) {
                    if (add_acc_new_pwd1_cb.getVisibility() != View.VISIBLE)
                        add_acc_new_pwd1_cb.setVisibility(View.VISIBLE);
                } else {
                    if (add_acc_new_pwd1_cb.getVisibility() != View.INVISIBLE)
                        add_acc_new_pwd1_cb.setVisibility(View.INVISIBLE);
                    return;
                }

                selectionStart = newPwd1Et.getSelectionStart();
                selectionEnd = newPwd1Et.getSelectionEnd();
                if (text.length() <= InputCheck.getInstance().PASSWORD_MAX_LENGTH) {

                } else {
                    s.delete(selectionStart - 1, selectionEnd);
                    newPwd1Et.setText(s);
                    newPwd1Et.setSelection(s.length());
                }
            }
        });

        add_acc_new_pwd_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String password = newPwdEt.getText().toString();
                int password_length = 0;
                if (!TextUtils.isEmpty(password)) {
                    password_length = password.length();
                }
                if (isChecked) {
                    newPwdEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    newPwdEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                newPwdEt.setSelection(password_length);
            }
        });

        add_acc_new_pwd1_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String password = newPwd1Et.getText().toString();
                int password_length = 0;
                if (!TextUtils.isEmpty(password)) {
                    password_length = password.length();
                }
                if (isChecked) {
                    newPwd1Et.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    newPwd1Et.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                newPwd1Et.setSelection(password_length);
            }
        });
    }

    @Override
    protected int topHelp() {
        showHelpDialog();
        return 0;
    }

    protected void showHelpDialog() {
        String showStr = "医生帐号有11位的数字组成，建议使用电话号码注册。\n" +
                "密码由6-18位的数字或者字母组成。\n" +
                "医生的密码可以在医生用设定的账号密码登陆后，在设置－密码管理中重新设定。\n" +
                "设置密码和重复密码的内容需要一致。";
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
            finish();
        }
    }

    @OnClick(R.id.change_ok)
    public void onClick() {
        String name = nameEt.getText().toString().trim();
        if (!checkAccountInput(name)) {
            return;
        }

        String newPwd = newPwdEt.getText().toString().trim();
        if (!checkPasswordInput(newPwd, 0)) {
            return;
        }

        final String newPwd1 = newPwd1Et.getText().toString().trim();
        if (!checkPasswordInput(newPwd1, 1)) {
            return;
        }

        if (!newPwd.equals(newPwd1)) {
            ToastUtil.s("两次密码输入不一致");
            return;
        }

        if (type == 1) {
            getCureInfoManage().getOneDoctor(account, new INormalEventListener() {
                @Override
                public void onValueEvent(int key, Object value) {
                    if (value == null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.s("该用户不存在");
                            }
                        });
                    } else {
                        DbDoctorEntity entity = (DbDoctorEntity) value;
                        if (TextUtils.isEmpty(entity.getAccount())) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil.s("该用户不存在");
                                }
                            });
                        } else {
                            entity.setPassword(newPwd1);
                            getCureInfoManage().save(entity, Constant.DOCTOR_UPDATE, new INormalEventListener() {
                                @Override
                                public void onValueEvent(int key, Object value) {
                                    final int result = (int) value;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (result == 1) {
                                                ToastUtil.s("修改用户密码成功,下次登录请使用新密码");
                                                finish();
                                            } else {
                                                ToastUtil.s("修改用户密码失败");
                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onErrEvent(int errCode) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ToastUtil.s("修改用户密码失败");
                                        }
                                    });
                                }
                            });
                        }
                    }
                }

                @Override
                public void onErrEvent(int errCode) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.s("该用户不存在");
                        }
                    });
                }
            });
        } else {
            String nick = etUserNick.getText().toString().trim();
            if (!checkNickInput(nick)) {
                return;
            }

            checkNameRepeat(name, nick, newPwd1);
        }
    }

    private boolean checkNameRepeat(final String name, final String nick, final String newPwd1) {
        final boolean bRepeat = false;
        getCureInfoManage().getOneDoctor(name, new INormalEventListener() {
            @Override
            public void onValueEvent(int key, Object value) {
                boolean bRepeat = false;
                if (value != null) {
                    DbDoctorEntity findDoctor = (DbDoctorEntity) value;
                    if (!TextUtils.isEmpty(findDoctor.getAccount())) {
                        bRepeat = true;
                    }
                }

                if (bRepeat) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.s("当前用户已经存在，您不需要重复添加!");
                        }
                    });
                } else {
                    DbDoctorEntity dbLoginEntity = new DbDoctorEntity();
                    dbLoginEntity.setAccount(name);
                    dbLoginEntity.setNick(nick);
                    dbLoginEntity.setPassword(newPwd1);
                    dbLoginEntity.setCreate_date(DateUtil.getCurDateStr("yyyy-MM-dd HH:mm:ss"));
                    dbLoginEntity.setIsLogin(0);
                    getCureInfoManage().save(dbLoginEntity, Constant.DOCTOR_ADD, new INormalEventListener() {
                        @Override
                        public void onValueEvent(int key, Object value) {
                            int result = (int) value;
                            if (result == 1) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ToastUtil.s("添加用户成功");
                                        finish();
                                    }
                                });

                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ToastUtil.s("添加用户失败");
                                    }
                                });
                            }
                        }

                        @Override
                        public void onErrEvent(int errCode) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil.s("添加用户失败");
                                }
                            });
                        }
                    });
                }
            }

            @Override
            public void onErrEvent(int errCode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.s("添加用户失败");
                    }
                });
            }
        });
        return bRepeat;
    }
}
