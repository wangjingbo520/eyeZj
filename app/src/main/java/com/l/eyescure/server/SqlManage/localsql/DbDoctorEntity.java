package com.l.eyescure.server.SqlManage.localsql;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by Look on 2017/1/14.
 * 病人信息
 */

@Table(name = "doctor_table")
public class DbDoctorEntity {
    @Column(name = "id", isId = true)
    private int id;
    @Column(name = "account", property = "UNIQUE")//唯一约束
    private String account;//账号
    @Column(name = "password")
    private String password;//密码
    @Column(name = "nick")
    private String nick;//医师姓名
    @Column(name = "date")  //创建时间
    private String create_date;
    @Column(name = "super")
    private int isLogin;//是否是管理员,mysql 没有boolean类型，这里用int代替。0 不是 1 是

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getCreate_date() {
        return create_date;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }

    public int getIsLogin() {
        return isLogin;
    }

    public void setIsLogin(int isLogin) {
        this.isLogin = isLogin;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
