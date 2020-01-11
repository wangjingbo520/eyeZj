package com.l.eyescure.server.callBack;

/**
 * Created by jerryco on 2016/9/18.
 */
public interface INormalEventListener<T> {
    void onValueEvent(int key,T value);
    void onErrEvent(int errCode);
}
