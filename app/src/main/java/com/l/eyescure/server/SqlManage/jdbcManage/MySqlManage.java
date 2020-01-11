package com.l.eyescure.server.SqlManage.jdbcManage;

import android.os.Handler;
import android.text.TextUtils;

import com.apkfuns.logutils.LogUtils;
import com.l.eyescure.application.MyApplication;
import com.l.eyescure.server.SqlManage.jdbcManage.sqlmsg.MsgUnit;
import com.l.eyescure.server.SqlManage.localsql.DbCureDetailEntity;
import com.l.eyescure.server.SqlManage.localsql.DbCureStepEntity;
import com.l.eyescure.server.SqlManage.localsql.DbDoctorEntity;
import com.l.eyescure.server.SqlManage.localsql.DbPatientEntity;
import com.l.eyescure.server.SqlManage.localsql.DbPatientImgEntity;
import com.l.eyescure.server.StorageManager.SharedPreferencesUtil;
import com.l.eyescure.server.callBack.INormalEventListener;
import com.l.eyescure.util.Constant;
import com.l.eyescure.util.InputCheck;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/25.
 */
public class MySqlManage {
    private Connection mConn = null;
    private static MySqlManage instance;
    private static int SEND_MSG_TIME = 200;
    private Handler mHandler;

    private List<MsgUnit> ls_student = new ArrayList<>();

    public static MySqlManage getInstance() {
        if (instance == null) {
            instance = new MySqlManage();
        }
        return instance;
    }

    public MySqlManage() {
        mHandler = new Handler();
        mHandler.post(readMsgRunable);
    }

    Runnable readMsgRunable = new Runnable() {
        @Override
        public void run() {
            readMsg();
            mHandler.postDelayed(readMsgRunable, 200);
        }
    };

    public void sendMsg(MsgUnit msg) {
        ls_student.add(msg);
    }

    private void readMsg() {
        if (ls_student.isEmpty())
            return;
        MsgUnit msg = ls_student.get(0);
        if (msg == null) {
            ls_student.remove(0);
            return;
        }

        String cmdinfo = msg.getSql_cmd();
        INormalEventListener callback = msg.getCallback();
        if (TextUtils.isEmpty(cmdinfo) || callback == null) {
            ls_student.remove(0);
            return;
        }

        int Type = msg.getMsgType();
        Object value = msg.getValueInfo();

        msgDispose(Type, cmdinfo, value, callback);

        ls_student.remove(0);
        checkNoMsgDisConectDb();
    }

    private void msgDispose(int Type, String cmdinfo, Object value, INormalEventListener callback) {
        LogUtils.e("操作jdbc数据库，命令为：" + Type + "指令为：" + cmdinfo);
        Connection conn = getConn();
        if (conn == null) {
            MyApplication.getInstance().showToastStr("网络异常，请检查网络状况！");
            return;
        }
        PreparedStatement pstmt;
        try {
            pstmt = (PreparedStatement) conn.prepareStatement(cmdinfo);
        } catch (SQLException e) {
            e.printStackTrace();
            MyApplication.getInstance().showToastStr("网络异常，请检查网络状况！");
            return;
        }
        switch (Type) {
            case 0: {
                sendNormalMsgToSql(pstmt, callback);
                break;
            }
            case 3: {
                sendQueryDoctorMsgToSql(cmdinfo, callback);
                break;
            }
            case 4: {
                sendQueryOneDoctorMsgToSql(cmdinfo, callback);
                break;
            }
            case 22: {
                sendQueryPatientMsgToSql(cmdinfo, callback);
                break;
            }
            case 23: {
                sendQueryOnePatientMsgToSql(cmdinfo, callback);
                break;
            }
            case 41: {
                sendQueryCureinfoMsgToSql(cmdinfo, callback);
                break;
            }
            case 42: {
                sendQueryOneCureinfoMsgToSql(cmdinfo, callback);
                break;
            }
            case 60: {
                sendQueryPatientImgMsgToSql(cmdinfo, callback);
                break;
            }
            case 61: {
                addPatientImgMsgToSql(cmdinfo, value, callback);
                break;
            }
            case 62: {
                updatePatientImgMsgToSql(cmdinfo, value, callback);
                break;
            }
            case 80: {
                sendQueryCureStepMsgToSql(cmdinfo, callback);
                break;
            }
            case 81: {
                addCureStepMsgToSql(cmdinfo, callback);
                break;
            }
        }
    }

