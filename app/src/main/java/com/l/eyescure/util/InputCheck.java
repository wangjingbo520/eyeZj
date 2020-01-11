package com.l.eyescure.util;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jerryco on 2016/9/26.
 */
public class InputCheck {
    private static InputCheck instance = null;
    public static int NICK_MIN_LENGTH = 1;
    public static int NICK_MAX_LENGTH = 24;
    public static int ACCOUNT_LENGTH = 11;

    public static int PASSWORD_MIN_LENGTH = 6;
    public static int PASSWORD_MAX_LENGTH = 18;

    public static InputCheck getInstance() {
        if (instance == null) {
            instance = new InputCheck();
        }
        return instance;
    }

    public InputCheck() {

    }

    /***
     * 校验字符串为汉字，字符，数字
     *
     * @param text
     * @return
     */
    public boolean checkInputNick(String text) {
        if (TextUtils.isEmpty(text))
            return false;

        //长度不够，返回格式错误
        if (text.getBytes().length < NICK_MIN_LENGTH || text.getBytes().length > NICK_MAX_LENGTH)
            return false;

        Pattern p = Pattern.compile("[\\u4e00-\\u9fa50-9A-Za-z_]{1,24}");
        Matcher m = p.matcher(text);
        if (m.matches()) {
            return true;
        }
        return false;
    }

    /***
     * 校验字符串为数字
     *
     * @param text
     * @return
     */
    public boolean checkInputNum(String text) {
        if (TextUtils.isEmpty(text))
            return false;

        Pattern p = Pattern.compile("[0-9]*");
        Matcher m = p.matcher(text);
        if (m.matches()) {
            return true;
        }
        return false;
    }

    /***
     * 校验字符串为字符，数字
     *
     * @param text
     * @return
     */
    public boolean checkInputPassword(String text) {
        if (TextUtils.isEmpty(text))
            return false;

        //长度不够，返回格式错误
        if (text.length() < PASSWORD_MIN_LENGTH || text.length() > PASSWORD_MAX_LENGTH)
            return false;

        Pattern p = Pattern.compile("[0-9A-Za-z]{6,18}");
        Matcher m = p.matcher(text);
        if (m.matches()) {
            return true;
        }
        return false;
    }

    /***
     * 校验字符串全为数字,字母，下划线，且是11位
     *
     * @param text
     * @return
     */
    public boolean checkInputAccount(String text) {
        if (TextUtils.isEmpty(text))
            return false;
        if (text.length() != ACCOUNT_LENGTH)
            return false;

        Pattern p = Pattern.compile("[0-9A-Za-z_]*");
        Matcher m = p.matcher(text);
        if (m.matches()) {
            return true;
        }
        return false;
    }

    public boolean checkIp(String text) {
        if (TextUtils.isEmpty(text))
            return false;

        Pattern p = Pattern.compile("[0-9.]*");
        Matcher m = p.matcher(text);
        if (m.matches()) {
            return true;
        }
        return false;
    }
}
