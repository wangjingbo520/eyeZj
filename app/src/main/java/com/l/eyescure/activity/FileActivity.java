package com.l.eyescure.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.l.eyescure.R;
import com.l.eyescure.server.SqlManage.localsql.DbPatientEntity;
import com.l.eyescure.server.callBack.INormalEventListener;
import com.l.eyescure.util.Constant;
import com.l.eyescure.util.ToastUtil;
import com.l.eyescure.view.TopBarView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by luka on 2017-03-13.
 * 档案
 */
public class FileActivity extends BaseActivity {

    @BindView(R.id.file_view_top)
    TopBarView viewTop;

    @Override
    protected boolean isNeedTitle() {
        return false;
    }

    @Override
    protected void initView() {
        setTitle("档案管理");
        setTopBarView(viewTop);
    }

    @Override
    protected int topHelp() {
        showHelpDialog();
        return 0;
    }

    protected void showHelpDialog() {
        String showStr = "新增病例可以创建新的病人档案，创建时需要完善病人信息。\n" +
                "搜索病例按钮可以输入病人的编号查找病人的全部治疗信息。";
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
        ToastUtil.s("进行病人资料编辑的时候，不能进行治疗!");
    }

    @Override
    protected void topFile() {

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

    @Override
    public int bindLayout() {
        return R.layout.activity_file;
    }

    @OnClick({R.id.file_add, R.id.file_search})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.file_add://新增
                Intent intent = new Intent(context, AddPatientActivity.class);
                startActivity(intent);
//                showAddDialog();
                break;
            case R.id.file_search://搜索
                showSearchDialog();
                break;
        }
    }


    protected void showSearchDialog() {
        LayoutInflater inflater = LayoutInflater.from(FileActivity.this);
        View layout = inflater.inflate(R.layout.dialog_search, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(FileActivity.this, R.style.AlertDialogCustom);
        final EditText ipEt = (EditText) layout.findViewById(R.id.name_id);
        ipEt.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(layout);
        builder.setTitle("搜索");
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
                if (!checkAccountInput(input_str)) {
                    return;
                } else {
                    getCureInfoManage().getOnePatientDetail(ipEt.getText().toString().trim(), new INormalEventListener() {
                        @Override
                        public void onValueEvent(int key, final Object value) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (value == null) {
                                        ToastUtil.s("没有该病人的档案");
                                    } else {
                                        final DbPatientEntity entity = (DbPatientEntity) value;
                                        if (TextUtils.isEmpty(entity.getName())) {
                                            ToastUtil.s("没有该病人的档案");
                                        } else {
                                            Bundle bundle = new Bundle();
                                            bundle.putString("cureNumber", entity.getNumber());
                                            readyGo(PatientDetailActivity.class, bundle);
                                            finish();
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
                                    ToastUtil.s("获取该病人的档案失败");
                                }
                            });
                        }
                    });
                    dialog.dismiss();
                }
            }
        });
        builder.show();


    }


}
