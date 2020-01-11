package com.l.eyescure.activity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apkfuns.logutils.LogUtils;
import com.kevin.crop.UCrop;
import com.l.eyescure.R;
import com.l.eyescure.application.MyApplication;
import com.l.eyescure.server.SqlManage.localsql.DbDoctorEntity;
import com.l.eyescure.server.SqlManage.localsql.DbPatientEntity;
import com.l.eyescure.server.SqlManage.localsql.DbPatientImgEntity;
import com.l.eyescure.server.callBack.INormalEventListener;
import com.l.eyescure.util.CameraUtil;
import com.l.eyescure.util.Constant;
import com.l.eyescure.util.IDCardUtil;
import com.l.eyescure.util.JayUtils;
import com.l.eyescure.util.ToastUtil;
import com.l.eyescure.view.TopBarView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Author:Look
 * Version: V1.0
 * Description:编辑病人
 * Date: 2017/4/9
 */

public class EditPatientActivity extends BaseActivity {
    @BindView(R.id.patient_number_et)
    EditText patientNumberEt;
    @BindView(R.id.name_et)
    EditText nameEt;
    @BindView(R.id.sex_et)
    TextView sexEt;
    @BindView(R.id.id_number_et)
    EditText idNumberEt;
    @BindView(R.id.birthday_et)
    TextView birthdayEt;
    @BindView(R.id.head_img)
    ImageView headImg;
    @BindView(R.id.edit_view_top)
    TopBarView viewTop;
    @BindView(R.id.doctor_et)
    TextView doctor_et;

    @BindView(R.id.main_clear)
    RelativeLayout mainClear;
    @BindView(R.id.main_add)
    RelativeLayout mainAdd;
    @BindView(R.id.main_delete)
    RelativeLayout mainDelete;

    private Bitmap bm = null;
    private String path = null;
    private String cureNumber = null;
    private String doctorNumber = null;
    private DbPatientEntity entity;
    private static final int CAMERA_REQUEST_CODE = 1;    // 相机拍照标记

    @Override
    protected int bindLayout() {
        return R.layout.activity_editpaient;
    }

    @Override
    protected boolean isNeedTitle() {
        return false;
    }

    @Override
    protected void initView() {
        setTitle("编辑病人资料");
        cureNumber = getIntent().getExtras().getString("patientid");
        doctorNumber = getIntent().getExtras().getString("patientDoctor");

        setTopBarView(viewTop);
    }

