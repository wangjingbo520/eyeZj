package com.l.eyescure.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.apkfuns.logutils.LogUtils;
import com.l.eyescure.R;
import com.l.eyescure.activity.adapter.StorageListAdapter;
import com.l.eyescure.server.SqlManage.localsql.DbDoctorEntity;
import com.l.eyescure.server.callBack.INormalChangeCallback;
import com.l.eyescure.server.callBack.INormalEventListener;
import com.l.eyescure.server.callBack.IStorageChangeCallback;
import com.l.eyescure.server.pdfManager.StorageInfo;
import com.l.eyescure.util.Constant;
import com.l.eyescure.util.SPUtils;
import com.l.eyescure.util.ToastUtil;
import com.l.eyescure.view.TopBarView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class SetSavePdfPathActivity extends BaseActivity {
    @BindView(R.id.storages_gv_list)
    GridView storages_gv_list;

    @BindView(R.id.storages_tx_ok)
    TextView storages_tx_ok;

    @BindView(R.id.save_view_top)
    TopBarView viewTop;

    @BindView(R.id.storage_tf_con)
    RelativeLayout storage_tf_con;

    @BindView(R.id.storages_tf_btn)
    RelativeLayout storages_tf_btn;

    @BindView(R.id.storage_tf_check_sign)
    ImageView storage_tf_check_sign;

    private List<StorageInfo> allStorages = new ArrayList<>();
    private StorageListAdapter mStorageListAdapter;
    private int curPostion = 0;
    private int pageType = 0;
    private DbDoctorEntity dbLoginEntity = null;

    @Override
    protected int bindLayout() {
        return R.layout.activity_set_save_pdf_path;
    }

    @Override
    protected boolean isNeedTitle() {
        return false;
    }

    protected void initView() {
        pageType = getIntent().getIntExtra("savetype", 0);
        setTopBarView(viewTop);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(getStorageManage()!=null){
            getStorageManage().registerStorageInfoChangedCallback(storageChange);
            getStorageManage().registerTfInfoChangedCallback(tfChange);
        }

        initData();

        if (pageType == 0) {
            setTitle("设置导出设备");
            refreshSdInfo();
        } else if (pageType == 1) {
            setTitle("设置保存设备");
            storages_tf_btn.setVisibility(View.GONE);
        }
    }

    private void refreshSdInfo() {
        String sdPath = getStorageManage().getHandwareSd();
        if (!TextUtils.isEmpty(sdPath)) {
            storages_tf_btn.setVisibility(View.VISIBLE);
            if (getStorageManage().IsSameSdCard()) {
                storage_tf_check_sign.setBackgroundResource(R.drawable.storage_checked_p);
            } else {
                storage_tf_check_sign.setBackgroundResource(R.drawable.storage_checked_n);
            }
        } else {
            storages_tf_btn.setVisibility(View.GONE);
        }
    }

    private IStorageChangeCallback storageChange = new IStorageChangeCallback() {
        @Override
        public void onChanged(List<StorageInfo> allStorages) {
            refreshStorageInfo(allStorages);
        }
    };

    private INormalChangeCallback tfChange = new INormalChangeCallback() {
        @Override
        public void onChanged(final boolean bMount) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (bMount) {
                        refreshSdInfo();
                    } else {
                        storages_tf_btn.setVisibility(View.GONE);
                    }
                }
            });
        }
    };

    @Override
    protected void onDestroy() {
        getStorageManage().unregisterStorageInfoChangedCallback(storageChange);
        getStorageManage().unregisterTfInfoChangedCallback(tfChange);
        super.onDestroy();
    }

    @Override
    protected int topHelp() {
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
        if(getCureInfoManage().isSuperLogin()) {
            readyGo(AccountManagerActivity.class);
        }
    }

    private void refreshStorageInfo(final List<StorageInfo> mUsbList) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                refreshStorageData(mUsbList);

                if (mStorageListAdapter != null)
                    mStorageListAdapter.notifyDataSetChanged();
            }
        });
    }

    private void refreshStorageData(List<StorageInfo> mUsbList) {
        allStorages.clear();
        for (int i = 0; i < mUsbList.size(); i++) {
            StorageInfo mInfo = mUsbList.get(i);
            if (mInfo != null)
                allStorages.add(mInfo);
        }
    }

    private void initData() {
        String account = SPUtils.get(this, "account", "").toString();
		if(getCureInfoManage()!=null){
        getCureInfoManage().getOneDoctor(account, new INormalEventListener() {
            @Override
            public void onValueEvent(int key, Object value) {
                if (value == null) {
                    LogUtils.e("获取到用户数据为空");
                } else {
                    dbLoginEntity = (DbDoctorEntity)value;
                    if(TextUtils.isEmpty(dbLoginEntity.getAccount())){
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

     if(getStorageManage()!=null){
        List<StorageInfo> mList = getStorageManage().getAllStorages();
        refreshStorageData(mList);
        if (allStorages == null || allStorages.size() == 0) {
            Toast.makeText(this, "您未插入存储设备！", Toast.LENGTH_SHORT).show();
        }
		}

        mStorageListAdapter = new StorageListAdapter(this, allStorages, pageType);
        storages_gv_list.setAdapter(mStorageListAdapter);

        storages_gv_list.setOnItemClickListener(storageClick);

        curPostion = getCurPosition();
        storages_gv_list.setSelection(curPostion);
    }

    private int getCurPosition() {
        String path = getSavedPosition();
        if (TextUtils.isEmpty(path) || allStorages.size() == 0)
            return -1;
        if (TextUtils.isEmpty(path))
            return -1;
        for (int i = 0; i < allStorages.size(); i++) {
            StorageInfo curinfo = allStorages.get(i);
            if (curinfo == null || (TextUtils.isEmpty(curinfo.path))) {
                continue;
            }

            if ((curinfo.path).equals(path)) {
                return i;
            }
        }
        return -1;
    }

    private String getSavedPosition() {
        String path = null;
        if (pageType == 0) {
            path = util.readString(Constant.STORAGE_USB_PATH);
        } else if (pageType == 1) {
            path = util.readString(Constant.STORAGE_TF_PATH);
        }
        return path;
    }

    AdapterView.OnItemClickListener storageClick = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            StorageInfo clickInfo = allStorages.get(position);
            clickInfo.isChecked = true;

            curPostion = position;
            mStorageListAdapter.notifyDataSetChanged();

            //记忆当前的位置
            if (pageType == 1) {
                getStorageManage().saveUsbPath(clickInfo.path);
            } else {
                getStorageManage().saveTFPath(1, clickInfo.path);
                storage_tf_check_sign.setBackgroundResource(R.drawable.storage_checked_n);
            }
        }
    };

    @OnClick({R.id.storages_tx_ok, R.id.storage_tf_con})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.storages_tx_ok: {
                if (pageType == 1) {
                    saveUsbInfo();
                } else {
                    saveTfInfo();
                }
                break;
            }
            case R.id.storage_tf_con: {
                //选中TF卡
                getStorageManage().saveTFPath(0, null);
                storage_tf_check_sign.setBackgroundResource(R.drawable.storage_checked_p);
                break;
            }
        }
    }

    private void saveTfInfo() {

    }

    private void saveUsbInfo() {
        if (curPostion < 0)
            return;
        if (allStorages.size() == 0)
            return;
        StorageInfo info = allStorages.get(curPostion);
        if (info == null || (TextUtils.isEmpty(info.path)))
            return;
        String path = info.path;
        if (pageType == 0) {
            util.saveString(Constant.STORAGE_USB_PATH, path);
        } else if (pageType == 1) {
            util.saveString(Constant.STORAGE_TF_PATH, path);
        }
        getCureInfoManage().save(dbLoginEntity, Constant.DOCTOR_UPDATE, null);
    }
}
