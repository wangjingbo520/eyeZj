package com.l.eyescure.server.pdfManager;

/**
 * Created by Administrator on 2017/8/4.
 */
public class StorageInfo {
    public String path;
    public String state;
    public String lable;
    public boolean isRemoveable;
    public boolean isChecked;

    public StorageInfo(String path,String lable) {
        this.path = path;
        this.lable = lable;
    }
    public boolean isMounted() {
        return "mounted".equals(state);
    }
    @Override
    public String toString() {
        return "StorageInfo [path=" + path + ", state=" + state
                + ", isRemoveable=" + isRemoveable + ", lable=" + lable + "]";
    }
}