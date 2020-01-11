package com.l.eyescure.server.printManager;

/**
 * Created by Administrator on 2017/9/7.
 */
public class PrintJobMsg {
    private int cmd_id;
    private int id;
    private String name;
    private String reason;
    private PrinterInfo printer;

    public PrintJobMsg(int cmd_id, int id, String name, String reason, PrinterInfo printer) {
        this.cmd_id = cmd_id;
        this.id = id;
        this.name = name;
        this.reason = reason;
        this.printer = printer;
    }

    public int getCmd_id() {
        return cmd_id;
    }

    public void setCmd_id(int cmd_id) {
        this.cmd_id = cmd_id;
    }

    public PrinterInfo getPrinter() {
        return printer;
    }

    public void setPrinter(PrinterInfo printer) {
        this.printer = printer;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
