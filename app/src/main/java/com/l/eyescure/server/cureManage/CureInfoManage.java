package com.l.eyescure.server.cureManage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.apkfuns.logutils.LogUtils;
import com.l.eyescure.application.MyApplication;
import com.l.eyescure.server.EyesModule;
import com.l.eyescure.server.ServerHelper;
import com.l.eyescure.server.SqlManage.jdbcManage.MySqlManage;
import com.l.eyescure.server.SqlManage.jdbcManage.sqlmsg.JdbcMySqlStrFactory;
import com.l.eyescure.server.SqlManage.jdbcManage.sqlmsg.MsgUnit;
import com.l.eyescure.server.SqlManage.localsql.DbCureDetailEntity;
import com.l.eyescure.server.SqlManage.localsql.DbCureStepEntity;
import com.l.eyescure.server.SqlManage.localsql.DbDoctorEntity;
import com.l.eyescure.server.SqlManage.localsql.DbPatientEntity;
import com.l.eyescure.server.SqlManage.localsql.DbPatientImgEntity;
import com.l.eyescure.server.StorageManager.SharedPreferencesUtil;
import com.l.eyescure.server.StorageManager.StorageManager;
import com.l.eyescure.server.callBack.INormalEventListener;
import com.l.eyescure.util.Constant;
import com.l.eyescure.util.DateUtil;
import com.l.eyescure.util.ThreadUtil;

import org.xutils.DbManager;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/9/14.
 */
public class CureInfoManage extends EyesModule {
    public int getInfoMode = 0; // 0 从本地获得数据 1 从服务器获得数据
    public SharedPreferencesUtil util;
    private DbManager db;
    private DbManager.DaoConfig daoConfig;
    private DbDoctorEntity loginDoctorEntity, superDoctorEntity;
    private Handler mHandler;
    private LocalBroadcastManager broadcastManager;
    private boolean bDbSetedDir = false;

    public DbDoctorEntity getLoginDoctorEntity() {
        return loginDoctorEntity;
    }

    public void setLoginDoctorEntity(DbDoctorEntity loginDoctorEntity) {
        this.loginDoctorEntity = loginDoctorEntity;
    }

    public String getLoginerAccount() {
        DbDoctorEntity doctor = getLoginDoctorEntity();
        if (doctor != null) {
            return doctor.getAccount();
        }
        return null;
    }

    public boolean isSuperLogin() {
        if (loginDoctorEntity == null)
            return false;
        String account = loginDoctorEntity.getAccount();
        if (!TextUtils.isEmpty(account) && account.equals("admins")) {
            return true;
        }
        return false;
    }

    public String getSuperPassword() {
        String password;
        if (getInfoMode == 0) {
            password = util.readString(Constant.GET_LOCAL_SUPER_PASSWORD);
        } else {
            password = util.readString(Constant.GET_NET_SUPER_PASSWORD);
        }
        if (TextUtils.isEmpty(password)) {
            password = "123456";
        }
        return password;
    }


    @Override
    public void init(int step) {
        switch (step) {
            case 0: {
                init0();
                break;
            }
            case 1: {
                init1();
            }
        }
    }

    private void init0() {
        mHandler = new Handler();
        util = new SharedPreferencesUtil(mContext);

        syncNetMode();

        if (broadcastManager == null)
            broadcastManager = LocalBroadcastManager.getInstance(mContext);

        broadcastManager.sendBroadcast(new Intent(Constant.ACTION_CURE_MANAGE_INIT_OK));
    }

    public void init1() {
        db = getDbManger();
        fristSaveSuperAccount();
    }

    public void syncNetMode() {
        getInfoMode = util.readInt(Constant.GET_NET_MODE);

        if (getInfoMode < 0) {
            getInfoMode = 1;
            util.saveInt(Constant.GET_NET_MODE, 1);
        }
    }

    public boolean isbDbSetedDir() {
        return bDbSetedDir;
    }

    public void setbDbSetedDir(boolean bDbSetedDir) {
        this.bDbSetedDir = bDbSetedDir;
    }

    /**
     * 得到DbManager
     *
     * @return
     */
    public DbManager getDbManger() {
        if (db == null) {
            if (daoConfig == null) {
                daoConfig = getConfig();
            }
            db = x.getDb(daoConfig);
        }
        return db;
    }

