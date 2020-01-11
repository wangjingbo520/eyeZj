package com.l.eyescure.server.callBack;

import com.l.eyescure.server.pdfManager.StorageInfo;

import java.util.List;

/**
 * Created by jerryco on 2016/8/1.
 */
public interface IStorageChangeCallback {
    void onChanged(List<StorageInfo> allStorages);
}