    @Override
    protected void onResume() {
        super.onResume();
		if(getCureInfoManage()!=null){
        getCureInfoManage().getOnePatientDetail(cureNumber, new INormalEventListener() {
            @Override
            public void onValueEvent(int key, Object value) {
                if (value == null) {
                    LogUtils.e("未获取到目标病人的数据");
                } else {
                    entity = (DbPatientEntity) value;
                    if (TextUtils.isEmpty(entity.getName())) {
                        LogUtils.e("未获取到目标病人的数据");
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                patientNumberEt.setText(entity.getNumber());
                                nameEt.setText(entity.getName());
                                sexEt.setText(entity.getSex());
                                idNumberEt.setText(entity.getIdNumber());
                                birthdayEt.setText(entity.getBirthday());

                                getCureInfoManage().getPatientImg(entity.getNumber(), new INormalEventListener() {
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
                                                ToastUtil.s("未获取到用户头像");
                                            }
                                        });
                                    }
                                });

                                getCureInfoManage().getOneDoctor(entity.getDoctorid(), new INormalEventListener() {
                                    @Override
                                    public void onValueEvent(int key, Object value) {
                                        if (value != null) {
                                            final DbDoctorEntity findDoctor = (DbDoctorEntity) value;
                                            if (!TextUtils.isEmpty(findDoctor.getAccount())) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        String nick = findDoctor.getNick();
                                                        doctor_et.setText(nick);
                                                    }
                                                });
                                            }
                                        }
                                    }

                                    @Override
                                    public void onErrEvent(int errCode) {

                                    }
                                });
                                patientNumberEt.setEnabled(false);
                                mainClear.setVisibility(View.INVISIBLE);
                                mainAdd.setVisibility(View.VISIBLE);
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
                        ToastUtil.s("获取到目标病人数据失败");
                    }
                });
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
        String showStr = "除了病人编号外，其他资料都能编辑。\n" +
                "修改资料，将更新数据到的病人信息数据库。\n" +
                "删除资料将删除该病人的所有信息，包括治疗数据";
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
        finish();
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

    @OnClick({R.id.main_clear, R.id.main_add, R.id.sex_et, R.id.birthday_et, R.id.main_delete, R.id.doctor_et})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.doctor_et:
                JayUtils.hideKeyboard(EditPatientActivity.this);
                chooseDoctor();
                break;
            case R.id.sex_et://性别选择
                JayUtils.hideKeyboard(EditPatientActivity.this);
                chooseSex();
                break;
            case R.id.birthday_et://生日选择
                JayUtils.hideKeyboard(EditPatientActivity.this);
                chooseDate();
                break;
            case R.id.main_delete://删除病例
                showDeleteDialog();
                break;
            case R.id.main_clear://清空输入
                patientNumberEt.setText("");
                nameEt.setText("");
                idNumberEt.setText("");
                birthdayEt.setText("");
                sexEt.setText("");
                headImg.setImageBitmap(null);
                path = null;
                break;
            case R.id.main_add://编辑病例
                JayUtils.hideKeyboard(EditPatientActivity.this);
                final String number = patientNumberEt.getText().toString().trim();
                final String name = nameEt.getText().toString().trim();
                final String idNumber = idNumberEt.getText().toString().trim();
                final String birthday = birthdayEt.getText().toString().trim();
                final String sex = sexEt.getText().toString().trim();
                final String doctor = doctorNumber;
                if (TextUtils.isEmpty(number)) {
                    ToastUtil.s("请输入病人编号");
                    return;
                } else if (TextUtils.isEmpty(name)) {
                    ToastUtil.s("请输入姓名");
                    return;
                } else if (TextUtils.isEmpty(sex)) {
                    ToastUtil.s("请输入性别");
                    return;
                } else if (TextUtils.isEmpty(idNumber)) {
                    ToastUtil.s("请输入证件编号");
                    return;
                } else if (!IDCardUtil.validate_effective(idNumber).equals("true")) {
                    ToastUtil.s(IDCardUtil.validate_effective(idNumber));
                    return;
                } else if (TextUtils.isEmpty(birthday)) {
                    ToastUtil.s("请输入生日");
                    return;
                } else {
                    entity.setNumber(number);
                    entity.setName(name);
                    entity.setSex(sex);
                    entity.setIdNumber(idNumber);
                    entity.setBirthday(birthday);
                    entity.setDoctorid(doctor);
                    getCureInfoManage().save(entity, Constant.PATIENT_UPDATE, new INormalEventListener() {
                        @Override
                        public void onValueEvent(int key, Object value) {
                            int result = (int) value;
                            if (result == 1) {
                                LogUtils.e("保存编辑病人数据成功");
                                MyApplication.getInstance().activePatientId = number;

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
                break;
        }
    }

    private void chooseDoctor() {
        getCureInfoManage().getAllDoctorDetail(new INormalEventListener() {
            @Override
            public void onValueEvent(int key, final Object value) {
                if (value == null) {
                    LogUtils.e("没有获取到用户信息");
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final List<DbDoctorEntity> listPerson = (List<DbDoctorEntity>) value;
                            if (listPerson.size() == 0) {
                                ToastUtil.s("没有获取到注册医生信息！");
                                return;
                            }
                            final String items[] = new String[listPerson.size()];
                            int curDoctorIndex = 0;
                            for (int i = 0; i < listPerson.size(); i++) {
                                DbDoctorEntity mEntity = listPerson.get(i);
                                String mNick = mEntity.getNick();
                                if (TextUtils.isEmpty(mNick)) {
                                    items[i] = "**";
                                } else {
                                    items[i] = mNick;
                                }

                                String mDoctorid = mEntity.getAccount();
                                if (mDoctorid.equals(doctorNumber)) {
                                    curDoctorIndex = i;
                                }
                            }

                            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogCustom);  //先得到构造器
                            builder.setTitle("选择主治医生"); //设置标题
                            builder.setSingleChoiceItems(items, curDoctorIndex, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    doctor_et.setText(items[which]);
                                    doctorNumber = listPerson.get(which).getAccount();
                                }
                            });
                            builder.create().show();
                        }
                    });
                }
            }

            @Override
            public void onErrEvent(int errCode) {
                LogUtils.e("获取注册医生信息失败");
            }
        });
    }

    protected void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditPatientActivity.this, R.style.AlertDialogCustom);
        builder.setTitle("警告");
        builder.setMessage("确定删除当前病例吗？若确定将会删除当前病人资料和病人所有病例数据。请谨慎删除！");
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
                if (!TextUtils.isEmpty(cureNumber)) {
                    getCureInfoManage().delPatientDetail(cureNumber, new INormalEventListener() {
                        @Override
                        public void onValueEvent(int key, Object value) {

                        }

                        @Override
                        public void onErrEvent(int errCode) {

                        }
                    });

                    getCureInfoManage().delCureDetail(cureNumber, new INormalEventListener() {
                        @Override
                        public void onValueEvent(int key, Object value) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent i = new Intent(EditPatientActivity.this, MainActivity.class);
                                    startActivity(i);
                                }
                            });
                        }

                        @Override
                        public void onErrEvent(int errCode) {
                            LogUtils.e("删除病人数据失败");
                        }
                    });
                }
            }
        });
        builder.show();
    }

    public void chooseSex() {
        final String items[] = {"男", "女"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);  //先得到构造器
        builder.setTitle("选择性别"); //设置标题
        builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                sexEt.setText(items[which]);
            }
        });
        builder.create().show();
    }

    private void chooseDate() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(EditPatientActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                birthdayEt.setText(year + "年" + (monthOfYear + 1) + "月" + dayOfMonth + "日");
            }
        }, 1990, 0, 1);
        datePickerDialog.show();
    }

    @OnClick(R.id.head_img)
    public void onClick() {
        if (CameraUtil.hasCamera()) {
            takePhoto();
        } else {
            ToastUtil.s("当前设备没有可用摄像头");
        }
    }

    private String mTempPhotoPath;

    private void takePhoto() {
        mTempPhotoPath = Environment.getExternalStorageDirectory() + File.separator + "photo.jpg";

        Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //下面这句指定调用相机拍照后的照片存储的路径
        takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(mTempPhotoPath)));
        startActivityForResult(takeIntent, CAMERA_REQUEST_CODE);
    }

    /**
     * 处理剪切成功的返回值
     *
     * @param result
     */
    private void handleCropResult(Intent result) {
        deleteTempPhotoFile();
        final Uri resultUri = UCrop.getOutput(result);
        if (null != resultUri) {
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            headImg.setScaleType(ImageView.ScaleType.FIT_XY);
            headImg.setImageBitmap(bitmap);

            final File myCaptureFile = new File(resultUri.getPath());

            getCureInfoManage().getPatientImg(cureNumber, new INormalEventListener() {
                @Override
                public void onValueEvent(int key, Object value) {
                    boolean bSave = (key == 0) ? true : false;

                    DbPatientImgEntity entity = new DbPatientImgEntity();
                    entity.setAccount(cureNumber);
                    entity.setFile_image(myCaptureFile);

                    if (bSave) {
                        getCureInfoManage().save(entity, Constant.PATIENT_IMG_ADD, new INormalEventListener() {
                            @Override
                            public void onValueEvent(int key, Object value) {
                                int result = (int) value;
                                if (result == 1) {
                                    LogUtils.e("保存图片成功");
                                } else {
                                    LogUtils.e("保存图片失败");
                                }
                            }

                            @Override
                            public void onErrEvent(int errCode) {
                                LogUtils.e("保存图片失败");
                            }
                        });
                    } else {
                        getCureInfoManage().save(entity, Constant.PATIENT_IMG_UPDATE, new INormalEventListener() {
                            @Override
                            public void onValueEvent(int key, Object value) {
                                int result = (int) value;
                                if (result == 1) {
                                    LogUtils.e("更新图片成功");
                                } else {
                                    LogUtils.e("更新图片失败");
                                }
                            }

                            @Override
                            public void onErrEvent(int errCode) {
                                LogUtils.e("更新图片失败");
                            }
                        });
                    }
                }

                @Override
                public void onErrEvent(int errCode) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.s("保存头像失败");
                        }
                    });
                }
            });

        } else {
            ToastUtil.s("无法剪切选择图片");
        }
    }

    /**
     * 处理剪切失败的返回值
     *
     * @param result
     */
    private void handleCropError(Intent result) {
        deleteTempPhotoFile();
        final Throwable cropError = UCrop.getError(result);
        if (cropError != null) {
            ToastUtil.s(cropError.getMessage());
        } else {
            ToastUtil.s("无法剪切选择图片");
        }
    }

    /**
     * 删除拍照临时文件
     */
    private void deleteTempPhotoFile() {
        File tempFile = new File(mTempPhotoPath);
        if (tempFile.exists() && tempFile.isFile()) {
            tempFile.delete();
        }
    }

    public void startCropActivity(Uri uri) {
        Uri url_filePath = Uri.fromFile(new File(path));
        UCrop.of(uri, url_filePath)
                .withAspectRatio(1, 1)
                .withMaxResultSize(200, 200)
                .withTargetActivity(CropActivity.class)
                .start(this);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CAMERA_REQUEST_CODE: {
                    String tfPath = null;
                    if (getCureInfoManage().getInfoMode == 0) {
                        tfPath = getStorageManage().getExtendCachePath();
                        if (TextUtils.isEmpty(tfPath)) {
                            ToastUtil.s("您没有装备TF卡，无法存储头像");
                            return;
                        }
                    } else
                        tfPath = getStorageManage().getInnerCachePath();

                    path = tfPath + "/" + cureNumber + ".jpg";
                    File temp = new File(mTempPhotoPath);
                    startCropActivity(Uri.fromFile(temp));
                    break;
                }
                case UCrop.REQUEST_CROP: {
                    handleCropResult(data);
                    break;
                }
                case UCrop.RESULT_ERROR: {
                    handleCropError(data);
                    break;
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bm != null) {
            headImg.setImageBitmap(null);
            bm.recycle();
            bm = null;
        }
    }
}