    /**
     * 得到daoconfig
     */
    public DbManager.DaoConfig getConfig() {
        if (daoConfig == null) {
            daoConfig = new DbManager.DaoConfig().setDbName("EyesCure")// 数据库名
                    .setDbVersion(1);  // 数据库版本
            setDaoConfigDir(true);
        } else {
            daoConfig = getDbManger().getDaoConfig();
        }
        return daoConfig;
    }

    public void setDaoConfigDir(boolean bCreat) {
        if (bDbSetedDir)
            return;

        StorageManager storageManager = (StorageManager) ServerHelper.getInstance().getModule(ServerHelper.MODULE_ID_STORAGE);
        String path = storageManager.getHandwareSd();
        if (!TextUtils.isEmpty(path)) {
            path = path + "/" + "EyesCureDb";

            File mFile = new File(path);
            if (!mFile.exists()) {
                if (!mFile.mkdir()) {
                    return;
                }
            }
            daoConfig.setDbDir(mFile); // 数据库存储路径
            bDbSetedDir = true;

            if (!bCreat) {
                db = x.getDb(daoConfig);
                fristSaveSuperAccount();
            }
        }
    }

    @Override
    public void done(int step) {

    }

    public void fristSaveSuperAccount() {
        int netMode = util.readInt(Constant.GET_NET_MODE);
        if (netMode < 0) {
            netMode = 1;
            util.saveInt(Constant.GET_NET_MODE, 1);
        }

        final int finalNetMode = netMode;
        getOneDoctor("admins", new INormalEventListener() {
            @Override
            public void onValueEvent(int key, Object value) {
                boolean haveFindSuper = false;
                if (value == null) {
                    LogUtils.e("未能获取超级用户");
                } else {
                    DbDoctorEntity entity = (DbDoctorEntity) value;
                    if (!TextUtils.isEmpty(entity.getAccount())) {
                        haveFindSuper = true;
                        superDoctorEntity = entity;
                        saveSuperPassword(entity);
                    }
                }

                if (!haveFindSuper) {
                    if (finalNetMode == 0) {
                        if (bDbSetedDir) {
                            DbDoctorEntity loginEntity = new DbDoctorEntity();
                            loginEntity.setAccount("admins");
                            loginEntity.setPassword("123456");
                            loginEntity.setNick("超级管理员");
                            loginEntity.setCreate_date(DateUtil.getCurDateStr());
                            loginEntity.setIsLogin(1);
                            save(loginEntity, Constant.DOCTOR_ADD, new INormalEventListener() {
                                @Override
                                public void onValueEvent(int key, Object value) {
                                    if (value == null) {
                                        LogUtils.e("未能存储超级用户");
                                        return;
                                    }
                                    int result = (int) value;
                                    if (result == 1) {
                                        LogUtils.e("已存储超级用户");
                                    } else {
                                        LogUtils.e("未能存储超级用户");
                                    }
                                }

                                @Override
                                public void onErrEvent(int errCode) {
                                    LogUtils.e("未能存储超级用户");
                                }
                            });
                        } else {
                            MyApplication.getInstance().showToastStr("没有检测到TF卡，请插入TF卡！");
                        }
                    } else {
                        MyApplication.getInstance().showToastStr("数据异常，没有检测到管理员账号，请联系数据库服务人员！");
                    }
                }
            }

            @Override
            public void onErrEvent(int errCode) {
                LogUtils.e("获取超级用户信息失败");
            }
        });
    }

    public void saveSuperPassword(DbDoctorEntity entity) {
        if (entity == null)
            return;
        String password = entity.getPassword();
        if (TextUtils.isEmpty(password))
            return;
        int netMode = util.readInt(Constant.GET_NET_MODE);
        if (netMode == 0) {
            util.saveString(Constant.GET_LOCAL_SUPER_PASSWORD, password);
        } else if (netMode == 1) {
            util.saveString(Constant.GET_NET_SUPER_PASSWORD, password);
        }
    }

    public void getPatientAllCureDetail(final String patientId, final INormalEventListener callback) {
        if (TextUtils.isEmpty(patientId) || callback == null)
            return;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                // 0 从本地获得数据 1 从服务器获得数据
                if (getInfoMode == 0) {
                    List<DbCureDetailEntity> users = null;
                    try {
                        ThreadUtil.isMainThread();
                        users = db.selector(DbCureDetailEntity.class).where("number", "=", patientId).findAll();
                    } catch (DbException e) {
                        e.printStackTrace();
                        callback.onErrEvent(Constant.ERR_CMD_EXECUDT_FAILED);
                        return;
                    }
                    callback.onValueEvent(1, users);
                } else {
                    String mySql_str = JdbcMySqlStrFactory.queryCurInfoDb(patientId);
                    MsgUnit msg = new MsgUnit(mySql_str, Constant.CUREINFO_QUERY, callback);
                    Log.e("=====>", "run: " + msg.toString());
                    MySqlManage.getInstance().sendMsg(msg);
                }
            }
        });
    }
