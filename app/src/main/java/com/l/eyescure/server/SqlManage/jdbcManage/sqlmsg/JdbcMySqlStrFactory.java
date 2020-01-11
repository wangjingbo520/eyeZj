package com.l.eyescure.server.SqlManage.jdbcManage.sqlmsg;

import com.l.eyescure.server.SqlManage.localsql.DbCureDetailEntity;
import com.l.eyescure.server.SqlManage.localsql.DbCureStepEntity;
import com.l.eyescure.server.SqlManage.localsql.DbDoctorEntity;
import com.l.eyescure.server.SqlManage.localsql.DbPatientEntity;
import com.l.eyescure.server.SqlManage.localsql.DbPatientImgEntity;
import com.l.eyescure.util.Constant;

/**
 * Created by Administrator on 2017/8/30.
 */
public class JdbcMySqlStrFactory {
    public static String insertDoctorDb(DbDoctorEntity entity) {
        String sql = "insert into " + Constant.DOCTOR_DB_NAME + "(account,password,nick,date,super) values('" +
                entity.getAccount() + "','" +
                entity.getPassword() + "','" +
                entity.getNick() + "','" +
                entity.getCreate_date() + "'," +
                entity.getIsLogin() + ")";
        return sql;
    }

    public static String deleteDoctorDb(String account) {
        String sql = "delete from " + Constant.DOCTOR_DB_NAME + " where account = '" + account + "'";
        return sql;
    }

    public static String updateDoctorDb(DbDoctorEntity doctor) {
        String sql = "UPDATE " + Constant.DOCTOR_DB_NAME + " SET" +
                " password='" + doctor.getPassword() + "'," +
                " nick='" + doctor.getNick() + "'," +
                " date='" + doctor.getCreate_date() + "'," +
                " super='" + doctor.getIsLogin() +
                "' where account ='" + doctor.getAccount() + "'";
        return sql;
    }

    public static String queryDoctorDb() {
        String sql = "select * from " + Constant.DOCTOR_DB_NAME;
        return sql;
    }

    public static String queryOneDoctorDb(String account) {
        String sql = "select * from " + Constant.DOCTOR_DB_NAME + " where account = '" + account + "'";
        return sql;
    }

    public static String insertPatientDb(DbPatientEntity patient) {
        String sql = "insert into " + Constant.PATIENT_DB_NAME + " (doctorid,number,name,sex,identity,birthday,totalcurenum) values('" +
                patient.getDoctorid() + "','" +
                patient.getNumber() + "','" +
                patient.getName() + "','" +
                patient.getSex() + "','" +
                patient.getIdNumber() + "','" +
                patient.getBirthday() + "','" +
                patient.getCureNumber() + "')";
        return sql;
    }

    public static String updatePatientDb(DbPatientEntity patient) {
        String sql = "UPDATE " + Constant.PATIENT_DB_NAME + " SET " +
                "doctorid ='" + patient.getDoctorid() + "'," +
                "name ='" + patient.getName() + "'," +
                "sex ='" + patient.getSex() + "'," +
                "identity ='" + patient.getIdNumber() + "'," +
                "birthday ='" + patient.getBirthday() + "'," +
                "totalcurenum ='" + patient.getCureNumber() + "'" +
                " where number ='" + patient.getNumber() + "'";
        return sql;
    }

    public static String updatePatientImgDb(DbPatientImgEntity entity) {
        String sql = "UPDATE " + Constant.PATIENT_IMG_DB_NAME + " SET " +
                "image = ? " + " where account = ?";
        return sql;
    }

    public static String queryPatientDb(String doctorid) {
        String sql = "select * from " + Constant.PATIENT_DB_NAME + " where doctorid = '" + doctorid + "'";
        return sql;
    }

    public static String queryOnePatientDb(String number) {
        String sql = "select * from " + Constant.PATIENT_DB_NAME + " where number = '" + number + "'";
        return sql;
    }

    public static String deletePatientDb(String account) {
        String sql = "delete from " + Constant.PATIENT_DB_NAME + " where number ='" + account + "'";
        return sql;
    }

