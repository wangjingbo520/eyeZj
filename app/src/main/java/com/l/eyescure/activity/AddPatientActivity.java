package com.l.eyescure.activity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.apkfuns.logutils.LogUtils;
import com.kevin.crop.UCrop;
import com.l.eyescure.R;
import com.l.eyescure.application.MyApplication;
import com.l.eyescure.server.SqlManage.localsql.DbCureDetailEntity;
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
 * Description:新增病例
 * Date: 2017/4/9
 */

public class AddPatientActivity extends BaseActivity {
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
    @BindView(R.id.add_paient_view_top)
    TopBarView viewTop;

    private static final int CAMERA_REQUEST_CODE = 1;    // 相机拍照标记
    //    private final int TAKE_PICTURE = 1;
    private String filePath = null;
    private DbCureDetailEntity userEntity = null;
    private int currentNumber = 0;
    private boolean isExist = false;
    private Bitmap bm = null;
    private String number = null;

    @Override
    protected int bindLayout() {
        return R.layout.activity_addpaient;
    }

    @Override
    protected boolean isNeedTitle() {
        return false;
    }

    @Override
    protected void initView() {
        setTitle("新增病例");
//        Bundle bundle = getIntent().getExtras();
//        if (bundle != null){
//            userEntity = (DbCureDetailEntity) bundle.getSerializable("data");
//            if (userEntity != null){
//                showLoadDialog("加载数据中...");
//                currentNumber = Integer.valueOf(userEntity.getNumber());
//                search();
//            }
//        }
        setTopBarView(viewTop);
    }

    @Override
    protected int topHelp() {
        showHelpDialog();
        return 0;
    }

