package com.l.eyescure.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.apkfuns.logutils.LogUtils;
import com.l.eyescure.R;
import com.l.eyescure.activity.adapter.CureDetailListAdapter;
import com.l.eyescure.application.MyApplication;
import com.l.eyescure.server.SqlManage.localsql.DbCureDetailEntity;
import com.l.eyescure.server.SqlManage.localsql.DbDoctorEntity;
import com.l.eyescure.server.SqlManage.localsql.DbPatientEntity;
import com.l.eyescure.server.callBack.INormalEventListener;
import com.l.eyescure.server.cureManage.CureInfoManage;
import com.l.eyescure.util.Constant;
import com.l.eyescure.util.CustomCountDownTimer;
import com.l.eyescure.util.DateUtil;
import com.l.eyescure.util.ToastUtil;
import com.l.eyescure.view.CureDialog;
import com.l.eyescure.view.CureDialog2;
import com.l.eyescure.view.TopBarView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author:Look
 * Version: V1.0
 * Description:病人详情
 * Date: 2017/6/4  这里可以点击治疗了
 */

public class PatientDetailActivity extends BaseActivity {
    @BindView(R.id.list_view)
    ListView listView;
    @BindView(R.id.head_img)
    ImageView headImg;
    @BindView(R.id.num_person)
    TextView numPerson;
    @BindView(R.id.sex_person)
    TextView sexPerson;
    @BindView(R.id.docter_person)
    TextView docterPerson;
    @BindView(R.id.name_person)
    TextView namePerson;
    @BindView(R.id.year_person)
    TextView yearPerson;
    @BindView(R.id.detail_view_top)
    TopBarView viewTop;

    private CureDetailListAdapter adapter;
    private List<DbCureDetailEntity> list = new ArrayList<>();
    private String patientNumber;//病人编号
    private String patientDoctor;//病人编号
    private String cureId; //病例编号
    private CureDialog cureDialog = new CureDialog();
    private CureDialog2 cureDialog2 = new CureDialog2();
    private Bitmap bm = null;
    private Handler mHandler;
    private DbPatientEntity curPatient;
    private Dialog mDialog2;
    private Dialog dialog2;
    private Dialog mDialog;

    private CustomCountDownTimer dialogTimer;

    @Override
    protected int bindLayout() {
        return R.layout.activity_patientdetail;
    }

    @Override
    protected boolean isNeedTitle() {
        return false;
    }

