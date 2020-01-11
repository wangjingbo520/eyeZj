package com.l.eyescure.util;

/**
 * Created by Look on 2016/12/27.
 * 常量类
 */

public class Constant {
    public static String out_port_path = "/dev/ttyS0";
    public static int out_port_bitrate = 9600;

    public static final String DRIVER = "com.mysql.jdbc.Driver";
    public static final String JDBC_URL_F = "jdbc:mysql://%s:3306/eyescure";
    public static final String JDBC_URL_IP = "192.168.1.66";
    //    public static final String JDBC_URL = "jdbc:mysql://10.0.2.2:3306/eyescure"; //虚拟机调试
    public static final String SQL_ACCOUNT = "root1";
    public static final String SQL_PASSWORD = "root1";

    public static final int PRINTER_TYPE = 0; //0 爱普生打印  1 惠普打印
    public static final String PRINTER_TYPE_STR = "print_type_str";
    public static final int PRINT_SCREEM_TYPE = 1; //0 竖屏  1 横屏

    public static final String DOCTOR_DB_NAME = "doctor_table";
    public static final String PATIENT_DB_NAME = "patient_table";
    public static final String CUREINFO_DB_NAME = "cureDetail_table";
    public static final String PATIENT_IMG_DB_NAME = "patient_img_table";
    public static final String CURE_STEP_DB_NAME = "cure_step_table";

    public static final String GET_NET_MODE = "get_net_mode";
    public static final String GET_NET_SUPER_PASSWORD = "get_net_super_password";
    public static final String GET_LOCAL_SUPER_PASSWORD= "get_local_super_password";

    public static String LEFT = "left";//左眼
    public static String RIGHT = "right";//右眼
    public static String QY_MODE = "qy_mode";//气压模式
    public static String cache = "/EyeCure_Cache";//缓存地址
    public static int FILE_CHOOSER_REQUEST_CODE = 0x02;   //请求码

    public static String PdfCache = "/EyeCure_PdfCache";//Pdf文档缓存地址

    public static final String ACTION_PORT_INIT_OK = "action_port_init_ok";
    public static final String ACTION_PORT_INIT_ERR = "action_port_init_err";
    public static final String ACTION_CURE_MANAGE_INIT_OK = "action_cure_manage_init_err";
    public static final String ACTION_HEART_BEEB_ERR= "action_heart_beeb_err";

    public static final String CUR_DETAIL_SAVE_NUM = "cur_detail_num";
    public static final String CUR_DETAIL_SAVE_ID = "cur_detail_id";

    public final static String USB_ACTION = "android.hardware.usb.action.USB_STATE";
    public final static String TF_MOUNTED_ACTION = "android.intent.action.MEDIA_MOUNTED";
    public final static String TF_EJECT_ACTION = "android.intent.action.MEDIA_EJECT";

    public static int SELF_CHECK = 0x02;      //自检
    public static int HOT_EYES_PIECE = 0x14;  //气压阀工作
    public static int AIR_VALUE_SET = 0x04;   //气压阀工作
    public static int STOP_AIR_WORK = 0x06;   //气压模式
    public static int HOT_START = 0x08;       //加热片加热
    public static int HOT_STOP = 0x0a;        //停止加热工作
    public static int QUERY_AIR = 0x0c;       //查询气压
    public static int QUERY_HOT = 0x0e;       //查询温度
    public static int CURE_COMPLATE = 0x10;   //治疗完成并自毁命令
    public static int BEEB_BEEB = 0x12;       //蜂鸣器
    public static int HEART_BEEB = 0x23;      //心跳
    public static int PORT_INIT = 0x21;       //握手消息

    public static String STORAGE_USB_PATH = "shard_usb_path";      //USB存储路径
    public static String STORAGE_TF_PATH = "shard_tf_path";        //TF卡存储路径
    public static String STORAGE_PRINT_EDGE = "shard_print_edge";   //打印的边界值

    public static int ERR_FILE_NOT_EXIT = 4000; //文件不存在错误
    public static int ERR_FILE_PATH_EMPTY = 4001; //文件路径为空
    public static int ERR_CMD_EXECUDT_FAILED = 4002; //命令执行失败
    public static int ERR_INTERNET_DISCONNEXT = 4003; //网路连接有问题

    public static final int PRINT_EDGE_LEFT = 15;
    public static final int PRINT_EDGE_RIGHT = 15;
    public static final int PRINT_EDGE_TOP = 10;
    public static final int PRINT_EDGE_BOTTOM = 10;

    public static final int DOCTOR_ADD = 0;
    public static final int DOCTOR_DELETE = 1;
    public static final int DOCTOR_UPDATE = 2;
    public static final int DOCTOR_QUERY = 3;
    public static final int DOCTOR_ONE_QUERY = 4;

    public static final int PATIENT_ADD = 20;
    public static final int PATIENT_UPDATE = 21;
    public static final int PATIENT_QUERY = 22;
    public static final int PATIENT_ONE_QUERY = 23;
    public static final int PATIENT_DELETE = 24;

    public static final int CUREINFO_ADD = 40;
    public static final int CUREINFO_QUERY = 41;
    public static final int CUREINFO_ONE_QUERY = 42;
    public static final int CUREINFO_UPDATE = 43;
    public static final int CUREINFO_DELETE = 44;

    public static final int PATIENT_IMG_QUERY = 60;
    public static final int PATIENT_IMG_ADD = 61;
    public static final int PATIENT_IMG_UPDATE = 62;

    public static final int CURE_STEP_QUERY = 80;
    public static final int CURE_STEP_ADD = 81;
}
