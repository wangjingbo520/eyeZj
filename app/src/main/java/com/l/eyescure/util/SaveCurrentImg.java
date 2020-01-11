package com.l.eyescure.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;

import com.l.eyescure.server.ServerHelper;
import com.l.eyescure.server.StorageManager.StorageManager;
import com.l.eyescure.server.cureManage.CureInfoManage;
import com.l.eyescure.server.pdfManager.PdfManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by admin on 2017/4/11.
 * 截图保存图片到本地
 */

public class SaveCurrentImg {

    public static String getScreenHot(View v, String cureNumber) {
        String filePath = null;
        try {
            int saveType = ((CureInfoManage)ServerHelper.getInstance().getModule(ServerHelper.MODULE_ID_CUREINFO)).getInfoMode;
            StorageManager storageManager = ((StorageManager)ServerHelper.getInstance().getModule(ServerHelper.MODULE_ID_STORAGE));
            if(saveType == 0) {
                filePath = storageManager.getExtendCachePath();
                if(TextUtils.isEmpty(filePath)){
                    ToastUtil.s("您没有装备TF卡，无法存储头像");
                    return null;
                }
            }
            else
                filePath = storageManager.getInnerCachePath();

            filePath = filePath + "/" + cureNumber + ".jpg";
            Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas();
            canvas.setBitmap(bitmap);
            v.draw(canvas);
            try {
                FileOutputStream fos = new FileOutputStream(filePath);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            } catch (FileNotFoundException e) {
                throw new InvalidParameterException();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return filePath;
    }

    public static String getScreenPdfHot(View v, String patientName) {
        long time = System.currentTimeMillis();
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");

        String storagePath;
        int saveType = ((CureInfoManage)ServerHelper.getInstance().getModule(ServerHelper.MODULE_ID_CUREINFO)).getInfoMode;
        StorageManager storageManager = ((StorageManager)ServerHelper.getInstance().getModule(ServerHelper.MODULE_ID_STORAGE));
        if(saveType == 0) {
            storagePath = storageManager.getExtendCachePath();
            if(TextUtils.isEmpty(storagePath)){
                ToastUtil.s("您没有装备TF卡，无法存储头像");
                return null;
            }
        }
        else
            storagePath = storageManager.getInnerCachePath();

       final String filePath = storagePath + File.separator + patientName + "_" + sdf.format(date) + ".pdf";
        Bitmap bm = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        final byte[] bMap = baos.toByteArray();

        new Thread(new Runnable() {
            @Override
            public void run() {
                File file = PdfManager.Pdf(bMap, filePath);
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start();
        return filePath;
    }


    /**
     * 获取SDCard的目录路径功能
     *
     * @return
     */
    private static String getSDCardPath() {
        File sdcardDir = null;
        //判断SDCard是否存在
        boolean sdcardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        if (sdcardExist) {
            sdcardDir = Environment.getExternalStorageDirectory();
        }
        return sdcardDir.toString();
    }
}
