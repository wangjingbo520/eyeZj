package com.l.eyescure.activity.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.l.eyescure.R;
import com.l.eyescure.activity.SetSavePdfPathActivity;
import com.l.eyescure.server.pdfManager.StorageInfo;

import java.util.List;

/**
 * Created by wudi on 2016/11/23.
 */
public class StorageListAdapter extends BaseAdapter {
    private Context context;
    private List<StorageInfo> storageData;
    private LayoutInflater layoutInflater;
    private int type = 0;

    public StorageListAdapter(Context context, List<StorageInfo> myStorageData,int type) {
        this.context = context;
        this.storageData = myStorageData;
        layoutInflater = LayoutInflater.from(context);
        this.type = type;
    }

    private static class ViewHolder {
        ImageView storage_sign;
        ImageView storage_check_sign;
        TextView name;
    }

    @Override
    public int getCount() {
        return storageData.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        DisplayMetrics dm = new DisplayMetrics();
        ((SetSavePdfPathActivity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.storage_list_item, null);
            holder.storage_sign = (ImageView) convertView.findViewById(R.id.storage_sign);
            holder.storage_check_sign = (ImageView) convertView.findViewById(R.id.storage_check_sign);
            holder.name = (TextView) convertView.findViewById(R.id.storage_name);

            convertView.setPadding(1, 1, 1, 1);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        StorageInfo storageInfo = storageData.get(position);
        final String filePath = storageInfo.path;

        if (!TextUtils.isEmpty(filePath)) {
            if (filePath.indexOf("usb") != -1) {
                String fileName = storageInfo.lable;
                holder.storage_sign.setBackgroundResource(R.drawable.storage_usb);
                holder.name.setText(fileName);
            } else {
                holder.storage_sign.setBackgroundResource(R.drawable.storage_sd);
                holder.name.setText("SD");
            }
        }

        boolean bChecked = storageInfo.isChecked;
        if (bChecked) {
            holder.storage_check_sign.setBackgroundResource(R.drawable.storage_checked_p);
        } else {
            holder.storage_check_sign.setBackgroundResource(R.drawable.storage_checked_n);
        }

        return convertView;
    }
}