    private void checkNoMsgDisConectDb() {
        if (ls_student.size() == 0) {
            if (mConn != null) {
                try {
                    mConn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String getIP() {
        String ss = Constant.JDBC_URL_F;
        SharedPreferencesUtil util = new SharedPreferencesUtil(MyApplication.getInstance().mContext);
        String ss_save = util.readString("saveIP");
        if (!TextUtils.isEmpty(ss_save)) {
            ss = String.format(ss, ss_save);
        } else {
            ss = String.format(ss, Constant.JDBC_URL_IP);
        }
        return ss;
    }

    public Connection getConn() {
        try {
            if (mConn != null && !mConn.isClosed()) {
                return mConn;
            }
            if (mConn == null)
                Class.forName(Constant.DRIVER);       //classLoader,加载对应驱动
            mConn = (Connection) DriverManager.getConnection(getIP(), Constant.SQL_ACCOUNT, Constant.SQL_PASSWORD);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mConn;
    }

    private void sendNormalMsgToSql(PreparedStatement pstmt, INormalEventListener callback) {
        int i = 0;
        try {
            i = pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            if (callback != null)
                callback.onErrEvent(Constant.ERR_CMD_EXECUDT_FAILED);
            return;
        }

        if (callback != null)
            callback.onValueEvent(1, i);
    }

    private void updatePatientImgMsgToSql(String cmdinfo, Object value, INormalEventListener callback) {
        if (callback == null)
            return;
        if (value == null || TextUtils.isEmpty(cmdinfo)) {
            callback.onErrEvent(Constant.ERR_FILE_PATH_EMPTY);
            return;
        }

        DbPatientImgEntity entity = (DbPatientImgEntity) value;
        String acount = entity.getAccount();
        if (TextUtils.isEmpty(acount)) {
            callback.onErrEvent(Constant.ERR_FILE_PATH_EMPTY);
            return;
        }

        File file = entity.getFile_image();
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Connection conn = getConn();
        if (conn == null) {
            callback.onErrEvent(Constant.ERR_INTERNET_DISCONNEXT);
            return;
        }
        LogUtils.e("连接异常，未能执行");
        PreparedStatement pstmt;
        int i = 0;

        try {
            pstmt = (PreparedStatement) conn.prepareStatement(cmdinfo);
            pstmt.setBinaryStream(1, inputStream, (int) (file.length()));
            pstmt.setString(2, acount);

            i = pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            callback.onErrEvent(Constant.ERR_CMD_EXECUDT_FAILED);
            return;
        }

        callback.onValueEvent(1, i);
    }

    private void addPatientImgMsgToSql(String cmdinfo, Object value, INormalEventListener callback) {
        if (callback == null)
            return;
        if (value == null || TextUtils.isEmpty(cmdinfo)) {
            callback.onErrEvent(Constant.ERR_FILE_PATH_EMPTY);
            return;
        }

        DbPatientImgEntity entity = (DbPatientImgEntity) value;
        String acount = entity.getAccount();
        if (TextUtils.isEmpty(acount)) {
            callback.onErrEvent(Constant.ERR_FILE_PATH_EMPTY);
            return;
        }

        File file = entity.getFile_image();
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (inputStream == null) {
            callback.onErrEvent(Constant.ERR_FILE_PATH_EMPTY);
            return;
        }

        Connection conn = getConn();
        if (conn == null) {
            callback.onErrEvent(Constant.ERR_INTERNET_DISCONNEXT);
            return;
        }
        LogUtils.e("连接异常，未能执行");
        PreparedStatement pstmt;
        int i = 0;

        try {
            pstmt = (PreparedStatement) conn.prepareStatement(cmdinfo);
            pstmt.setString(1, acount);
            pstmt.setBinaryStream(2, inputStream, (int) (file.length()));

            i = pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            callback.onErrEvent(Constant.ERR_CMD_EXECUDT_FAILED);
            return;
        }

        callback.onValueEvent(1, i);
    }

    private void sendQueryOneDoctorMsgToSql(String sql, INormalEventListener callback) {
        Connection conn = getConn();
        DbDoctorEntity doctor = new DbDoctorEntity();
        PreparedStatement pstmt;
        try {
            pstmt = (PreparedStatement) conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                doctor.setAccount(rs.getString(1));
                doctor.setPassword(rs.getString(2));
                doctor.setNick(rs.getString(3));
                doctor.setCreate_date(rs.getString(4));
                doctor.setIsLogin(rs.getInt(5));
                break;
            }

            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            if (callback != null)
                callback.onErrEvent(Constant.ERR_CMD_EXECUDT_FAILED);
            return;
        }

        if (callback != null)
            callback.onValueEvent(1, doctor);
    }

    private void sendQueryDoctorMsgToSql(String sql, INormalEventListener callback) {
        Connection conn = getConn();
        List<DbDoctorEntity> doctors = new ArrayList<>();
        PreparedStatement pstmt;
        try {
            pstmt = (PreparedStatement) conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                DbDoctorEntity doc = new DbDoctorEntity();
                doc.setAccount(rs.getString(1));
                doc.setPassword(rs.getString(2));
                doc.setNick(rs.getString(3));
                doc.setCreate_date(rs.getString(4));
                doc.setIsLogin(rs.getInt(5));
                doctors.add(doc);
            }

            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            if (callback != null)
                callback.onErrEvent(Constant.ERR_CMD_EXECUDT_FAILED);
            return;
        }

        if (callback != null)
            callback.onValueEvent(1, doctors);
    }

    private void sendQueryPatientMsgToSql(String sql, INormalEventListener callback) {
        Connection conn = getConn();
        List<DbPatientEntity> patients = new ArrayList<>();
        PreparedStatement pstmt;
        try {
            pstmt = (PreparedStatement) conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                DbPatientEntity patient = new DbPatientEntity();
                patient.setNumber(rs.getString(1));
                patient.setDoctorid(rs.getString(2));
                patient.setName(rs.getString(3));
                patient.setSex(rs.getString(4));
                patient.setIdNumber(rs.getString(5));
                patient.setBirthday(rs.getString(6));
                String mTotalCureNumber = rs.getString(7);
                //  String cure_model = rs.getString(rs.);

                if (InputCheck.getInstance().checkInputNum(mTotalCureNumber)) {
                    int mNum = Integer.valueOf(mTotalCureNumber);
                    patient.setCureNumber(mNum);
                }
                patients.add(patient);
            }

            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            if (callback != null)
                callback.onErrEvent(Constant.ERR_CMD_EXECUDT_FAILED);
            return;
        }

        if (callback != null)
            callback.onValueEvent(1, patients);
    }

    private void sendQueryOnePatientMsgToSql(String sql, INormalEventListener callback) {
        Connection conn = getConn();
        DbPatientEntity patient = new DbPatientEntity();
        PreparedStatement pstmt;
        try {
            pstmt = (PreparedStatement) conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                patient.setNumber(rs.getString(1));
                patient.setDoctorid(rs.getString(2));
                patient.setName(rs.getString(3));
                patient.setSex(rs.getString(4));
                patient.setIdNumber(rs.getString(5));
                patient.setBirthday(rs.getString(6));
                patient.setCureNumber(rs.getInt(7));
                break;
            }

            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            if (callback != null)
                callback.onErrEvent(Constant.ERR_CMD_EXECUDT_FAILED);
            return;
        }

        if (callback != null)
            callback.onValueEvent(1, patient);
    }

    private void sendQueryCureinfoMsgToSql(String sql, INormalEventListener callback) {
        Connection conn = getConn();
        List<DbCureDetailEntity> curinfos = new ArrayList<>();
        PreparedStatement pstmt;
        try {
            pstmt = (PreparedStatement) conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                DbCureDetailEntity curinfo = new DbCureDetailEntity();
                curinfo.setCureId(rs.getString(1));
                curinfo.setNumber(rs.getString(2));
                curinfo.setCureDate(rs.getString(3));
                curinfo.setLeftPressDataX(rs.getString(4));
                curinfo.setLeftPressDataY(rs.getString(5));
                curinfo.setRightPressDataX(rs.getString(6));
                curinfo.setRightPressDataY(rs.getString(7));
                curinfo.setLeftTempDataX(rs.getString(8));
                curinfo.setLeftTempDataY(rs.getString(9));
                curinfo.setRightTempDataX(rs.getString(10));
                curinfo.setRightTempDataY(rs.getString(11));
                String ss = rs.getString(12);
                curinfo.setLeftTime(Long.valueOf(TextUtils.isEmpty(ss) ? "0" : ss));
                ss = rs.getString(13);
                curinfo.setRightTime(Long.valueOf(TextUtils.isEmpty(ss) ? "0" : ss));
                curinfo.setDoctorName(rs.getString(14));
                curinfo.setCure_model(rs.getInt(15));
                curinfos.add(curinfo);
            }

            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            if (callback != null)
                callback.onErrEvent(Constant.ERR_CMD_EXECUDT_FAILED);
            return;
        }

        if (callback != null)
            callback.onValueEvent(1, curinfos);
    }