    protected void showHelpDialog() {
        String showStr = "病人的编号不能重复，需要11位数字组成，建议病人的手机号。\n" +
                "病人编号是搜索病人信息的关键字。\n" +
                "清空输入会清除病人的所有信息，需要重新输入。\n" +
                "新增病例会提交当前输入的信息到服务器。提交成功，则病例建立。";
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


    /**
     * 递归查找当前数据是否存在
     */
//    private void search(){
//        isExist = MyDbUtils.getInstance().searchNum(String.valueOf(currentNumber));
//        if (isExist){
//            currentNumber += (int)(Math.random() * 10);
//            search();
//        }else{
//            dismissLoad();
//            patientNumberEt.setText(currentNumber+"");
//            nameEt.setText(userEntity.getName());
//            sexEt.setText(userEntity.getSex());
//            idNumberEt.setText(userEntity.getIdNumber());
//            birthdayEt.setText(userEntity.getBirthday());
//            if (userEntity.getHeadPath() != null) {
//                bm = BitmapFactory.decodeFile(userEntity.getHeadPath());
//                headImg.setScaleType(ImageView.ScaleType.FIT_XY);
//                headImg.setImageBitmap(bm);
//            }
//        }
//    }
    @OnClick({R.id.main_clear, R.id.main_add, R.id.sex_et, R.id.birthday_et})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sex_et://性别选择
                JayUtils.hideKeyboard(AddPatientActivity.this);
                chooseSex();
                break;
            case R.id.birthday_et://生日选择
                JayUtils.hideKeyboard(AddPatientActivity.this);
                chooseDate();
                break;
            case R.id.main_clear://清空输入
                patientNumberEt.setText("");
                nameEt.setText("");
                idNumberEt.setText("");
                birthdayEt.setText("");
                sexEt.setText("");
                headImg.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.patient_img_default_bg));
                filePath = null;
                break;
            case R.id.main_add://新增病例
                JayUtils.hideKeyboard(AddPatientActivity.this);
                number = patientNumberEt.getText().toString().trim();
                if (!checkAccountInput(number)) {
                    break;
                }

                String name = nameEt.getText().toString().trim();
                if (!checkNickInput(name)) {
                    break;
                }

                String idNumber = idNumberEt.getText().toString().trim();
                String birthday = birthdayEt.getText().toString().trim();
                String sex = sexEt.getText().toString().trim();
                if (TextUtils.isEmpty(sex)) {
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
                    checkPatientRepeat(number, name, sex, idNumber, birthday);
                }
                break;
        }
    }

    private void checkPatientRepeat(final String number, final String name, final String sex, final String idNumber, final String birthday) {
        getCureInfoManage().getOnePatientDetail(number, new INormalEventListener() {
            @Override
            public void onValueEvent(int key, Object value) {
                DbPatientEntity entity = (DbPatientEntity) value;
                if (entity != null && !TextUtils.isEmpty(entity.getName())) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.s("该编号用户已存在，请更改编号");
                        }
                    });
                } else {
                    getCureInfoManage().getPatientAllCureDetail(number, new INormalEventListener() {
                        @Override
                        public void onValueEvent(int key, Object value) {
                            DbPatientEntity userEntity = new DbPatientEntity();
                            String doctor = getCureInfoManage().getLoginerAccount();
                            userEntity.setDoctorid(doctor);
                            userEntity.setNumber(number);
                            userEntity.setName(name);
                            userEntity.setSex(sex);
                            userEntity.setIdNumber(idNumber);
                            userEntity.setBirthday(birthday);

                            List<DbCureDetailEntity> detailEntities = (List<DbCureDetailEntity>) value;
                            if (detailEntities == null) {
                                userEntity.setCureNumber(0);
                            } else {
                                userEntity.setCureNumber(detailEntities.size());
                            }

                            getCureInfoManage().save(userEntity, Constant.PATIENT_ADD, new INormalEventListener() {
                                @Override
                                public void onValueEvent(int key, Object value) {
                                    final int result = (int) value;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (result == 1) {
                                                ToastUtil.s("新增病例成功");
                                                MyApplication.getInstance().activePatientId = number;

                                                finish();
                                            } else {
                                                ToastUtil.s("新增病例失败");
                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onErrEvent(int errCode) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ToastUtil.s("新增病例失败");
                                        }
                                    });
                                }
                            });
                        }

                        @Override
                        public void onErrEvent(int errCode) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil.s("新增病例失败");
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
                        ToastUtil.s("新增病例失败");
                    }
                });
            }
        });
    }

    @OnClick(R.id.head_img)
    public void onClick() {
        number = patientNumberEt.getText().toString().trim();
        if (!checkAccountInput(number)) {
            return;
        }

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
        DatePickerDialog datePickerDialog = new DatePickerDialog(AddPatientActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                birthdayEt.setText(year + "年" + (monthOfYear + 1) + "月" + dayOfMonth + "日");
            }
        }, 1990, 0, 1);
        datePickerDialog.show();
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

                    filePath = tfPath + "/" + number + ".jpg";
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
//        if (requestCode == TAKE_PICTURE) {
//            if (resultCode == RESULT_OK) {
//                Bitmap bm = (Bitmap) data.getExtras().get("data");
//                filePath = MyApplication.getInstance().getCachePath() + "/" + number + ".jpg";
////                ToastUtil.s(filePath);
//                File myCaptureFile = new File(filePath);
//                try {
//                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
//                    /* 采用压缩转档方法 */
//                    bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
//                    /* 调用flush()方法，更新BufferStream */
//                    bos.flush();
//                    /* 结束OutputStream */
//                    bos.close();
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                bm = BitmapUtil.getImageThumbnail(filePath,50,50);
//                headImg.setScaleType(ImageView.ScaleType.FIT_XY);
//                headImg.setImageBitmap(bm);//想图像显示在ImageView视图上，private ImageView img;
//            }
//        }
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

            //保存图片
            final File myCaptureFile = new File(resultUri.getPath());
            DbPatientImgEntity entity = new DbPatientImgEntity();
            entity.setAccount(number);
            entity.setFile_image(myCaptureFile);
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
        Uri url_filePath = Uri.fromFile(new File(filePath));
        UCrop.of(uri, url_filePath)
                .withAspectRatio(1, 1)
                .withMaxResultSize(200, 200)
                .withTargetActivity(CropActivity.class)
                .start(this);
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
