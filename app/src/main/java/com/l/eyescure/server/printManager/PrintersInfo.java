package com.l.eyescure.server.printManager;

import org.cups4j.PrinterStateEnum;

/**
 * Created by Administrator on 2017/8/4.
 */
public class PrintersInfo {
    private String name;
    private PrinterStateEnum state;
    private String stateReason;
    private String stateMessage;
    private boolean isChecked;

    public PrintersInfo(String name, PrinterStateEnum state, String stateReason, String stateMessage, boolean isChecked) {
        this.name = name;
        this.state = state;
        this.stateReason = stateReason;
        this.stateMessage = stateMessage;
        this.isChecked = isChecked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PrinterStateEnum getState() {
        return state;
    }

    public void setState(PrinterStateEnum state) {
        this.state = state;
    }

    public String getStateReason() {
        return stateReason;
    }

    public void setStateReason(String stateReason) {
        this.stateReason = stateReason;
    }

    public String getStateMessage() {
        return stateMessage;
    }

    public void setStateMessage(String stateMessage) {
        this.stateMessage = stateMessage;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

}