    private void sendQueryOneCureinfoMsgToSql(String sql, INormalEventListener callback) {
        Connection conn = getConn();
        DbCureDetailEntity curinfo = new DbCureDetailEntity();
        PreparedStatement pstmt;
        try {
            pstmt = (PreparedStatement) conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                curinfo.setCureId(rs.getString(1));
                curinfo.setNumber(rs.getString(2));
                curinfo.setCureDate(rs.getString(3));
                curinfo.setLeftPressDataX(rs.getString(4));
                curinfo.setLeftPressDataY(rs.getString(5));
                curinfo.setRightPressDataX(rs.getString(6));
                curinfo.setRightPressDataY(rs.getString(7));
                curinfo.setLeftTempDataX(rs.getString(8));
                curinfo.setLeftTempDataY(rs.getString(9));
                curinfo.setRightTempDataX(rs.getString(10));
                curinfo.setRightTempDataY(rs.getString(11));
                String ss = rs.getString(12);
                curinfo.setLeftTime(Long.valueOf(TextUtils.isEmpty(ss) ? "0" : ss));
                ss = rs.getString(13);
                curinfo.setRightTime(Long.valueOf(TextUtils.isEmpty(ss) ? "0" : ss));
                curinfo.setDoctorName(rs.getString(14));
                break;
            }

            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            if (callback != null)
                callback.onErrEvent(Constant.ERR_CMD_EXECUDT_FAILED);
            return;
        }