    public static String insertCurInfoDb(DbCureDetailEntity curinfo) {
        String sql = "insert into " + Constant.CUREINFO_DB_NAME +
                " (number,cureId,curedate,leftpressdatax,leftpressdatay,rightpressdatax,rightpressdatay,lefttempdatax,lefttempdatay,righttempdatax,righttempdatay,lefttime,righttime,cure_model,doctorname) values('" +
                curinfo.getNumber() + "','" +
                curinfo.getCureId() + "','" +
                curinfo.getCureDate() + "','" +
                curinfo.getLeftPressDataX() + "','" +
                curinfo.getLeftPressDataY() + "','" +
                curinfo.getRightPressDataX() + "','" +
                curinfo.getRightPressDataY() + "','" +
                curinfo.getLeftTempDataX() + "','" +
                curinfo.getLeftTempDataY() + "','" +
                curinfo.getRightTempDataX() + "','" +
                curinfo.getRightTempDataY() + "','" +
                curinfo.getLeftTime() + "','" +
                curinfo.getRightTime() + "','" +
                curinfo.getCure_model() + "','" +
                curinfo.getDoctorName() + "')";
        return sql;
    }

    public static String insertPatientImgDb() {
        String sql = "insert into " + Constant.PATIENT_IMG_DB_NAME +
                " (account,image) values(?,?)";
        return sql;
    }

    public static String updateCurInfoDb(DbCureDetailEntity curinfo) {
        String sql = "UPDATE " + Constant.CUREINFO_DB_NAME + " SET " +
                "number ='" + curinfo.getNumber() + "'," +
                "curedate ='" + curinfo.getCureDate() + "'," +
                "leftpressdatax ='" + curinfo.getLeftPressDataX() + "'," +
                "leftpressdatay ='" + curinfo.getLeftPressDataY() + "'," +
                "rightpressdatax ='" + curinfo.getRightPressDataX() + "'," +
                "rightpressdatay ='" + curinfo.getRightPressDataY() + "'," +
                "lefttempdatax ='" + curinfo.getLeftTempDataX() + "'," +
                "lefttempdatay ='" + curinfo.getLeftTempDataY() + "'," +
                "righttempdatax ='" + curinfo.getRightTempDataX() + "'," +
                "righttempdatay ='" + curinfo.getRightPressDataY() + "'," +
                "lefttime ='" + curinfo.getLeftTime() + "'," +
                "righttime ='" + curinfo.getRightTime() + "'," +
                "doctorname  ='" + curinfo.getDoctorName() +
                " where cureId ='" + curinfo.getCureId() + "'";
        return sql;
    }

    public static String deleteCureDetailDb(String account) {
        String sql = "delete from " + Constant.CUREINFO_DB_NAME + " where number ='" + account + "'";
        return sql;
    }

    public static String deleteOneCureDetailDb(String account) {
        String sql = "delete from " + Constant.CUREINFO_DB_NAME + " where cureId ='" + account + "'";
        return sql;
    }

    public static String queryCurInfoDb(String patientID) {
        String sql = "select * from " + Constant.CUREINFO_DB_NAME + " where number ='" + patientID + "'";
        return sql;
    }

    public static String queryOneCurInfoDb(String cureId) {
        String sql = "select * from " + Constant.CUREINFO_DB_NAME + " where cureId ='" + cureId + "'";
        return sql;
    }

    public static String queryPatientImgDb(String account) {
        String sql = "select * from " + Constant.PATIENT_IMG_DB_NAME + " where account ='" + account + "'";
        return sql;
    }

    public static String insertCureStepDb(DbCureStepEntity entity) {
        String sql = "insert into " + Constant.CURE_STEP_DB_NAME + "(cureID,step_str) values('" +
                entity.getAccount() + "','" +
                entity.getStep_str() + "'" + ")";
        return sql;
    }

    public static String queryCureStepDb(String cureID) {
        String sql = "select * from " + Constant.CURE_STEP_DB_NAME + " where cureID ='" + cureID + "'";
        return sql;
    }
}
