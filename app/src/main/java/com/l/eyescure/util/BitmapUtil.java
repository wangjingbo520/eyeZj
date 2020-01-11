package com.l.eyescure.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BitmapUtil {

    /**
     * 通过文件路径获取文件的创建时间
     */
    public static String getFileCreatedTime(String filePath) {
        String string = "";
        File file = new File(filePath);
        Date date = new Date(file.lastModified());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日 HH:mm:ss");
        string = sdf.format(date);
        return string;
    }

    /**
     * 通过文件路径获取文件的大小,并自动格式化
     */
    public static String getVideoLength(String filePath) {
        String string = "0B";
        DecimalFormat df = new DecimalFormat("#.00");
        File file = new File(filePath);
        long length = file.length();
        if (length == 0) { // 文件大小为0,直接返回0B
            return string;
        }
        if (length < 1024) { // 文件小于1KB,单位为 B
            string = df.format((double) length) + "B";
        } else if (length < 1048576) {// 文件小于1M,单 位为 KB
            string = df.format((double) length / 1024) + "K";
        } else if (length < 1073741824) {// 文件小于1G,单位为 MB
            string = df.format((double) length / 1048576) + "M";
        } else {
            string = df.format((double) length / 1073741824) + "G";
        }
        return string;
    }

    /**
     * 获取缩略图
     * @param imagePath:文件路径
     * @param width:缩略图宽度
     * @param height:缩略图高度
     * @return
     */
    public static Bitmap getImageThumbnail(String imagePath, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; //关于inJustDecodeBounds的作用将在下文叙述
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
        int h = options.outHeight;//获取图片高度
        int w = options.outWidth;//获取图片宽度
        int scaleWidth = w / width; //计算宽度缩放比
        int scaleHeight = h / height; //计算高度缩放比
        int scale = 1;//初始缩放比
        if (scaleWidth < scaleHeight) {//选择合适的缩放比
            scale = scaleWidth;
        } else {
            scale = scaleHeight;
        }
        if (scale <= 0) {//判断缩放比是否符合条件
//            be = 1;
        }
        options.inSampleSize = scale;
        // 重新读入图片，读取缩放后的bitmap，注意这次要把inJustDecodeBounds 设为 false
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    //将图片转换为二进制流
    public static byte[] readStream(String imagepath) throws Exception {
        FileInputStream fs = new FileInputStream(imagepath);
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while (-1 != (len = fs.read(buffer))) {
            outStream.write(buffer, 0, len);
        }
        outStream.close();
        fs.close();
        return outStream.toByteArray();
    }

    //将二进制转化为bitmap
    public Bitmap getBitmapFromByte(byte[] temp) {
        if (temp != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(temp, 0, temp.length);
            return bitmap;
        } else {
            return null;
        }
    }
}