        if (callback != null)
            callback.onValueEvent(1, curinfo);
    }

    private void sendQueryPatientImgMsgToSql(String sql, INormalEventListener callback) {
        if (callback == null)
            return;

        Connection conn = getConn();
        if (conn == null) {
            callback.onErrEvent(Constant.ERR_INTERNET_DISCONNEXT);
            return;
        }

        DbPatientImgEntity curinfo = new DbPatientImgEntity();
        PreparedStatement pstmt;
        try {
            pstmt = (PreparedStatement) conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                curinfo.setAccount(rs.getString(1));
                curinfo.setStream_image(rs.getBinaryStream(2));
                break;
            }
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            if (callback != null)
                callback.onErrEvent(Constant.ERR_CMD_EXECUDT_FAILED);
            return;
        }

        callback.onValueEvent(1, curinfo);
    }

    private void sendQueryCureStepMsgToSql(String sql, INormalEventListener callback) {
        if (callback == null)
            return;

        Connection conn = getConn();
        if (conn == null) {
            callback.onErrEvent(Constant.ERR_INTERNET_DISCONNEXT);
            return;
        }

        DbCureStepEntity curinfo = new DbCureStepEntity();
        PreparedStatement pstmt;
        try {
            pstmt = (PreparedStatement) conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                curinfo.setAccount(rs.getString(1));
                curinfo.setStep_str(rs.getString(2));
                break;
            }
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            if (callback != null)
                callback.onErrEvent(Constant.ERR_CMD_EXECUDT_FAILED);
            return;
        }

        callback.onValueEvent(1, curinfo);
    }

    private void addCureStepMsgToSql(String cmdinfo, INormalEventListener callback) {
        if (callback == null)
            return;
        if (TextUtils.isEmpty(cmdinfo)) {
            callback.onErrEvent(Constant.ERR_FILE_PATH_EMPTY);
            return;
        }

        Connection conn = getConn();
        if (conn == null) {
            callback.onErrEvent(Constant.ERR_INTERNET_DISCONNEXT);
            return;
        }
        LogUtils.e("连接异常，未能执行");
        PreparedStatement pstmt;
        int i = 0;

        try {
            pstmt = (PreparedStatement) conn.prepareStatement(cmdinfo);
            i = pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            callback.onErrEvent(Constant.ERR_CMD_EXECUDT_FAILED);
            return;
        }

        callback.onValueEvent(1, i);
    }
}
