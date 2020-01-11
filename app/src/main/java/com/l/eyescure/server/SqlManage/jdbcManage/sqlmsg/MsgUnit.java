package com.l.eyescure.server.SqlManage.jdbcManage.sqlmsg;

import com.l.eyescure.server.callBack.INormalEventListener;

/**
 * Created by Administrator on 2017/8/30.
 */
public class MsgUnit {
    private String sql_cmd;
    private INormalEventListener callback;
    private int MsgType;
    private Object valueInfo;

    public MsgUnit(String sql_cmd, int MsgType, INormalEventListener callback) {
        this.sql_cmd = sql_cmd;
        this.MsgType = MsgType;
        this.callback = callback;
    }

    public MsgUnit(String sql_cmd, int MsgType, Object valueInfo, INormalEventListener callback) {
        this.sql_cmd = sql_cmd;
        this.MsgType = MsgType;
        this.valueInfo = valueInfo;
        this.callback = callback;
    }

    public Object getValueInfo() {
        return valueInfo;
    }

    public void setValueInfo(Object valueInfo) {
        this.valueInfo = valueInfo;
    }

    public String getSql_cmd() {
        return sql_cmd;
    }

    public void setSql_cmd(String sql_cmd) {
        this.sql_cmd = sql_cmd;
    }

    public INormalEventListener getCallback() {
        return callback;
    }

    public void setCallback(INormalEventListener callback) {
        this.callback = callback;
    }

    public int getMsgType() {
        return MsgType;
    }

    public void setMsgType(int msgType) {
        MsgType = msgType;
    }

    @Override
    public String toString() {
        return "MsgUnit{" +
                "sql_cmd='" + sql_cmd + '\'' +
                ", callback=" + callback +
                ", MsgType=" + MsgType +
                ", valueInfo=" + valueInfo +
                '}';
    }
}
