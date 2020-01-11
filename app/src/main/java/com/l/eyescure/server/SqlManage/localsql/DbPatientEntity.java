package com.l.eyescure.server.SqlManage.localsql;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;

/**
 * Created by Look on 2017/1/14.
 * 病人信息
 */

@Table(name = "patient_table")
public class DbPatientEntity implements Serializable {
    @Column(name = "id", isId = true)
    private int id;
    @Column(name = "doctorid") //主治医生ID
    private String doctorid;
    @Column(name = "number", property = "UNIQUE")
    private String number;//编号
    @Column(name = "name")
    private String name;//病人姓名
    @Column(name = "sex")
    private String sex;// 性别
    @Column(name = "identity")
    private String idNumber;//身份证
    @Column(name = "birthday")
    private String birthday;//生日
    @Column(name = "totalcurenum")
    private int cureNumber;//治疗次数

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDoctorid() {
        return doctorid;
    }

    public void setDoctorid(String doctorid) {
        this.doctorid = doctorid;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public int getCureNumber() {
        return cureNumber;
    }

    public void setCureNumber(int cureNumber) {
        this.cureNumber = cureNumber;
    }
}
