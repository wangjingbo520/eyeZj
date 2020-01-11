package com.l.eyescure.server.callBack;

/**
 * Created by jerryco on 2016/8/1.
 */
public interface ISavePathChangeCallback {
    void onChanged(int type,String path); //type: 0: tf卡  1:usb
}
