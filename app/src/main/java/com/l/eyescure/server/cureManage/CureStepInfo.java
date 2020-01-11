package com.l.eyescure.server.cureManage;

/**
 * Created by Administrator on 2017/11/8.
 */
public class CureStepInfo {
    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getLorR() {
        return LorR;
    }

    public void setLorR(String lorR) {
        LorR = lorR;
    }

    public String getMode() {
        return Mode;
    }

    public void setMode(String mode) {
        Mode = mode;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    private int num = 0;
    private String LorR = "左眼";
    private String Mode = "开始";
    private String Time = "00分00秒";
}
