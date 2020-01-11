package com.l.eyescure.server;

import android.content.Context;

/**
 * Created by jerryco on 2016/9/13.
 */
public abstract class EyesModule {
    protected Context mContext;

    public EyesModule() {

    }

    public abstract void init(int step);

    public abstract void done(int step);

    public Object getModule(int id) {
        return this;
    }

    public void setApplicationContext(Context context) {
        mContext = context;
    }
}
