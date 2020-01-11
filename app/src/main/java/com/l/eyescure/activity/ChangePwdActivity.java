package com.l.eyescure.activity;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.l.eyescure.R;
import com.l.eyescure.server.SqlManage.localsql.DbDoctorEntity;
import com.l.eyescure.server.callBack.INormalEventListener;
import com.l.eyescure.util.Constant;
import com.l.eyescure.util.InputCheck;
import com.l.eyescure.util.SPUtils;
import com.l.eyescure.util.ToastUtil;
import com.l.eyescure.view.TopBarView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Author:Look
 * Version: V1.0
 * Description:修改密码
 * Date: 2017/4/9
 */

public class ChangePwdActivity extends BaseActivity {
    @BindView(R.id.old_number_et)
    EditText oldNumberEt;
    @BindView(R.id.new_pwd_et)
    EditText newPwdEt;
    @BindView(R.id.new_pwd1_et)
    EditText newPwd1Et;
    @BindView(R.id.change_pwn_view_top)
    TopBarView viewTop;
    @BindView(R.id.old_number_et_cb)
    CheckBox old_number_et_cb;
    @BindView(R.id.new_pwd_et_cb)
    CheckBox new_pwd_et_cb;
    @BindView(R.id.new_pwd1_et_cb)
    CheckBox new_pwd1_et_cb;

    private DbDoctorEntity dbLoginEntity = null;

    @Override
    protected int bindLayout() {
        return R.layout.activity_changepwd;
    }

    @Override
    protected boolean isNeedTitle() {
        return false;
    }

    @Override
    protected void initView() {
        setTitle("修改密码");
        setTopBarView(viewTop);

        oldNumberEt.addTextChangedListener(new TextWatcher() {
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
                String text = oldNumberEt.getText().toString();
                if (!TextUtils.isEmpty(text)) {
                    if (old_number_et_cb.getVisibility() != View.VISIBLE)
                        old_number_et_cb.setVisibility(View.VISIBLE);
                } else {
                    if (old_number_et_cb.getVisibility() != View.INVISIBLE)
                        old_number_et_cb.setVisibility(View.INVISIBLE);
                    return;
                }

                selectionStart = oldNumberEt.getSelectionStart();
                selectionEnd = oldNumberEt.getSelectionEnd();
                if (text.length() <= InputCheck.getInstance().PASSWORD_MAX_LENGTH) {

                } else {
                    s.delete(selectionStart - 1, selectionEnd);
                    oldNumberEt.setText(s);
                    oldNumberEt.setSelection(s.length());
                }
            }
        });

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
                    if (new_pwd_et_cb.getVisibility() != View.VISIBLE)
                        new_pwd_et_cb.setVisibility(View.VISIBLE);
                } else {
                    if (new_pwd_et_cb.getVisibility() != View.INVISIBLE)
                        new_pwd_et_cb.setVisibility(View.INVISIBLE);
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
                    if (new_pwd1_et_cb.getVisibility() != View.VISIBLE)
                        new_pwd1_et_cb.setVisibility(View.VISIBLE);
                } else {
                    if (new_pwd1_et_cb.getVisibility() != View.INVISIBLE)
                        new_pwd1_et_cb.setVisibility(View.INVISIBLE);
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


        old_number_et_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String password = oldNumberEt.getText().toString();
                int password_length = 0;
                if (!TextUtils.isEmpty(password)) {
                    password_length = password.length();
                }
                if (isChecked) {
                    oldNumberEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    oldNumberEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                oldNumberEt.setSelection(password_length);
            }
        });

        new_pwd_et_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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

        new_pwd1_et_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
    protected void onResume() {
        super.onResume();
        String account = (String) SPUtils.get(this, "account", "");
		if(getCureInfoManage()!=null){
        getCureInfoManage().getOneDoctor(account, new INormalEventListener() {
            @Override
            public void onValueEvent(int key, Object value) {
                if (value != null)
                    dbLoginEntity = (DbDoctorEntity) value;
            }

            @Override
            public void onErrEvent(int errCode) {

            }
        });
		}
    }

    @Override
    protected int topHelp() {
        showHelpDialog();
        return 0;
    }

    protected void showHelpDialog() {
        String showStr = "密码由6-18位的数字或者字母组成。\n" +
                "如果你忘记了旧密码，请联系管理员，重置你的密码。\n" +
                "新密码和重复新密码的内容需要一致。";
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
        finish();
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

    @OnClick(R.id.change_ok)
    public void onClick() {
        String oldPwd = oldNumberEt.getText().toString().trim();
        if (!checkPasswordInput(oldPwd, 3)) {
            return;
        }

        String newPwd = newPwdEt.getText().toString().trim();
        if (!checkPasswordInput(newPwd, 2)) {
            return;
        }

        String newPwd1 = newPwd1Et.getText().toString().trim();
        if (!checkPasswordInput(newPwd, 1)) {
            return;
        }

        if (!oldPwd.equals(dbLoginEntity.getPassword())) {
            ToastUtil.s("您输入的旧密码有误");
        } else if (!newPwd.equals(newPwd1)) {
            ToastUtil.s("您输入的新密码，两次密码输入不一致");
        } else {
            dbLoginEntity.setPassword(newPwd1);
            final String mAccount = dbLoginEntity.getAccount();
            getCureInfoManage().save(dbLoginEntity, Constant.DOCTOR_UPDATE, new INormalEventListener() {
                @Override
                public void onValueEvent(int key, Object value) {
                    final int result = (int) value;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (result == 1) {
                                ToastUtil.s("您修改密码成功！下次登录此帐号时请使用新密码");

                                getCureInfoManage().saveSuperPassword(dbLoginEntity);
                                finish();
                            } else {
                                ToastUtil.s("修改密码失败");
                            }
                        }
                    });
                }

                @Override
                public void onErrEvent(int errCode) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.s("修改密码失败");
                        }
                    });
                }
            });
        }
    }
}