    @Override
    protected void onResume() {
        Log.e("mdq", "show time = " + System.currentTimeMillis());
        showLoadDialog("正在加载病例数据...");
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dismissLoad();
            }
        }, 5000);
        super.onResume();
        initDate();
    }

    private void initDate() {
        initAllCureDetail(true);
        initOnePatientDetail();
    }

    private void initAllCureDetail(final boolean bInit) {

        CureInfoManage cureInfoManage = getCureInfoManage();
        if (cureInfoManage == null) {
            LogUtils.e("cureInfoManage=null");
            return;
        }
        getCureInfoManage().getPatientAllCureDetail(patientNumber, new INormalEventListener() {
            @Override
            public void onValueEvent(int key, Object value) {
                if (value == null) {
                    LogUtils.e("获取病人所有治疗数据为空");
                } else {
                    List<DbCureDetailEntity> mlist = (List<DbCureDetailEntity>) value;
                    list.clear();
                    if (mlist.size() == 0) {
                        LogUtils.e("获取病人所有治疗数据为空");
                    } else {
                        for (int i = 0; i < mlist.size(); i++) {
                            list.add(mlist.get(i));
                        }

                        //排序
                        Collections.sort(list, new Comparator<DbCureDetailEntity>() {
                            @Override
                            public int compare(DbCureDetailEntity lhs, DbCureDetailEntity rhs) {
                                String time_1_str = lhs.getCureId();
                                if (TextUtils.isEmpty(time_1_str) || time_1_str.length() < 24) {
                                    return 0;
                                }
                                long time_1 = Long.valueOf(time_1_str.substring(11, 24));
                                String time_2_str = rhs.getCureId();
                                if (TextUtils.isEmpty(time_2_str) || time_2_str.length() < 24) {
                                    return 0;
                                }
                                long time_2 = Long.valueOf(time_2_str.substring(11, 24));

                                if (time_1 > time_2) {
                                    return 1;
                                } else if (time_1 < time_2) {
                                    return -1;
                                } else {
                                    return 0;
                                }
                            }
                        });

                        DbCureDetailEntity mEntity = list.get(0);
                        if (mEntity != null) {
                            cureId = mEntity.getCureId();
                            util.saveString(Constant.CUR_DETAIL_SAVE_ID, cureId);
                        }
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (bInit) {
                                adapter = new CureDetailListAdapter(context, list);
                                listView.setAdapter(adapter);
                            } else {
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });
                }

                dismissLoad();
            }

            @Override
            public void onErrEvent(int errCode) {
                LogUtils.e("获取病人所有治疗数据失败");
                dismissLoad();
            }
        });
    }

    private void initOnePatientDetail() {
        if (getCureInfoManage() == null) {
            return;
        }
        getCureInfoManage().getOnePatientDetail(patientNumber, new INormalEventListener() {
            @Override
            public void onValueEvent(int key, Object value) {
                if (value == null) {
                    LogUtils.e("获取病人数据为空");
                } else {
                    final DbPatientEntity user = (DbPatientEntity) value;
                    if (TextUtils.isEmpty(user.getName())) {
                        LogUtils.e("获取病人数据为空");
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                curPatient = user;

                                getCureInfoManage().getPatientImg(user.getNumber(), new INormalEventListener() {
                                    @Override
                                    public void onValueEvent(final int key, final Object value) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (value != null) {
                                                    if (key == 1) {
                                                        bm = (Bitmap) value;
                                                        headImg.setScaleType(ImageView.ScaleType.FIT_XY);
                                                        headImg.setImageBitmap(bm);
                                                    } else {
                                                        ToastUtil.s("未获取到用户头像");
                                                    }
                                                } else {
                                                    ToastUtil.s("未获取到用户头像");
                                                }
                                            }
                                        });
                                    }

                                    @Override
                                    public void onErrEvent(int errCode) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                ToastUtil.s("未获取到用户头像！");
                                            }
                                        });
                                    }
                                });

                                numPerson.setText(user.getNumber());
                                namePerson.setText(user.getName());
                                sexPerson.setText(user.getSex());
                                patientDoctor = user.getDoctorid();
                                getCureInfoManage().getOneDoctor(patientDoctor, new INormalEventListener() {
                                    @Override
                                    public void onValueEvent(int key, Object value) {
                                        if (value != null) {
                                            final DbDoctorEntity findDoctor = (DbDoctorEntity) value;
                                            if (!TextUtils.isEmpty(findDoctor.getAccount())) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        String nick = findDoctor.getNick();
                                                        docterPerson.setText(nick);
                                                    }
                                                });
                                            }
                                        }
                                    }

                                    @Override
                                    public void onErrEvent(int errCode) {

                                    }
                                });
                                LogUtils.e(user.getBirthday());
                                yearPerson.setText((DateUtil.getCurrentYear() - Integer.valueOf(user.getBirthday().substring(0, 4))) + "");
                            }
                        });
                    }
                }
            }

            @Override
            public void onErrEvent(int errCode) {

            }
        });
    }

    protected void showDelDialog(int position) {
        DbCureDetailEntity mCure = list.get(position);
        if (mCure == null) {
            ToastUtil.s("删除病人治疗记录失败！");
            return;
        }

        final String mCureNumber = mCure.getCureId();
        if (TextUtils.isEmpty(mCureNumber)) {
            ToastUtil.s("删除病人治疗记录失败！");
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogCustom);
        builder.setTitle("警告");
        builder.setMessage("您确定删除当前治疗记录吗？");
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
                getCureInfoManage().delOneCureDetail(mCureNumber, new INormalEventListener() {
                    @Override
                    public void onValueEvent(int key, Object value) {
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.s("删除病人治疗记录成功！");
                                initAllCureDetail(false);

                                final DbPatientEntity entity = curPatient;
                                entity.setCureNumber(entity.getCureNumber() - 1);
                                getCureInfoManage().save(entity, Constant.PATIENT_UPDATE, new INormalEventListener() {
                                    @Override
                                    public void onValueEvent(int key, Object value) {
                                        int result = (int) value;
                                        if (result == 1) {
                                            MyApplication.getInstance().activePatientId = entity.getNumber();

                                            finish();
                                        } else {
                                            LogUtils.e("保存编辑病人数据失败");
                                        }
                                    }

                                    @Override
                                    public void onErrEvent(int errCode) {
                                        LogUtils.e("保存编辑病人数据失败");
                                    }
                                });
                            }
                        }, 500);
                    }

                    @Override
                    public void onErrEvent(int errCode) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.s("删除病人治疗记录失败！");
                            }
                        });
                    }
                });
            }
        });
        builder.show();
    }

    private AdapterView.OnItemClickListener clickPatientDetail = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            DbCureDetailEntity user = list.get(position);
            if (user == null) {
                return;
            }


            for (int i = 0; i < list.size(); i++) {
                Log.e("====>", "第" + i + "条数据是: " + list.get(i).toString());

            }

            String curid = user.getCureId();
            Intent intent = new Intent(context, LookPrintActivity.class);
            intent.putExtra("patientid", patientNumber);
            intent.putExtra("curid", curid);
            //这里应该增加模式类型传值,在对象里面新增cure_model字段，以前的标准数据默认=0
            int cure_model = user.getCure_model();
            intent.putExtra("cure_model", cure_model);
            startActivity(intent);
        }
    };

    @Override
    protected void initView() {
        mHandler = new Handler();
        setTitle("详情信息");
        patientNumber = getIntent().getExtras().getString("cureNumber");
        util.saveString(Constant.CUR_DETAIL_SAVE_NUM, patientNumber);

        LogUtils.e("number====" + patientNumber);
        //  list = new ArrayList<DbCureDetailEntity>();
        //initDate();

        setTopBarView(viewTop);

        listView.setOnItemClickListener(clickPatientDetail);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showDelDialog(position);
                return true;
            }
        });


        dialogTimer = new CustomCountDownTimer(10 * 1000, 1000) {
            @Override
            public void onTick(long millis) {
            }

            @Override
            public void onFinish() {
                intToZijian(1);
            }

            @Override
            public void onMyInterval() {
            }
        };
    }

    @Override
    protected void onDestroy() {
        util.saveString(Constant.CUR_DETAIL_SAVE_NUM, "");
        util.saveString(Constant.CUR_DETAIL_SAVE_ID, "");
        if (dialogTimer != null) {
            dialogTimer.stop();
            dialogTimer = null;
        }

        if (mDialog2 != null) {
            if (mDialog2.isShowing()) {
                mDialog2.dismiss();
            }
        }
        super.onDestroy();
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
        setBtnClick();
    }

    private Runnable checkTfSdCardRunnable = new Runnable() {
        @Override
        public void run() {
            showNoTfDialog();
        }
    };

    protected void showNoTfDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogCustom);
        builder.setTitle("警告");
        builder.setMessage("您没有插入存储卡，将无法存储治疗数据，是否继续？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(context, SelfInspectionActivity.class);
                LogUtils.e(patientNumber);
                i.putExtra("patientid", patientNumber);
                //这里默认标准模式，因为你没有选择治疗模式
                i.putExtra("cure_model", 1);
                startActivity(i);
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

    @Override
    protected void topCare() {
        getMessageManage().resetPort();
        mDialog = cureDialog.creatDialog(this, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (v.getId()) {
                            case R.id.dialog_exit: {
                                cureDialog.cancel();
                                break;
                            }
                            case R.id.start_cure_btn: {
                                cureDialog.cancel();
                                showSelectType();
                                break;
                            }
                            case R.id.print_btn: {
                                topPrintf();
                                cureDialog.cancel();
                                break;
                            }
                            case R.id.file_btn: {
                                boolean bCanSet = false;
                                String mCurDoctor = getCureInfoManage().getLoginerAccount();
                                if (!TextUtils.isEmpty(patientDoctor) && !TextUtils.isEmpty(mCurDoctor)) {
                                    if (patientDoctor.equals(mCurDoctor))
                                        bCanSet = true;
                                }

                                if (bCanSet) {
                                    if (!TextUtils.isEmpty(patientNumber)) {
                                        Intent intent = new Intent(context, EditPatientActivity.class);
                                        intent.putExtra("patientid", patientNumber);
                                        intent.putExtra("patientDoctor", patientDoctor);
                                        startActivity(intent);
                                    } else {
                                        ToastUtil.s("请选择用户");
                                    }
                                } else {
                                    ToastUtil.s("不是主治医生，没有权限修改！");
                                }
                                cureDialog.cancel();
                                break;
                            }
                        }
                    }
                }

        );
        mDialog.show();
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
        String showStr = "点击治疗会弹出操作框，可进行治疗、查看/打印、病例编辑操作。\n" +
                "点击开始治疗，会对当前病人进行治疗，治疗前会自检。\n" +
                "点击查看/打印，会显示当前病人的所有治疗记录，可左右翻页预览。但会加载较长的时间\n" +
                "点击病例编辑，可编辑当前病人的头像、姓名、主治医生等基本信息。\n" +
                "点击病人的病例列表的单项表格，可打开该条病例的预览界面。";
        showHelpDialog(showStr);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }


    private void showSelectType() {
        if (dialogTimer != null) {
            dialogTimer.start();
        } else {

            dialogTimer = new CustomCountDownTimer(10 * 1000, 1000) {
                @Override
                public void onTick(long millis) {
                }

                @Override
                public void onFinish() {
                    intToZijian(1);
                }

                @Override
                public void onMyInterval() {
                }
            };
        }

        dialog2 = cureDialog2.creatDialog(this, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (v.getId()) {
                            case R.id.dialog_exit:
                                cureDialog2.cancel();
                                if (dialogTimer != null) {
                                    dialogTimer.stop();
                                }
                                intToZijian(1);
                                break;
                            case R.id.ll1:
                                //1:标准按摩模式
                                intToZijian(1);
                                cureDialog2.cancel();
                                break;
                            case R.id.ll2:
                                //2:长按摩模式
                                intToZijian(2);
                                cureDialog2.cancel();
                                break;
                            case R.id.ll3:
                                //3: 长保压按摩模式
                                intToZijian(3);
                                cureDialog2.cancel();
                                break;
                            default:
                                break;

                        }
                    }
                }
        );
        dialog2.show();
    }


    //自检
    private void intToZijian(int cure_model) {
        if (dialogTimer != null) {
            dialogTimer.stop();

        }

        if (mDialog != null) {
            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }
        }

        if (dialog2 != null) {
            if (dialog2.isShowing()) {
                dialog2.dismiss();
            }
        }

        // 0 从本地获得数据 1 从服务器获得数据
        if (getCureInfoManage().getInfoMode == 0 && TextUtils.isEmpty(getTfCardPath())) {
            mHandler.postDelayed(checkTfSdCardRunnable, 1000);
        } else {
            Intent i = new Intent(context, SelfInspectionActivity.class);
            LogUtils.e(patientNumber);
            i.putExtra("patientid", patientNumber);
            //0: 标准模式
            i.putExtra("cure_model", cure_model);
            startActivity(i);
        }

    }


}
