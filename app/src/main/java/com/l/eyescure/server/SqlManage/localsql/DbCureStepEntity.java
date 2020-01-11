package com.l.eyescure.server.SqlManage.localsql;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = "cure_step_table")
public class DbCureStepEntity {
    @Column(name = "id", isId = true)
    private int id;
    @Column(name = "cureID", property = "UNIQUE")//唯一约束
    private String account;//账号
    @Column(name = "step_str")
    private String step_str; //图片内容

    public DbCureStepEntity() {
    }

    public DbCureStepEntity(String account, String step_str) {
        this.account = account;
        this.step_str = step_str;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getStep_str() {
        return step_str;
    }

    public void setStep_str(String step_str) {
        this.step_str = step_str;
    }
}
