package com.l.eyescure.server;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;
import android.util.Log;

import com.l.eyescure.server.StorageManager.StorageManager;
import com.l.eyescure.server.cureManage.CureInfoManage;
import com.l.eyescure.server.portManage.MessageManage;
import com.l.eyescure.server.printManager.PrintManager;
import com.l.eyescure.util.DeferredHandler;

import java.util.HashMap;

import static com.l.eyescure.application.MyApplication.TAG;

/**
 * Created by jerryco on 2016/8/20.
 */
public class ServerHelper {
    private static final int SYSTEM_INIT_DELAY = 1000;

    // 将所有的模块都集中在一块，跟注册表一样
    public static final int MODULE_ID_INITPORT = 0;
    public static final int MODULE_ID_STORAGE = 1;
    public static final int MODULE_ID_PRINT = 2;
    public static final int MODULE_ID_CUREINFO = 3;
//    public static final int MODULE_ID_CONTACT = 4;
//    public static final int MODULE_ID_REGISTER = 5;
//    public static final int MODULE_ID_SUPER= 6;

    public static final int MODULE_ID_MAX = 12;

    // 模块定义
    private class Module {
        public int id;
        public Class<?> module;

        public Module(int id, Class<?> module) {
            this.id = id;
            this.module = module;
        }
    }

    // 注册表（模块的初始化有先后顺序限制）
    private final Module[] moduleTable = new Module[]{
            new Module(MODULE_ID_INITPORT, MessageManage.class),
            new Module(MODULE_ID_STORAGE, StorageManager.class),
            new Module(MODULE_ID_PRINT, PrintManager.class),
            new Module(MODULE_ID_CUREINFO, CureInfoManage.class),
//            new Module(MODULE_ID_CONTACT, ContactManager.class),
//            new Module(MODULE_ID_SUPER, SuperUserManager.class),
//            new Module(MODULE_ID_REGISTER, RegisterManager.class),
    };

    private HashMap<Integer, EyesModule> modulesMap = new HashMap<>();
    private Handler mHandler = null;

    private DeferredHandler mMainHandler = null;

    // 系统是否初始化完成。
    private boolean mIsSystemReady = false;

    private static Context sContext;
    private static ServerHelper instance;

    public synchronized static ServerHelper getInstance() {
        if (instance == null) {
            instance = new ServerHelper();
        }
        return instance;
    }

    public static void setApplicationContext(Context context) {
        if (sContext != null) {
            Log.w(TAG, "ServerHelper::setApplicationContext called twice! old=" + sContext + " new=" + context);
            return;
        }

        sContext = context.getApplicationContext();
    }

    // 先将各模块实例化
    private void createAllModule() {
        for (int i = 0; i < moduleTable.length; i++) {
            Module module = moduleTable[i];

            EyesModule eyesModule = null;
            try {
                eyesModule = (EyesModule) module.module.newInstance();
            } catch (Exception e) {
                Log.d(TAG, "ServerHelper::createAllModule: " + e.toString());
                continue;
            }

            // Hash表中保存各模块
            modulesMap.put(module.id, eyesModule);

            // 设置application context
            eyesModule.setApplicationContext(sContext);
        }
    }

    private void initAllModule() {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < MODULE_ID_MAX; j++) {
                EyesModule module = modulesMap.get(j);
                if (module != null) {
                    module.init(i);
                }
            }
        }
    }

    private ServerHelper() {
        HandlerThread handlerThread = new HandlerThread("***eyes-main-thread***", Process.THREAD_PRIORITY_BACKGROUND);
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper());

        Runnable r = new Runnable() {
            @Override
            public void run() {
                synchronized (this) {
                    // 创建各模块
                    createAllModule();

                    // 对模块进行初始化，严格安装顺序
                    initAllModule();

                    mIsSystemReady = true;

                    Log.v(TAG, "ServerHelper::init: init done");
                }
            }
        };

        // 延迟启动应用主要是为了保证application已经完全启动了
        mHandler.postDelayed(r, SYSTEM_INIT_DELAY);
    }

    public Object getModule(int id) {
        if (modulesMap.isEmpty()) {
            return null;
        }

        if (!modulesMap.containsKey(id)) {
            return null;
        }

        EyesModule module = modulesMap.get(id);
        assert module != null;

        return module.getModule(id);
    }


    public boolean isSystemReady() {
        return mIsSystemReady;
    }


    public void runOnMainThread(Runnable r) {
        // If we are on the worker thread, post onto the main handler
        mMainHandler.post(r);
    }

    public void onTerminate() {
        // cancel internet monitor
        Runnable r = new Runnable() {
            @Override
            public void run() {
                synchronized (this) {
                    done();
                }
            }
        };

        mHandler.post(r);


    }

    private void done() {
        Log.i(TAG, "ServerHelper::done");

        for (int i = 0; i < 5; i++) {
            for (EyesModule module : modulesMap.values()) {
                module.done(i);
            }
        }
    }
}