//    public boolean isMainThread() {
//        return Looper.getMainLooper().getThread().getId() == Thread.currentThread().getId();
//    }

    public void getOneCureDetail(final String curID, final INormalEventListener callback) {
        if (TextUtils.isEmpty(curID) || callback == null)
            return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (getInfoMode == 0) {
                    DbCureDetailEntity user = null;
                    try {
//                        boolean mainThread = isMainThread();
//                        Log.e("=======>", "线程运行在主线程吗？==》" + mainThread);

                        user = db.selector(DbCureDetailEntity.class).where("cureId", "=", curID).findFirst();
                    } catch (DbException e) {
                        e.printStackTrace();
                        callback.onErrEvent(Constant.ERR_CMD_EXECUDT_FAILED);
                        return;
                    }
                    callback.onValueEvent(1, user);
                } else {
                    String mySql_str = JdbcMySqlStrFactory.queryOneCurInfoDb(curID);
                    MsgUnit msg = new MsgUnit(mySql_str, Constant.CUREINFO_ONE_QUERY, callback);
                    MySqlManage.getInstance().sendMsg(msg);
                }
            }
        }).start();

    }

    public void getCurrCureDetailIndex(final String patientId, final String curID, final INormalEventListener callback) {
        if (TextUtils.isEmpty(patientId) || TextUtils.isEmpty(curID) || callback == null)
            return;
        new Thread(new Runnable() {
            @Override
            public void run() {

                if (getInfoMode == 0) {
                    int result = 0;
                    List<DbCureDetailEntity> users = null;
                    try {
                        users = db.selector(DbCureDetailEntity.class).where("number", "=", patientId).findAll();
                    } catch (DbException e) {
                        e.printStackTrace();
                        callback.onErrEvent(Constant.ERR_CMD_EXECUDT_FAILED);
                        return;
                    }
                    if (users == null) {
                        callback.onValueEvent(0, -1);
                    } else {
                        for (int i = 0; i < users.size(); i++) {
                            DbCureDetailEntity cde = users.get(i);
                            if (cde != null && !TextUtils.isEmpty(cde.getCureId()) && cde.getCureId().equals(curID)) {
                                callback.onValueEvent(1, i);
                                return;
                            }
                        }
                        callback.onValueEvent(0, -1);
                    }
                } else {
                    String mySql_str = JdbcMySqlStrFactory.queryCurInfoDb(patientId);
                    MsgUnit msg = new MsgUnit(mySql_str, Constant.CUREINFO_QUERY, new INormalEventListener() {
                        @Override
                        public void onValueEvent(int key, Object value) {
                            List<DbCureDetailEntity> users = (List<DbCureDetailEntity>) value;
                            if (users.size() == 0) {
                                callback.onValueEvent(0, -1);
                            } else {
                                for (int i = 0; i < users.size(); i++) {
                                    DbCureDetailEntity cde = users.get(i);
                                    if (cde != null && !TextUtils.isEmpty(cde.getCureId()) && cde.getCureId().equals(curID)) {
                                        callback.onValueEvent(1, i);
                                        return;
                                    }
                                }
                                callback.onValueEvent(0, -1);
                            }
                        }

                        @Override
                        public void onErrEvent(int errCode) {
                            callback.onErrEvent(Constant.ERR_CMD_EXECUDT_FAILED);
                        }
                    });
                    MySqlManage.getInstance().sendMsg(msg);
                }
            }
        }).start();

    }

    public void getAllDoctorDetail(final INormalEventListener callback) {
        if (callback == null)
            return;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (getInfoMode == 0) {
                    List<DbDoctorEntity> users = new ArrayList<>();
                    try {
                        users = db.selector(DbDoctorEntity.class).findAll();
                    } catch (DbException e) {
                        e.printStackTrace();
                        callback.onErrEvent(Constant.ERR_CMD_EXECUDT_FAILED);
                        return;
                    }
                    callback.onValueEvent(1, users);
                } else {
                    String mySql_str = JdbcMySqlStrFactory.queryDoctorDb();
                    MsgUnit msg = new MsgUnit(mySql_str, Constant.DOCTOR_QUERY, callback);
                    MySqlManage.getInstance().sendMsg(msg);
                }
            }
        });
    }

    public void getOneDoctor(final String doctorId, final INormalEventListener callback) {
        if (TextUtils.isEmpty(doctorId) || callback == null)
            return;

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (getInfoMode == 0) {
                    DbDoctorEntity user = null;
                    try {
                        user = db.selector(DbDoctorEntity.class).where("account", "=", doctorId).findFirst();
                    } catch (DbException e) {
                        e.printStackTrace();
                        callback.onErrEvent(Constant.ERR_CMD_EXECUDT_FAILED);
                        return;
                    }

                    callback.onValueEvent(1, user);
                } else {

                    String mID = doctorId;
                    if (mID.equals("admins")) {
                        mID = "admins     ";
                    }
                    String mySql_str = JdbcMySqlStrFactory.queryOneDoctorDb(mID);
                    MsgUnit msg = new MsgUnit(mySql_str, Constant.DOCTOR_ONE_QUERY, callback);
                    MySqlManage.getInstance().sendMsg(msg);
                }
            }
        });
    }

    public void getDoctorAllPatientDetail(final String doctorId, final INormalEventListener callback) {
        if (TextUtils.isEmpty(doctorId) || callback == null)
            return;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (getInfoMode == 0) {
                    List<DbPatientEntity> users = null;
                    try {
                        users = db.selector(DbPatientEntity.class).where("doctorid", "=", doctorId).findAll();
                    } catch (DbException e) {
                        e.printStackTrace();
                        callback.onErrEvent(Constant.ERR_CMD_EXECUDT_FAILED);
                        return;
                    }
                    callback.onValueEvent(1, users);
                } else {
                    String mySql_str = JdbcMySqlStrFactory.queryPatientDb(doctorId);
                    MsgUnit msg = new MsgUnit(mySql_str, Constant.PATIENT_QUERY, callback);
                    MySqlManage.getInstance().sendMsg(msg);
                }
            }
        });
    }

    public void getOnePatientDetail(final String patientId, final INormalEventListener callback) {
        if (TextUtils.isEmpty(patientId) || callback == null)
            return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (getInfoMode == 0) {
                    DbPatientEntity user = null;
                    try {
                        user = db.selector(DbPatientEntity.class).where("number", "=", patientId).findFirst();
                    } catch (DbException e) {
                        e.printStackTrace();
                        callback.onErrEvent(Constant.ERR_CMD_EXECUDT_FAILED);
                        return;
                    }
                    callback.onValueEvent(1, user);
                } else {
                    String mySql_str = JdbcMySqlStrFactory.queryOnePatientDb(patientId);
                    MsgUnit msg = new MsgUnit(mySql_str, Constant.PATIENT_ONE_QUERY, callback);
                    MySqlManage.getInstance().sendMsg(msg);
                }
            }
        }).start();

    }

    private void savePatientImg(final Object entity, final INormalEventListener callback) {
        if (entity == null || callback == null)
            return;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (getInfoMode == 0) {
                    //代码中自动存储了
                    return;
                } else {
                    String mySql_str = JdbcMySqlStrFactory.insertPatientImgDb();

                    if (TextUtils.isEmpty(mySql_str)) {
                        callback.onErrEvent(Constant.ERR_CMD_EXECUDT_FAILED);
                        return;
                    }

                    MsgUnit msg = new MsgUnit(mySql_str, Constant.PATIENT_IMG_ADD, callback);

                    MySqlManage.getInstance().sendMsg(msg);
                }
            }
        });
    }

    /**
     * 保存数据
     *
     * @param entity
     * @return true 保存成功 ，false保存失败
     */
    public synchronized void save(final Object entity, final int cmdType, final INormalEventListener callback) {
        if (entity == null || callback == null)
            return;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                int cmd = cmdType;
                if (getInfoMode == 0) {
                    switch (cmdType) {
                        case 61:
                        case 62: {
                            break;
                        }
                        case 0:
                        case 2:
                        case 20:
                        case 21:
                        case 40:
                        case 43:
                        case 81: {
                            try {
                                db.replace(entity);
                            } catch (DbException e) {
                                e.printStackTrace();
                                callback.onErrEvent(Constant.ERR_CMD_EXECUDT_FAILED);
                                return;
                            }
                            callback.onValueEvent(0, 1);
                            break;
                        }
                    }
                } else {
                    String mySql_str = "";
                    switch (cmdType) {
                        case 0: {
                            mySql_str = JdbcMySqlStrFactory.insertDoctorDb((DbDoctorEntity) entity);
                            cmd = 0;
                            break;
                        }
                        case 2: {
                            mySql_str = JdbcMySqlStrFactory.updateDoctorDb((DbDoctorEntity) entity);
                            cmd = 0;
                            break;
                        }
                        case 20: {
                            mySql_str = JdbcMySqlStrFactory.insertPatientDb((DbPatientEntity) entity);
                            cmd = 0;
                            break;
                        }
                        case 21: {
                            mySql_str = JdbcMySqlStrFactory.updatePatientDb((DbPatientEntity) entity);
                            cmd = 0;
                            break;
                        }
                        case 40: {
                            mySql_str = JdbcMySqlStrFactory.insertCurInfoDb((DbCureDetailEntity) entity);
                            cmd = 0;
                            break;
                        }
                        case 43: {
                            mySql_str = JdbcMySqlStrFactory.updateCurInfoDb((DbCureDetailEntity) entity);
                            cmd = 0;
                            break;
                        }
                        case 61: {
                            mySql_str = JdbcMySqlStrFactory.insertPatientImgDb();
                            break;
                        }
                        case 62: {
                            mySql_str = JdbcMySqlStrFactory.updatePatientImgDb((DbPatientImgEntity) entity);
                            break;
                        }
                        case 81: {
                            mySql_str = JdbcMySqlStrFactory.insertCureStepDb((DbCureStepEntity) entity);
                            break;
                        }
                    }

                    if (TextUtils.isEmpty(mySql_str)) {
                        callback.onErrEvent(Constant.ERR_CMD_EXECUDT_FAILED);
                        return;
                    }

                    MsgUnit msg = new MsgUnit(mySql_str, cmd, entity, callback);
                    MySqlManage.getInstance().sendMsg(msg);
                }
            }
        });
    }

    public void delDoctorDetail(final String doctorId, final INormalEventListener callback) {
        if (TextUtils.isEmpty(doctorId) || callback == null)
            return;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (getInfoMode == 0) {
                    int result = 0;
                    try {
                        WhereBuilder b = WhereBuilder.b();
                        b.and("account", "=", doctorId);
                        result = db.delete(DbDoctorEntity.class, b);
                    } catch (DbException e) {
                        e.printStackTrace();
                        callback.onErrEvent(Constant.ERR_CMD_EXECUDT_FAILED);
                        return;
                    }
                    callback.onValueEvent(1, result);
                } else {
                    String mySql_str = JdbcMySqlStrFactory.deleteDoctorDb(doctorId);
                    MsgUnit msg = new MsgUnit(mySql_str, 0, callback);
                    MySqlManage.getInstance().sendMsg(msg);
                }
            }
        });
    }

    public void delPatientDetail(final String patientId, final INormalEventListener callback) {
        if (TextUtils.isEmpty(patientId) || callback == null)
            return;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (getInfoMode == 0) {
                    int result = 0;
                    try {
                        WhereBuilder b = WhereBuilder.b();
                        b.and("number", "=", patientId);
                        result = db.delete(DbPatientEntity.class, b);
                    } catch (DbException e) {
                        e.printStackTrace();
                        callback.onErrEvent(Constant.ERR_CMD_EXECUDT_FAILED);
                        return;
                    }
                    callback.onValueEvent(1, result);
                } else {
                    String mySql_str = JdbcMySqlStrFactory.deletePatientDb(patientId);
                    MsgUnit msg = new MsgUnit(mySql_str, 0, callback);

                    MySqlManage.getInstance().sendMsg(msg);
                }
            }
        });
    }

    public void delOneCureDetail(final String cureId, final INormalEventListener callback) {
        if (TextUtils.isEmpty(cureId) || callback == null)
            return;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (getInfoMode == 0) {
                    int result = 0;
                    try {
                        WhereBuilder b = WhereBuilder.b();
                        b.and("cureId", "=", cureId);
                        result = db.delete(DbCureDetailEntity.class, b);
                    } catch (DbException e) {
                        e.printStackTrace();
                        callback.onErrEvent(Constant.ERR_CMD_EXECUDT_FAILED);
                        return;
                    }
                    callback.onValueEvent(1, result);
                } else {
                    String mySql_str = JdbcMySqlStrFactory.deleteOneCureDetailDb(cureId);
                    MsgUnit msg = new MsgUnit(mySql_str, 0, callback);

                    MySqlManage.getInstance().sendMsg(msg);
                }
            }
        });
    }

    public void delCureDetail(final String cureId, final INormalEventListener callback) {
        if (TextUtils.isEmpty(cureId) || callback == null)
            return;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (getInfoMode == 0) {
                    int result = 0;
                    try {
                        WhereBuilder b = WhereBuilder.b();
                        b.and("number", "=", cureId);
                        result = db.delete(DbCureDetailEntity.class, b);
                    } catch (DbException e) {
                        e.printStackTrace();
                        callback.onErrEvent(Constant.ERR_CMD_EXECUDT_FAILED);
                        return;
                    }
                    callback.onValueEvent(1, result);
                } else {
                    String mySql_str = JdbcMySqlStrFactory.deleteCureDetailDb(cureId);
                    MsgUnit msg = new MsgUnit(mySql_str, 0, callback);

                    MySqlManage.getInstance().sendMsg(msg);
                }
            }
        });
    }

    public void getPatientImg(final String account, final INormalEventListener callback) {
        if (callback == null)
            return;
        if (TextUtils.isEmpty(account)) {
            callback.onErrEvent(Constant.ERR_CMD_EXECUDT_FAILED);
            return;
        }

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (getInfoMode == 0) {
                    String tfPath = ((StorageManager) ServerHelper.getInstance().getModule(ServerHelper.MODULE_ID_STORAGE)).getExtendCachePath();
                    if (TextUtils.isEmpty(tfPath)) {
                        callback.onErrEvent(Constant.ERR_FILE_PATH_EMPTY);
                        return;
                    }
                    String filePath = tfPath + "/" + account + ".jpg";
                    Bitmap bm = BitmapFactory.decodeFile(filePath);
                    if (bm == null) {
                        callback.onValueEvent(0, bm);
                    } else {
                        callback.onValueEvent(1, bm);
                    }

                } else {
                    String mySql_str = JdbcMySqlStrFactory.queryPatientImgDb(account);
                    MsgUnit msg = new MsgUnit(mySql_str, Constant.PATIENT_IMG_QUERY, new INormalEventListener() {
                        @Override
                        public void onValueEvent(int key, Object value) {
                            if (value == null) {
                                callback.onValueEvent(0, value);
                                return;
                            }
                            DbPatientImgEntity entity = (DbPatientImgEntity) value;

                            if (TextUtils.isEmpty(entity.getAccount())) {
                                callback.onValueEvent(0, value);
                                return;
                            }

                            InputStream stream = entity.getStream_image();
                            Bitmap bm = BitmapFactory.decodeStream(stream);
                            if (bm == null) {
                                callback.onValueEvent(1, bm);
                            } else {
                                callback.onValueEvent(1, bm);
                            }
                        }

                        @Override
                        public void onErrEvent(int errCode) {
                            callback.onErrEvent(Constant.ERR_CMD_EXECUDT_FAILED);
                        }
                    });
                    MySqlManage.getInstance().sendMsg(msg);
                }
            }
        });
    }

    public void getCureStep(final String CureID, final INormalEventListener callback) {
        if (callback == null)
            return;
        if (TextUtils.isEmpty(CureID)) {
            callback.onErrEvent(Constant.ERR_CMD_EXECUDT_FAILED);
            return;
        }

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (getInfoMode == 0) {
                    DbCureStepEntity step;
                    try {
                        step = db.selector(DbCureStepEntity.class).where("cureId", "=", CureID).findFirst();
                    } catch (DbException e) {
                        e.printStackTrace();
                        callback.onErrEvent(Constant.ERR_CMD_EXECUDT_FAILED);
                        return;
                    }
                    callback.onValueEvent(1, step);

                } else {
                    String mySql_str = JdbcMySqlStrFactory.queryCureStepDb(CureID);
                    MsgUnit msg = new MsgUnit(mySql_str, Constant.CURE_STEP_QUERY, new INormalEventListener() {
                        @Override
                        public void onValueEvent(int key, Object value) {
                            if (value == null) {
                                callback.onValueEvent(0, value);
                                return;
                            }
                            DbCureStepEntity entity = (DbCureStepEntity) value;
                            if (entity != null) {
                                callback.onValueEvent(1, value);
                            }
                        }

                        @Override
                        public void onErrEvent(int errCode) {
                            callback.onErrEvent(Constant.ERR_CMD_EXECUDT_FAILED);
                        }
                    });
                    MySqlManage.getInstance().sendMsg(msg);
                }
            }
        });
    }
}
