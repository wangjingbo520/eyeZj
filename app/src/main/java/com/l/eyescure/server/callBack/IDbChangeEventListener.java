package com.l.eyescure.server.callBack;

/**
 * Created by jerryco on 2016/9/18.
 */
public interface IDbChangeEventListener<T> {
    void onChangeEvent(T value);
}
