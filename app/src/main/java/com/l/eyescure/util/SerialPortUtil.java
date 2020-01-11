package com.l.eyescure.util;

/**
 * Created by luka on 2017/1/11.
 */
public class SerialPortUtil {

    public static int CRC16(byte str[], int len) {
        int flag, i, iTmp = 0;
        int crc = 0x0FFFF;

        while (len-- != 0) {
            crc ^= (str[iTmp] & 0x0FF);
            for (i = 0x80; i != 0; i /= 2) {
                flag = crc & 0x01;
                crc /= 2;
                if (flag != 0) {
                    crc ^= 0xA001;
                }
            }
            iTmp++;
        }
        crc &= 0x0FFFF;
        crc = ((crc << 8) | (crc >> 8)) & 0x0FFFF;
        return crc;
    }

    public static String byte2hex(byte [] buffer){
        String h = "";

        if(buffer == null)
            return  h;

        for(int i = 0; i < buffer.length; i++){
            String temp = Integer.toHexString(buffer[i] & 0xFF);
            if(temp.length() == 1){
                temp = "0" + temp;
            }
            h = h + " "+ temp;
        }

        return h;

    }

}
