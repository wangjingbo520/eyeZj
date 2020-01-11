package com.l.eyescure.application;

import android.content.Context;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;


import com.l.eyescure.server.ServerHelper;
import com.l.eyescure.util.ToastUtil;

import org.xutils.x;

//import com.github.moduth.blockcanary.BlockCanaryContext;
//import com.github.moduth.blockcanary.BlockCanary;
//import com.github.moduth.blockcanary.internal.BlockInfo;
//import java.io.File;
//import java.util.LinkedList;
//import java.util.List;

//import com.didichuxing.doraemonkit.DoraemonKit;
/**
 * Created by Look on 2016/12/27.
 */

public class MyApplication extends MultiDexApplication {
    public static final String TAG = "eyescure";
    private static MyApplication myApplication;
    public String loginerNick = "未知";
    public boolean loginerIsSuper = false;
    public Handler mHandler;
    public String activePatientId = "";
    public Context mContext;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;
        mHandler = new Handler();
        mContext = this;

       // BlockCanary.install(this, new AppBlockCanaryContext()).start();

      //  DoraemonKit.install(MyApplication.getInstance());

        ServerHelper.setApplicationContext(this);
        ServerHelper.getInstance();
//        CrashReport.initCrashReport(this, "7c80bef675", true);//测试模式 发布记得false

        x.Ext.init(this);//初始化xtuils3
        x.Ext.setDebug(false);// 是否输出调试


    }

    public static MyApplication getInstance() {
        return myApplication;
    }

    public void showToastStr(final String showStr) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ToastUtil.s(showStr);
            }
        }, 1000);
    }


//    public class AppBlockCanaryContext extends BlockCanaryContext {
//        // 实现各种上下文，包括应用标示符，用户uid，网络类型，卡慢判断阙值，Log保存位置等
//
//        /**
//         * Implement in your project.
//         *
//         * @return Qualifier which can specify this installation, like version + flavor.
//         */
//        public String provideQualifier() {
//            return "unknown";
//        }
//
//        /**
//         * Implement in your project.
//         *
//         * @return user id
//         */
//        public String provideUid() {
//            return "uid";
//        }
//
//        /**
//         * Network type
//         *
//         * @return {@link String} like 2G, 3G, 4G, wifi, etc.
//         */
//        public String provideNetworkType() {
//            return "unknown";
//        }
//
//        /**
//         * Config monitor duration, after this time BlockCanary will stop, use
//         * with {@code BlockCanary}'s isMonitorDurationEnd
//         *
//         * @return monitor last duration (in hour)
//         */
//        public int provideMonitorDuration() {
//            return -1;
//        }
//
//        /**
//         * Config block threshold (in millis), dispatch over this duration is regarded as a BLOCK. You may set it
//         * from performance of device.
//         *
//         * @return threshold in mills
//         */
//        public int provideBlockThreshold() {
//            return 5000;
//        }
//
//        /**
//         * Thread stack dump interval, use when block happens, BlockCanary will dump on main thread
//         * stack according to current sample cycle.
//         * <p>
//         * Because the implementation mechanism of Looper, real dump interval would be longer than
//         * the period specified here (especially when cpu is busier).
//         * </p>
//         *
//         * @return dump interval (in millis)
//         */
//        public int provideDumpInterval() {
//            return provideBlockThreshold();
//        }
//
//        /**
//         * Path to save log, like "/blockcanary/", will save to sdcard if can.
//         *
//         * @return path of log files
//         */
//        public String providePath() {
//            return "/blockcanary/";
//        }
//
//        /**
//         * If need notification to notice block.
//         *
//         * @return true if need, else if not need.
//         */
//        public boolean displayNotification() {
//            return true;
//        }
//
//        /**
//         * Implement in your project, bundle files into a zip file.
//         *
//         * @param src  files before compress
//         * @param dest files compressed
//         * @return true if compression is successful
//         */
//        public boolean zip(File[] src, File dest) {
//            return false;
//        }
//
//        /**
//         * Implement in your project, bundled log files.
//         *
//         * @param zippedFile zipped file
//         */
//        public void upload(File zippedFile) {
//            throw new UnsupportedOperationException();
//        }
//
//
//        /**
//         * Packages that developer concern, by default it uses process name,
//         * put high priority one in pre-order.
//         *
//         * @return null if simply concern only package with process name.
//         */
//        public List<String> concernPackages() {
//            return null;
//        }
//
//        /**
//         * Filter stack without any in concern package, used with @{code concernPackages}.
//         *
//         * @return true if filter, false it not.
//         */
//        public boolean filterNonConcernStack() {
//            return false;
//        }
//
//        /**
//         * Provide white list, entry in white list will not be shown in ui list.
//         *
//         * @return return null if you don't need white-list filter.
//         */
//        public List<String> provideWhiteList() {
//            LinkedList<String> whiteList = new LinkedList<>();
//            whiteList.add("org.chromium");
//            return whiteList;
//        }
//
//        /**
//         * Whether to delete files whose stack is in white list, used with white-list.
//         *
//         * @return true if delete, false it not.
//         */
//        public boolean deleteFilesInWhiteList() {
//            return true;
//        }
//
//        /**
//         * Block interceptor, developer may provide their own actions.
//         */
//        public void onBlock(Context context, BlockInfo blockInfo) {
//
//        }
//    }
}
