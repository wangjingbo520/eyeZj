package com.l.eyescure.server.callBack;

/**
 * Created by jerryco on 2016/8/1.
 */
public interface IRecvMsgByServiceCallback {
    void onReceive(int cmd, byte[] data);

    void onReceiveErr(String msgKey, String info, int err_ID);
}
