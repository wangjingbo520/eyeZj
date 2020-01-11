package com.l.eyescure.server.SqlManage.localsql;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;

/**
 * Author:Look
 * Version: V1.0
 * Description: 病人详情
 * Date: 2017/6/4
 */

@Table(name = "cureDetail_table")
public class DbCureDetailEntity implements Serializable {
    @Column(name = "id", isId = true)
    private int id;
    @Column(name = "number")     //病人id
    private String number;
    @Column(name = "cureId", property = "UNIQUE")  //当前治疗索引 由病人id + 当前时间的long型值
    private String cureId;
    @Column(name = "curedate")
    private String cureDate;//治疗日期
    @Column(name = "leftpressdatax")
    private String leftPressDataX;//左眼压力X数据
    @Column(name = "leftpressdatay")
    private String leftPressDataY;//左眼压力Y数据
    @Column(name = "rightpressdatax")
    private String rightPressDataX;//右眼压力x数据
    @Column(name = "rightpressdatay")
    private String rightPressDataY;//右眼压力y数据
    @Column(name = "lefttempdatax")
    private String leftTempDataX;//左眼温度X数据
    @Column(name = "lefttempdatay")
    private String leftTempDataY;//左眼温度Y数据
    @Column(name = "righttempdatax")
    private String rightTempDataX;// 右眼温度X数据
    @Column(name = "righttempdatay")
    private String rightTempDataY;// 右眼温度Y数据
    @Column(name = "lefttime")
    private long leftTime;//左眼治疗时间
    @Column(name = "righttime")
    private long rightTime;//右眼治疗时间
    @Column(name = "doctorname ")
    private String doctorName;//治疗医师姓名
    @Column(name = "filePath")
    private String filePath;//病例文档位置
    @Column(name = "cure_model")
    private int cure_model = 1; //治疗模式


    public int getCure_model() {
        return cure_model;
    }

    public void setCure_model(int cure_model) {
        this.cure_model = cure_model;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getCureId() {
        return cureId;
    }

    public void setCureId(String cureId) {
        this.cureId = cureId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCureDate() {
        return cureDate;
    }

    public void setCureDate(String cureDate) {
        this.cureDate = cureDate;
    }

    public String getLeftPressDataX() {
        return leftPressDataX;
    }

    public void setLeftPressDataX(String leftPressDataX) {
        this.leftPressDataX = leftPressDataX;
    }

    public String getLeftPressDataY() {
        return leftPressDataY;
    }

    public void setLeftPressDataY(String leftPressDataY) {
        this.leftPressDataY = leftPressDataY;
    }

    public String getRightPressDataX() {
        return rightPressDataX;
    }

    public void setRightPressDataX(String rightPressDataX) {
        this.rightPressDataX = rightPressDataX;
    }

    public String getRightPressDataY() {
        return rightPressDataY;
    }

    public void setRightPressDataY(String rightPressDataY) {
        this.rightPressDataY = rightPressDataY;
    }

    public String getLeftTempDataX() {
        return leftTempDataX;
    }

    public void setLeftTempDataX(String leftTempDataX) {
        this.leftTempDataX = leftTempDataX;
    }

    public String getLeftTempDataY() {
        return leftTempDataY;
    }

    public void setLeftTempDataY(String leftTempDataY) {
        this.leftTempDataY = leftTempDataY;
    }

    public String getRightTempDataX() {
        return rightTempDataX;
    }

    public void setRightTempDataX(String rightTempDataX) {
        this.rightTempDataX = rightTempDataX;
    }

    public String getRightTempDataY() {
        return rightTempDataY;
    }

    public void setRightTempDataY(String rightTempDataY) {
        this.rightTempDataY = rightTempDataY;
    }

    public long getLeftTime() {
        return leftTime;
    }

    public void setLeftTime(long leftTime) {
        this.leftTime = leftTime;
    }

    public long getRightTime() {
        return rightTime;
    }

    public void setRightTime(long rightTime) {
        this.rightTime = rightTime;
    }


    @Override
    public String toString() {
        return "DbCureDetailEntity{" +
                "id=" + id +
                ", number='" + number + '\'' +
                ", cureId='" + cureId + '\'' +
                ", cureDate='" + cureDate + '\'' +
                ", leftPressDataX='" + leftPressDataX + '\'' +
                ", leftPressDataY='" + leftPressDataY + '\'' +
                ", rightPressDataX='" + rightPressDataX + '\'' +
                ", rightPressDataY='" + rightPressDataY + '\'' +
                ", leftTempDataX='" + leftTempDataX + '\'' +
                ", leftTempDataY='" + leftTempDataY + '\'' +
                ", rightTempDataX='" + rightTempDataX + '\'' +
                ", rightTempDataY='" + rightTempDataY + '\'' +
                ", leftTime=" + leftTime +
                ", rightTime=" + rightTime +
                ", doctorName='" + doctorName + '\'' +
                ", filePath='" + filePath + '\'' +
                ", cure_model=" + cure_model +
                '}';
    }
}
