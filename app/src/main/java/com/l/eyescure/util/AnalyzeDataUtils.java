package com.l.eyescure.util;

import android.util.Log;

/**
 * Created by Look on 2017/1/16.
 * 数据解析类
 */

public class AnalyzeDataUtils {
    private static String TAG = AnalyzeDataUtils.class.getSimpleName();

    public AnalyzeDataUtils(){
    }

    private boolean doCheck(byte[] reciveData,int dataLength){
        if(reciveData[0] != reciveData[1] || reciveData[0] != reciveData[2] ||
                reciveData[0] != reciveData[3] || reciveData[0] != 0x55){
            Log.e(TAG, "数据帧头错误");
            return false;
        }
        if(reciveData[dataLength-1] != reciveData[dataLength-2] ||
                reciveData[dataLength-1] != reciveData[dataLength-3]|| reciveData[dataLength-1] !=
                reciveData[dataLength-4] || reciveData[dataLength-1] != (byte)0xAA){
            Log.e(TAG, "数据帧尾错误");
//            return false;
        }
//        if(((reciveData[5] & 0xFF) + ((reciveData[6]<< 8) & 0xFFFF)) != dataLength){
//            Log.e(TAG, "数据长度错误");
//            return false;
//        }
        return true;
    }

    public byte[] analyze(byte[] buffer,int size){
        byte[] result = null;
        if (!doCheck(buffer,size)){//数据校验失败
            return null;
        }
        byte[] command = new byte[1];//指令码
        int length = (int)buffer[5] + (int)buffer[6];//内容长度
        byte[] content = new byte[length];//内容
        command[0] = buffer[4];
        for (int i = 1;i <= length;i++){
            content[i-1] = buffer[6+i];
        }
        result = concat(command,content);
        return result;
    }

    private  byte[] concat(byte[] a, byte[] b) {
        byte[] c= new byte[a.length+b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }
}
