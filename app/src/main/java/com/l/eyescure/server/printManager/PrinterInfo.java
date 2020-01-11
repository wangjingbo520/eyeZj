package com.l.eyescure.server.printManager;

/**
 * Created by Administrator on 2017/9/7.
 */
public class PrinterInfo {
    private String name;
    private int status;
    private String reason;
    private boolean bAccept = false;

    public PrinterInfo(String name, int status,String reason, boolean bAccept) {
        this.name = name;
        this.status = status;
        this.reason = reason;
        this.bAccept = bAccept;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean isbAccept() {
        return bAccept;
    }

    public void setbAccept(boolean bAccept) {
        this.bAccept = bAccept;
    }
}
